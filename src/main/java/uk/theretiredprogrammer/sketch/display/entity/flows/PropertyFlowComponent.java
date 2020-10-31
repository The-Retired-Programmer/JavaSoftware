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

import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyNamed;
import uk.theretiredprogrammer.sketch.core.entity.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyMap;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyMap.PropertyConfig.MANDATORY;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyMap.PropertyConfig.OPTIONAL;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class PropertyFlowComponent extends PropertyMap implements PropertyNamed {

    private static final ObservableList<String> typenames;

    static {
        typenames = FXCollections.observableArrayList();
        typenames.addAll("complexflow", "constantflow", "gradientflow", "testflow");
    }

    public static ObservableList<String> getTypenames() {
        return typenames;
    }

    public static PropertyFlowComponent factory(String type, Supplier<Area> getdisplayarea) {
        switch (type) {
            case "testflow" -> {
                return new PropertyTestFlowComponent(getdisplayarea, type);
            }
            case "complexflow" -> {
                return new PropertyComplexFlowComponent(getdisplayarea, type);
            }
            case "constantflow" -> {
                return new PropertyConstantFlowComponent(getdisplayarea, type);
            }
            case "gradientflow" -> {
                return new PropertyGradientFlowComponent(getdisplayarea, type);
            }
            default ->
                throw new ParseFailure("Missing or Unknown type parameter in a flow definition (" + type + ")");
        }
    }

    private final PropertyConfig<PropertyString, String> name = new PropertyConfig<>("name", MANDATORY, (s) -> new PropertyString(s, "<newname>"));
    private final PropertyConfig<PropertyInteger, Integer> zlevel = new PropertyConfig<>("zlevel", OPTIONAL, (s) -> new PropertyInteger(s, 0));
    private final PropertyConfig<PropertyArea, Area> area = new PropertyConfig<>("area", OPTIONAL, (s) -> new PropertyArea(s, AREAZERO));
    private final PropertyConfig<PropertyConstrainedString, String> type;
    private final Supplier<Area> getdisplayarea;

    public PropertyFlowComponent(Supplier<Area> getdisplayarea, String flowtype) {
        this.getdisplayarea = getdisplayarea;
        type = new PropertyConfig<>("type", MANDATORY, (s) -> new PropertyConstrainedString(s, typenames, flowtype));
        this.addConfig(name, zlevel, area, type);
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public PropertyFlowComponent get() {
        return this;
    }

    public String getName() {
        return name.get("PropertyFlowComponent name");
    }

    public PropertyString getNameProperty() {
        return name.getProperty("PropertyFlowComponent name");
    }

    public int getZlevel() {
        return zlevel.get("PropertyFlowComponent zlevel");
    }

    public Area getArea() {
        Area windarea = area.get("PropertyFlowComponent area");
        return windarea.equals(AREAZERO) ? getdisplayarea.get() : windarea;
    }

    public String getType() {
        return type.get("PropertyFlowComponent type");
    }
}
