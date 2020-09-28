/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.flows;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.properties.PropertyItem;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class WaterFlow extends Flow {

    public static WaterFlow create(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray waterarray = parsedjson.getJsonArray("water");
        FlowComponentSet flowcomponents = new FlowComponentSet();
        if (waterarray != null) {
            for (JsonValue waterv : waterarray) {
                if (waterv.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject water = (JsonObject) waterv;
                    flowcomponents.add(FlowComponentFactory.createflowelement(controllersupplier, water));
                } else {
                    throw new IOException("Malformed Definition File - <water> array contains items other that <water> objects");
                }
            }
        }
        return new WaterFlow(controllersupplier, flowcomponents);
    }

    private WaterFlow(Supplier<Controller> controllersupplier, FlowComponentSet flowcomponents) throws IOException {
        super(controllersupplier, null, flowcomponents);
    }

    @Override
    public Map<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        super.properties(map);
        return map;
    }
}
