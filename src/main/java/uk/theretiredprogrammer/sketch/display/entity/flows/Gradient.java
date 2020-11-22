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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Dble;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class Gradient implements ModelProperty<Gradient> {

    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("north", "south", "east", "west");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    public static final Gradient GRADIENTDEFAULT = new Gradient();

    private final ConstrainedString type = new ConstrainedString(typenames);
    private final ObservableList<Dble> speeds = FXCollections.observableArrayList();

    public Gradient() {
        setType("north");
    }

    public Gradient(String type) {
        setType(type);
    }

    public Gradient(Gradient value) {
        set(value);
    }

    public Gradient(String type, Collection<Dble> speeds) {
        setType(type);
        addAllSpeedProperties(speeds);
    }

    public Gradient(String type, List<Double> speeds) {
        setType(type);
        addAllSpeeds(speeds);
    }

    public Gradient(String type, Double... speeds) {
        setType(type);
        addAllSpeeds(speeds);
    }

    public final void set(Gradient value) {
        setType(value.getType());
        addAllSpeedProperties(value.getSpeeds());
    }

    public final void setType(String type) {
        this.type.set(type);
    }

    public final void clearSpeeds() {
        speeds.clear();
    }

    public final void add(Dble speed) {
        speeds.add(speed);
    }

    public final void add(double speed) {
        speeds.add(new Dble(speed));
    }

    public final void addAllSpeedProperties(Collection<Dble> speeds) {
        this.speeds.addAll(speeds);
    }

    public final void addAllSpeeds(Collection<Double> speeds) {
        addAllSpeedProperties(collectiontoList(speeds));
    }

    private List<Dble> collectiontoList(Collection<Double> speeds) {
        return speeds.stream().map(s -> new Dble(s)).collect(Collectors.toList());
    }

    public final void addAllSpeeds(Double... speeds) {
        addAllSpeedProperties(arraytoList(speeds));
    }

    private List<Dble> arraytoList(Double... speeds) {
        return Arrays.asList(speeds).stream().map(s -> new Dble(s)).collect(Collectors.toList());
    }

    public ObservableList<Dble> getSpeeds() {
        return speeds;
    }

    public ConstrainedString getTypeProperty() {
        return type;
    }

    public String getType() {
        return type.get();
    }

    @Override
    public void setOnChange(Runnable onchange) {
    }

    @Override
    public Gradient parsevalue(JsonValue value) {
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

    public SpeedVector getFlow(Location pos) {
        return null;  //TODO - getFlow not yet implemented - return null
    }

    public double getMeanFlowDirection() throws IOException {
        switch (type.get()) {
            case "north" -> {
                return 0;
            }
            case "south" -> {
                return 180;
            }
            case "east" -> {
                return 90;
            }
            case "west" -> {
                return -90;
            }
            default ->
                throw new IOException("Illegal gradient direction");
        }
    }
}
