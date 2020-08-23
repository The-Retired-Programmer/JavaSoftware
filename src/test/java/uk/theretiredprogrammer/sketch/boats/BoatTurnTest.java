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
package uk.theretiredprogrammer.sketch.boats;

import java.io.IOException;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Location;
import static uk.theretiredprogrammer.sketch.strategy.Decision.PORT;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatTurnTest extends TurnTest {

    final static double DELTA = 0.005;
    private Location startturnlocation;
    private Location endturnlocation;

    @Test
    public void test1() throws IOException {
        System.out.println("tacking radius calculation");
        runturntest(45, -45, PORT, 12, new Location(58.28, 58.28), new Location(57.94, 60.56),
                () -> this.setboatintvalue("heading", 45));
        testradiusY(1.378);
    }

    @Test
    public void test1A() throws IOException {
        System.out.println("tacking radius calculation - 15kt");
        runturntest(45, -45, PORT, 12, new Location(67.05, 67.05), new Location(66.07, 71.10),
                () -> this.setboatintvalue("heading", 45),
                () -> this.setwindspeed(15));
        testradiusY(2.18);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("bearaway radius calculation");
        runturntest(-45, -135, PORT, 12, new Location(41.72, 58.28), new Location(38.06, 57.24),
                () -> this.setboatintvalue("heading", -45));
        testradiusX(1.85);
    }

    @Test
    public void test2A() throws IOException {
        System.out.println("bearaway radius calculation - 15kt");
        runturntest(-45, -135, PORT, 12, new Location(32.95, 67.05), new Location(23.22, 63.60),
                () -> this.setboatintvalue("heading", -45),
                () -> this.setwindspeed(15));
        testradiusX(4.45);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("gybing radius calculation");
        runturntest(-135, 135, PORT, 12, new Location(41.23, 41.23), new Location(42.09, 37.97),
                () -> this.setboatintvalue("heading", -135));
        testradiusY(1.69);
    }

    @Test
    public void test3A() throws IOException {
        System.out.println("gybing radius calculation - 15kt");
        runturntest(-135, 135, PORT, 12, new Location(14.83, 14.83), new Location(18.17, 2.21),
                () -> this.setboatintvalue("heading", -135),
                () -> this.setwindspeed(15));
        testradiusY(6.56);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("luffup radius calculation");
        runturntest(135, 45, PORT, 12, new Location(58.77, 41.23), new Location(62.59, 42.23),
                () -> this.setboatintvalue("heading", 135));
        testradiusX(1.99);
    }

    @Test
    public void test4A() throws IOException {
        System.out.println("luffup radius calculation - 15kt");
        runturntest(135, 45, PORT, 12, new Location(85.17, 14.83), new Location(99.33, 17.96),
                () -> this.setboatintvalue("heading", 135),
                () -> this.setwindspeed(15));
        testradiusX(7.80);
    }

    private void runturntest(int startangleint, int finishangleint,
            boolean turndirection, int secondstospeed,
            Location expectedstartturnlocation, Location expectedendturnlocation,
            Supplier<String>... configs) throws IOException {
        Angle startangle = new Angle(startangleint);
        Angle targetangle = new Angle(finishangleint);
        setupForTurn("/boat-turn-calculation.json", configs);
        Boat boat = getUptospeed(secondstospeed);
        Angle direction = boat.direction;
        assertEquals(startangle, direction);
        startturnlocation = boat.location;
        assertAll("startturnlocation",
                () -> assertEquals(expectedstartturnlocation.getY(), startturnlocation.getY(), DELTA),
                () -> assertEquals(expectedstartturnlocation.getX(), startturnlocation.getX(), DELTA)
        );
        boat = makeTurn(targetangle, turndirection);
        direction = boat.direction;
        assertEquals(targetangle, direction);
        endturnlocation = boat.location;
        assertAll("endturnlocation",
                () -> assertEquals(expectedendturnlocation.getY(), endturnlocation.getY(), DELTA),
                () -> assertEquals(expectedendturnlocation.getX(), endturnlocation.getX(), DELTA)
        );
    }

    private void testradiusY(double expectedradius) {
        //remove the overrun from the final point and estimate turn arc
        double excessx = endturnlocation.getX() - startturnlocation.getX();
        double estimatedy = endturnlocation.getY() + excessx;
        double arcy = estimatedy - startturnlocation.getY();
        double turnradius = Math.sqrt(arcy * arcy / 2);
        assertEquals(expectedradius, turnradius, DELTA);
    }

    private void testradiusX(double expectedradius) {
        //remove the overrun from the final point and estimate turn arc
        double excessy = endturnlocation.getY() - startturnlocation.getY();
        double estimatedx = endturnlocation.getX() - excessy;
        double arcx = estimatedx - startturnlocation.getX();
        double turnradius = Math.sqrt(arcx * arcx / 2);
        assertEquals(expectedradius, turnradius, DELTA);
    }

//    @Test
//    public void test2() throws IOException {
//        System.out.println("bearaway radius calculation");
//        Angle targetangle = new Angle(-135);
//        TurnDirection turndirection = PORT; 
//        Boat boat = setupForTurn(targetangle, turndirection, "/boat-turn-calculation.json",
//                () -> this.setboatintvalue("heading", -45));
//        Angle direction = boat.direction;
//        assertEquals(targetangle, direction);
//        Location location = boat.location;
//        assertAll("location",
//            () -> assertEquals(49.17, location.getY(), DELTA),
//            () -> assertEquals(46.04, location.getX(), DELTA)
//        );
//    }
//    
//    @Test
//    public void test3() throws IOException {
//        System.out.println("gybe radius calculation");
//        Angle targetangle = new Angle(135);
//        TurnDirection turndirection = PORT; 
//        Boat boat = setupForTurn(targetangle, turndirection, "/boat-turn-calculation.json",
//                () -> this.setboatintvalue("heading", -135));
//        Angle direction = boat.direction;
//        assertEquals(targetangle, direction);
//        Location location = boat.location;
//        assertAll("location",
//            () -> assertEquals(46.27, location.getY(), DELTA),
//            () -> assertEquals(51.11, location.getX(), DELTA)
//        );
//    }
//    
//    @Test
//    public void test4() throws IOException {
//        System.out.println("luff radius calculation");
//        Angle targetangle = new Angle(45);
//        TurnDirection turndirection = PORT; 
//        Boat boat = setupForTurn(targetangle, turndirection, "/boat-turn-calculation.json",
//                () -> this.setboatintvalue("heading", 135));
//        Angle direction = boat.direction;
//        assertEquals(targetangle, direction);
//        Location location = boat.location;
//        assertAll("location",
//            () -> assertEquals(51.04, location.getY(), DELTA),
//            () -> assertEquals(53.96, location.getX(), DELTA)
//        );
//    }
}
