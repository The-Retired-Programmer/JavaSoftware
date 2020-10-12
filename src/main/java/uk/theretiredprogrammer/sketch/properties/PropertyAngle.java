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
import jakarta.json.JsonValue;
import java.io.IOException;
import javafx.scene.control.TextField;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.controller.Controller;

/**
 *
 * @author richard
 */
public class PropertyAngle extends PropertyElement<Angle> {

    private final PropertyDouble angleproperty;

    public PropertyAngle(Angle defaultvalue) {
        this(null, defaultvalue);
    }

    public PropertyAngle(String key, Angle defaultvalue) {
        setKey(key);
        angleproperty = new PropertyDouble(defaultvalue == null ? null : defaultvalue.getDegrees());
    }

    @Override
    public final Angle get() {
        Double angle = angleproperty.get();
        return angle == null ? null : new Angle(angleproperty.get());
    }

    @Override
    public final void set(Angle newangle) {
        angleproperty.set(newangle == null ? null : newangle.getDegrees());
    }

    @Override
    public final Angle parsevalue(JsonValue jvalue) throws IOException {
        return new Angle(angleproperty.parsevalue(jvalue));
    }

    @Override
    public JsonValue toJson() {
        Double angle = angleproperty.get();
        return angle == null ? JsonValue.NULL : Json.createValue(angle);
    }

    @Override
    public TextField getField(Controller controller) {
        return angleproperty.getField(controller, 7);
    }

    @Override
    public TextField getField(Controller controller, int size) {
        return angleproperty.getField(controller, size);
    }

    @Override
    public final void parse(JsonValue jvalue) throws IOException {
        set(parsevalue(jvalue));
    }
}
