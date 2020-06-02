/*
 * Copyright 2020 richard linsdale.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ListOf {
    
    public static <T> Optional<List<T>> parse(JsonObject jobj, String key,  Function<JsonValue,T> parseelement) throws IOException {
        if (jobj == null) {
            return Optional.empty();
        }
        JsonValue value = jobj.get(key);
        if (value == null) {
            return Optional.empty();
        }
            if (value.getValueType() == JsonValue.ValueType.ARRAY) {
                JsonArray jarray = (JsonArray) value;
                int size = jarray.size();
                List<T> result = new ArrayList<>();
                for(int i = 0; i < size; i++) {
                    T r = parseelement.apply(jarray.get(i));
                    if (r == null) {
                       throw new IOException("Malformed Definition file - within Array " + key); 
                    }
                    result.add(r);
                }
                return Optional.of(result);
            }
        throw new IOException("Malformed Definition file - Array expected with " + key);
    }
    
}
