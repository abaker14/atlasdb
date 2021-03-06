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
package com.palantir.util.jmx;

import com.palantir.annotations.PgNotExtendableApi;
import com.palantir.annotations.PgPublicApi;

@PgPublicApi
@PgNotExtendableApi
public interface OperationStats {

    /**
     * This returns the percentage of calls that were returned in {@code millis} milliseconds
     *
     * These are often referred to as understats u2000 is the percentage of calls finished in 2ms
     * {@code percentCallsFinishedInMillis(2000)} is also known as u2000
     *
     *
     * @param millis
     * @return
     */
    public abstract double getPercentCallsFinishedInMillis(int millis);

    /**
     * This returns the approximate number of milliseconds for a given percentile
     *
     * {@code getPercentileMillis(90)} is also known as tp90
     * {@code getPercentileMillis(50)} is the median
     */
    public abstract double getPercentileMillis(double perc);

    public abstract void clearStats();

    public abstract long getTotalTime();

    public abstract long getTotalCalls();

    public abstract long getTimePerCallInMillis();

    public abstract double getStandardDeviationInMillis();

    public abstract long getMaxCallTime();

    public abstract long getMinCallTime();

    public abstract double getMedianTimeRequestInMillis();

}
