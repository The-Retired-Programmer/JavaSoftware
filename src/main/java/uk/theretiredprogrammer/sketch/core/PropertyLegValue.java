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

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public class PropertyLegValue extends PropertyItem {

    private SimpleStringProperty marknameproperty = new SimpleStringProperty();
    private SimpleStringProperty roundingproperty = new SimpleStringProperty();

    public final LegValue getValue() throws IOException {
        return new LegValue(marknameproperty.get(), roundingproperty.get());
    }

    public final void setValue(LegValue newleg) {
        marknameproperty.setValue(newleg.getMarkname());
        roundingproperty.set(newleg.getRoundingdirection());
    }

    public final LegValue get() throws IOException {
        return new LegValue(marknameproperty.get(), roundingproperty.get());
    }

    public final void set(LegValue newleg) {
        marknameproperty.setValue(newleg.getMarkname());
        roundingproperty.set(newleg.getRoundingdirection());
    }

    @Override
    public Node createPropertySheetItem(Controller controller) {
        HBox hbox = new HBox();
        //
        ComboBox marknamefield = new ComboBox(controller.course.getMarknames());
        marknamefield.valueProperty().bindBidirectional(marknameproperty);
        hbox.getChildren().add(marknamefield);
        //
        ComboBox roundingfield = new ComboBox(LegValue.getRoundingdirections());
        roundingfield.valueProperty().bindBidirectional(roundingproperty);
        hbox.getChildren().add(roundingfield);
        //
        return hbox;
    }
}
