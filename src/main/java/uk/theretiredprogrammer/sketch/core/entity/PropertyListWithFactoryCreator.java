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
package uk.theretiredprogrammer.sketch.core.entity;

import uk.theretiredprogrammer.sketch.core.entity.PropertyAny;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

/**
 *
 * @author richard
 * @param <P>
 */
public abstract class PropertyListWithFactoryCreator<P extends PropertyAny> extends PropertyList<P> {

    private final Function<String, P> factorycreator;
    private final String parametername;

    public PropertyListWithFactoryCreator(Function<String, P> factorycreator) {
        this(factorycreator, "type");
    }

    public PropertyListWithFactoryCreator(Function<String, P> factorycreator, String parametername) {
        this.factorycreator = factorycreator;
        this.parametername = parametername;
    }

    @Override
    public void parse(JsonValue jvalue) {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray array = ((JsonArray) jvalue);
            for (JsonValue value : array) {
                switch (value.getValueType()) {
                    case OBJECT -> {
                        JsonObject oparams = (JsonObject) value;
                        String type = oparams.getString(parametername, "UNKNOWN");
                        P oproperty = factorycreator.apply(type);
                        oproperty.parse(oparams);
                        add(oproperty);
                    }
                    default ->
                        throw new ParseFailure("Malformed Definition File - array contains items other than the expected object/array (key=" + getKeyForInfo() + ")");
                }
            }
        }
    }
}
