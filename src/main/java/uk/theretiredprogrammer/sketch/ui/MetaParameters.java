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
package uk.theretiredprogrammer.sketch.ui;

import jakarta.json.JsonObject;

/**
 *
 * @author richard
 */
public class MetaParameters {
    
    private final JsonObject metaobj;
    
    public MetaParameters(JsonObject parsedjson) {
        metaobj = parsedjson.getJsonObject("meta");
    }
    
    public JsonObject toJson() {
        return metaobj;
    }
}
