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
package uk.theretiredprogrammer.racetrainingsketch.flows;

import java.io.IOException;
import java.util.function.Supplier;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.core.Area;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.IntegerParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.core.StringParser;
import uk.theretiredprogrammer.racetrainingsketch.ui.Controller;
import uk.theretiredprogrammer.racetrainingsketch.ui.SailingArea;

/**
 * Abstract Class describing a Flow Model. A Flow Model represents variable
 * flows over a area.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class FlowComponent {

    private final String name;
    private Area area;
    private int zlevel;

    public FlowComponent(Supplier<Controller>controllersupplier, JsonObject paramsobj) throws IOException {
        SailingArea scenario = controllersupplier.get().sailingarea;
        name = StringParser.parse(paramsobj, "name").orElse("");
        Location bottomleft = Location.parse(paramsobj, "location").orElse(new Location(scenario.west, scenario.south));
        double width = DoubleParser.parse(paramsobj, "width").orElse(scenario.east - scenario.west);
        double height = DoubleParser.parse(paramsobj, "height").orElse(scenario.north - scenario.south);
        zlevel = IntegerParser.parse(paramsobj, "zlevel").orElse(0);
        area = new Area(bottomleft, width, height);
    }

    public void change(JsonObject params) throws IOException {
        Location bottomleft = Location.parse(params, "location").orElse(area.getBottomleft());
        double width = DoubleParser.parse(params, "width").orElse(area.getWidth());
        double height = DoubleParser.parse(params, "height").orElse(area.getHeight());
        zlevel = IntegerParser.parse(params, "zlevel").orElse(zlevel);
        area = new Area(bottomleft, width, height);
    }
    
    public String getName(){
        return name;
    }
    
    public Area getArea() {
        return area;
    }
    
    public int getZlevel() {
        return zlevel;
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
