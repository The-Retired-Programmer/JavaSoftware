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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.core.entity.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

public class FlowShiftModel extends ModelMap {

    private final PropertyBoolean showflow = new PropertyBoolean(false);
    private final PropertyDouble showflowinterval = new PropertyDouble(100.0);
    private final PropertyColour showflowcolour = new PropertyColour(Color.BLACK);
    private final PropertyDegrees swingangle = new PropertyDegrees(0);
    private final PropertyInteger swingperiod = new PropertyInteger(0);
    private final PropertyDegrees shiftangle = new PropertyDegrees(0);
    private final PropertyInteger shiftperiod = new PropertyInteger(0);
    private final PropertyBoolean randomshifts = new PropertyBoolean(false);

    public FlowShiftModel() {
        addProperty("showflow", showflow);
        addProperty("showflowinterval", showflowinterval);
        addProperty("showflowcolour", showflowcolour);
        addProperty("swingangle", swingangle);
        addProperty("swingperiod", swingperiod);
        addProperty("shiftangle", shiftangle);
        addProperty("shiftperiod", shiftperiod);
        addProperty("randomshifts", randomshifts);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseOptionalProperty("showflow", showflow, jobj);
        parseOptionalProperty("showflowinterval", showflowinterval, jobj);
        parseOptionalProperty("showflowcolour", showflowcolour, jobj);
        parseOptionalProperty("swingangle", swingangle, jobj);
        parseOptionalProperty("swingperiod", swingperiod, jobj);
        parseOptionalProperty("shiftangle", shiftangle, jobj);
        parseOptionalProperty("shiftperiod", shiftperiod, jobj);
        parseOptionalProperty("randomshifts", randomshifts, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("showflow", showflow.toJson());
        job.add("showflowinterval", showflowinterval.toJson());
        job.add("showflowcolour", showflowcolour.toJson());
        job.add("swingangle", swingangle.toJson());
        job.add("swingperiod", swingperiod.toJson());
        job.add("shiftangle", shiftangle.toJson());
        job.add("shiftperiod", shiftperiod.toJson());
        job.add("randomshifts", randomshifts.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        showflow.setOnChange(onchange);
        showflowinterval.setOnChange(onchange);
        showflowcolour.setOnChange(onchange);
        swingangle.setOnChange(onchange);
        swingperiod.setOnChange(onchange);
        shiftangle.setOnChange(onchange);
        shiftperiod.setOnChange(onchange);
        randomshifts.setOnChange(onchange);
    }

    public boolean isShowflow() {
        return showflow.get();
    }

    public double getShowflowinterval() {
        return showflowinterval.get();
    }

    public Color getShowflowcolour() {
        return showflowcolour.get();
    }

    public PropertyDegrees getSwingangle() {
        return swingangle;
    }

    public int getSwingperiod() {
        return swingperiod.get();
    }

    public PropertyDegrees getShiftangle() {
        return shiftangle;
    }

    public int getShiftperiod() {
        return shiftperiod.get();
    }

    public boolean isRandomshifts() {
        return randomshifts.get();
    }
}
