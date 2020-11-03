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
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;

public abstract class FlowComponentModel extends ModelProperties {

    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("complexflow", "constantflow", "gradientflow", "testflow");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    public static FlowComponentModel factory(String type, Supplier<Area> getdisplayarea) {
        switch (type) {
            case "testflow" -> {
                return new TestFlowComponentModel(getdisplayarea, type);
            }
            case "complexflow" -> {
                return new ComplexFlowComponentModel(getdisplayarea, type);
            }
            case "constantflow" -> {
                return new ConstantFlowComponentModel(getdisplayarea, type);
            }
            case "gradientflow" -> {
                return new GradientFlowComponentModel(getdisplayarea, type);
            }
            default ->
                throw new ParseFailure("Missing or Unknown type parameter in a flow definition (" + type + ")");
        }
    }

    private final PropertyString name = new PropertyString("name", "<newname>");
    private final PropertyInteger zlevel = new PropertyInteger("zlevel", 0);
    private final PropertyArea area = new PropertyArea("area", AREAZERO);
    private final PropertyConstrainedString type;
    private final Supplier<Area> getdisplayarea;

    public FlowComponentModel(Supplier<Area> getdisplayarea, String flowtype) {
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
}
