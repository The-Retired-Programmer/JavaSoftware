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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import java.io.IOException;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES180;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES90;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREESMINUS90;

public class Gradient {
    
    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("north", "south", "east", "west");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    public static final Gradient GRADIENTDEFAULT = new Gradient();

    private final PropertyConstrainedString type = new PropertyConstrainedString(typenames);
    private final ObservableList<PropertyDouble> speeds;

    public Gradient() {
        this("north", new SimpleListProperty<>());
    }

    public Gradient(String type, ObservableList<PropertyDouble> speeds) {
        this.type.set(type);
        this.speeds = speeds;
    }

    public ObservableList<PropertyDouble> getSpeeds() {
        return speeds;
    }

    public SimpleStringProperty getTypeProperty() {
        return type;
    }
    
    public String getType() {
        return type.get();
    }

    public PropertySpeedVector getFlow(PropertyLocation pos) {
        return null;  //TODO - getFlow not yet implemented - return null
    }

    public PropertyDegrees getMeanFlowDirection() throws IOException {
        switch (type.get()) {
            case "north" -> {
                return DEGREES0;
            }
            case "south" -> {
                return DEGREES180;
            }
            case "east" -> {
                return DEGREES90;
            }
            case "west" -> {
                return DEGREESMINUS90;
            }
            default ->
                throw new IOException("Illegal gradient direction");
        }
    }
}
