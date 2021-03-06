/**
 * Copyright 2015 Palantir Technologies
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.common.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.palantir.common.base.Throwables;
import com.palantir.common.concurrent.NamedThreadFactory;
import com.palantir.common.concurrent.PTExecutors;
import com.palantir.exception.PalantirInterruptedException;

/**
 * Proxy that calls the requested method in another thread waits on a Future.
 * If the calling thread is interrupted, this proxy will throw a PalantirInterruptedException.
 * If given the CancelDelgate#Cancel option, it will also interrupt the delegated thread.
 * @author dcohen
 *
 */
public final class InterruptibleProxy implements DelegatingInvocationHandler {

    private final CancelDelegate cancel;

    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(Class<T> interfaceClass, T delegate,
            CancelDelegate cancel) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass}, new InterruptibleProxy(delegate, cancel));
    }

    private final Object delegate;
    private static final ExecutorService executor = PTExecutors.newCachedThreadPool(
            new NamedThreadFactory("Interruptible Proxy", true /* isDaemon */ ));

    private InterruptibleProxy(Object delegate, CancelDelegate cancel) {
        this.delegate = delegate;
        this.cancel = cancel;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Future<Object> future = executor.submit(new Callable<Object>(){
            @Override
            public Object call() throws Exception {
                try {
                    return method.invoke(delegate, args);
                } catch (InvocationTargetException e) {
                    Throwables.rewrapAndThrowIfInstance(e.getCause(), Exception.class);
                    Throwables.rewrapAndThrowIfInstance(e.getCause(), Error.class);
                    throw Throwables.rewrapAndThrowUncheckedException(e.getCause());
                }
            }
        });
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw Throwables.rewrap(e.getCause());
        } catch (InterruptedException e) {
            throw new PalantirInterruptedException(e);
        } finally {
            future.cancel(cancel.equals(CancelDelegate.CANCEL));
        }
    }

    @Override
    public Object getDelegate() {
        return delegate;
    }
}

