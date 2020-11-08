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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.ModelArray;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;

public class FlowComponentSet extends ModelArray<FlowComponent> {

    private final Supplier<PropertyArea> getdisplayarea;

    public FlowComponentSet(Supplier<PropertyArea> getdisplayarea) {
        this.getdisplayarea = getdisplayarea;
    }

    @Override
    protected FlowComponent createAndParse(JsonValue jval) {
        if (jval.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new ParseFailure("Malformed Definition File - array contains items other than Objects");
        }
        JsonObject jobj = (JsonObject) jval;
        FlowComponent flowc = FlowComponent.factory(
                jobj.getString("type", "<undefined>"),
                getdisplayarea
        );
        flowc.parse(jobj);
        return flowc;
    }

    @Override
    public FlowComponent get(String name) {
        throw new IllegalStateFailure("get(name) in not to be used");
    }

    public SpeedPolar getFlow(PropertyLocation pos) {
        int zlevel = -1;
        FlowComponent flowtouse = null;
        for (FlowComponent flow : getProperties()) {
            if (flow.getArea().isWithinArea(pos) && flow.getZlevel() > zlevel) {
                zlevel = flow.getZlevel();
                flowtouse = flow;
            }
        }
        return flowtouse != null ? flowtouse.getFlow(pos) : SpeedPolar.FLOWZERO;
    }

    public PropertyDegrees meanWindAngle() {
        for (FlowComponent flow : getProperties()) {
            PropertyDegrees res = flow.meanWindAngle();
            if (res != null) {
                return res;
            }
        }
        return null;
    }
}
