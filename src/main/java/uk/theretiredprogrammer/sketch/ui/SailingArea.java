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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.DoubleParser;

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

    public final double east;
    public final double west;
    public final double north;
    public final double south;
    private final double eastlimit;
    private final double westlimit;
    private final double northlimit;
    private final double southlimit;

    public SailingArea(JsonObject parsedjson) throws IOException {
        JsonObject paramsobj = parsedjson.getJsonObject("SAILING AREA");
        if (paramsobj == null) {
            throw new IOException("Malformed Definition File - missing SAILING AREA object");
        }
        east = DoubleParser.parse(paramsobj, "east").orElse(EAST_DEFAULT);
        eastlimit = DoubleParser.parse(paramsobj, "eastlimit").orElse(east);
        west = DoubleParser.parse(paramsobj, "west").orElse(WEST_DEFAULT);
        westlimit = DoubleParser.parse(paramsobj, "westlimit").orElse(west);
        north = DoubleParser.parse(paramsobj, "north").orElse(NORTH_DEFAULT);
        northlimit = DoubleParser.parse(paramsobj, "northlimit").orElse(north);
        south = DoubleParser.parse(paramsobj, "south").orElse(SOUTH_DEFAULT);
        southlimit = DoubleParser.parse(paramsobj, "southlimit").orElse(south);
    }
    
    public Map<String, Object> properties() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("north", north);
        map.put("northlimit", northlimit);
        map.put("east", east);
        map.put("eastlimit", eastlimit);
        map.put("south", south);
        map.put("southlimit", southlimit);
        map.put("west", west);
        map.put("westlimit", westlimit);
        return map;
    }
    
    @Override
     public void draw(Graphics2D g2D, double zoom) throws IOException {
        // set transform
        g2D.transform(new AffineTransform(zoom, 0, 0, -zoom, -west * zoom, north * zoom));
        // if limits set then darken limit areas
        if (westlimit > west) {
            Shape shape = new Rectangle2D.Double(west, south,
                    westlimit - west, north - south);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (eastlimit < east) {
            Shape shape = new Rectangle2D.Double(eastlimit, south,
                    east - eastlimit, north - south);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (northlimit < north) {
            Shape shape = new Rectangle2D.Double(west, northlimit,
                    east - west, north - northlimit);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
        if (southlimit > south) {
            Shape shape = new Rectangle2D.Double(west, south,
                    east - west, southlimit - south);
            g2D.setColor(Color.darkGray);
            g2D.draw(shape);
            g2D.fill(shape);
        }
    }

    public Dimension getGraphicDimension(double zoom) {
        double width = east - west;
        double depth = north - south;
        return new Dimension((int) (width * zoom), (int) (depth * zoom));
    }
}
