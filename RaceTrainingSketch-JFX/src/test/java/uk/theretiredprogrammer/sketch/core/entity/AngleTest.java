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

public class AngleTest {

    private static final double DELTA = 0.0000001;

    @Test
    public void testNormalisation() {
        System.out.println("normalisation");
        Angle instance = new Angle(20.0);
        assertEquals(20, instance.get(), DELTA);
        instance = new Angle(-20.0);
        assertEquals(340, instance.get(), DELTA);
        instance = new Angle(-180.0);
        assertEquals(180, instance.get(), DELTA);
        instance = new Angle(181.0);
        assertEquals(181, instance.get(), DELTA);
        instance = new Angle(720.0);
        assertEquals(0, instance.get(), DELTA);
        //
        Angle from = new Angle(20.0);
        instance = new Angle(from);
        assertEquals(20, instance.get(), DELTA);
        from.set(-20);
        instance = new Angle(from);
        assertEquals(340, instance.get(), DELTA);
        from.set(-180.0);
        instance = new Angle(from);
        assertEquals(180, instance.get(), DELTA);
        from.set(-179.0);
        instance = new Angle(from);
        assertEquals(181, instance.get(), DELTA);
        from.set(720.0);
        instance = new Angle(from);
        assertEquals(0, instance.get(), DELTA);
    }

    @Test
    public void testPlus() {
        System.out.println("plus");
        Angle instance = new Angle(20);
        assertEquals(110, instance.plus(90).get(), DELTA);
        instance = new Angle(40);
        Angle addvalue = new Angle(150);
        assertEquals(190, instance.plus(addvalue).get(), DELTA);
        instance = new Angle(0);
        addvalue.set(90);
        assertEquals(90, instance.plus(addvalue).get(), DELTA);
        instance = new Angle(320);
        addvalue.set(210);
        assertEquals(170, instance.plus(addvalue).get(), DELTA);
    }

    @Test
    public void testSub() {
        System.out.println("sub");
        Angle instance = new Angle(90);
        Angle subvalue = new Angle(20);
        assertEquals(70, instance.sub(subvalue).get(), DELTA);
        instance = new Angle(20);
        assertEquals(290, instance.sub(90).get(), DELTA);
        instance = new Angle(90);
        subvalue.set(260);
        assertEquals(190, instance.sub(subvalue).get(), DELTA);
    }

    @Test
    public void testMult_double() {
        System.out.println("mult");
        Angle instance = new Angle(40);
        assertEquals(100, instance.mult(2.5).get(), DELTA);
        assertEquals(40, instance.mult(10).get(), DELTA);
    }

    @Test
    public void testDiv_double() {
        System.out.println("div");
        Angle instance = new Angle(40);
        assertEquals(4, instance.div(10).get(), DELTA);
        assertEquals(1.6, instance.div(25).get(), DELTA);
    }

    @Test
    public void testOpposite() {
        System.out.println("opposite");
        Angle instance = new Angle(100);
        assertEquals(260, instance.opposite().get(), DELTA);
        instance = new Angle(260);
        assertEquals(100, instance.opposite().get(), DELTA);
        instance = new Angle(0);
        assertEquals(0, instance.opposite().get(), DELTA);
        instance = new Angle(180);
        assertEquals(180, instance.opposite().get(), DELTA);
    }

    @Test
    public void testOppositeif() {
        System.out.println("oppositeif");
        Angle instance = new Angle(100);
        assertEquals(260, instance.oppositeif(true).get(), DELTA);
        instance = new Angle(-100);
        assertEquals(100, instance.oppositeif(true).get(), DELTA);
        instance = new Angle(0);
        assertEquals(0, instance.oppositeif(true).get(), DELTA);
        instance = new Angle(180);
        assertEquals(180, instance.oppositeif(true).get(), DELTA);
        instance = new Angle(100);
        assertEquals(100, instance.oppositeif(false).get(), DELTA);
        instance = new Angle(260);
        assertEquals(260, instance.oppositeif(false).get(), DELTA);
        instance = new Angle(0);
        assertEquals(0, instance.oppositeif(false).get(), DELTA);
        instance = new Angle(180);
        assertEquals(180, instance.oppositeif(false).get(), DELTA);
    }

    @Test
    public void testGt() {
        System.out.println("gt");
        Angle instance = new Angle(100);
        Angle compare = new Angle(99);
        assert (instance.gt(compare));
        compare.set(100);
        assert (!instance.gt(compare));
        compare.set(101);
        assert (!instance.gt(compare));
        instance = new Angle(0);
        compare.set(359);
        assert (instance.gt(compare));
        compare.set(0);
        assert (!instance.gt(compare));
        compare.set(1);
        assert (!instance.gt(compare));
        instance = new Angle(359);
        compare.set(358);
        assert (instance.gt(compare));
        compare.set(359);
        assert (!instance.gt(compare));
        compare.set(0);
        assert (!instance.gt(compare));
    }

