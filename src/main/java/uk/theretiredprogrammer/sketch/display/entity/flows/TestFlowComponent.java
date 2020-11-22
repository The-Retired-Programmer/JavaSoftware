/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class TestFlowComponent extends FlowComponent {

    private final SpeedVector flow = new SpeedVector();
    private final Angle mean = new Angle();

    public TestFlowComponent(Supplier<Area> getdisplayarea, String type) {
        super(getdisplayarea, type);
        addProperty("flow", flow);
        addProperty("mean", mean);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        super.parseValues(jobj);
        parseOptionalProperty("flow", flow, jobj);
        parseOptionalProperty("mean", mean, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        super.toJson(job);
        job.add("flow", flow.toJson());
        job.add("mean", mean.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        super.setOnChange(onchange);
        flow.setOnChange(onchange);
        mean.setOnChange(onchange);
    }

    public SpeedVector getFlow() {
        return flow;
    }

    public Angle getmean() {
        return mean;
    }

    public void setFlow(SpeedVector newvalue) {
        flow.set(newvalue);
    }

    @Override
    public SpeedVector getFlow(Location pos) {
        testLocationWithinArea(pos);
        return getFlow();
    }

    @Override
    public Angle meanWindAngle() {
        return getmean();
    }
}
