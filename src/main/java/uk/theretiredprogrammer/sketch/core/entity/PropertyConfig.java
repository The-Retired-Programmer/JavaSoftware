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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.function.Function;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public class PropertyConfig<P extends PropertyAny, C extends Object> {

    public static final boolean MANDATORY = true;
    public static final boolean OPTIONAL = false;

    private final String key;
    private final boolean mandatoryparse;
    private final Function<String, P> creator;

    public PropertyConfig(String key, boolean mandatoryparse, Function<String, P> creator) {
        this.key = key;
        this.mandatoryparse = mandatoryparse;
        this.creator = creator;
    }

    public String getKey() {
        return key;
    }

    public P create() {
        return creator.apply(key);
    }

    public void parse(PropertyMap propertyentity, JsonObject parentobj) {
        if (mandatoryparse) {
            parseMandatoryProperty(propertyentity, key, parentobj);
        } else {
            parseOptionalProperty(propertyentity, key, parentobj);
        }
    }

    public final P getProperty(PropertyMap propertyentity, String messageleader) {
        P property = (P) propertyentity.propertymap.get(key);
        if (property == null) {
            throw new IllegalStateFailure(messageleader + " - missing key Property");
        }
        return property;
    }

    public P getProperty(PropertyMap propertyentity) {
        return getProperty(propertyentity, "PropertyConfig - getProperty");
    }

    public C get(PropertyMap propertyentity, String messageleader) {
        P property = getProperty(propertyentity, messageleader);
        var val = property.get();
        if (val == null) {
            throw new IllegalStateFailure(messageleader + " - null value");
        }
        return (C) val;
    }

    public C get(PropertyMap propertyentity) {
        return get(propertyentity, "Config - get");
    }

    private void parseMandatoryProperty(PropertyMap propertyentity, String key, JsonObject parentobj) {
        if (!parseOptionalProperty(propertyentity, key, parentobj)) {
            throw new ParseFailure("Missing a Mandatory parameter: " + key);
        }
    }

    private boolean parseOptionalProperty(PropertyMap propertyentity, String key, JsonObject parentobj) {
        PropertyAny property = propertyentity.propertymap.get(key);
        if (property == null) {
            throw new IllegalStateFailure("Config parse - missing initialised Config");
        }
        JsonValue jvalue = parentobj.get(key);
        if (jvalue == null) {
            return false;
        }
        property.parse(jvalue);
        return true;
    }
}
