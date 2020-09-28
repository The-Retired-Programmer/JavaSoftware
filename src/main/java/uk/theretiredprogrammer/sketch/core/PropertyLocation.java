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

import jakarta.json.Json;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyLocation extends PropertyItem {

    private SimpleDoubleProperty xproperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty yproperty = new SimpleDoubleProperty();

    public final Location getValue() {
        return new Location(xproperty.get(), yproperty.get());
    }

    public final void setValue(Location newlocation) {
        xproperty.set(newlocation.getX());
        yproperty.set(newlocation.getY());
    }

    public final Location get() {
        return new Location(xproperty.get(), yproperty.get());
    }

    public final void set(Location newlocation) {
        xproperty.set(newlocation.getX());
        yproperty.set(newlocation.getY());
    }

    public SimpleDoubleProperty PropertyLocationX() {
        return xproperty;
    }

    public final double getLocationX() {
        return xproperty.get();
    }

    public final void setLocationX(double newX) {
        xproperty.set(newX);
    }

    public SimpleDoubleProperty PropertyLocationY() {
        return yproperty;
    }

    public final double getLocationY() {
        return yproperty.get();
    }

    public final void setLocationY(double newY) {
        yproperty.set(newY);
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField xfield = new TextField(Double.toString(xproperty.get()));
        xfield.setPrefColumnCount(7);
        TextFormatter<Number> xtextformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        xfield.setTextFormatter(xtextformatter);
        xtextformatter.valueProperty().bindBidirectional(xproperty);
        TextField yfield = new TextField(Double.toString(yproperty.get()));
        yfield.setPrefColumnCount(7);
        TextFormatter<Number> ytextformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        yfield.setTextFormatter(ytextformatter);
        ytextformatter.valueProperty().bindBidirectional(yproperty);
        TextFlow content = new TextFlow(createTextFor("["), xfield, createTextFor(","), yfield, createTextFor("]"));
        return content;
    }

    @Override
    public JsonValue toJson() {
        return Json.createArrayBuilder()
                .add(xproperty.get())
                .add(yproperty.get())
                .build();
    }
}
