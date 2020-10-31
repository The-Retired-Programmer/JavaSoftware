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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;

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
    public final Area parsevalue(JsonValue value) {
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
        throw new ParseFailure("Malformed Definition file - List of 4 numbers expected");
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
    public Node getField() {
        return new HBox(
                createTextFor("["),
                xproperty.getField(7),
                createTextFor(","),
                yproperty.getField(7),
                createTextFor("] "),
                widthproperty.getField(7),
                createTextFor("x"),
                heightproperty.getField(7)
        );
    }

    @Override
    public Node getField(int size) {
        return new HBox(
                createTextFor("["),
                xproperty.getField(size),
                createTextFor(","),
                yproperty.getField(size),
                createTextFor("] "),
                widthproperty.getField(size),
                createTextFor("x"),
                heightproperty.getField(size)
        );
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
