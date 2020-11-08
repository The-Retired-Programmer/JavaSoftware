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
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedPolar;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import static uk.theretiredprogrammer.sketch.core.entity.SpeedPolar.FLOWZERO;

public class ComplexFlowComponent extends FlowComponent {

    private final PropertySpeedPolar northwestflow = new PropertySpeedPolar(FLOWZERO);
    private final PropertySpeedPolar northeastflow = new PropertySpeedPolar(FLOWZERO);
    private final PropertySpeedPolar southeastflow = new PropertySpeedPolar(FLOWZERO);
    private final PropertySpeedPolar southwestflow = new PropertySpeedPolar(FLOWZERO);

    public ComplexFlowComponent(Supplier<PropertyArea> getdisplayarea, String type) {
        super(getdisplayarea, type);
        addProperty("northwestflow", northwestflow);
        addProperty("northeastflow", northeastflow);
        addProperty("southeastflow", southeastflow);
        addProperty("southwestflow", southwestflow);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        super.parseValues(jobj);
        parseOptionalProperty("northwestflow", northwestflow, jobj);
        parseOptionalProperty("northeastflow", northeastflow, jobj);
        parseOptionalProperty("southeastflow", southeastflow, jobj);
        parseOptionalProperty("southwestflow", southwestflow, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        super.toJson(job);
        job.add("northwestflow", northwestflow.toJson());
        job.add("northeastflow", northeastflow.toJson());
        job.add("southeastflow", southeastflow.toJson());
        job.add("southwestflow", southwestflow.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        super.setOnChange(onchange);
        northwestflow.setOnChange(onchange);
        northeastflow.setOnChange(onchange);
        southeastflow.setOnChange(onchange);
        southwestflow.setOnChange(onchange);
    }

    public SpeedPolar getNorthwestflow() {
        return northwestflow.get();
    }

    public SpeedPolar getNortheastflow() {
        return northeastflow.get();
    }

    public SpeedPolar getSoutheastflow() {
        return southeastflow.get();
    }

    public SpeedPolar getSouthwestflow() {
        return southwestflow.get();
    }

    @Override
    public SpeedPolar getFlow(PropertyLocation pos) {
        testLocationWithinArea(pos);
        PropertyLocation bottomleft = getArea().getLocationProperty();
        double xfraction = (pos.getX() - bottomleft.getX()) / getArea().getWidth();
        double yfraction = (pos.getY() - bottomleft.getY()) / getArea().getHeight();
        PropertyLocation fractions = new PropertyLocation(xfraction, yfraction);
        return getSouthwestflow().extrapolate(
                getNorthwestflow(),
                getNortheastflow(),
                getSoutheastflow(),
                fractions
        );
    }
}
