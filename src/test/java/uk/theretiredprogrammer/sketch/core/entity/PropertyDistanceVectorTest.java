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
package uk.theretiredprogrammer.sketch.core.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES180;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES90;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyLocation.LOCATIONZERO;

public class PropertyDistanceVectorTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testCreation() {
        System.out.println("creation<distance,angle>");
        PropertyDistanceVector instance = new PropertyDistanceVector(100, new PropertyDegrees(90));
        assertEquals(100, instance.getDistance());
        assertEquals(90, instance.getDegrees());
    }

    @Test
    public void testCreation2() {
        System.out.println("creation<location.location>");
        PropertyDistanceVector instance = new PropertyDistanceVector(LOCATIONZERO, new PropertyLocation(10, 0));
        assertEquals(10, instance.getDistance());
        assertEquals(90, instance.getDegrees());
        instance = new PropertyDistanceVector(LOCATIONZERO, new PropertyLocation(0, 10));
        assertEquals(10, instance.getDistance());
        assertEquals(0, instance.getDegrees());
    }

    @Test
    public void testPolar2Location() {
        System.out.println("polar2Location");
        PropertyDistanceVector instance = new PropertyDistanceVector(100, new PropertyDegrees(90));
        PropertyLocation res = instance.toLocation(new PropertyLocation(10, 10));
        assertEquals(110, res.getX(), DELTA);
        assertEquals(10, res.getY(), DELTA);
        instance = new PropertyDistanceVector(100, new PropertyDegrees(180));
        res = instance.toLocation(new PropertyLocation(10, 10));
        assertEquals(10, res.getX(), DELTA);
        assertEquals(-90, res.getY(), DELTA);
        instance = new PropertyDistanceVector(100, new PropertyDegrees(-90));
        res = instance.toLocation(new PropertyLocation(10, 10));
        assertEquals(-90, res.getX(), DELTA);
        assertEquals(10, res.getY(), DELTA);
        instance = new PropertyDistanceVector(1, new PropertyDegrees(45));
        res = instance.toLocation(LOCATIONZERO);
        assertEquals(1 / Math.sqrt(2), res.getX(), DELTA);
        assertEquals(1 / Math.sqrt(2), res.getY(), DELTA);
        assertEquals(1, instance.getDistance());
        assertEquals(45, instance.getDegrees());
    }

    @Test
    public void testAdd() {
        System.out.println("add");
        PropertyDistanceVector instance = new PropertyDistanceVector(100, DEGREES0);
        PropertyDistanceVector res = (PropertyDistanceVector) instance.plus(instance);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(200, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(DEGREES0, instance.getDegreesProperty());
        PropertyDistanceVector other = new PropertyDistanceVector(100, DEGREES90);
        res = (PropertyDistanceVector) instance.plus(other);
        assertEquals(45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new PropertyDistanceVector(100, DEGREES180);
        res = (PropertyDistanceVector) instance.plus(other);
        assertEquals(90, res.getDegrees());
        assertEquals(0, res.getDistance(), DELTA);
    }

    @Test
    public void testSubtract() {
        System.out.println("subtract");
        PropertyDistanceVector instance = new PropertyDistanceVector(100, DEGREES0);
        PropertyDistanceVector other = new PropertyDistanceVector(50, DEGREES0);
        PropertyDistanceVector res = (PropertyDistanceVector) instance.sub(other);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(50, res.getDistance());
        assertEquals(100, instance.getDistance());
        assertEquals(DEGREES0, instance.getDegreesProperty());
        other = new PropertyDistanceVector(100, DEGREES90);
        res = (PropertyDistanceVector) instance.sub(other);
        assertEquals(-45, res.getDegrees());
        assertEquals(100 * Math.sqrt(2), res.getDistance(), DELTA);
        other = new PropertyDistanceVector(100, DEGREES180);
        res = (PropertyDistanceVector) instance.sub(other);
        assertEquals(0, res.getDegrees(), DELTA);
        assertEquals(200, res.getDistance(), DELTA);
    }

    @Test
    public void testMult() {
        System.out.println("mult");
        PropertyDistanceVector instance = new PropertyDistanceVector(100, DEGREES0);
        PropertyDistanceVector res = (PropertyDistanceVector) instance.mult(2);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(200, res.getDistance());
        res = (PropertyDistanceVector) instance.mult(0.655);
        assertEquals(DEGREES0, res.getDegreesProperty());
        assertEquals(65.5, res.getDistance(), DELTA);
        res = (PropertyDistanceVector) instance.mult(-0.655);
        assertEquals(180, res.getDegrees());
        assertEquals(65.5, res.getDistance(), DELTA);
    }

    @Test
    public void testDegreesDiff_Angle() {
        System.out.println("degreesDiff<PropertyDegrees>");
        PropertyDistanceVector instance = new PropertyDistanceVector(1.0, new PropertyDegrees(88));
        assertEquals(92, instance.degreesDiff(DEGREES180).get());
    }

    @Test
    public void testDegreesDiff_Polar() {
        System.out.println("degreesDiff<Polar>");
        PropertyDistanceVector instance = new PropertyDistanceVector(1, new PropertyDegrees(88));
        PropertyDistanceVector other = new PropertyDistanceVector(1, DEGREES180);
        assertEquals(92, instance.degreesDiff(other).get());
    }

    @Test
    public void testGetAngle() {
        System.out.println("getAngle");
        PropertyDistanceVector instance = new PropertyDistanceVector(1, new PropertyDegrees(88));
        assertEquals(88, instance.getDegrees());
    }
}
