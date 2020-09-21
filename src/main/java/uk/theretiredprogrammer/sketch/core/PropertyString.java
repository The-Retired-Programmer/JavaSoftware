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

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyString extends PropertyItem {

    private SimpleStringProperty stringproperty = new SimpleStringProperty();
    
    public PropertyString(String value) {
        setValue(value);
    }
    
    public PropertyString() {
    }

    public final String getValue() {
        return stringproperty.getValue();
    }

    public final void setValue(String newvalue) {
        stringproperty.setValue(newvalue);
    }

    public final String get() {
        return stringproperty.get();
    }

    public final void set(String newvalue) {
        stringproperty.set(newvalue);
    }

    public SimpleStringProperty PropertyString() {
        return stringproperty;
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField stringfield = new TextField(stringproperty.get());
        stringfield.textProperty().bindBidirectional(stringproperty);
        return stringfield;
    }
}
