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
import javafx.scene.text.TextFlow;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertySpeedPolar extends PropertyItem {

    private SimpleDoubleProperty angleproperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty speedproperty = new SimpleDoubleProperty();

    public final SpeedPolar getValue() {
        return new SpeedPolar(speedproperty.get(), new Angle(angleproperty.get()));
    }

    public final void setValue(SpeedPolar newpolar) {
        angleproperty.set(newpolar.getAngle().getDegrees());
        speedproperty.set(newpolar.getSpeed());
    }
    
    public final SpeedPolar get() {
        return new SpeedPolar(speedproperty.get(), new Angle(angleproperty.get()));
    }

    public final void set(SpeedPolar newpolar) {
        angleproperty.set(newpolar.getAngle().getDegrees());
        speedproperty.set(newpolar.getSpeed());
    }
    
    public final double getSpeed() {
        return speedproperty.get();
    }
    
    public final Angle getAngle() {
        return new Angle(angleproperty.get());
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        TextField speedfield = new TextField(Double.toString(speedproperty.get()));
        speedfield.setPrefColumnCount(6);
        TextFormatter<Number> speedformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        speedfield.setTextFormatter(speedformatter);
        speedformatter.valueProperty().bindBidirectional(speedproperty);
        //
        TextField anglefield = new TextField(Double.toString(angleproperty.get()));
        anglefield.setPrefColumnCount(7);
        TextFormatter<Number> angleformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        anglefield.setTextFormatter(angleformatter);
        angleformatter.valueProperty().bindBidirectional(angleproperty);
        //
        TextFlow flow = new TextFlow(speedfield, createTextFor("@"), anglefield, createTextFor("˚"));
        return flow;
    }
}
