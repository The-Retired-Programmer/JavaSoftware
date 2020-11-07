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

public class PropertyDegreesTest {

    private static final double DELTA = 0.0000001;

    @Test
    public void testNormalisation() {
        System.out.println("normalisation");
        PropertyDegrees instance = new PropertyDegrees(20.0);
        assertEquals(20, instance.get(), DELTA);
        instance = new PropertyDegrees(-20.0);
        assertEquals(-20, instance.get(), DELTA);
        instance = new PropertyDegrees(-180.0);
        assertEquals(180, instance.get(), DELTA);
        instance = new PropertyDegrees(181.0);
        assertEquals(-179, instance.get(), DELTA);
        instance = new PropertyDegrees(720.0);
        assertEquals(0, instance.get(), DELTA);
        //
        PropertyDegrees from = new PropertyDegrees(20.0);
        instance = new PropertyDegrees(from);
        assertEquals(20, instance.get(), DELTA);
        from.set(-20);
        instance = new PropertyDegrees(from);
        assertEquals(-20, instance.get(), DELTA);
        from.set(-180.0);
        instance = new PropertyDegrees(from);
        assertEquals(180, instance.get(), DELTA);
        from.set(-179.0);
        instance = new PropertyDegrees(from);
        assertEquals(-179, instance.get(), DELTA);
        from.set(720.0);
        instance = new PropertyDegrees(from);
        assertEquals(0, instance.get(), DELTA);
    }

    @Test
    public void testPlus() {
        System.out.println("plus");
        PropertyDegrees instance = new PropertyDegrees(20);
        assertEquals(110, instance.plus(DEGREES90).get(), DELTA);
        instance = new PropertyDegrees(40);
        PropertyDegrees addvalue = new PropertyDegrees(150);
        assertEquals(-170, instance.plus(addvalue).get(), DELTA);
        instance = new PropertyDegrees(0);
        addvalue.set(-270);
        assertEquals(90, instance.plus(addvalue).get(), DELTA);
        instance = new PropertyDegrees(-40);
        addvalue.set(-150);
        assertEquals(170, instance.plus(addvalue).get(), DELTA);
    }

    @Test
    public void testSub() {
        System.out.println("sub");
        PropertyDegrees instance = new PropertyDegrees(90);
        PropertyDegrees subvalue = new PropertyDegrees(20);
        assertEquals(70, instance.sub(subvalue).get(), DELTA);
        instance = new PropertyDegrees(20);
        assertEquals(-70, instance.sub(DEGREES90).get(), DELTA);
        instance = new PropertyDegrees(90);
        subvalue.set(-100);
        assertEquals(-170, instance.sub(subvalue).get(), DELTA);
    }


    @Test
    public void testMult_double() {
        System.out.println("mult");
        PropertyDegrees instance = new PropertyDegrees(40);
        assertEquals(100, instance.mult(2.5).get(), DELTA);
        assertEquals(40, instance.mult(10).get(), DELTA);
    }

    @Test
    public void testDiv_double() {
        System.out.println("div");
        PropertyDegrees instance = new PropertyDegrees(40);
        assertEquals(4, instance.div(10).get(), DELTA);
        assertEquals(1.6, instance.div(25).get(), DELTA);
    }

    @Test
    public void testNegative() {
        System.out.println("negative");
        PropertyDegrees instance = new PropertyDegrees(100);
        assertEquals(-100, instance.negative().get(), DELTA);
        instance = new PropertyDegrees(-100);
        assertEquals(100, instance.negative().get(), DELTA);
        instance = new PropertyDegrees(0);
        assertEquals(0, instance.negative().get(), DELTA);
        instance = new PropertyDegrees(180);
        assertEquals(180, instance.negative().get(), DELTA);
    }

    @Test
    public void testAbs() {
        System.out.println("abs");
        PropertyDegrees instance = new PropertyDegrees(-100);
        assertEquals(100, instance.abs().get(), DELTA);
        instance = new PropertyDegrees(100);
        assertEquals(100, instance.abs().get(), DELTA);
    }

    @Test
    public void testNegateif() {
        System.out.println("negateif");
        PropertyDegrees instance = new PropertyDegrees(100);
        assertEquals(-100, instance.negateif(true).get(), DELTA);
        instance = new PropertyDegrees(-100);
        assertEquals(100, instance.negateif(true).get(), DELTA);
        instance = new PropertyDegrees(0);
        assertEquals(0, instance.negateif(true).get(), DELTA);
        instance = new PropertyDegrees(180);
        assertEquals(180, instance.negateif(true).get(), DELTA);
        instance = new PropertyDegrees(100);
        assertEquals(100, instance.negateif(false).get(), DELTA);
        instance = new PropertyDegrees(-100);
        assertEquals(-100, instance.negateif(false).get(), DELTA);
        instance = new PropertyDegrees(0);
        assertEquals(0, instance.negateif(false).get(), DELTA);
        instance = new PropertyDegrees(180);
        assertEquals(180, instance.negateif(false).get(), DELTA);
    }
    
    @Test
    public void testGt() {
        System.out.println("gt");
        PropertyDegrees instance = new PropertyDegrees(100);
        PropertyDegrees compare = new PropertyDegrees(99);
        assert (instance.gt(compare));
        compare.set(100);
        assert (!instance.gt(compare));
        compare.set(101);
        assert (!instance.gt(compare));
        instance = new PropertyDegrees(-100);
        compare.set(-99);
        assert (!instance.gt(compare));
        compare.set(-100);
        assert (!instance.gt(compare));
        compare.set(-101);
        assert (instance.gt(compare));
    }

