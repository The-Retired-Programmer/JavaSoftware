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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.controller.Controller;

/**
 *
 * @author richard
 */
public class PropertyLocation extends PropertyElement<Location> {

    private final PropertyDouble xlocationproperty;
    private final PropertyDouble ylocationproperty;
    
    public PropertyLocation(Location defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyLocation(String key, Location defaultvalue) {
        setKey(key);
        xlocationproperty = new PropertyDouble(defaultvalue.getX());
        ylocationproperty = new PropertyDouble(defaultvalue.getY());
    }

    @Override
    public final Location get() {
        return new Location(xlocationproperty.get(), ylocationproperty.get());
    }

    @Override
    public final void set(Location newlocation) {
        xlocationproperty.set(newlocation.getX());
        ylocationproperty.set(newlocation.getY());
    }

    @Override
    public Location parsevalue(JsonValue jvalue) throws IOException {
        if (jvalue != null && jvalue.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) jvalue;
            if (values.size() == 2) {
                    return new Location(
                            xlocationproperty.parsevalue(values.get(0)),
                            ylocationproperty.parsevalue(values.get(1))
                    );
            }
        }
        throw new IOException("Malformed Definition file - List of 2 numbers expected");
    }

    @Override
    public JsonArray toJson() {
        return Json.createArrayBuilder()
                .add(xlocationproperty.get())
                .add(ylocationproperty.get())
                .build();
    }

    @Override
    public Node getField(Controller controller) {
        return getField(controller, 7);
    }

    @Override
    public Node getField(Controller controller, int size) {
        return new TextFlow(
                createTextFor("["),
                xlocationproperty.getField(controller, size),
                createTextFor(","),
                ylocationproperty.getField(controller, size),
                createTextFor("]")
        );
    }

    @Override
    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }
}
