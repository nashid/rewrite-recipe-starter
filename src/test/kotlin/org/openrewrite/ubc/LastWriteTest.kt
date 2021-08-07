/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.ubc

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaRecipeTest

class LastWriteTest : JavaRecipeTest {
    override val recipe: Recipe
        get() = LastWrite()

    @Test
    fun markLastWrite() = assertChanged(
            before = """
            class Test {
                void test() {
                    int n = 0;
                    if((n = 1) == 1) {
                        n = 2;
                        int n;
                        n = 3;
                        n += 1;
                        n++;
                    }
                }
            }
        """,
            after = """
            class Test {
                void test() {
                    int /*~~(definition of 1)~~>*/n = 0;
                    if((n = /*~~(write of 1)~~>*/1) == 1) {
                        n = /*~~(write of 1)~~>*/2;
                        int /*~~(definition of 2)~~>*/n;
                        n = /*~~(write of 2)~~>*/3;
                        n += 1;
                        n++;
                    }
                }
            }
        """,
        cycles = 1, expectedCyclesThatMakeChanges = 1
    )
}