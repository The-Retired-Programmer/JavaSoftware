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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static uk.theretiredprogrammer.sketch.core.Location.LOCATIONZERO;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DistancePolarTest {

    private final static double DELTA = 0.0000001;

    public DistancePolarTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreation() {
        System.out.println("creation<distance,angle>");
        DistancePolar instance = new DistancePolar(100, new Angle(90));
        assertEquals(100, instance.getDistance());
        assertEquals(new Angle(90), instance.getAngle());
    }

    @Test
    public void testCreation2() {
        System.out.println("creation<location.location>");
        DistancePolar instance = new DistancePolar(LOCATIONZERO, new Location(10, 0));
        assertEquals(10, instance.getDistance());
        assertEquals(new Angle(90), instance.getAngle());
        instance = new DistancePolar(LOCATIONZERO, new Location(0, 10));
        assertEquals(10, instance.getDistance());
        assertEquals(new Angle(0), instance.getAngle());
    }

    /**
     * Test of polar2Location method, of class DistancePolar.
     */
    @Test
    public void testPolar2Location() {
        System.out.println("polar2Location");
        DistancePolar instance = new DistancePolar(100, new Angle(90));
        Location res = instance.polar2Location(new Location(10, 10));
        assertEquals(110, res.getX(), DELTA);
        assertEquals(10, res.getY(), DELTA);
        instance = new DistancePolar(100, new Angle(180));
        res = instance.polar2Location(new Location(10, 10));
        assertEquals(10, res.getX(), DELTA);
        assertEquals(-90, res.getY(), DELTA);
        instance = new DistancePolar(100, new Angle(-90));
        res = instance.polar2Location(new Location(10, 10));
        assertEquals(-90, res.getX(), DELTA);
        assertEquals(10, res.getY(), DELTA);
        instance = new DistancePolar(1, new Angle(45));
        res = instance.polar2Location(LOCATIONZERO);
        assertEquals(1 / Math.sqrt(2), res.getX(), DELTA);
        assertEquals(1 / Math.sqrt(2), res.getY(), DELTA);
        assertEquals(1, instance.getDistance());
        assertEquals(new Angle(45), instance.getAngle());
    }
}
