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

public class GybingDownwindSailingStrategy_PortTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on port - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json");
        assertSAILON(decision);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("don't luff to downwind (on port) - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(50));
        assertSAILON(decision);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to downwind (on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(50),
                () -> setboattrue("downwindluffupiflifted"));
        assertTURN(decision, 45, false);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to downwind (stay on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(40),
                () -> setboattrue("downwindbearawayifheaded"));
        assertTURN(decision, 45, true);
    }

    @Test
    public void test5() throws IOException {
        System.out.println("gybe if headed (to starboard) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(50),
                () -> setboattrue("downwindgybeiflifted"));
        assertTURN(decision, 135, true);
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (port) - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 275),
                () -> setboatdirection(50),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertSAILON(decision);
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (port), bearway to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 275),
                () -> setboatdirection(35),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 50, true);
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (port), luff to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 275),
                () -> setboatdirection(55),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 50, false);
    }

    @Test
    public void test9() throws IOException {
        System.out.println("gybe onto best tack (starboard), from downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 265),
                () -> setboatdirection(45),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 130, true);
    }

    @Test
    public void test10() throws IOException {
        System.out.println("gybe onto best tack (starboard), above downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 265),
                () -> setboatdirection(35),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 130, true);
    }

    @Test
    public void test11() throws IOException {
        System.out.println("gybe onto best tack (starboard), below closehauled - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 265),
                () -> setboatdirection(60),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 130, true);
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to rh corner - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(87, 47));
        assertSAILON(decision);
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to rh corner - gybe onto Starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(88.1, 48.1));
        assertTURN(decision, 135, true);
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond rh corner - gybe onto starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(90, 50));
        assertTURN(decision, 135, true);
    }
}
