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
package uk.theretiredprogrammer.sketch.strategy;

import java.util.HashMap;
import java.util.Map;
import uk.theretiredprogrammer.sketch.boats.Boat;
import uk.theretiredprogrammer.sketch.boats.Boats;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;
import uk.theretiredprogrammer.sketch.course.Course;
import uk.theretiredprogrammer.sketch.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.flows.WindFlow;
import uk.theretiredprogrammer.sketch.properties.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatStrategies {

    private final Map<String, BoatStrategyForLeg> boatstrategies = new HashMap<>();

    public BoatStrategies(PropertySketch sketchproperty, Course course, Boats boats, WindFlow windflow, WaterFlow waterflow) {
        Leg firstleg = course.getFirstCourseLeg();
        boats.stream().forEach(boat -> boatstrategies.put(
                boat.getName(),
                BoatStrategyForLeg.getLegStrategy(boat, firstleg, windflow, waterflow)
        ));
    }

    public BoatStrategyForLeg getStrategy(Boat boat) {
        return boatstrategies.get(boat.getName());
    }

    public void timerAdvance(PropertySketch sketchproperty, int simulationtime, TimerLog timerlog, WindFlow windflow, WaterFlow waterflow) {
        boatstrategies.values().forEach(boatstrategy -> {
            BoatStrategyForLeg newstrategy = boatstrategy.nextTimeInterval(sketchproperty, simulationtime, timerlog, windflow, waterflow);
            if (newstrategy != null) {
                boatstrategies.put(boatstrategy.boat.getName(), newstrategy);
            }
        });
    }
}
