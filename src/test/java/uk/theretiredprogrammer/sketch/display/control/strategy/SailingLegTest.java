/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyLeg;

public class SailingLegTest {

    private static final double DELTA = 0.0000001;

    @Test
    public void testBeforemarkAngle0() {
        System.out.println("before mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 45),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testOnmarkAngle0() {
        System.out.println("on mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 50),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testBeyondmarkAngle0() {
        System.out.println("beyond mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 51),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeforemarkAngle0() {
        System.out.println("right before mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(70, 45),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightOnmarkAngle0() {
        System.out.println("right on mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(70, 50),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeyondmarkAngle0() {
        System.out.println("right beyond mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(70, 51),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeforemarkAngle0() {
        System.out.println("left before mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(20, 45),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftOnmarkAngle0() {
        System.out.println("left on mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(20, 50),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeyondmarkAngle0() {
        System.out.println("left beyond mark angle0");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(20, 51),
                new PropertyLocation(50, 50),
                0).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testBeforemarkAngle90() {
        System.out.println("before mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(45, 50),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testOnmarkAngle90() {
        System.out.println("on mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 50),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testBeyondmarkAngle90() {
        System.out.println("beyond mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(51, 50),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeforemarkAngle90() {
        System.out.println("right before mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(45, 30),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightOnmarkAngle90() {
        System.out.println("right on mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 30),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeyondmarkAngle90() {
        System.out.println("right beyond mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(51, 30),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeforemarkAngle90() {
        System.out.println("left before mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(45, 80),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(5.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftOnmarkAngle90() {
        System.out.println("left on mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 80),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeyondmarkAngle90() {
        System.out.println("left beyond mark angle90");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(51, 80),
                new PropertyLocation(50, 50),
                90).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testBeforemarkAngle45() {
        System.out.println("before mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(40, 40),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(Math.sqrt(200), calcrefdistance, DELTA);
    }

    @Test
    public void testOnmarkAngle45() {
        System.out.println("on mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 50),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testBeyondmarkAngle45() {
        System.out.println("beyond mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(50, 51),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeforemarkAngle45() {
        System.out.println("right before mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(80, 0),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(Math.sqrt(200), calcrefdistance, DELTA);
    }

    @Test
    public void testRightOnmarkAngle45() {
        System.out.println("right on mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(80, 20),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testRightBeyondmarkAngle45() {
        System.out.println("right beyond mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(80, 40),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeforemarkAngle45() {
        System.out.println("left before mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(20, 70),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(Math.sqrt(50), calcrefdistance, DELTA);
    }

    @Test
    public void testLeftOnmarkAngle45() {
        System.out.println("left on mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(30, 70),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }

    @Test
    public void testLeftBeyondmarkAngle45() {
        System.out.println("left beyond mark angle45");
        double calcrefdistance = CurrentLeg.getRefDistance(
                new PropertyLocation(35, 70),
                new PropertyLocation(50, 50),
                45).orElse(0.0);
        assertEquals(0.0, calcrefdistance, DELTA);
    }
}
