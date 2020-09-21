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
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyArea extends PropertyItem {

    private final SimpleDoubleProperty xproperty = new SimpleDoubleProperty();
    private final SimpleDoubleProperty yproperty = new SimpleDoubleProperty();
    private final SimpleDoubleProperty widthproperty = new SimpleDoubleProperty();
    private final SimpleDoubleProperty heightproperty = new SimpleDoubleProperty();

    public final Area getValue() {
        return new Area(new Location(xproperty.get(), yproperty.get()), widthproperty.get(), heightproperty.get());
    }

    public final void setValue(Area newarea) {
        xproperty.set(newarea.getBottomleft().getX());
        yproperty.set(newarea.getBottomleft().getY());
        widthproperty.set(newarea.getWidth());
        heightproperty.set(newarea.getHeight());
    }

    public final Area get() {
        return new Area(new Location(xproperty.get(), yproperty.get()), widthproperty.get(), heightproperty.get());
    }

    public final void set(Area newarea) {
        xproperty.set(newarea.getBottomleft().getX());
        yproperty.set(newarea.getBottomleft().getY());
        widthproperty.set(newarea.getWidth());
        heightproperty.set(newarea.getHeight());
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

    public SimpleDoubleProperty PropertyWidth() {
        return widthproperty;
    }

    public final double getWidth() {
        return widthproperty.get();
    }

    public final void setWidth(double newwidth) {
        widthproperty.set(newwidth);
    }

    public SimpleDoubleProperty PropertyHeight() {
        return heightproperty;
    }

    public final double getHeight() {
        return heightproperty.get();
    }

    public final void setHeight(double newheight) {
        heightproperty.set(newheight);
    }

    public final Location getLocation() {
        return new Location(xproperty.get(), yproperty.get());
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        HBox content = new HBox();
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
        TextField wfield = new TextField(Double.toString(widthproperty.get()));
        wfield.setPrefColumnCount(7);
        TextFormatter<Number> wtextformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        wfield.setTextFormatter(wtextformatter);
        wtextformatter.valueProperty().bindBidirectional(widthproperty);
        TextField hfield = new TextField(Double.toString(heightproperty.get()));
        wfield.setPrefColumnCount(7);
        TextFormatter<Number> htextformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        hfield.setTextFormatter(htextformatter);
        htextformatter.valueProperty().bindBidirectional(heightproperty);
        content.getChildren().addAll(createTextFor("["), xfield, createTextFor(","), yfield, createTextFor("] "),
                wfield, createTextFor("x"), hfield);
        return content;
    }
}
