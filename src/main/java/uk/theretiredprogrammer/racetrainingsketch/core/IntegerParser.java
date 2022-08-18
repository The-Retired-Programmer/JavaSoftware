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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.io.IOException;
import java.util.Optional;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class IntegerParser {

    public static Optional<Integer> parse(JsonObject jobj, String key) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            if (value.getValueType() == JsonValue.ValueType.NUMBER) {
                return Optional.of(((JsonNumber) value).intValueExact());
            }
        } catch (ArithmeticException ex) {
        }
        throw new IOException("Malformed Definition file - Integer expected with " + key);
    }
}
