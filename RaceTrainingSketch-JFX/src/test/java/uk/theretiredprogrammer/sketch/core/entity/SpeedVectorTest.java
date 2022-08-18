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
package uk.theretiredprogrammer.sketch.core.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpeedVectorTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testCreation() {
        System.out.println("creation<speed,degrees>");
        SpeedVector instance = new SpeedVector(100, 90);
        assertEquals(100, instance.getSpeed());
        assertEquals(90, instance.getDegrees());
    }

    @Test
    public void testPlus() {
        System.out.println("plus");
        SpeedVector instance = new SpeedVector(100, 0);
        SpeedVector res = instance.plus(instance);
        assertEquals(0, res.getDegrees());
        assertEquals(200, res.getSpeed());
        assertEquals(100, instance.getSpeed());
        assertEquals(0, instance.getDegrees());
        SpeedVector other = new SpeedVector(100, 90);
        res = instance.plus(other);
        assertEquals(45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getSpeed(), DELTA);
        other = new SpeedVector(100, 180);
        res = instance.plus(other);
        assertEquals(90, res.getDegrees());
        assertEquals(0, res.getSpeed(), DELTA);
    }

    @Test
    public void testSub() {
        System.out.println("sub");
        SpeedVector instance = new SpeedVector(100, 0);
        SpeedVector other = new SpeedVector(50, 0);
        SpeedVector res = instance.sub(other);
        assertEquals(0, res.getDegrees());
        assertEquals(50, res.getSpeed());
        assertEquals(100, instance.getSpeed());
        assertEquals(0, instance.getDegrees());
        other = new SpeedVector(100, 90);
        res = instance.sub(other);
        assertEquals(315, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getSpeed(), DELTA);
        other = new SpeedVector(100, 180);
        res = instance.sub(other);
        double resangle = res.getDegrees();
        assertTrue(resangle < DELTA || resangle > 360 - DELTA );
        assertEquals(200, res.getSpeed(), DELTA);
    }

    @Test
    public void testMult() {
        System.out.println("mult");
        SpeedVector instance = new SpeedVector(100, 0);
        SpeedVector res = instance.mult(2);
        assertEquals(0, res.getDegrees());
        assertEquals(200, res.getSpeed());
        res = instance.mult(0.655);
        assertEquals(0, res.getDegrees());
        assertEquals(65.5, res.getSpeed(), DELTA);
        res = instance.mult(-0.655);
        assertEquals(180, res.getDegrees());
        assertEquals(65.5, res.getSpeed(), DELTA);
    }

    @Test
    public void testDegreesDiff_Angle() {
        System.out.println("degreesDiff<PropertyDegrees>");
        SpeedVector instance = new SpeedVector(1.0, new Angle(88));
        assertEquals(92, instance.degreesDiff(180).get());
    }

    @Test
    public void testDegreesDiff_Polar() {
        System.out.println("degreesDiff<Polar>");
        SpeedVector instance = new SpeedVector(1, 88);
        SpeedVector other = new SpeedVector(1, 180);
        assertEquals(92, instance.degreesDiff(other).get());
    }

    @Test
    public void testGetAngle() {
        System.out.println("getAngle");
        SpeedVector instance = new SpeedVector(1, 88);
        assertEquals(88, instance.getDegrees());
    }
}
