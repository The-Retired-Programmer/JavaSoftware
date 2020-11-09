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

import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES0;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES180;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREES90;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees.DEGREESMINUS90;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class PropertyGradient implements ModelProperty<PropertyGradient> {

    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("north", "south", "east", "west");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    public static final PropertyGradient GRADIENTDEFAULT = new PropertyGradient();

    private final PropertyConstrainedString type = new PropertyConstrainedString(typenames);
    private final ObservableList<PropertyDouble> speeds = FXCollections.observableArrayList();

    public PropertyGradient() {
        setType("north");
    }

    public PropertyGradient(String type) {
        setType(type);
    }

    public PropertyGradient(PropertyGradient value) {
        set(value);
    }

    public PropertyGradient(String type, Collection<PropertyDouble> speeds) {
        setType(type);
        addAllSpeedProperties(speeds);
    }

    public PropertyGradient(String type, List<Double> speeds) {
        setType(type);
        addAllSpeeds(speeds);
    }

    public PropertyGradient(String type, Double... speeds) {
        setType(type);
        addAllSpeeds(speeds);
    }

    public final void set(PropertyGradient value) {
        setType(value.getType());
        addAllSpeedProperties(value.getSpeeds());
    }

    public final void setType(String type) {
        this.type.set(type);
    }

    public final void clearSpeeds() {
        speeds.clear();
    }

    public final void add(PropertyDouble speed) {
        speeds.add(speed);
    }

    public final void add(double speed) {
        speeds.add(new PropertyDouble(speed));
    }

    public final void addAllSpeedProperties(Collection<PropertyDouble> speeds) {
        this.speeds.addAll(speeds);
    }

    public final void addAllSpeeds(Collection<Double> speeds) {
        addAllSpeedProperties(collectiontoList(speeds));
    }

    private List<PropertyDouble> collectiontoList(Collection<Double> speeds) {
        return speeds.stream().map(s -> new PropertyDouble(s)).collect(Collectors.toList());
    }

    public final void addAllSpeeds(Double... speeds) {
        addAllSpeedProperties(arraytoList(speeds));
    }

    private List<PropertyDouble> arraytoList(Double... speeds) {
        return Arrays.asList(speeds).stream().map(s -> new PropertyDouble(s)).collect(Collectors.toList());
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

    @Override
    public void setOnChange(Runnable onchange) {
    }

    @Override
    public PropertyGradient parsevalue(JsonValue value) {
        return FromJson.gradientProperty(value);
    }

    @Override
    public JsonValue toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return getControl(7);
    }

    @Override
    public Node getControl(int size) {
        return UI.control(size, this, typenames);
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
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
