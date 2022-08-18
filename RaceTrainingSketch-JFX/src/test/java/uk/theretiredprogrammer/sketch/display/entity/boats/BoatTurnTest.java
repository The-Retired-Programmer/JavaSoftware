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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.PORT;

public class BoatTurnTest extends TurnTest {

    final static double DELTA = 0.005;
    private Location startturnlocation = new Location();
    private Location endturnlocation = new Location();

    @Test
    public void test1() {
        System.out.println("tacking radius calculation");
        runturntest(-45, -135, PORT, 12, new Location(58.28, 100-58.28), new Location(57.94, 100-60.56),
                () -> setboatdirection(-45));
        testradiusY(1.378);
    }

    @Test
    public void test1A() {
        System.out.println("tacking radius calculation - 15kt");
        runturntest(-45, -135, PORT, 12, new Location(67.05, 100-67.05), new Location(66.07, 100-71.10),
                () -> setboatdirection(-45),
                () -> setwindflow(15.0, -90.0));
        testradiusY(2.18);
    }

    @Test
    public void test2() {
        System.out.println("bearaway radius calculation");
        runturntest(-135, 135, PORT, 12, new Location(41.72, 100-58.28), new Location(38.06, 100-57.24),
                () -> setboatdirection(-135));
        testradiusX(1.85);
    }

    @Test
    public void test2A() {
        System.out.println("bearaway radius calculation - 15kt");
        runturntest(-135, 135, PORT, 12, new Location(32.95, 100-67.05), new Location(23.22, 100-63.60),
                () -> setboatdirection(-135),
                () -> setwindflow(15.0, -90.0));
        testradiusX(4.45);
    }

    @Test
    public void test3() {
        System.out.println("gybing radius calculation");
        runturntest(135, 45, PORT, 12, new Location(41.23, 100-41.23), new Location(42.09, 100-37.97),
                () -> setboatdirection(135));
        testradiusY(1.69);
    }

    @Test
    public void test3A() {
        System.out.println("gybing radius calculation - 15kt");
        runturntest(135, 45, PORT, 12, new Location(14.83, 100-14.83), new Location(18.17, 100-2.21),
                () -> setboatdirection(135),
                () -> setwindflow(15.0, -90.0));
        testradiusY(6.56);
    }

    @Test
    public void test4() {
        System.out.println("luffup radius calculation");
        runturntest(45, -45, PORT, 12, new Location(58.77, 100-41.23), new Location(62.59, 100-42.23),
                () -> setboatdirection(45));
        testradiusX(1.99);
    }

    @Test
    public void test4A() {
        System.out.println("luffup radius calculation - 15kt");
        runturntest(45, -45, PORT, 12, new Location(85.17, 100-14.83), new Location(99.33, 100-17.96),
                () -> setboatdirection(45),
                () -> setwindflow(15.0, -90.0));
        testradiusX(7.80);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void runturntest(int startangleint, int finishangleint,
            boolean turndirection, int secondstospeed,
            Location expectedstartturnlocation, Location expectedendturnlocation,
            Runnable... updateproperties) {
        try {
            Angle startangle = new Angle(startangleint);
            Angle targetangle = new Angle(finishangleint);
            setupForTurn("/boat-turn-calculation.json", updateproperties);
            Boat boat = getUptospeed(secondstospeed);
            Angle direction = boat.getDirection();
            assertEquals(startangle, direction);
            startturnlocation.set(boat.getLocation());
            assertAll("startturnlocation",
                    () -> assertEquals(expectedstartturnlocation.getY(), startturnlocation.getY(), DELTA),
                    () -> assertEquals(expectedstartturnlocation.getX(), startturnlocation.getX(), DELTA)
            );
            boat = makeTurn(targetangle, turndirection);
            direction = boat.getDirection();
            assertEquals(targetangle, direction);
            endturnlocation.set(boat.getLocation());
            assertAll("endturnlocation",
                    () -> assertEquals(expectedendturnlocation.getY(), endturnlocation.getY(), DELTA),
                    () -> assertEquals(expectedendturnlocation.getX(), endturnlocation.getX(), DELTA)
            );
        } catch (Exception ex) {
            StringWriter writer = new StringWriter();
            PrintWriter pwriter = new PrintWriter(writer);
            ex.printStackTrace(pwriter);
            fail("caught failure - " + ex.getMessage() + "\n" + writer.toString());
        }
    }

    private void testradiusY(double expectedradius) {
        //remove the overrun from the final point and estimate turn arc
        double excessx = endturnlocation.getX() - startturnlocation.getX();
        double estimatedy = endturnlocation.getY() - excessx;
        double arcy = estimatedy - startturnlocation.getY();
        double turnradius = Math.sqrt(arcy * arcy / 2);
        assertEquals(expectedradius, turnradius, DELTA);
    }

    private void testradiusX(double expectedradius) {
        //remove the overrun from the final point and estimate turn arc
        double excessy = endturnlocation.getY() - startturnlocation.getY();
        double estimatedx = endturnlocation.getX() + excessy;
        double arcx = estimatedx - startturnlocation.getX();
        double turnradius = Math.sqrt(arcx * arcx / 2);
        assertEquals(expectedradius, turnradius, DELTA);
    }
}
