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
package uk.theretiredprogrammer.sketch.properties;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.function.Supplier;

/**
 *
 * @author richard
 * @param <P>
 */
public abstract class PropertyListWithSimpleCreator<P extends PropertyAny> extends PropertyList<P> {

    private final Supplier<P> creator;

    public PropertyListWithSimpleCreator(Supplier<P> creator) {
        this.creator = creator;
    }

    @Override
    public void parse(JsonValue jvalue) throws IOException {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray array = ((JsonArray) jvalue);
            for (JsonValue value : array) {
                switch (value.getValueType()){
                    case OBJECT -> {
                        JsonObject oparams = (JsonObject) value;
                        P oproperty = creator.get();
                        oproperty.parse(oparams);
                        add(oproperty);
                    }
                    case ARRAY -> {
                        JsonArray aparams = (JsonArray) value;
                        P aproperty = creator.get();
                        aproperty.parse(aparams);
                        add(aproperty);
                    }
                    default -> throw new IOException("Malformed Definition File - array contains items other than the expected object/array (key=" + getKeyForInfo() + ")");
                }
            }
        }
    }
}
