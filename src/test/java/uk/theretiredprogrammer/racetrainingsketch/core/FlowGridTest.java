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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90MINUS;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class FlowGridTest {

    public FlowGridTest() {
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
     * Test of setFlow GetMeanFlowDirection methods, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetFlow_GetMeanFlowDirection() throws IOException {
        System.out.println("setFlow GetMeanFlowDirection");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(1, 1), 1, 1);
        instance.setFlow(flow).build();
        Angle res = instance.getMeanFlowDirection();
        assertEquals(90, res.getDegrees());
    }

    /**
     * Test of setFlow GetMeanFlowDirection methods, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test2_2_SetFlow_GetMeanFlowDirection() throws IOException {
        System.out.println("2x2 setFlow GetMeanFlowDirection");
        SpeedPolar majorflow = new SpeedPolar(10.0,ANGLE0);
        SpeedPolar minorflow = new SpeedPolar(10.0, new Angle(45));
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 2), 2, 2);
        instance.setFlowColumn(majorflow, 0).setFlow(minorflow, 1, 0).setFlow(majorflow, 1, 1).build();
        Angle res = instance.getMeanFlowDirection();
        assertEquals(11, res.getDegrees());
    }

    /**
     * Test of setFlow GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetFlow_GetFlow() throws IOException {
        System.out.println("setFlow_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(1, 1), 1, 1);
        instance.setFlow(flow).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0.25, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlow GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test2_1_SetFlow_GetFlow() throws IOException {
        System.out.println("2x1 setFlow_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 1), 2, 1);
        instance.setFlow(flow).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0.25, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1.75, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlow GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test2_1_SetFlowIndivid_GetFlow() throws IOException {
        System.out.println("2x1 setFlowIndivid_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 1), 2, 1);
        instance.setFlow(flow, 0, 0).setFlow(flow, 1, 0).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0.25, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1.75, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlowRow GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test2_1_SetFlowRow_GetFlow() throws IOException {
        System.out.println("2x1 setFlowRow_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 1), 2, 1);
        instance.setFlowRow(flow, 0).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0.25, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1.75, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlowColumn GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test2_1_SetFlowColumn_GetFlow() throws IOException {
        System.out.println("2x1 setFlowColumn_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 1), 2, 1);
        instance.setFlowColumn(flow, 0).setFlowColumn(flow, 1).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0.25, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1.75, 0.25));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlowColumn GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    // TODO - flow needs to be calculated using polars before this test would ever work
    @Disabled("Disabled until polar calculation of flows are implemented")
    @Test
    public void test2_2_SetFlow_GetFlow() throws IOException {
        System.out.println("2x2 setFlow<various>_GetFlow");
        SpeedPolar majorflow = new SpeedPolar(10.0, ANGLE0);
        SpeedPolar minorflow = new SpeedPolar(10.0, new Angle(45));
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 2), 2, 2);
        instance.setFlowColumn(majorflow, 0).setFlow(minorflow, 1, 0).setFlow(majorflow, 1, 1).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(45, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(12, res.getAngle().getDegrees()); //**
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(23, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }

    /**
     * Test of setFlowColumn GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    // TODO - flow needs to be calculated using polars before this test would ever work
    @Disabled("Disabled until polar calculation of flows are implemented")
    @Test
    public void test2_2_SetFlow2_GetFlow() throws IOException {
        System.out.println("2x2 setFlow<various, various>_GetFlow");
        SpeedPolar majorflow = new SpeedPolar(10.0, ANGLE0);
        SpeedPolar minorflow = new SpeedPolar(7.0, new Angle(45));
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 2), 2, 2);
        instance.setFlowColumn(majorflow, 0).setFlow(minorflow, 1, 0).setFlow(majorflow, 1, 1).build();
        SpeedPolar res = instance.getFlow(new Location(0, 0));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 0));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 0));
        assertEquals(45, res.getAngle().getDegrees());
        assertEquals(7.0, res.getSpeed());
        res = instance.getFlow(new Location(0, 1));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 1));
        assertEquals(12, res.getAngle().getDegrees()); //**
        assertEquals(9.25, res.getSpeed());
        res = instance.getFlow(new Location(2, 1));
        assertEquals(23, res.getAngle().getDegrees());
        assertEquals(8.5, res.getSpeed());
        res = instance.getFlow(new Location(0, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(1, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
        res = instance.getFlow(new Location(2, 2));
        assertEquals(0, res.getAngle().getDegrees());
        assertEquals(10.0, res.getSpeed());
    }
    
    /**
     * Test of setFlow GetFlow method, of class FlowGrid.
     *
     * @throws java.io.IOException
     */
    // TODO - flow needs to be calculated using polars before this test would ever work
    @Disabled("Disabled until polar calculation of flows are implemented")
    @Test
    public void test2_1_SetFlowInversion_GetFlow() throws IOException {
        System.out.println("2x1 setFlowIInversion_GetFlow");
        SpeedPolar flow = new SpeedPolar(10.0, ANGLE90);
        SpeedPolar iflow = new SpeedPolar(10.0, ANGLE90MINUS);
        FlowGrid instance = new FlowGrid(new Location(0, 0), new Location(2, 1), 2, 1);
        instance.setFlow(flow, 0, 0).setFlow(iflow, 1, 0).build();
        SpeedPolar res = instance.getFlow(new Location(1, 0.5));
        assertEquals(90, res.getAngle().getDegrees());
        assertEquals(0.0, res.getSpeed());
    }

}
