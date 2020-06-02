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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class AngleTest {
    
    public AngleTest() {
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
    
    /**
     * Test of add method, of class Angle.
     */
    @Test
    public void testNormalisation() {
        System.out.println("normalisation");
        Angle instance = new Angle(20);
        assertEquals(20, instance.getDegrees());
        instance = new Angle(-20);
        assertEquals(-20, instance.getDegrees());
        instance = new Angle(-180);
        assertEquals(180, instance.getDegrees());
        instance = new Angle(181);
        assertEquals(-179, instance.getDegrees());
        instance = new Angle(720);
        assertEquals(0, instance.getDegrees());
    }

    /**
     * Test of add method, of class Angle.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Angle other = new Angle(90);
        Angle instance = new Angle(20);
        assertEquals(110, instance.add(other).getDegrees());
        other = new Angle(150);
        instance = new Angle(40);
        assertEquals(-170, instance.add(other).getDegrees());
        other = new Angle(-270);
        instance = new Angle(0);
        assertEquals(90, instance.add(other).getDegrees());
        other = new Angle(-150);
        instance = new Angle(-40);
        assertEquals(170, instance.add(other).getDegrees());
    }

    /**
     * Test of sub method, of class Angle.
     */
    @Test
    public void testSub() {
        System.out.println("sub");
        Angle other = new Angle(20);
        Angle instance = new Angle(90);
        assertEquals(70, instance.sub(other).getDegrees());
        other = new Angle(90);
        instance = new Angle(20);
        assertEquals(-70, instance.sub(other).getDegrees());
        other = new Angle(-100);
        instance = new Angle(90);
        assertEquals(-170, instance.sub(other).getDegrees());
    }

    /**
     * Test of mult method, of class Angle.
     */
    @Test
    public void testMult_int() {
        System.out.println("mult<int>");
        int mult = 2;
        Angle instance = new Angle(45);
        assertEquals(90, instance.mult(mult).getDegrees());
        instance = new Angle(100);
        assertEquals(-160, instance.mult(mult).getDegrees());
        instance = new Angle(-30);
        assertEquals(-60, instance.mult(mult).getDegrees());
        instance = new Angle(-90);
        assertEquals(180, instance.mult(mult).getDegrees());
        mult = 11;
        instance = new Angle(90);
        assertEquals(-90, instance.mult(mult).getDegrees());
        assertEquals(new Angle(-90), instance.mult(mult));
    }

    /**
     * Test of mult method, of class Angle.
     */
    @Test
    public void testMult_double() {
        System.out.println("mult<double>");
        double mult = 2.5;
        Angle instance = new Angle(40);
        assertEquals(100, instance.mult(mult).getDegrees());
        mult = 10;
        assertEquals(40, instance.mult(mult).getDegrees());
    }

    /**
     * Test of div method, of class Angle.
     */
    @Test
    public void testDiv_int() {
        System.out.println("div<int>");
        int div = 2;
        Angle instance = new Angle(40);
        assertEquals(20, instance.div(div).getDegrees());
        div = 10;
        assertEquals(4, instance.div(div).getDegrees());
    }

    /**
     * Test of div method, of class Angle.
     */
    @Test
    public void testDiv_Angle() {
        System.out.println("div<angle>");
        Angle div = new Angle(10);
        Angle instance = new Angle(40);
        assertEquals(4, instance.div(div));
        div = new Angle(25);
        assertEquals(1.6, instance.div(div));
    }

    /**
     * Test of negate method, of class Angle.
     */
    @Test
    public void testNegate() {
        System.out.println("negate");
        Angle instance = new Angle(100);
        assertEquals(-100, instance.negate().getDegrees());
        instance = new Angle(-100);
        assertEquals(100, instance.negate().getDegrees());
        instance = new Angle(0);
        assertEquals(0, instance.negate().getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.negate().getDegrees());
    }

    /**
     * Test of abs method, of class Angle.
     */
    @Test
    public void testAbs() {
        System.out.println("abs");
        Angle instance = new Angle(-100);
        assertEquals(100, instance.abs().getDegrees());
        instance = new Angle(100);
        assertEquals(100, instance.abs().getDegrees());
    }

    /**
     * Test of negateif method, of class Angle.
     */
    @Test
    public void testNegateif() {
        System.out.println("negateif");
        Angle instance = new Angle(100);
        assertEquals(-100, instance.negateif(true).getDegrees());
        instance = new Angle(-100);
        assertEquals(100, instance.negateif(true).getDegrees());
        instance = new Angle(0);
        assertEquals(0, instance.negateif(true).getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.negateif(true).getDegrees());
        instance = new Angle(100);
        assertEquals(100, instance.negateif(false).getDegrees());
        instance = new Angle(-100);
        assertEquals(-100, instance.negateif(false).getDegrees());
        instance = new Angle(0);
        assertEquals(0, instance.negateif(false).getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.negateif(false).getDegrees());
    }
    
    @Test
    public void testReflectH() {
        System.out.println("reflectH");
        Angle instance = new Angle(0);
        assertEquals(180, instance.reflectH().getDegrees());
        instance = new Angle(90);
        assertEquals(90, instance.reflectH().getDegrees());
        instance = new Angle(180);
        assertEquals(0, instance.reflectH().getDegrees());
        instance = new Angle(270);
        assertEquals(-90, instance.reflectH().getDegrees());
        instance = new Angle(45);
        assertEquals(135, instance.reflectH().getDegrees());
        instance = new Angle(135);
        assertEquals(45, instance.reflectH().getDegrees());
        instance = new Angle(-135);
        assertEquals(-45, instance.reflectH().getDegrees());
        instance = new Angle(-45);
        assertEquals(-135, instance.reflectH().getDegrees());
        //
        instance = new Angle(100);
        assertEquals(80, instance.reflectH().getDegrees());
        instance = new Angle(20);
        assertEquals(160, instance.reflectH().getDegrees());
        instance = new Angle(-60);
        assertEquals(-120, instance.reflectH().getDegrees());
        instance = new Angle(-140);
        assertEquals(-40, instance.reflectH().getDegrees());
    }
    
    @Test
    public void testReflectHif() {
        System.out.println("reflectH");
        Angle instance = new Angle(0);
        assertEquals(180, instance.reflectHif(true).getDegrees());
        instance = new Angle(90);
        assertEquals(90, instance.reflectHif(true).getDegrees());
        instance = new Angle(180);
        assertEquals(0, instance.reflectHif(true).getDegrees());
        instance = new Angle(270);
        assertEquals(-90, instance.reflectHif(true).getDegrees());
        instance = new Angle(45);
        assertEquals(135, instance.reflectHif(true).getDegrees());
        instance = new Angle(135);
        assertEquals(45, instance.reflectHif(true).getDegrees());
        instance = new Angle(-135);
        assertEquals(-45, instance.reflectHif(true).getDegrees());
        instance = new Angle(-45);
        assertEquals(-135, instance.reflectHif(true).getDegrees());
        instance = new Angle(100);
        assertEquals(80, instance.reflectHif(true).getDegrees());
        instance = new Angle(20);
        assertEquals(160, instance.reflectHif(true).getDegrees());
        instance = new Angle(-60);
        assertEquals(-120, instance.reflectHif(true).getDegrees());
        instance = new Angle(-140);
        assertEquals(-40, instance.reflectHif(true).getDegrees());
        //
        instance = new Angle(0);
        assertEquals(0, instance.reflectHif(false).getDegrees());
        instance = new Angle(90);
        assertEquals(90, instance.reflectHif(false).getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.reflectHif(false).getDegrees());
        instance = new Angle(270);
        assertEquals(-90, instance.reflectHif(false).getDegrees());
        instance = new Angle(45);
        assertEquals(45, instance.reflectHif(false).getDegrees());
        instance = new Angle(135);
        assertEquals(135, instance.reflectHif(false).getDegrees());
        instance = new Angle(-135);
        assertEquals(-135, instance.reflectHif(false).getDegrees());
        instance = new Angle(-45);
        assertEquals(-45, instance.reflectHif(false).getDegrees());
        instance = new Angle(100);
        assertEquals(100, instance.reflectHif(false).getDegrees());
        instance = new Angle(20);
        assertEquals(20, instance.reflectHif(false).getDegrees());
        instance = new Angle(-60);
        assertEquals(-60, instance.reflectHif(false).getDegrees());
        instance = new Angle(-140);
        assertEquals(-140, instance.reflectHif(false).getDegrees());
    }
    
    @Test
    public void testReflectV() {
        System.out.println("reflectV");
        Angle instance = new Angle(0);
        assertEquals(0, instance.reflectV().getDegrees());
        instance = new Angle(90);
        assertEquals(-90, instance.reflectV().getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.reflectV().getDegrees());
        instance = new Angle(270);
        assertEquals(90, instance.reflectV().getDegrees());
        instance = new Angle(45);
        assertEquals(-45, instance.reflectV().getDegrees());
        instance = new Angle(135);
        assertEquals(-135, instance.reflectV().getDegrees());
        instance = new Angle(-135);
        assertEquals(135, instance.reflectV().getDegrees());
        instance = new Angle(-45);
        assertEquals(45, instance.reflectV().getDegrees());
        //
        instance = new Angle(100);
        assertEquals(-100, instance.reflectV().getDegrees());
        instance = new Angle(20);
        assertEquals(-20, instance.reflectV().getDegrees());
        instance = new Angle(-60);
        assertEquals(60, instance.reflectV().getDegrees());
        instance = new Angle(-140);
        assertEquals(140, instance.reflectV().getDegrees());
    }
    
    @Test
    public void testReflectVif() {
        System.out.println("reflectVif");
        Angle instance = new Angle(0);
        assertEquals(0, instance.reflectVif(true).getDegrees());
        instance = new Angle(90);
        assertEquals(-90, instance.reflectVif(true).getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.reflectVif(true).getDegrees());
        instance = new Angle(270);
        assertEquals(90, instance.reflectVif(true).getDegrees());
        instance = new Angle(45);
        assertEquals(-45, instance.reflectVif(true).getDegrees());
        instance = new Angle(135);
        assertEquals(-135, instance.reflectVif(true).getDegrees());
        instance = new Angle(-135);
        assertEquals(135, instance.reflectVif(true).getDegrees());
        instance = new Angle(-45);
        assertEquals(45, instance.reflectVif(true).getDegrees());
        //
        instance = new Angle(100);
        assertEquals(-100, instance.reflectVif(true).getDegrees());
        instance = new Angle(20);
        assertEquals(-20, instance.reflectVif(true).getDegrees());
        instance = new Angle(-60);
        assertEquals(60, instance.reflectVif(true).getDegrees());
        instance = new Angle(-140);
        assertEquals(140, instance.reflectVif(true).getDegrees());
        //
        instance = new Angle(0);
        assertEquals(0, instance.reflectVif(false).getDegrees());
        instance = new Angle(90);
        assertEquals(90, instance.reflectVif(false).getDegrees());
        instance = new Angle(180);
        assertEquals(180, instance.reflectVif(false).getDegrees());
        instance = new Angle(270);
        assertEquals(-90, instance.reflectVif(false).getDegrees());
        instance = new Angle(45);
        assertEquals(45, instance.reflectVif(false).getDegrees());
        instance = new Angle(135);
        assertEquals(135, instance.reflectVif(false).getDegrees());
        instance = new Angle(-135);
        assertEquals(-135, instance.reflectVif(false).getDegrees());
        instance = new Angle(-45);
        assertEquals(-45, instance.reflectVif(false).getDegrees());
        //
        instance = new Angle(100);
        assertEquals(100, instance.reflectVif(false).getDegrees());
        instance = new Angle(20);
        assertEquals(20, instance.reflectVif(false).getDegrees());
        instance = new Angle(-60);
        assertEquals(-60, instance.reflectVif(false).getDegrees());
        instance = new Angle(-140);
        assertEquals(-140, instance.reflectVif(false).getDegrees());
    }

    /**
     * Test of isPositive method, of class Angle.
     */
    @Test
    public void testIsPositive() {
        System.out.println("isPositive");
        Angle instance = new Angle(100);
        assert(instance.isPositive());
        instance = new Angle(-100);
        assert(!instance.isPositive());
        instance = new Angle(0);
        assert(instance.isPositive());
        instance = new Angle(-1);
        assert(!instance.isPositive());
        instance = new Angle(180);
        assert(instance.isPositive());
    }
    
    /**
     * Test of isZero method, of class Angle.
     */
    @Test
    public void testIsZero() {
        System.out.println("isZero");
        Angle instance = new Angle(100);
        assert(!instance.isZero());
        instance = new Angle(-100);
        assert(!instance.isZero());
        instance = new Angle(0);
        assert(instance.isZero());
        instance = new Angle(-1);
        assert(!instance.isZero());
        instance = new Angle(1);
        assert(!instance.isZero());
        instance = new Angle(180);
        assert(!instance.isZero());
        instance = new Angle(-179);
        assert(!instance.isZero());
        instance = new Angle(360);
        assert(instance.isZero());
    }

    /**
     * Test of isNegative method, of class Angle.
     */
    @Test
    public void testIsNegative() {
        System.out.println("isNegative");
        Angle instance = new Angle(100);
        assert(!instance.isNegative());
        instance = new Angle(-100);
        assert(instance.isNegative());
        instance = new Angle(0);
        assert(!instance.isNegative());
        instance = new Angle(-1);
        assert(instance.isNegative());
        instance = new Angle(180);
        assert(!instance.isNegative());
    }

    /**
     * Test of gt method, of class Angle.
     */
    @Test
    public void testGt() {
        System.out.println("gt");
        Angle instance = new Angle(100);
        Angle other = new Angle(99);
        assert(instance.gt(other));
        other = new Angle(100);
        assert(!instance.gt(other));
        other = new Angle(101);
        assert(!instance.gt(other));
        //
        instance = new Angle(-100);
        other = new Angle(-99);
        assert(!instance.gt(other));
        other = new Angle(-100);
        assert(!instance.gt(other));
        other = new Angle(-101);
        assert(instance.gt(other));
    }

    /**
     * Test of gteq method, of class Angle.
     */
    @Test
    public void testGteq() {
        System.out.println("gteq");
        Angle instance = new Angle(100);
        Angle other = new Angle(99);
        assert(instance.gteq(other));
        other = new Angle(100);
        assert(instance.gteq(other));
        other = new Angle(101);
        assert(!instance.gteq(other));
        //
        instance = new Angle(-100);
        other = new Angle(-99);
        assert(!instance.gteq(other));
        other = new Angle(-100);
        assert(instance.gteq(other));
        other = new Angle(-101);
        assert(instance.gteq(other));
    }

    /**
     * Test of lt method, of class Angle.
     */
    @Test
    public void testLt() {
        System.out.println("lt");
        Angle instance = new Angle(100);
        Angle other = new Angle(99);
        assert(!instance.lt(other));
        other = new Angle(100);
        assert(!instance.lt(other));
        other = new Angle(101);
        assert(instance.lt(other));
        //
        instance = new Angle(-100);
        other = new Angle(-99);
        assert(instance.lt(other));
        other = new Angle(-100);
        assert(!instance.lt(other));
        other = new Angle(-101);
        assert(!instance.lt(other));
    }

    /**
     * Test of lteq method, of class Angle.
     */
    @Test
    public void testLteq() {
        System.out.println("lteq");
        Angle instance = new Angle(100);
        Angle other = new Angle(99);
        assert(!instance.lteq(other));
        other = new Angle(100);
        assert(instance.lteq(other));
        other = new Angle(101);
        assert(instance.lteq(other));
        //
        instance = new Angle(-100);
        other = new Angle(-99);
        assert(instance.lteq(other));
        other = new Angle(-100);
        assert(instance.lteq(other));
        other = new Angle(-101);
        assert(!instance.lteq(other));
    }

    /**
     * Test of angleDiff method, of class Angle.
     */
    @Test
    public void testAngleDiff() {
        System.out.println("angleDiff");
        Angle instance = new Angle(88);
        Angle other = new Angle(180);
        assertEquals(92, instance.angleDiff(other).getDegrees());
        instance = new Angle(175);
        other = new Angle(-175);
        assertEquals(10, instance.angleDiff(other).getDegrees());
        instance = new Angle(-175);
        other = new Angle(0);
        assertEquals(175, instance.angleDiff(other).getDegrees());
        instance = new Angle(0);
        other = new Angle(180);
        assertEquals(180, instance.angleDiff(other).getDegrees());
        instance = new Angle(-10);
        other = new Angle(180);
        assertEquals(-170, instance.angleDiff(other).getDegrees());
    }

    /**
     * Test of absAngleDiff method, of class Angle.
     */
    @Test
    public void testAbsAngleDiff() {
        System.out.println("absAngleDiff");
        Angle instance = new Angle(88);
        Angle other = new Angle(180);
        assertEquals(92, instance.absAngleDiff(other).getDegrees());
        instance = new Angle(175);
        other = new Angle(-175);
        assertEquals(10, instance.absAngleDiff(other).getDegrees());
        instance = new Angle(-175);
        other = new Angle(0);
        assertEquals(175, instance.absAngleDiff(other).getDegrees());
        instance = new Angle(0);
        other = new Angle(180);
        assertEquals(180, instance.absAngleDiff(other).getDegrees());
        instance = new Angle(-10);
        other = new Angle(180);
        assertEquals(170, instance.absAngleDiff(other).getDegrees());
    }

    /**
     * Test of getRadians method, of class Angle.
     */
    @Test
    public void testGetRadians() {
        System.out.println("getRadians");
        Angle instance = new Angle(88);
        assertEquals(Math.toRadians(88), instance.getRadians());
    }

    /**
     * Test of getDegrees method, of class Angle.
     */
    @Test
    public void testGetDegrees() {
        System.out.println("getDegrees");
        Angle instance = new Angle(88);
        assertEquals(88, instance.getDegrees());
    }
    
    @Test
    public void testInverse() {
        System.out.println("Inverse");
        Angle instance = new Angle(0);
        assertEquals(new Angle(180), instance.inverse());
        instance = new Angle(10);
        assertEquals(new Angle(-170), instance.inverse());
        instance = new Angle(90);
        assertEquals(new Angle(-90), instance.inverse());
        instance = new Angle(-90);
        assertEquals(new Angle(90), instance.inverse());
    }

    /**
     * Test of set method, of class Angle.
     * @throws java.lang.Exception
     */
    @Test
    public void testparse() throws Exception {
        System.out.println("parse");
        Angle angle = Angle.parse(getAngleParameters(), "angle").orElse(Angle.ANGLE0);
        assertEquals(88, angle.getDegrees());
    }
    
    private JsonObject getAngleParameters() {
        return Json.createObjectBuilder()
                .add("angle", 88)
                .build();
    }
    
    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void testparse_bad() throws Exception {
        System.out.println("parse bad");
        Assertions.assertThrows(IOException.class, () -> {
            Angle angle = Angle.parse(getBadAngleParameters(), "angle").orElse(Angle.ANGLE0);
        });
    }
    
    private JsonObject getBadAngleParameters() {
        return Json.createObjectBuilder()
                .add("angle", 88.22)
                .build();
    }

    /**
     * Test of equals method, of class Angle.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Angle other = new Angle(88);
        Angle instance = new Angle(88);
        assert(instance.equals(other));
    }
}
