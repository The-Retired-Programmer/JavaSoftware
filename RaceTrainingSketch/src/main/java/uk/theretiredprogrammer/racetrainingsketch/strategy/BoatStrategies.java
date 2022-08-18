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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import uk.theretiredprogrammer.racetrainingsketch.boats.Boat;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatStrategies {

    private final Map<String, BoatStrategyForLeg> boatstrategies = new HashMap<>();

    public BoatStrategies(Controller controller) throws IOException {
        Leg firstleg = controller.course.getFirstCourseLeg();
        for (var boat : controller.boats.getBoats()) {
            boatstrategies.put(boat.name, BoatStrategyForLeg.getLegStrategy(controller, boat, firstleg));
        }
    }

    public BoatStrategyForLeg getStrategy(Boat boat) {
        return boatstrategies.get(boat.name);
    }

    public void timerAdvance(Controller controller, int simulationtime, TimerLog timerlog) throws IOException {
        for (var boatstrategy : boatstrategies.values()) {
            BoatStrategyForLeg newstrategy = boatstrategy.nextTimeInterval(controller, simulationtime, timerlog);
            if (newstrategy != null ) {
                boatstrategies.put(boatstrategy.boat.name, newstrategy);
            }
        }
    }
}
