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
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author richard
 */
public class PropertyGradient extends PropertyItem {

    private SimpleStringProperty typeproperty = new SimpleStringProperty();
    private SimpleListProperty<SimpleDoubleProperty> speedsproperty = new SimpleListProperty<>();

    public final Gradient getValue() {
        return new Gradient(typeproperty.get(), speedsproperty.get());
    }

    public final void setValue(Gradient newgradient) {
        typeproperty.setValue(newgradient.getType());
        speedsproperty.set(newgradient.getSpeeds());
    }

    public final Gradient get() {
        return new Gradient(typeproperty.get(), speedsproperty.get());
    }

    public final void set(Gradient newgradient) {
        typeproperty.setValue(newgradient.getType());
        speedsproperty.set(newgradient.getSpeeds());
    }

    @Override
    public Node createPropertySheetItem() {
        ComboBox typefield = new ComboBox(get().getTypes());
        typefield.valueProperty().bindBidirectional(typeproperty);
        HBox hbox = new HBox();
        hbox.getChildren().add(typefield);
        //
        speedsproperty.get().forEach((speed) -> buildDoubleField(speed));
        return hbox;
    }

    private TextField buildDoubleField(SimpleDoubleProperty bindto) {
        TextField doublefield = new TextField(Double.toString(bindto.get()));
        doublefield.setPrefColumnCount(6);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(bindto);
        return doublefield;
    }
}
