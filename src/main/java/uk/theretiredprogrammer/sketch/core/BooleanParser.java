/*
 * Copyright 2020 Richard Linsdale.
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
package uk.theretiredprogrammer.sketch.core;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BooleanParser {

    public static Optional<Boolean> parse(JsonObject jobj, String key) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value.getValueType() == JsonValue.ValueType.TRUE) {
            return Optional.of(true);
        }
        if (value.getValueType() == JsonValue.ValueType.FALSE) {
            return Optional.of(false);
        }
        throw new IOException("Malformed Definition file - Boolean expected with " + key);
    }
}
