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
import java.util.Comparator;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.ModelList;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;

public class FlowComponentSet extends ModelList<FlowComponent> {

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

    public PropertySpeedVector getFlow(PropertyLocation pos) {
        return this.stream()
                .filter(flow -> flow.getArea().isWithinArea(pos))
                .sorted(Comparator.comparingInt(FlowComponent::getZlevel).reversed())
                .findFirst()
                .map(flow -> flow.getFlow(pos))
                .orElse(new PropertySpeedVector());

    }

    public PropertyDegrees meanWindAngle() {
        return stream().map(flow -> flow.meanWindAngle())
                .filter(angle -> angle != null)
                .findFirst().orElse(null);
    }
}
