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
package uk.theretiredprogrammer.sketch.properties.entity;

import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class PropertyFlowComponent extends PropertyMap implements PropertyNamed {

    public static PropertyFlowComponent factory(String type, Supplier<Area> getdisplayarea) {
        switch (type) {
            case "testflow" -> {
                return new PropertyTestFlowComponent(getdisplayarea);
            }
            case "complexflow" -> {
                return new PropertyComplexFlowComponent(getdisplayarea);
            }
            case "constantflow" -> {
                return new PropertyConstantFlowComponent(getdisplayarea);
            }
            case "gradientflow" -> {
                return new PropertyGradientFlowComponent(getdisplayarea);
            }
            default ->
                throw new ParseFailure("Missing or Unknown type parameter in a flow definition");
        }
    }

    private final Config<PropertyString, String> name = new Config<>("name", MANDATORY, (s) -> new PropertyString(s, null));
    private final Config<PropertyInteger, Integer> zlevel = new Config<>("zlevel", OPTIONAL, (s) -> new PropertyInteger(s, 0));
    private final Config<PropertyArea, Area> area = new Config<>("area", OPTIONAL, (s) -> new PropertyArea(s, AREAZERO));
    private final Config<PropertyConstrainedString, String> type;
    private final Supplier<Area> getdisplayarea;

    public PropertyFlowComponent(Supplier<Area> getdisplayarea) {
        this.getdisplayarea = getdisplayarea;
        ObservableList<String> types = FXCollections.observableArrayList();
        types.addAll("complexflow", "constantflow", "gradientflow", "testflow");
        type = new Config<>("type", MANDATORY, (s) -> new PropertyConstrainedString(s, types));
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
