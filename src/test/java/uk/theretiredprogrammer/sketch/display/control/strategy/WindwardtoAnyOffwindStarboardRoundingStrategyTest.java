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

public class WindwardtoAnyOffwindStarboardRoundingStrategyTest extends SailingStrategyTest {

    private static final Angle DELTAANGLE = new Angle(5);

    @Test
    public void testStarboardlayline1A() throws IOException {
        System.out.println("starboard layline 1A");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding.json",
                () -> setboatlocation(52, 16));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline2A() throws IOException {
        System.out.println("starboard layline 2A");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding.json",
                () -> setboatlocation(50, 14));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline3A() throws IOException {
        System.out.println("starboard layline 3A");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48, 12));
        assertTURN(decision, 315, true);
    }

    @Test
    public void testStarboardlayline4A() throws IOException {
        System.out.println("starboard layline 4A");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(47.8, 11.8));
        assertTURN(decision, 315, true);
    }

    @Test
    public void testPortlayline1A() throws IOException {
        System.out.println("port layline 1A");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding.json",
                () -> setboatlocation(44, 12));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.plus(DELTAANGLE));
    }

    @Test
    public void testPortlayline2A() throws IOException {
        System.out.println("port layline 2A");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding.json",
                () -> setboatlocation(46, 10));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.plus(DELTAANGLE));
    }

    @Test
    public void testPortlayline3A() throws IOException {
        System.out.println("port layline 3A");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48, 8));
        assertMARKROUNDING(decision, 45, true);
    }

    @Test
    public void testPortlayline4A() throws IOException {
        System.out.println("port layline 4A");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48.2, 7.8));
        assertMARKROUNDING(decision, 45, true);
    }

    @Test
    public void testStarboardlayline1B() throws IOException {
        System.out.println("starboard layline 1B");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocation(44, 12));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline2B() throws IOException {
        System.out.println("starboard layline 2B");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding-90Wind.json",
                () -> setboatlocation(46, 10));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline3B() throws IOException {
        System.out.println("starboard layline 3B");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48, 8));
        assertTURN(decision, 45, true);
    }

    @Test
    public void testStarboardlayline4B() throws IOException {
        System.out.println("starboard layline 4B");
        Decision decision = makeDecision("/upwind-starboardtack-starboardrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48.2, 7.8));
        assertTURN(decision, 45, true);
    }

    @Test
    public void testPortlayline1B() throws IOException {
        System.out.println("port layline 1B");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding-90Wind.json",
                () -> setboatlocation(48, 4));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.plus(DELTAANGLE));
    }

    @Test
    public void testPortlayline2B() throws IOException {
        System.out.println("port layline 2B");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding-90Wind.json",
                () -> setboatlocation(50, 6));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.plus(DELTAANGLE));
    }

    @Test
    public void testPortlayline3B() throws IOException {
        System.out.println("port layline 3B");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52, 8));
        assertMARKROUNDING(decision, 135, true);
    }

    @Test
    public void testPortlayline4B() throws IOException {
        System.out.println("port layline 4B");
        Decision decision = makeDecision("/upwind-porttack-starboardrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52.2, 8.2));
        assertMARKROUNDING(decision, 135, true);
    }
}
