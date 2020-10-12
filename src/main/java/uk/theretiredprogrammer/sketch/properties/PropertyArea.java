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
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.controller.Controller;

/**
 *
 * @author richard
 */
public class PropertyArea extends PropertyElement<Area> {

    private final PropertyDouble xproperty;
    private final PropertyDouble yproperty;
    private final PropertyDouble widthproperty;
    private final PropertyDouble heightproperty;

    public PropertyArea(Area defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyArea(String key, Area defaultvalue) {
        setKey(key);
        xproperty = new PropertyDouble(defaultvalue == null ? null : defaultvalue.getBottomleft().getX());
        yproperty = new PropertyDouble(defaultvalue == null ? null : defaultvalue.getBottomleft().getY());
        widthproperty = new PropertyDouble(defaultvalue == null ? null : defaultvalue.getWidth());
        heightproperty = new PropertyDouble(defaultvalue == null ? null : defaultvalue.getHeight());
    }

//    public PropertyValueArea(JsonValue jvalue) throws IOException {
//        this(jvalue, null);
//    }
//
//    public PropertyValueArea(JsonValue jvalue, Area defaultvalue) throws IOException {
//        this(defaultvalue);
//        set(parsevalue(jvalue));
//    }
    @Override
    public final Area get() {
        Double xval = xproperty.get();
        return xval == null ? null
                : new Area(new Location(xval, yproperty.get()), widthproperty.get(), heightproperty.get());
    }

    @Override
    public final void set(Area newarea) {
        xproperty.set(newarea == null ? null : newarea.getBottomleft().getX());
        yproperty.set(newarea == null ? null : newarea.getBottomleft().getY());
        widthproperty.set(newarea == null ? null : newarea.getWidth());
        heightproperty.set(newarea == null ? null : newarea.getHeight());
    }

    @Override
    public final Area parsevalue(JsonValue value) throws IOException {
        if (value != null && value.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) value;
            if (values.size() == 4) {
                return new Area(
                        xproperty.parsevalue(values.get(0)),
                        yproperty.parsevalue(values.get(1)),
                        widthproperty.parsevalue(values.get(2)),
                        heightproperty.parsevalue(values.get(3))
                );
            }
        }
        throw new IOException("Malformed Definition file - List of 4 numbers expected");
    }

    @Override
    public JsonValue toJson() {
        Double xval = xproperty.get();
        return xval == null ? JsonValue.NULL
                : Json.createArrayBuilder()
                        .add(xval)
                        .add(yproperty.get())
                        .add(widthproperty.get())
                        .add(heightproperty.get())
                        .build();
    }

    @Override
    public Node getField(Controller controller) {
        return new HBox(
                createTextFor("["),
                xproperty.getField(controller, 7),
                createTextFor(","),
                yproperty.getField(controller, 7),
                createTextFor("] "),
                widthproperty.getField(controller, 7),
                createTextFor("x"),
                heightproperty.getField(controller, 7)
        );
    }

    @Override
    public Node getField(Controller controller, int size) {
        return new HBox(
                createTextFor("["),
                xproperty.getField(controller, size),
                createTextFor(","),
                yproperty.getField(controller, size),
                createTextFor("] "),
                widthproperty.getField(controller, size),
                createTextFor("x"),
                heightproperty.getField(controller, size)
        );
    }

    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }
}
