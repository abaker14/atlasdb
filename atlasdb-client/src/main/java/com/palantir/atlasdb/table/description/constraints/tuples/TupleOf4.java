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
package com.palantir.atlasdb.table.description.constraints.tuples;


public class TupleOf4<A,B,C,D> implements Tuple{
    private final A one;
    private final B two;
    private final C three;
    private final D four;
    public static <A,B,C,D> TupleOf4<A,B,C,D> of(A a, B b, C c, D d) {
        return new TupleOf4<A,B,C,D>(a,b,c,d);
    }
    public TupleOf4(A a, B b, C c, D d) {
        this.one=a;
        this.two=b;
        this.three=c;
        this.four=d;
    }

    public A field1() {
        return one;
    }

    public B field2() {
        return two;
    }

    public C field3() {
        return three;
    }

    public D field4() {
        return four;
    }

    @Override
    public int getSize() {
        return 4;
    }

}

