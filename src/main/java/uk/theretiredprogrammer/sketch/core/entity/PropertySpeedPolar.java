/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public class PropertySpeedPolar extends PropertyElement<SpeedPolar> {

    private final PropertyDouble speedproperty;
    private final PropertyAngle directionproperty;

    public PropertySpeedPolar(SpeedPolar defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertySpeedPolar(String key, SpeedPolar defaultvalue) {
        setKey(key);
        speedproperty = new PropertyDouble(defaultvalue.getSpeed());
        directionproperty = new PropertyAngle(defaultvalue.getAngle());
    }

    public void setOnChange(Runnable onchange) {
        //setOnChange((c) -> onchange.run());
    }

    public void setOnChange(ChangeListener cl) {
        speedproperty.propertyDouble().addListener(cl);
        directionproperty.propertyAngle().addListener(cl);
    }

    @Override
    public final SpeedPolar get() {
        return new SpeedPolar(speedproperty.get(), new Angle(directionproperty.get()));
    }

    @Override
    public final void set(SpeedPolar newpolar) {
        speedproperty.set(newpolar.getSpeed());
        directionproperty.set(newpolar.getAngle());
    }

    @Override
    public final SpeedPolar parsevalue(JsonValue value) {
        if (value != null && value.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray values = (JsonArray) value;
            if (values.size() == 2) {
                return new SpeedPolar(
                        speedproperty.parsevalue(values.get(0)),
                        directionproperty.parsevalue(values.get(1))
                );
            }
        }
        throw new ParseFailure("Malformed Definition file - two numbers expected");
    }

    @Override
    public JsonArray toJson() {
        return Json.createArrayBuilder()
                .add(speedproperty.get())
                .add(directionproperty.get().getDegrees())
                .build();
    }

    @Override
    public Node getField() {
        return getField(6, 7);
    }

    @Override
    public Node getField(int size) {
        return getField(size, size);
    }

    private Node getField(int sizespeed, int sizedirection) {
        return new TextFlow(
                speedproperty.getField(sizespeed),
                createTextFor("@"),
                directionproperty.getField(sizedirection),
                createTextFor("˚"));
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
