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
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class GybingDownwindtoWindwardStarboardRoundingStrategyTest extends SailingStrategyTest {

    @Test
    public void testStarboardlayline1A() throws IOException {
        System.out.println("starboard layline 1A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding.json",
                () -> setboatlocationvalue("location", 56, 12));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testStarboardlayline2A() throws IOException {
        System.out.println("starboard layline 2A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding.json",
                () -> setboatlocationvalue("location", 54, 10));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testStarboardlayline3A() throws IOException {
        System.out.println("starboard layline 3A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding.json",
                () -> setboatlocationvalue("location", 52, 8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(-45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testStarboardlayline4A() throws IOException {
        System.out.println("starboard layline 4A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding.json",
                () -> setboatlocationvalue("location", 51.8, 7.8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(-45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testPortlayline1A() throws IOException {
        System.out.println("port layline 1A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding.json",
                () -> setboatlocationvalue("location", 48, 16));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testPortlayline2A() throws IOException {
        System.out.println("port layline 2A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding.json",
                () -> setboatlocationvalue("location", 50, 14));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testPortlayline3A() throws IOException {
        System.out.println("port layline 3A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding.json",
                () -> setboatlocationvalue("location", 52, 12));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(-45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testPortlayline4A() throws IOException {
        System.out.println("port layline 4A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding.json",
                () -> setboatlocationvalue("location", 52.2, 11.8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(-45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testStarboardlayline1B() throws IOException {
        System.out.println("starboard layline 1B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 52, 4));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testStarboardlayline2B() throws IOException {
        System.out.println("starboard layline 2B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 50, 6));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testStarboardlayline3B() throws IOException {
        System.out.println("starboard layline 3B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 48, 8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testStarboardlayline4B() throws IOException {
        System.out.println("starboard layline 4B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 47.8, 8.2));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testPortlayline1B() throws IOException {
        System.out.println("port layline 1B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 56, 12));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testPortlayline2B() throws IOException {
        System.out.println("port layline 2B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 54, 10));
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void testPortlayline3B() throws IOException {
        System.out.println("port layline 3B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 52, 8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void testPortlayline4B() throws IOException {
        System.out.println("port layline 4B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-starboardrounding-90Wind.json",
                () -> setboatlocationvalue("location", 51.8, 7.8));
        assertEquals(MARKROUNDING, decision.getAction());
        assertEquals(new Angle(45), decision.getAngle());
        assert (decision.isClockwise());
    }
}
