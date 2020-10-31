/*
 * Copyright 2020 richard Linsdale.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.display.control.strategy.Strategy;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.PropertySketch;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Boats {

    private List<Boat> boats = new ArrayList<>();

    public Boats(PropertySketch sketchproperty, Leg firstleg, WindFlow windflow, WaterFlow waterflow) {
        for (var boatproperty : sketchproperty.getBoats().getList()) {
            boats.add(BoatFactory.createBoat(boatproperty, sketchproperty, firstleg, windflow, waterflow));
        }
    }

    public Stream<Boat> stream() {
        return boats.stream();
    }

    public Boat getBoat(String name) {
        return boats.stream().filter(boat -> boat.getName().equals(name)).findFirst().orElse(null);
    }

    public void timerAdvance(PropertySketch sketchproperty, int simulationtime, DecisionController timerlog, WindFlow windflow, WaterFlow waterflow) {
        boats.forEach(boat -> {
            Strategy newstrategy = boat.getStrategy().nextTimeInterval(sketchproperty, simulationtime, timerlog, windflow, waterflow);
            if (newstrategy != null) {
                boat.setStrategy(newstrategy);
            }
        });
    }
}
