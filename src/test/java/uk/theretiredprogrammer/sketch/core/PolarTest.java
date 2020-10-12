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
package uk.theretiredprogrammer.sketch.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PolarTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testAdd() {
        System.out.println("add");
        DistancePolar instance = new DistancePolar(100, new Angle(0));
        DistancePolar res = (DistancePolar) instance.add(instance);
        assertEquals(new Angle(0), res.getAngle());
        assertEquals(200, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(new Angle(0), instance.getAngle());
        DistancePolar other = new DistancePolar(100, new Angle(90));
        res = (DistancePolar) instance.add(other);
        assertEquals(45, res.getAngle().getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistancePolar(100, new Angle(180));
        res = (DistancePolar) instance.add(other);
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(0, res.getDistance(), DELTA);
    }

    @Test
    public void testSubtract() {
        System.out.println("subtract");
        DistancePolar instance = new DistancePolar(100, new Angle(0));
        DistancePolar other = new DistancePolar(50, new Angle(0));
        DistancePolar res = (DistancePolar) instance.subtract(other);
        assertEquals(new Angle(0), res.getAngle());
        assertEquals(50, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(new Angle(0), instance.getAngle());
        other = new DistancePolar(100, new Angle(90));
        res = (DistancePolar) instance.subtract(other);
        assertEquals(-45, res.getAngle().getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistancePolar(100, new Angle(180));
        res = (DistancePolar) instance.subtract(other);
        assertEquals(0, res.getAngle().getDegrees(), DELTA);
        assertEquals(200, res.getDistance(), DELTA);
    }

    @Test
    public void testMult() {
        System.out.println("mult");
        DistancePolar instance = new DistancePolar(100, new Angle(0));
        DistancePolar res = (DistancePolar) instance.mult(2);
        assertEquals(new Angle(0), res.getAngle());
        assertEquals(200, res.getDistance());
        res = (DistancePolar) instance.mult(0.655);
        assertEquals(new Angle(0), res.getAngle());
        assertEquals(65.5, res.getDistance(), DELTA);
        res = (DistancePolar) instance.mult(-0.655);
        assertEquals(180, res.getAngle().getDegrees());
        assertEquals(65.5, res.getDistance(), DELTA);
    }

    @Test
    public void testAngleDiff_Angle() {
        System.out.println("angleDiff<Angle>");
        DistancePolar instance = new DistancePolar(1, new Angle(88));
        assertEquals(92, instance.angleDiff(new Angle(180)).getDegrees());
    }

    @Test
    public void testAngleDiff_Polar() {
        System.out.println("angleDiff<Polar>");
        DistancePolar instance = new DistancePolar(1, new Angle(88));
        DistancePolar other = new DistancePolar(1, new Angle(180));
        assertEquals(92, instance.angleDiff(other).getDegrees());
    }

    @Test
    public void testGetAngle() {
        System.out.println("getAngle");
        DistancePolar instance = new DistancePolar(1, new Angle(88));
        assertEquals(88, instance.getAngle().getDegrees());
    }

    @Test
    public void testMeanAngle1() {
        System.out.println("meanAngle1");
        Polar[][] array = new Polar[2][1];
        array[0][0] = new SpeedPolar(1, ANGLE0);
        array[1][0] = new SpeedPolar(10, new Angle(50));
        assertEquals(new Angle(25), Polar.meanAngle(array));
    }

    @Test
    public void testMeanAngle2() {
        System.out.println("meanAngle2");
        Polar[][] array = new Polar[2][2];
        array[0][0] = new SpeedPolar(1, ANGLE0);
        array[1][0] = new SpeedPolar(5, ANGLE0);
        array[0][1] = new SpeedPolar(10, ANGLE0);
        array[1][1] = new SpeedPolar(15, new Angle(45));
        assertEquals(10.7990805, Polar.meanAngle(array).getDegrees(), DELTA);
    }
}
