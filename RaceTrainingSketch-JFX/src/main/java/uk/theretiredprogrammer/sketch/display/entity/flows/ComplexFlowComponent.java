/*
 * Copyright 2014-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class ComplexFlowComponent extends FlowComponent {

    private final SpeedVector topleftflow;
    private final SpeedVector toprightflow;
    private final SpeedVector bottomrightflow;
    private final SpeedVector bottomleftflow;

    public ComplexFlowComponent(Supplier<Area> getdisplayarea, String type) {
        super(getdisplayarea, type);
        topleftflow = new SpeedVector();
        toprightflow = new SpeedVector();
        bottomrightflow = new SpeedVector();
        bottomleftflow = new SpeedVector();
        addProperty("topleftflow", topleftflow);
        addProperty("toprightflow", toprightflow);
        addProperty("bottomrightflow", bottomrightflow);
        addProperty("bottomleftflow", bottomleftflow);
    }
    
    public ComplexFlowComponent(String name, ComplexFlowComponent clonefrom) {
        super(name, clonefrom);
        topleftflow = new SpeedVector(clonefrom.topleftflow);
        toprightflow = new SpeedVector(clonefrom.toprightflow);
        bottomrightflow = new SpeedVector(clonefrom.bottomrightflow);
        bottomleftflow = new SpeedVector(clonefrom.bottomleftflow);
        addProperty("topleftflow", topleftflow);
        addProperty("toprightflow", toprightflow);
        addProperty("bottomrightflow", bottomrightflow);
        addProperty("bottomleftflow", bottomleftflow);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        super.parseValues(jobj);
        parseOptionalProperty("topleftflow", topleftflow, jobj);
        parseOptionalProperty("toprightflow", toprightflow, jobj);
        parseOptionalProperty("bottomrightflow", bottomrightflow, jobj);
        parseOptionalProperty("bottomleftflow", bottomleftflow, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        super.toJson(job);
        job.add("topleftflow", topleftflow.toJson());
        job.add("toprightflow", toprightflow.toJson());
        job.add("bottomrightflow", bottomrightflow.toJson());
        job.add("bottomleftflow", bottomleftflow.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        super.setOnChange(onchange);
        topleftflow.setOnChange(onchange);
        toprightflow.setOnChange(onchange);
        bottomrightflow.setOnChange(onchange);
        bottomleftflow.setOnChange(onchange);
    }

    @Override
    public SpeedVector getFlow(Location pos) {
        testLocationWithinArea(pos);
        Location topleft = getArea().getLocationProperty();
        double xfraction = (pos.getX() - topleft.getX()) / getArea().getWidth();
        double yfraction = (pos.getY() - topleft.getY()) / getArea().getHeight();
        Location fractions = new Location(xfraction, yfraction);
        return topleftflow.extrapolate(
                toprightflow,
                bottomrightflow,
                bottomleftflow,
                fractions
        );
    }
}
