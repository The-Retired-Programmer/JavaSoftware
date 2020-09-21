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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyAngle extends PropertyItem {

    private SimpleDoubleProperty angleproperty = new SimpleDoubleProperty();

    public final Angle getValue() {
        return new Angle(angleproperty.get());
    }

    public final void setValue(Angle newangle) {
        angleproperty.set(newangle.getDegrees());
    }

    public final double get() {
        return angleproperty.get();
    }

    public final void set(double newangle) {
        angleproperty.set(newangle);
    }

    public SimpleDoubleProperty PropertyAngle() {
        return angleproperty;
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField doublefield = new TextField(Double.toString(angleproperty.get()));
        doublefield.setPrefColumnCount(7);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(angleproperty);
        return doublefield;
    }
}
