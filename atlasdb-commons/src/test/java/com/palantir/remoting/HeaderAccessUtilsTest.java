/**
 * Copyright 2017 Palantir Technologies
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
package com.palantir.remoting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.palantir.common.remoting.HeaderAccessUtils;

public class HeaderAccessUtilsTest {
    private static final String FOO = "foo";
    private static final String KEY_1 = "variables";
    private static final String KEY_2 = "unix";
    private static final String KEY_3 = "haltingProblemProofs";
    private static final ImmutableList<String> VALUE_1 = ImmutableList.of(FOO, "bar", "baz");
    private static final ImmutableList<String> VALUE_2 = ImmutableList.of("ls", "du", "cut");
    private static final ImmutableList<String> VALUE_3 = ImmutableList.of();

    private static final Map<String, Collection<String>> HEADERS = ImmutableMap.<String, Collection<String>>builder()
            .put(KEY_1, VALUE_1)
            .put(KEY_2, VALUE_2)
            .put(KEY_3, VALUE_3)
            .build();

    @Test
    public void caseInsensitiveContainsEntryIgnoresCaseOnKeys() {
        assertCaseInsensitiveContainsEntry(KEY_1, FOO, true);
        assertCaseInsensitiveContainsEntry(KEY_1.toUpperCase(), FOO, true);
        assertCaseInsensitiveContainsEntry("VaRiAbLES", FOO, true);
    }

    @Test
    public void caseInsensitiveContainsEntryRespectsCaseOnValues() {
        assertCaseInsensitiveContainsEntry(KEY_1, "FoO", false);
        assertCaseInsensitiveContainsEntry(KEY_2, "Cut", false);
    }

    @Test
    public void caseInsensitiveContainsEntryReturnsFalseIfNoKeyMatches() {
        assertCaseInsensitiveContainsEntry("keyboards", "qwerty", false);
    }

    @Test
    public void caseInsensitiveContainsEntryReturnsFalseOnEmptyListForMatchingKey() {
        assertCaseInsensitiveContainsEntry(KEY_3, "marginTooSmallToContainThis", false);
    }

    @Test
    public void caseInsensitiveContainsEntryShortcircuits() {
        Map<String, Collection<String>> testMap = Maps.newLinkedHashMap();
        String additionalCommand = "ps ax | awk '{print $1}' | xargs kill -9";
        testMap.put(KEY_2, VALUE_2);
        testMap.put(KEY_2.toUpperCase(), ImmutableList.of(additionalCommand));
        assertThat(HeaderAccessUtils.shortcircuitingCaseInsensitiveContainsEntry(testMap, KEY_2, additionalCommand),
                is(false));
    }

    private static void assertCaseInsensitiveContainsEntry(String key, String value, boolean outcome) {
        assertThat(HeaderAccessUtils.shortcircuitingCaseInsensitiveContainsEntry(HEADERS, key, value), is(outcome));
    }
}
