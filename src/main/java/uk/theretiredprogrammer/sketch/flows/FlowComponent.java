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
package uk.theretiredprogrammer.sketch.flows;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.IntegerParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyArea;
import uk.theretiredprogrammer.sketch.core.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.core.StringParser;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.SailingArea;

/**
 * Abstract Class describing a Flow Model. A Flow Model represents variable
 * flows over a area.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class FlowComponent {

    private final String name;
    private final PropertyArea areaproperty = new PropertyArea();
    private final PropertyInteger zlevelproperty = new PropertyInteger();

    public FlowComponent(Supplier<Controller>controllersupplier, JsonObject paramsobj) throws IOException {
        SailingArea sailingarea = controllersupplier.get().sailingarea;
        Area sarea = sailingarea.getArea();
        name = StringParser.parse(paramsobj, "name").orElse("");
        Location bottomleft = Location.parse(paramsobj, "location").orElse(sarea.getBottomleft());
        double width = DoubleParser.parse(paramsobj, "width").orElse(sarea.getWidth());
        double height = DoubleParser.parse(paramsobj, "height").orElse(sarea.getHeight());
        zlevelproperty.set(IntegerParser.parse(paramsobj, "zlevel").orElse(0));
        areaproperty.set(new Area(bottomleft, width, height));
    }

    public void change(JsonObject params) throws IOException {
        Location bottomleft = Location.parse(params, "location").orElse(areaproperty.getLocation());
        double width = DoubleParser.parse(params, "width").orElse(areaproperty.getWidth());
        double height = DoubleParser.parse(params, "height").orElse(areaproperty.getHeight());
        zlevelproperty.set(IntegerParser.parse(params, "zlevel").orElse(zlevelproperty.get()));
        areaproperty.set(new Area(bottomleft, width, height));
    }
    
    void properties(LinkedHashMap<String,Object> map) {
        if (name != null && !name.isEmpty()) {
            map.put("name", name);
        }
        map.put("type", getFlowType());
        map.put("area", areaproperty);
        map.put("zlevel", zlevelproperty);
    }
    
    public abstract LinkedHashMap<String,Object> properties();
    
    public String getName(){
        return name;
    }
    
    public abstract String getFlowType();
    
    public Area getArea() {
        return areaproperty.get();
    }
    
    public int getZlevel() {
        return zlevelproperty.get();
    }
    
    public abstract SpeedPolar getFlow(Location pos) throws IOException;
    
    void testLocationWithinArea(Location pos) throws IOException {
        if (!getArea().isWithinArea(pos)) {
            throw new IOException("Location is not with the Area " + pos);
        }
    }
    
    public Angle meanWindAngle() {
        return null; // should override if manual control  of mean wind angle required
    }
}
