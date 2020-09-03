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
package uk.theretiredprogrammer.sketch.ui;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyDouble;
import uk.theretiredprogrammer.sketch.jfx.DisplaySurface;

/**
 * The Information to describe the Simulation "Field of play
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SailingArea implements Displayable {

    // dimensions of the visible "playing surface" in metres
    // (default is a 1km square with 0,0 in centre).
    public static final double EAST_DEFAULT = -500;
    public static final double WEST_DEFAULT = 500;
    public static final double NORTH_DEFAULT = 500;
    public static final double SOUTH_DEFAULT = -500;

    private final PropertyDouble eastproperty = new PropertyDouble();
//    public final double getEast() {
//        return eastproperty.get();
//    } 
    private final PropertyDouble westproperty = new PropertyDouble();

    private final PropertyDouble northproperty = new PropertyDouble();
    private final PropertyDouble southproperty = new PropertyDouble();
    private final PropertyDouble eastlimitproperty = new PropertyDouble();
    private final PropertyDouble westlimitproperty = new PropertyDouble();
    private final PropertyDouble northlimitproperty = new PropertyDouble();
    private final PropertyDouble southlimitproperty = new PropertyDouble();

    public SailingArea(JsonObject parsedjson) throws IOException {
        JsonObject paramsobj = parsedjson.getJsonObject("SAILING AREA");
        if (paramsobj == null) {
            throw new IOException("Malformed Definition File - missing SAILING AREA object");
        }
        eastproperty.set(DoubleParser.parse(paramsobj, "east").orElse(EAST_DEFAULT));
        eastlimitproperty.set(DoubleParser.parse(paramsobj, "eastlimit").orElse(eastproperty.get()));
        westproperty.set(DoubleParser.parse(paramsobj, "west").orElse(WEST_DEFAULT));
        westlimitproperty.set(DoubleParser.parse(paramsobj, "westlimit").orElse(westproperty.get()));
        northproperty.set(DoubleParser.parse(paramsobj, "north").orElse(NORTH_DEFAULT));
        northlimitproperty.set(DoubleParser.parse(paramsobj, "northlimit").orElse(northproperty.get()));
        southproperty.set(DoubleParser.parse(paramsobj, "south").orElse(SOUTH_DEFAULT));
        southlimitproperty.set(DoubleParser.parse(paramsobj, "southlimit").orElse(southproperty.get()));
    }

    public Map<String, Object> properties() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("north", northproperty);
        map.put("northlimit", northlimitproperty);
        map.put("east", eastproperty);
        map.put("eastlimit", eastlimitproperty);
        map.put("south", southproperty);
        map.put("southlimit", southlimitproperty);
        map.put("west", westproperty);
        map.put("westlimit", westlimitproperty);
        return map;
    }

    public Area getArea() {
        return new Area(new Location(westproperty.get(), southproperty.get()),
                eastproperty.get() - westproperty.get(), northproperty.get() - southproperty.get());
    }

    @Override
    public void draw(DisplaySurface canvas, double zoom) throws IOException {
        canvas.drawrectangle(
                new Area(new Location(westlimitproperty.get(), southlimitproperty.get()),
                        eastlimitproperty.get() - westlimitproperty.get(), northlimitproperty.get() - southlimitproperty.get()),
                Color.LIGHTSEAGREEN
        );
        if (westlimitproperty.get() > westproperty.get()) {
            canvas.drawrectangle(
                    new Area(new Location(westproperty.get(), southlimitproperty.get()), westlimitproperty.get() - westproperty.get(), northlimitproperty.get() - southlimitproperty.get()),
                    Color.OLIVEDRAB
            );
        }
        if (eastlimitproperty.get() < eastproperty.get()) {
            canvas.drawrectangle(
                    new Area(new Location(eastlimitproperty.get(), southlimitproperty.get()),
                            eastproperty.get() - eastlimitproperty.get(), northlimitproperty.get() - southlimitproperty.get()),
                    Color.OLIVEDRAB
            );
        }
        if (northlimitproperty.get() < northproperty.get()) {
            canvas.drawrectangle(
                    new Area(new Location(westproperty.get(), northlimitproperty.get()),
                            eastproperty.get() - westproperty.get(), northproperty.get() - northlimitproperty.get()),
                    Color.OLIVEDRAB
            );
        }
        if (southlimitproperty.get() > southproperty.get()) {
            canvas.drawrectangle(
                    new Area(new Location(westproperty.get(), southproperty.get()),
                            eastproperty.get() - westproperty.get(), southlimitproperty.get() - southproperty.get()),
                    Color.OLIVEDRAB
            );
        }
    }

    public double getWidth(double zoom) {
        return (eastproperty.get() - westproperty.get()) * zoom;
    }

    public double getHeight(double zoom) {
        return (northproperty.get() - southproperty.get()) * zoom;
    }
}
