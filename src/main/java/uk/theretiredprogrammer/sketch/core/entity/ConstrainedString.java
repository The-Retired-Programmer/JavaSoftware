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

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class ConstrainedString extends SimpleStringProperty implements ModelProperty<String> {

    private ObservableList<String> constraints;

    public ConstrainedString() {
        super.set(null);
    }

    public ConstrainedString(ObservableList<String> constraints) {
        this(null, constraints);
    }

    public ConstrainedString(String defaultvalue, ObservableList<String> constraints) {
        this.constraints = constraints;
        if (defaultvalue != null && (!constraints.contains(defaultvalue))) {
            throw new IllegalStateFailure("Bad default value - not in constraints list");
        }
        super.set(defaultvalue);
    }
    
    public ConstrainedString(ConstrainedString clonefrom){
        this.constraints = clonefrom.constraints;
        super.set(clonefrom.get());
    }

    @Override
    public void setOnChange(Runnable onchange) {
        addListener((o, oldval, newval) -> onchange.run());
    }

    @Override
    public final void set(String newvalue) {
        if (newvalue == null) {
            super.set(null);
        } else {
            if (constraints.contains(newvalue)) {
                super.set(newvalue);
            } else {
                throw new ParseFailure("Constrained String - value not in allowed set");
            }
        }
    }

    @Override
    public String parsevalue(JsonValue jvalue) {
        return FromJson.constrainedString(jvalue, constraints);
    }

    @Override
    public JsonValue toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public ComboBox getControl() {
        return getControl(0);
    }

    @Override
    public ComboBox getControl(int size) {
        return UI.control(this, constraints);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        super.set(parsevalue(jvalue));
    }
}
