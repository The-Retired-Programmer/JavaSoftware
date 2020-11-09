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
package uk.theretiredprogrammer.sketch.core.entity;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public abstract class ModelProperties implements Model {

    private final ObservableMap<String, Model> properties = FXCollections.observableMap(new LinkedHashMap<>());
    
    protected abstract void parseValues(JsonObject jobj);

    @Override
    public void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.OBJECT) {
            parseValues((JsonObject) jvalue);
        }
    }

    protected void parseMandatoryProperty(String key, Model model, JsonObject parentobj) {
        if (!parseOptionalProperty(key, model, parentobj)) {
            throw new ParseFailure("Missing a Mandatory parameter: " + key);
        }
    }

    protected boolean parseOptionalProperty(String key, Model model, JsonObject parentobj) {
        JsonValue jvalue = parentobj.get(key);
        if (jvalue == null) {
            return false;
        }
        model.parse(jvalue);
        return true;
    }

    public Stream<Entry<String,Model>> stream(){
        return properties.entrySet().stream();
    }

    protected void addProperty(String key, Model property) {
        properties.put(key, property);
    }
}
