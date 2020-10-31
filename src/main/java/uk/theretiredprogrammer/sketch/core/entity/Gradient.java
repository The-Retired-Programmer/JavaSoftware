/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.core.entity;

import java.io.IOException;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE180;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE90;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE90MINUS;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Gradient {

    public static final Gradient GRADIENTDEFAULT = new Gradient();

    private final String type;
    private final ObservableList<PropertyDouble> speeds;

    public Gradient() {
        this.type = "north";
        this.speeds = new SimpleListProperty<>();
    }

    public Gradient(String type, ObservableList<PropertyDouble> speeds) {
        this.type = type;
        this.speeds = speeds;
    }

    public ObservableList<PropertyDouble> getSpeeds() {
        return speeds;
    }

    public String getType() {
        return type;
    }

    public ObservableList<String> getTypes() {
        ObservableList<String> types = new SimpleListProperty<>();
        types.addAll("north", "south", "east", "west");
        return types;
    }

    public SpeedPolar getFlow(Location pos) {
        return null;  //TODO - getFlow not yet implemented - return null
    }

    public Angle getMeanFlowDirection() throws IOException {
        switch (type) {
            case "north" -> {
                return ANGLE0;
            }
            case "south" -> {
                return ANGLE180;
            }
            case "east" -> {
                return ANGLE90;
            }
            case "west" -> {
                return ANGLE90MINUS;
            }
            default ->
                throw new IOException("Illegal gradient direction");
        }
    }
}