    @Test
    public void testGteq() {
        System.out.println("gteq");
        PropertyDegrees instance = new PropertyDegrees(100);
        PropertyDegrees compare = new PropertyDegrees(99);
        assert (instance.gteq(compare));
        compare.set(100);
        assert (instance.gteq(compare));
        compare.set(101);
        assert (!instance.gteq(compare));
        //
        instance = new PropertyDegrees(-100);
        compare.set(-99);
        assert (!instance.gteq(compare));
        compare.set(-100);
        assert (instance.gteq(compare));
        compare.set(-101);
        assert (instance.gteq(compare));
    }

    @Test
    public void testLt() {
        System.out.println("lt");
        PropertyDegrees instance = new PropertyDegrees(100);
        PropertyDegrees compare = new PropertyDegrees(99);
        assert (!instance.lt(compare));
        compare.set(100);
        assert (!instance.lt(compare));
        compare.set(101);
        assert (instance.lt(compare));
        //
        instance = new PropertyDegrees(-100);
        compare.set(-99);
        assert (instance.lt(compare));
        compare.set(-100);
        assert (!instance.lt(compare));
        compare.set(-101);
        assert (!instance.lt(compare));
    }

    @Test
    public void testLteq() {
        System.out.println("lteq");
        PropertyDegrees instance = new PropertyDegrees(100);
        PropertyDegrees compare = new PropertyDegrees(99);
        assert (!instance.lteq(compare));
        compare.set(100);
        assert (instance.lteq(compare));
        compare.set(101);
        assert (instance.lteq(compare));
        //
        instance = new PropertyDegrees(-100);
        compare.set(-99);
        assert (instance.lteq(compare));
        compare.set(-100);
        assert (instance.lteq(compare));
        compare.set(-101);
        assert (!instance.lteq(compare));
    }

    @Test
    public void testDegreesDiff() {
        System.out.println("degreesDiff");
        PropertyDegrees instance = new PropertyDegrees(88);
        assertEquals(92, instance.degreesDiff(DEGREES180).get(), DELTA);
        instance = new PropertyDegrees(175);
        PropertyDegrees diff = new PropertyDegrees(-175);
        assertEquals(10, instance.degreesDiff(diff).get(), DELTA);
        instance = new PropertyDegrees(-175);
        assertEquals(175, instance.degreesDiff(DEGREES0).get(), DELTA);
        instance = new PropertyDegrees(0);
        assertEquals(180, instance.degreesDiff(DEGREES180).get(), DELTA);
        instance = new PropertyDegrees(-10);
        assertEquals(-170, instance.degreesDiff(DEGREES180).get(), DELTA);
    }
    
    @Test
    public void testAbsDegreesDiff() {
        System.out.println("absDegreesDiff");
        PropertyDegrees instance = new PropertyDegrees(88);
        assertEquals(92, instance.absDegreesDiff(DEGREES180).get(), DELTA);
        instance = new PropertyDegrees(175);
        PropertyDegrees diff = new PropertyDegrees(-175);
        assertEquals(10, instance.absDegreesDiff(diff).get(), DELTA);
        instance = new PropertyDegrees(-175);
        assertEquals(175, instance.absDegreesDiff(DEGREES0).get(), DELTA);
        instance = new PropertyDegrees(0);
        assertEquals(180, instance.absDegreesDiff(DEGREES180).get(), DELTA);
        instance = new PropertyDegrees(-10);
        assertEquals(170, instance.absDegreesDiff(DEGREES180).get(), DELTA);
    }


    @Test
    public void testBetween() {
        System.out.println("between");
        PropertyDegrees instance = new PropertyDegrees(-90);
        assert (!instance.between(DEGREES90, DEGREES180));
        instance = new PropertyDegrees(140);
        assert (instance.between(DEGREES90, DEGREES180));
        PropertyDegrees max = new PropertyDegrees(-179);
        assert (instance.between(DEGREES90, max));
        instance = new PropertyDegrees(160);
        PropertyDegrees min = new PropertyDegrees(-117);
        max.set(-27);
        assert (!instance.between(min, max));
        instance = new PropertyDegrees(151);
        min.set(-118);
        max.set(-28);
        assert (!instance.between(min, max));
        instance = new PropertyDegrees(152);
        assert (!instance.between(min, max));
        instance = new PropertyDegrees(153);
        assert (!instance.between(min, max));
        instance = new PropertyDegrees(180);
        assert (!instance.between(min, max));
        instance = new PropertyDegrees(-179);
        assert (!instance.between(min, max));
    }

    @Test
    public void testGetRadians() {
        System.out.println("getRadians");
        PropertyDegrees instance = new PropertyDegrees(88);
        assertEquals(Math.toRadians(88), instance.getRadians(), DELTA);
    }

    @Test
    public void testGet() {
        System.out.println("get");
        PropertyDegrees instance = new PropertyDegrees(88);
        assertEquals(88, instance.get(), DELTA);
    }

    @Test
    public void testInverse() {
        System.out.println("Inverse");
        PropertyDegrees instance = new PropertyDegrees(0);
        assertEquals(180, instance.inverse().get());
        instance = new PropertyDegrees(10);
        assertEquals(-170, instance.inverse().get());
        instance = new PropertyDegrees(90);
        assertEquals(-90, instance.inverse().get());
        instance = new PropertyDegrees(-90);
        assertEquals(90, instance.inverse().get());
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        PropertyDegrees other = new PropertyDegrees(88);
        PropertyDegrees instance = new PropertyDegrees(88);
        assert (instance.equals(other));
    }
}
