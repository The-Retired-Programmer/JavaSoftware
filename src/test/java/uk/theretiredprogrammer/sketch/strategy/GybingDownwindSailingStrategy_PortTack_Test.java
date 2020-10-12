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
                () -> setboatdirection(140));
        assertSAILON(decision);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to downwind (on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(140),
                () -> setboattrue("downwindluffupiflifted"));
        assertTURN(decision, 135, false);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to downwind (stay on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(130),
                () -> setboattrue("downwindbearawayifheaded"));
        assertTURN(decision, 135, true);
    }

    @Test
    public void test5() throws IOException {
        System.out.println("gybe if headed (to starboard) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatdirection(140),
                () -> setboattrue("downwindgybeiflifted"));
        assertTURN(decision, -135, true);
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (port) - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 5),
                () -> setboatdirection(140),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertSAILON(decision);
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (port), bearway to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 5),
                () -> setboatdirection(125),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 140, true);
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (port), luff to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, 5),
                () -> setboatdirection(145),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, 140, false);
    }

    @Test
    public void test9() throws IOException {
        System.out.println("gybe onto best tack (starboard), from downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, -5),
                () -> setboatdirection(135),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, -140, true);
    }

    @Test
    public void test10() throws IOException {
        System.out.println("gybe onto best tack (starboard), above downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, -5),
                () -> setboatdirection(125),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, -140, true);

    }

    @Test
    public void test11() throws IOException {
        System.out.println("gybe onto best tack (starboard), below closehauled - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindflow(4, -5),
                () -> setboatdirection(150),
                () -> setboattrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertTURN(decision, -140, true);
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to rh corner - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(87, 53));
        assertSAILON(decision);
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to rh corner - gybe onto Starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(88.1, 51.9));
        assertTURN(decision, -135, true);
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond rh corner - gybe onto starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocation(90, 50));
        assertTURN(decision, -135, true);
    }
    // TODO - all tests are based on port mark roundings - 12-14 both _ and A should be repeated on starboard
    // TODO - need to add tests for mean wind direction at alternative angle 
    // TODO - need to add tests for case when wind angles move across discontinuity (ie ANGLE180)
    // TODO - need to add channel decision tests
}
