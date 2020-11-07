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
package uk.theretiredprogrammer.sketch.core.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES180;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES90;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PolarTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testAdd() {
        System.out.println("add");
        DistancePolar instance = new DistancePolar(100, DEGREES0);
        DistancePolar res = (DistancePolar) instance.plus(instance);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(200, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(DEGREES0, instance.getDegreesProperty());
        DistancePolar other = new DistancePolar(100, DEGREES90);
        res = (DistancePolar) instance.plus(other);
        assertEquals(45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistancePolar(100, DEGREES180);
        res = (DistancePolar) instance.plus(other);
        assertEquals(90, res.getDegrees());
        assertEquals(0, res.getDistance(), DELTA);
    }

    @Test
    public void testSubtract() {
        System.out.println("subtract");
        DistancePolar instance = new DistancePolar(100, DEGREES0);
        DistancePolar other = new DistancePolar(50, DEGREES0);
        DistancePolar res = (DistancePolar) instance.sub(other);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(50, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(DEGREES0, instance.getDegreesProperty());
        other = new DistancePolar(100, DEGREES90);
        res = (DistancePolar) instance.sub(other);
        assertEquals(-45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistancePolar(100, DEGREES180);
        res = (DistancePolar) instance.sub(other);
        assertEquals(0, res.getDegrees(), DELTA);
        assertEquals(200, res.getDistance(), DELTA);
    }

    @Test
    public void testMult() {
        System.out.println("mult");
        DistancePolar instance = new DistancePolar(100, DEGREES0);
        DistancePolar res = (DistancePolar) instance.mult(2);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(200, res.getDistance());
        res = (DistancePolar) instance.mult(0.655);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(65.5, res.getDistance(), DELTA);
        res = (DistancePolar) instance.mult(-0.655);
        assertEquals(180, res.getDegrees());
        assertEquals(65.5, res.getDistance(), DELTA);
    }

    @Test
    public void testDegreesDiff_Angle() {
        System.out.println("degreesDiff<PropertyDegrees>");
        DistancePolar instance = new DistancePolar(1.0, new PropertyDegrees(88));
        assertEquals(92, instance.degreesDiff(DEGREES180).get());
    }
    
    @Test
    public void testDegreesDiff_Polar() {
        System.out.println("degreesDiff<Polar>");
        DistancePolar instance = new DistancePolar(1, new PropertyDegrees(88));
        DistancePolar other = new DistancePolar(1, DEGREES180);
        assertEquals(92, instance.degreesDiff(other).get());
    }

    @Test
    public void testGetAngle() {
        System.out.println("getAngle");
        DistancePolar instance = new DistancePolar(1, new PropertyDegrees(88));
        assertEquals(88, instance.getDegrees());
    }

    @Test
    public void testMeanAngle1() {
        System.out.println("meanAngle1");
        Polar[][] array = new Polar[2][1];
        array[0][0] = new SpeedPolar(1, DEGREES0);
        array[1][0] = new SpeedPolar(10, new PropertyDegrees(50));
        assertEquals(new PropertyDegrees(25), Polar.meanAngle(array));
    }

    @Test
    public void testMeanAngle2() {
        System.out.println("meanAngle2");
        Polar[][] array = new Polar[2][2];
        array[0][0] = new SpeedPolar(1, DEGREES0);
        array[1][0] = new SpeedPolar(5, DEGREES0);
        array[0][1] = new SpeedPolar(10, DEGREES0);
        array[1][1] = new SpeedPolar(15, new PropertyDegrees(45));
        assertEquals(10.7990805, Polar.meanAngle(array).get(), DELTA);
    }
}
