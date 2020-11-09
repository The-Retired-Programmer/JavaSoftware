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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelArray;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.display.control.strategy.Strategy;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public class Boats extends ModelArray<Boat> {

    private final SketchModel model;

    public Boats(SketchModel model) {
        this.model = model;
    }

    @Override
    protected Boat createAndParse(JsonValue jval) {
        if (jval.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new ParseFailure("Malformed Definition File - array contains items other than Objects");
        }
        JsonObject jobj = (JsonObject) jval;
        Boat boat = BoatFactory.createBoat(
                jobj.getString("type", "<undefined>"),
                model.getCourse().getFirstLeg(),
                new WindFlow(model),
                new WaterFlow(model)
        );
        boat.parse(jobj);
        return boat;
    }

    @Override
    public Boat get(String name) {
        return stream().filter(boat -> name.equals(boat.getName())).findFirst()
                .orElseThrow(() -> new IllegalStateFailure("can't find Boat with name " + name));
    }

    public void timerAdvance(SketchModel sketchproperty, int simulationtime, DecisionController timerlog, WindFlow windflow, WaterFlow waterflow) {
        stream().forEach(boat -> {
            Strategy newstrategy = boat.getStrategy().nextTimeInterval(sketchproperty, simulationtime, timerlog, windflow, waterflow);
            if (newstrategy != null) {
                boat.setStrategy(newstrategy);
            }
        });
    }
}
