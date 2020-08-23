/*
 * Copyright 2020 richard linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.sketch.strategy;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class WindwardSailingStrategy_PortTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on port - sailon");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json");
        assertSAILON(decision);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("don't luff to closehauled (on port) - sailon");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(-5));
        assertSAILON(decision);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to closehauled (on port) - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparam("upwindluffupiflifted", true));
        assertTURN(decision, 40, false);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to closehauled (stay on port) - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparam("upwindbearawayifheaded", true));
        assertTURN(decision, 50, true);
    }

    @Test
    public void test5() throws IOException {
        System.out.println("tack if headed (to starboard) - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparam("upwindtackifheaded", true));
        assertTURN(decision, -40, false);
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (port) - sailon");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"),
                () -> setboatintvalue("heading", 40));
        assertSAILON(decision);
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (port), bearway to closehauled - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"),
                () -> setboatintvalue("heading", 35));
        assertTURN(decision, 40, true);
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (port), luff to closehauled - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"),
                () -> setboatintvalue("heading", 50));
        assertTURN(decision, 40, false);
    }

    @Test
    public void test9() throws IOException {
        System.out.println("tack onto best tack (starboard), from closehauled - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"),
                () -> setboatintvalue("heading", 50));
        assertTURN(decision, -40, false);
    }

    @Test
    public void test10() throws IOException {
        System.out.println("tack onto best tack (starboard), above closehauled - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, -40, false);
    }

    @Test
    public void test11() throws IOException {
        System.out.println("tack onto best tack (starboard), below closehauled - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"),
                () -> setboatintvalue("heading", 55));
        assertTURN(decision, -40, false);
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to starboard corner - sailon");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocationvalue("location", 88, 48));
        assertSAILON(decision);
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to starboard corner (on line of mark - sail on");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocationvalue("location", 90, 50));
        assertSAILON(decision);
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond starboard corner - tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocationvalue("location", 92.2, 52.2));
        assertTURN(decision, -45, false);
    }

    @Test
    public void test15() throws IOException {
        System.out.println("sail beyond starboard corner - tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocationvalue("location", 94, 54));
        assertTURN(decision, -45, false);
    }
    // TODO - all tests are based on port mark roundings - 12-15 both _ and A should be repeated on starboard
    // TODO - need to add tests for mean wind direction at alternative angle 
    // TODO - need to add tests for case when wind angles move across discontinuity (ie ANGLE180)
    // TODO - need to add channel decision tests
}
