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

public class DistanceVectorTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testCreation() {
        System.out.println("creation<distance,angle>");
        DistanceVector instance = new DistanceVector(100, new Angle(90));
        assertEquals(100, instance.getDistance());
        assertEquals(90, instance.getDegrees());
    }

    @Test
    public void testCreation2() {
        System.out.println("creation<location.location>");
        DistanceVector instance = new DistanceVector(new Location(), new Location(10, 0));
        assertEquals(10, instance.getDistance());
        assertEquals(0, instance.getDegrees());
        instance = new DistanceVector(new Location(), new Location(0, 10));
        assertEquals(10, instance.getDistance());
        assertEquals(90, instance.getDegrees());
    }

    @Test
    public void testPolar2Location() {
        System.out.println("polar2Location");
        DistanceVector instance = new DistanceVector(100, new Angle(90));
        Location res = instance.toLocation(new Location(10, 10));
        assertEquals(10, res.getX(), DELTA);
        assertEquals(110, res.getY(), DELTA);
        instance = new DistanceVector(100, new Angle(180));
        res = instance.toLocation(new Location(10, 10));
        assertEquals(-90, res.getX(), DELTA);
        assertEquals(10, res.getY(), DELTA);
        instance = new DistanceVector(100, new Angle(270));
        res = instance.toLocation(new Location(10, 10));
        assertEquals(10, res.getX(), DELTA);
        assertEquals(-90, res.getY(), DELTA);
        instance = new DistanceVector(1, new Angle(45));
        res = instance.toLocation(new Location());
        assertEquals(1 / Math.sqrt(2), res.getX(), DELTA);
        assertEquals(1 / Math.sqrt(2), res.getY(), DELTA);
        assertEquals(1, instance.getDistance());
        assertEquals(45, instance.getDegrees());
    }

    @Test
    public void testAdd() {
        System.out.println("add");
        DistanceVector instance = new DistanceVector(100, 0);
        DistanceVector res = (DistanceVector) instance.plus(instance);
        assertEquals(0, res.getDegrees());
        assertEquals(200, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(0, instance.getDegrees());
        DistanceVector other = new DistanceVector(100, 90);
        res = (DistanceVector) instance.plus(other);
        assertEquals(45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistanceVector(100, 180);
        res = (DistanceVector) instance.plus(other);
        assertEquals(90, res.getDegrees());
        assertEquals(0, res.getDistance(), DELTA);
    }

    @Test
    public void testSubtract() {
        System.out.println("subtract");
        DistanceVector instance = new DistanceVector(100, 0);
        DistanceVector other = new DistanceVector(50, 0);
        DistanceVector res = (DistanceVector) instance.sub(other);
        assertEquals(0, res.getDegrees());
        assertEquals(50, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(0, instance.getDegrees());
        other = new DistanceVector(100, 90);
        res = (DistanceVector) instance.sub(other);
        assertEquals(315, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new DistanceVector(100, 180);
        res = (DistanceVector) instance.sub(other);
        assertEquals(0, res.getDegrees(), DELTA);
        assertEquals(200, res.getDistance(), DELTA);
    }

    @Test
    public void testMult() {
        System.out.println("mult");
        DistanceVector instance = new DistanceVector(100, 0);
        DistanceVector res = (DistanceVector) instance.mult(2);
        assertEquals(0, res.getDegrees());
        assertEquals(200, res.getDistance());
        res = (DistanceVector) instance.mult(0.655);
        assertEquals(0, res.getDegrees());
        assertEquals(65.5, res.getDistance(), DELTA);
        res = (DistanceVector) instance.mult(-0.655);
        assertEquals(180, res.getDegrees());
        assertEquals(65.5, res.getDistance(), DELTA);
    }

    @Test
    public void testDegreesDiff_Angle() {
        System.out.println("degreesDiff<PropertyDegrees>");
        DistanceVector instance = new DistanceVector(1.0, new Angle(88));
        assertEquals(92, instance.degreesDiff(180).get());
    }

    @Test
    public void testDegreesDiff_Polar() {
        System.out.println("degreesDiff<Polar>");
        DistanceVector instance = new DistanceVector(1, 88);
        DistanceVector other = new DistanceVector(1, 180);
        assertEquals(92, instance.degreesDiff(other).get());
    }

    @Test
    public void testGetAngle() {
        System.out.println("getAngle");
        DistanceVector instance = new DistanceVector(1, 88);
        assertEquals(88, instance.getDegrees());
    }
}
