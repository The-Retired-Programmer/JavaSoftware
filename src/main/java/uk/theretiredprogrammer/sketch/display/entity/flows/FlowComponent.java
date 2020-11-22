/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Int;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Strg;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public abstract class FlowComponent extends ModelMap {

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

    private final Strg name = new Strg("<newname>");
    private final Int zlevel = new Int(0);
    private final Area area = new Area();
    private final ConstrainedString type;
    private final Supplier<Area> getdisplayarea;

    public FlowComponent(Supplier<Area> getdisplayarea, String flowtype) {
        this.getdisplayarea = getdisplayarea;
        type = new ConstrainedString(flowtype, typenames);
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

    public Strg getNameProperty() {
        return name;
    }

    public int getZlevel() {
        return zlevel.get();
    }

    public Area getArea() {
        Area windarea = area;
        return windarea.equals(AREAZERO) ? getdisplayarea.get() : windarea;
    }

    public String getType() {
        return type.get();
    }

    public abstract SpeedVector getFlow(Location pos);

    void testLocationWithinArea(Location pos) {
        if (!(getArea().isWithinArea(pos))) {
            throw new IllegalStateFailure("PropertyLocation is not with the Area " + pos);
        }
    }

    public Angle meanWindAngle() {
        return null; // should override if manual control  of mean wind angle required
    }
}
