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
import uk.theretiredprogrammer.sketch.core.entity.Angle;

public class GybingDownwindtoWindwardPortRoundingStrategyTest extends SailingStrategyTest {

    private static final Angle DELTAANGLE = new Angle(5);

    @Test
    public void testStarboardlayline1A() throws IOException {
        System.out.println("starboard layline 1A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding.json",
                () -> setboatlocation(52, 84));
        Angle starboardreaching = getStarboardReaching();
        assertSailing(decision, starboardreaching, starboardreaching.plus(DELTAANGLE));
    }

    @Test
    public void testStarboardlayline2A() throws IOException {
        System.out.println("starboard layline 2A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding.json",
                () -> setboatlocation(50, 86));
        Angle starboardreaching = getStarboardReaching();
        assertSailing(decision, starboardreaching, starboardreaching.plus(DELTAANGLE));
    }

    @Test
    public void testStarboardlayline3A() throws IOException {
        System.out.println("starboard layline 3A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding.json",
                () -> setboatlocation(48, 88));
        assertTURN(decision, getPortReaching(), false);
    }

    @Test
    public void testStarboardlayline4A() throws IOException {
        System.out.println("starboard layline 4A");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding.json",
                () -> setboatlocation(47.8, 88.2));
        assertTURN(decision, getPortReaching(), false);
    }

    @Test
    public void testPortlayline1A() throws IOException {
        System.out.println("port layline 1A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding.json",
                () -> setboatlocation(44, 88));
        Angle portreaching = getPortReaching();
        assertSailing(decision, portreaching.sub(DELTAANGLE), portreaching);
    }

    @Test
    public void testPortlayline2A() throws IOException {
        System.out.println("port layline 2A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding.json",
                () -> setboatlocation(46, 90));
        Angle portreaching = getPortReaching();
        assertSailing(decision, portreaching.sub(DELTAANGLE), portreaching);
    }

    @Test
    public void testPortlayline3A() throws IOException {
        System.out.println("port layline 3A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding.json",
                () -> setboatlocation(48, 92));
        assertMARKROUNDING(decision, 315, false);
    }

    @Test
    public void testPortlayline4A() throws IOException {
        System.out.println("port layline 4A");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding.json",
                () -> setboatlocation(48.2, 92.2));
        assertMARKROUNDING(decision, 315, false);
    }

    @Test
    public void testStarboardlayline1B() throws IOException {
        System.out.println("starboard layline 1B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(56, 92));
        Angle starboardreaching = new Angle(225);
        assertSailing(decision, starboardreaching, starboardreaching.plus(DELTAANGLE));
    }

    @Test
    public void testStarboardlayline2B() throws IOException {
        System.out.println("starboard layline 2B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(54, 90));
        Angle starboardreaching = new Angle(225);
        assertSailing(decision, starboardreaching, starboardreaching.plus(DELTAANGLE));
    }

    @Test
    public void testStarboardlayline3B() throws IOException {
        System.out.println("starboard layline 3B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(52, 88));
        assertTURN(decision, 135, false);
    }

    @Test
    public void testStarboardlayline4B() throws IOException {
        System.out.println("starboard layline 4B");
        Decision decision = makeDecision("/gybedownwindtowindward-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(51.8, 87.8));
        assertTURN(decision, 135, false);
    }

    @Test
    public void testPortlayline1B() throws IOException {
        System.out.println("port layline 1B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding-90Wind.json",
                () -> setboatlocation(52, 84));
        Angle portreaching = new Angle(135);
        assertSailing(decision, portreaching.sub(DELTAANGLE), portreaching);
    }

    @Test
    public void testPortlayline2B() throws IOException {
        System.out.println("port layline 2B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding-90Wind.json",
                () -> setboatlocation(50, 86));
        Angle portreaching = new Angle(135);
        assertSailing(decision, portreaching.sub(DELTAANGLE), portreaching);
    }

    @Test
    public void testPortlayline3B() throws IOException {
        System.out.println("port layline 3B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding-90Wind.json",
                () -> setboatlocation(48, 88));
        assertMARKROUNDING(decision, 45, false);
    }

    @Test
    public void testPortlayline4B() throws IOException {
        System.out.println("port layline 4B");
        Decision decision = makeDecision("/gybedownwindtowindward-porttack-portrounding-90Wind.json",
                () -> setboatlocation(47.8, 88.2));
        assertMARKROUNDING(decision, 45, false);
    }
}
