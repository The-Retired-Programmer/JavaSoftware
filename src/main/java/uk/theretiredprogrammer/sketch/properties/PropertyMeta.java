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

import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.core.IllegalStateFailure;

/**
 *
 * @author richard
 */
public class PropertyMeta extends PropertyMap {

    public PropertyMeta(String key) {
        setKey(key);
    }

    @Override
    public PropertyMeta get() {
        return this;
    }

    @Override
    public void parse(JsonValue jval) {
        if (jval != null && jval.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject metaobj = ((JsonObject) jval).getJsonObject(getKey());
            if (metaobj != null) {
                metaobj.forEach((metakey, jvalue) -> {
                    switch (jvalue.getValueType()) {
                        case NUMBER -> {
                            try {
                                add(new PropertyInteger(metakey, ((JsonNumber) jvalue).intValueExact()));
                            } catch (ArithmeticException ex) {
                                add(new PropertyDouble(metakey, ((JsonNumber) jvalue).doubleValue()));
                            }
                        }
                        case STRING ->
                            add(new PropertyString(metakey, ((JsonString) jvalue).getString()));
                        case TRUE ->
                            add(new PropertyBoolean(metakey, true));
                        case FALSE ->
                            add(new PropertyBoolean(metakey, false));
                        case NULL ->
                            add(null);
                        default ->
                            throw new IllegalStateFailure("PropertyMeta parse - unexpected values in switch");
                    }
                });
            }
        }
    }
}
