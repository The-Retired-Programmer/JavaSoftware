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

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyInteger extends PropertyItem {

    private SimpleIntegerProperty integerproperty = new SimpleIntegerProperty();

    public final Integer getValue() {
        return integerproperty.getValue();
    }

    public final void setValue(Integer newvalue) {
        integerproperty.setValue(newvalue);
    }

    public final int get() {
        return integerproperty.get();
    }

    public final void set(int newint) {
        integerproperty.set(newint);
    }

    public SimpleIntegerProperty PropertyInteger() {
        return integerproperty;
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField intfield = new TextField(Integer.toString(integerproperty.get()));
        intfield.setPrefColumnCount(5);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(integerproperty);
        return intfield;
    }
}
