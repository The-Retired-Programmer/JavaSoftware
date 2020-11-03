/*
 * Copyright 2014-2020 Richard Linsdale.
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

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;

/**
 * Abstract Class describing a Flow Model. A Flow Model represents variable
 * flows over a area.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class FlowComponent extends ModelProperties {

    public static FlowComponent factory(String type, Supplier<Area> getdisplayarea) {
        switch (type) {
            case "testflow" -> {
                return new TestFlowComponent(getdisplayarea, type);
            }
            case "complexflow" -> {
                return new ComplexFlowComponent(getdisplayarea, type);
            }
            case "constantflow" -> {
                return new ConstantFlowComponent(getdisplayarea, type);
            }
            case "gradientflow" -> {
                return new GradientFlowComponent(getdisplayarea, type);
            }
            default ->
                throw new IllegalStateFailure("Missing or Unknown type parameter in a flow definition (" + type + ")");
        }
    }

    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("complexflow", "constantflow", "gradientflow", "testflow");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    private final PropertyString name = new PropertyString("name", "<newname>");
    private final PropertyInteger zlevel = new PropertyInteger("zlevel", 0);
    private final PropertyArea area = new PropertyArea("area", AREAZERO);
    private final PropertyConstrainedString type;
    private final Supplier<Area> getdisplayarea;

    public FlowComponent(Supplier<Area> getdisplayarea, String flowtype) {
        this.getdisplayarea = getdisplayarea;
        type = new PropertyConstrainedString("type", typenames, flowtype);
        addProperty("name", name);
        addProperty("zlevel", zlevel);
        addProperty("area", area);
        addProperty("type", type);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseMandatoryProperty("name", name, jobj);
        parseOptionalProperty("zlevel", zlevel, jobj);
        parseOptionalProperty("area", area, jobj);
        parseMandatoryProperty("type", type, jobj);
    }

    protected void toJson(JsonObjectBuilder job) {
        job.add("name", name.toJson());
        job.add("zlevel", zlevel.toJson());
        job.add("area", area.toJson());
        job.add("type", type.toJson());
    }

    @Override
    public void setOnChange(Runnable onchange) {
        name.setOnChange(onchange);
        zlevel.setOnChange(onchange);
        area.setOnChange(onchange);
        type.setOnChange(onchange);
    }

    public String getName() {
        return name.get();
    }

    public PropertyString getNameProperty() {
        return name;
    }

    public int getZlevel() {
        return zlevel.get();
    }

    public Area getArea() {
        Area windarea = area.get();
        return windarea.equals(AREAZERO) ? getdisplayarea.get() : windarea;
    }

    public String getType() {
        return type.get();
    }

    public abstract SpeedPolar getFlow(Location pos);

    void testLocationWithinArea(Location pos) {
        if (!(getArea().isWithinArea(pos))) {
            throw new IllegalStateFailure("Location is not with the Area " + pos);
        }
    }

    public Angle meanWindAngle() {
        return null; // should override if manual control  of mean wind angle required
    }
}
