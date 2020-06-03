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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.racetrainingsketch.strategy.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GybingDownwindSailingStrategy_PortTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on port - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json");
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test2() throws IOException {
        System.out.println("don't luff to downwind (on port) - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatintvalue("heading", 140));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to downwind (on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatintvalue("heading", 140),
                () -> setboatparam("downwindluffupiflifted", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(135), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to downwind (stay on port) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatintvalue("heading", 130),
                () -> setboatparam("downwindbearawayifheaded", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(135), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test5() throws IOException {
        System.out.println("gybe if headed (to starboard) - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatintvalue("heading", 140),
                () -> setboatparam("downwindgybeiflifted", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-135), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (port) - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(5),
                () -> setboatintvalue("heading", 140),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (port), bearway to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(5),
                () -> setboatintvalue("heading", 125),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(140), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (port), luff to downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(5),
                () -> setboatintvalue("heading", 145),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(140), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void test9() throws IOException {
        System.out.println("gybe onto best tack (starboard), from downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(-5),
                () -> setboatintvalue("heading", 135),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-140), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test10() throws IOException {
        System.out.println("gybe onto best tack (starboard), above downwind - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(-5),
                () -> setboatintvalue("heading", 125),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-140), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test11() throws IOException {
        System.out.println("gybe onto best tack (starboard), below closehauled - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setwindfrom(-5),
                () -> setboatintvalue("heading", 150),
                () -> setboatparamstrue("downwindsailonbestgybe", "downwindbearawayifheaded", "downwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-140), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to rh corner - sailon");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocationvalue("location", 87, 53));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to rh corner - gybe onto Starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocationvalue("location", 88.1, 51.9));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-135), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond rh corner - gybe onto starboard - turn");
        Decision decision = makeDecision("/gybedownwind-porttack.json",
                () -> setboatlocationvalue("location", 90, 50));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-135), decision.getAngle());
        assert (decision.isClockwise());
    }
    // TODO - all tests are based on port mark roundings - 12-14 both _ and A should be repeated on starboard
    // TODO - need to add tests for mean wind direction at alternative angle 
    // TODO - need to add tests for case when wind angles move across discontinuity (ie ANGLE180)
    // TODO - need to add channel decision tests
}
