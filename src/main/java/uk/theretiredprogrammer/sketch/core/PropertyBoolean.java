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
package uk.theretiredprogrammer.sketch.core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author richard
 */
public class PropertyBoolean extends PropertyItem {

    private SimpleBooleanProperty booleanproperty = new SimpleBooleanProperty();

    public final Boolean getValue() {
        return booleanproperty.getValue();
    }

    public final void setValue(Boolean newvalue) {
        booleanproperty.setValue(newvalue);
    }

    public final boolean get() {
        return booleanproperty.get();
    }

    public final void set(boolean newboolean) {
        booleanproperty.set(newboolean);
    }

    public SimpleBooleanProperty PropertyBoolean() {
        return booleanproperty;
    }

    @Override
    public Node createPropertySheetItem() {
        CheckBox booleanfield = new CheckBox();
        booleanfield.setSelected(booleanproperty.get());
        booleanfield.selectedProperty().bindBidirectional(booleanproperty);
        return booleanfield;
    }
}