    @Test
    public void testGteq() {
        System.out.println("gteq");
        Angle instance = new Angle(100);
        Angle compare = new Angle(99);
        assert (instance.gteq(compare));
        compare.set(100);
        assert (instance.gteq(compare));
        compare.set(101);
        assert (!instance.gteq(compare));
        instance = new Angle(0);
        compare.set(359);
        assert (instance.gteq(compare));
        compare.set(0);
        assert (instance.gteq(compare));
        compare.set(1);
        assert (!instance.gteq(compare));
        instance = new Angle(359);
        compare.set(358);
        assert (instance.gteq(compare));
        compare.set(359);
        assert (instance.gteq(compare));
        compare.set(0);
        assert (!instance.gteq(compare));
    }

    @Test
    public void testLt() {
        System.out.println("lt");
        Angle instance = new Angle(100);
        Angle compare = new Angle(99);
        assert (!instance.lt(compare));
        compare.set(100);
        assert (!instance.lt(compare));
        compare.set(101);
        assert (instance.lt(compare));
        instance = new Angle(0);
        compare.set(359);
        assert (!instance.lt(compare));
        compare.set(0);
        assert (!instance.lt(compare));
        compare.set(1);
        assert (instance.lt(compare));
        instance = new Angle(359);
        compare.set(358);
        assert (!instance.lt(compare));
        compare.set(359);
        assert (!instance.lt(compare));
        compare.set(0);
        assert (instance.lt(compare));
    }

    @Test
    public void testLteq() {
        System.out.println("lteq");
        Angle instance = new Angle(100);
        Angle compare = new Angle(99);
        assert (!instance.lteq(compare));
        compare.set(100);
        assert (instance.lteq(compare));
        compare.set(101);
        assert (instance.lteq(compare));
        instance = new Angle(0);
        compare.set(359);
        assert (!instance.lteq(compare));
        compare.set(0);
        assert (instance.lteq(compare));
        compare.set(1);
        assert (instance.lteq(compare));
        instance = new Angle(359);
        compare.set(358);
        assert (!instance.lteq(compare));
        compare.set(359);
        assert (instance.lteq(compare));
        compare.set(0);
        assert (instance.lteq(compare));
    }

    @Test
    public void testDegreesDiff() {
        System.out.println("degreesDiff");
        Angle instance = new Angle(88);
        assertEquals(92, instance.degreesDiff(180).get(), DELTA);
        instance = new Angle(175);
        Angle diff = new Angle(185);
        assertEquals(10, instance.degreesDiff(diff).get(), DELTA);
        instance = new Angle(185);
        assertEquals(175, instance.degreesDiff(0).get(), DELTA);
        instance = new Angle(0);
        assertEquals(180, instance.degreesDiff(180).get(), DELTA);
        instance = new Angle(350);
        assertEquals(190, instance.degreesDiff(180).get(), DELTA);
    }

    @Test
    public void testBetween() {
        System.out.println("between");
        Angle instance = new Angle(270);
        assert (!instance.between(90, 180));
        instance = new Angle(140);
        assert (instance.between(90, 180));
        assert (instance.between(90, 181));
        instance = new Angle(160);
        Angle min = new Angle(243);
        Angle max = new Angle(343);
        assert (!instance.between(min, max));
        instance = new Angle(151);
        min.set(242);
        max.set(326);
        assert (!instance.between(min, max));
        instance = new Angle(152);
        assert (!instance.between(min, max));
        instance = new Angle(153);
        assert (!instance.between(min, max));
        instance = new Angle(180);
        assert (!instance.between(min, max));
        instance = new Angle(181);
        assert (!instance.between(min, max));
    }

    @Test
    public void testGetRadians() {
        System.out.println("getRadians");
        Angle instance = new Angle(88);
        assertEquals(Math.toRadians(88), instance.getRadians(), DELTA);
    }

    @Test
    public void testGet() {
        System.out.println("get");
        Angle instance = new Angle(88);
        assertEquals(88, instance.get(), DELTA);
    }

    @Test
    public void testInverse() {
        System.out.println("Inverse");
        Angle instance = new Angle(0);
        assertEquals(180, instance.inverse().get());
        instance = new Angle(10);
        assertEquals(190, instance.inverse().get());
        instance = new Angle(90);
        assertEquals(270, instance.inverse().get());
        instance = new Angle(270);
        assertEquals(90, instance.inverse().get());
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        Angle other = new Angle(88);
        Angle instance = new Angle(88);
        assert (instance.equals(other));
    }
}
