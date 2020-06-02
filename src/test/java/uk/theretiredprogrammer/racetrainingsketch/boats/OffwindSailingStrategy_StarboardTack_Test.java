/*
 * Copyright 2020 richard linsdale
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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.SAILON;
import static uk.theretiredprogrammer.racetrainingsketch.boats.Decision.DecisionAction.TURN;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90MINUS;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class OffwindSailingStrategy_StarboardTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on starboard reach - sailon");
        Decision decision = makeDecision("/reach-starboardtack.json");
        assertEquals(SAILON, decision.getAction());
    }

    @Test
    public void test2() throws IOException {
        System.out.println("on starboard reach - pointing high - turn");
        Decision decision = makeDecision("/reach-starboardtack.json",
                () -> setboatintvalue("heading", -80));
        assertEquals(TURN, decision.getAction());
        assertEquals(ANGLE90MINUS, decision.getAngle());
        assert (!decision.isClockwise());
    }

    @Test
    public void test3() throws IOException {
        System.out.println("on starboard reach - pointing low - turn");
        Decision decision = makeDecision("/reach-starboardtack.json",
                () -> setboatintvalue("heading", -100));
        assertEquals(TURN, decision.getAction());
        assertEquals(ANGLE90MINUS, decision.getAngle());
        assert (decision.isClockwise());
    }

    @Test
    public void test4() throws IOException {
        System.out.println("on starboard reach - sailing low - turn to closer reach");
        Decision decision = makeDecision("/reach-starboardtack.json",
                () -> setboatlocationvalue("location", 40, 50));
        assertEquals(TURN, decision.getAction());
        assertEquals(new Angle(-84), decision.getAngle());
        assert (decision.isClockwise());
    }
}
