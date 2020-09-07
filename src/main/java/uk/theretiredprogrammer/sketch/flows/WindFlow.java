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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class WindFlow extends Flow {
    
    public static WindFlow create(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray windarray = parsedjson.getJsonArray("WIND");
        if (windarray == null) {
            throw new IOException("Malformed Definition File - missing WIND array");
        }
        FlowComponentSet flowcomponents = new FlowComponentSet();
        for (JsonValue windv : windarray) {
            if (windv.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject wind = (JsonObject) windv;
                flowcomponents.add(FlowComponentFactory.createflowelement(controllersupplier, wind));
            } else {
                throw new IOException("Malformed Definition File - WIND array contains items other that wind objects");
            }
        }
        JsonObject windshiftparams = parsedjson.getJsonObject("WIND-SHIFTS");
        return new WindFlow(controllersupplier, windshiftparams, flowcomponents);
    }
    private final FlowComponentSet flowcomponents;
    
    private WindFlow(Supplier<Controller> controllersupplier, JsonObject params, FlowComponentSet flowcomponents) throws IOException {
        super(controllersupplier, params, flowcomponents);
        this.flowcomponents = flowcomponents;
    }
    
    public Map<String, Object> properties() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        super.properties(map);
        return map;
    }
}
