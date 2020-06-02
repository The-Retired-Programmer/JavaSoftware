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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class WindwardSailingStrategy_StarboardTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on startboard - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json");
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test2() throws IOException {
        System.out.println("don't luff to closehauled (on starboard) - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(5));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test3() throws IOException {
        System.out.println("luff to closehauled (on startboard) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparam("upwindluffupiflifted", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test4() throws IOException {
        System.out.println("bearaway to closehauled (stay on starboard) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparam("upwindbearawayifheaded", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-50), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void test5() throws IOException {
        System.out.println("tack if headed (to port) - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparam("upwindtackifheaded", true));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test6() throws IOException {
        System.out.println("still best tack (starboard) - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatintvalue("heading", -40),
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test7() throws IOException {
        System.out.println("still best tack (starboard), bearway to closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatintvalue("heading", -35),
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-40), decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void test8() throws IOException {
        System.out.println("still best tack (starboard), luff to closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test9() throws IOException {
        System.out.println("tack onto best tack (port), from closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatintvalue("heading", -50),
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test10() throws IOException {
        System.out.println("tack onto best tack (starboard), above closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test11() throws IOException {
        System.out.println("tack onto best tack (port), below closehauled - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatintvalue("heading", -55),
                () -> setwindfrom(-5),
                () -> setboatparamstrue("upwindsailonbesttack", "upwindbearawayifheaded", "upwindluffupiflifted"));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(40), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test12() throws IOException {
        System.out.println("sail near to port corner - sailon");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocationvalue("location", 13, 47));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test13() throws IOException {
        System.out.println("sail to port corner layline, tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocationvalue("location", 12.1, 47.9));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test14() throws IOException {
        System.out.println("sail beyond port corner layline, tack onto starboard - turn");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocationvalue("location", 11, 49));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }
    // TODO - all tests are based on port mark roundings - 12-15 both _ and A should be repeated on starboard
    // TODO - need to add tests for mean wind direction at alternative angle 
    // TODO - need to add tests for case when wind angles move across discontinuity (ie ANGLE180)
    // TODO - need to add channel decision tests
}
