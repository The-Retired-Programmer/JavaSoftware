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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.entity.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class WindwardtoAnyOffwindPortRoundingStrategyTest extends SailingStrategyTest {

    private static final Angle DELTAANGLE = new Angle(5);

    @Test
    public void testStarboardlayline1A() throws IOException {
        System.out.println("starboard layline 1A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(56, 88));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline2A() throws IOException {
        System.out.println("starboard layline 2A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(54, 90));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline3A() throws IOException {
        System.out.println("starboard layline 3A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52, 92));
        assertMARKROUNDING(decision, -135, false);
    }

    @Test
    public void testStarboardlayline4A() throws IOException {
        System.out.println("starboard layline 4A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(51.8, 92.2));
        assertMARKROUNDING(decision, -135, false);
    }

    @Test
    public void testStarboardlayline5A() throws IOException {
        System.out.println("starboard layline 5A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(52, 92));
        assertTURN(decision, -135, false);
    }

    @Test
    public void testStarboardlayline6A() throws IOException {
        System.out.println("starboard layline 6A");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding.json",
                () -> setboatlocation(51.8, 92.2));
        assertTURN(decision, -135, false);
    }

    @Test
    public void testPortlayline1A() throws IOException {
        System.out.println("port layline 1A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocation(48, 84));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.add(DELTAANGLE));
    }

    @Test
    public void testPortlayline2A() throws IOException {
        System.out.println("port layline 2A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocation(50, 46));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.add(DELTAANGLE));
    }

    @Test
    public void testPortlayline3A() throws IOException {
        System.out.println("port layline 3A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52, 88));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testPortlayline4A() throws IOException {
        System.out.println("port layline 4A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52.2, 88.2));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testPortlayline5A() throws IOException {
        System.out.println("port layline 5A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocation(52, 88));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testPortlayline6A() throws IOException {
        System.out.println("port layline 6A");
        Decision decision = makeDecision("/upwind-porttack-portrounding.json",
                () -> setboatlocation(52.2, 88.2));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testStarboardlayline1B() throws IOException {
        System.out.println("starboard layline 1B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(48, 84));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline2B() throws IOException {
        System.out.println("starboard layline 2B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(50, 86));
        Angle starboardclosedhauled = getStarboardCloseHauled();
        assertSailing(decision, starboardclosedhauled.sub(DELTAANGLE), starboardclosedhauled);
    }

    @Test
    public void testStarboardlayline3B() throws IOException {
        System.out.println("starboard layline 3B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52, 88));
        assertMARKROUNDING(decision, -45, false);
    }

    @Test
    public void testStarboardlayline4B() throws IOException {
        System.out.println("starboard layline 4B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(52.2, 88.2));
        assertMARKROUNDING(decision, -45, false);
    }

    @Test
    public void testStarboardlayline5B() throws IOException {
        System.out.println("starboard layline 5B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(52, 88));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testStarboardlayline6B() throws IOException {
        System.out.println("starboard layline 6B");
        Decision decision = makeDecision("/upwind-starboardtack-portrounding-90Wind.json",
                () -> setboatlocation(52.2, 88.2));
        assertTURN(decision, -45, false);
    }

    @Test
    public void testPortlayline1B() throws IOException {
        System.out.println("port layline 1B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboatlocation(44, 92));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.add(DELTAANGLE));
    }

    @Test
    public void testPortlayline2B() throws IOException {
        System.out.println("port layline 2B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboatlocation(46, 90));
        Angle portclosedhauled = getPortCloseHauled();
        assertSailing(decision, portclosedhauled, portclosedhauled.add(DELTAANGLE));
    }

    @Test
    public void testPortlayline3B() throws IOException {
        System.out.println("port layline 3B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48, 88));
        assertTURN(decision, 45, false);
    }

    @Test
    public void testPortlayline4B() throws IOException {
        System.out.println("port layline 4B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboattrue("reachdownwind"),
                () -> setboatlocation(48.2, 87.8));
        assertTURN(decision, 45, false);
    }

    @Test
    public void testPortlayline5B() throws IOException {
        System.out.println("port layline 3B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboatlocation(48, 88));
        assertTURN(decision, 45, false);
    }

    @Test
    public void testPortlayline6B() throws IOException {
        System.out.println("port layline 4B");
        Decision decision = makeDecision("/upwind-porttack-portrounding-90Wind.json",
                () -> setboatlocation(48.2, 87.8));
        assertTURN(decision, 45, false);
    }
}
