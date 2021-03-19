/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class WindwardSailingStrategy_StarboardTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on startboard - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json");
        assertSAILON(decision);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("don't luff to closehauled (on starboard) - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 275));
        assertSAILON(decision);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to closehauled (on startboard) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 275),
                () -> setboattrue("upwindluffupiflifted"));
        assertTURN(decision, 230, true);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to closehauled (stay on starboard) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 265),
                () -> setboattrue("upwindbearawayifheaded"));
        assertTURN(decision, 220, false);
    }

    @Test
    public void test5() throws IOException {
        System.out.println("tack if headed (to port) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 265),
                () -> setboattrue("upwindtackifheaded"));
        assertTURN(decision, 310, true);
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (starboard) - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatdirection(230),
                () -> setwindflow(4, 275),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertSAILON(decision);
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (starboard), bearway to closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatdirection(235),
                () -> setwindflow(4, 275),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, 230, false);
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (starboard), luff to closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 275),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, 230, true);
    }

    @Test
    public void test9() throws IOException {
        System.out.println("tack onto best tack (port), from closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatdirection(220),
                () -> setwindflow(4, 265),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, 310, true);
    }

    @Test
    public void test10() throws IOException {
        System.out.println("tack onto best tack (starboard), above closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindflow(4, 265),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, 310, true);
    }

    @Test
    public void test11() throws IOException {
        System.out.println("tack onto best tack (port), below closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatdirection(215),
                () -> setwindflow(4, 265),
                () -> setboattrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertTURN(decision, 310, true);
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to port corner - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(13, 53));
        assertSAILON(decision);
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to port corner layline, tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(12.1, 52.1));
        assertTURN(decision, 315, true);
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond port corner layline, tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(11, 51));
        assertTURN(decision, 315, true);
    }
}
