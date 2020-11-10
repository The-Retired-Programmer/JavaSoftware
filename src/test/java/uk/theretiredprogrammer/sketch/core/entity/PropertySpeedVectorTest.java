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

public class PropertySpeedVectorTest {

    private final static double DELTA = 0.0000001;

    @Test
    public void testCreation() {
        System.out.println("creation<speed,degrees>");
        PropertySpeedVector instance = new PropertySpeedVector(100, 90);
        assertEquals(100, instance.getSpeed());
        assertEquals(90, instance.getDegrees());
    }

    @Test
    public void testMeanAngle1() {
        System.out.println("meanAngle1");
        PropertySpeedVector[][] array = new PropertySpeedVector[2][1];
        array[0][0] = new PropertySpeedVector(1, 0);
        array[1][0] = new PropertySpeedVector(10, 50);
        assertEquals(new PropertyDegrees(25), PropertySpeedVector.meanAngle(array));
    }

    @Test
    public void testMeanAngle2() {
        System.out.println("meanAngle2");
        PropertySpeedVector[][] array = new PropertySpeedVector[2][2];
        array[0][0] = new PropertySpeedVector(1, 0);
        array[1][0] = new PropertySpeedVector(5, 0);
        array[0][1] = new PropertySpeedVector(10, 0);
        array[1][1] = new PropertySpeedVector(15, 45);
        assertEquals(10.7990805, PropertySpeedVector.meanAngle(array).get(), DELTA);
    }
}
