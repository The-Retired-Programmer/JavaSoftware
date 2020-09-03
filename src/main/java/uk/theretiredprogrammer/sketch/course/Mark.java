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
package uk.theretiredprogrammer.sketch.course;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.BooleanParser;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.DistancePolar;
import uk.theretiredprogrammer.sketch.core.StringParser;
import uk.theretiredprogrammer.sketch.jfx.DisplaySurface;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Mark {

    private static final double SIZE = 1; // set up as 1 metre diameter object

    public String name;
    final Location location;
    //
    private final boolean windwardlaylines;
    private final boolean downwindlaylines;
    private final double laylinelength;
    private final Color laylinecolor;
    private final Color color;

    private final Supplier<Controller> controllersupplier;

    public Mark(Supplier<Controller> controllersupplier, JsonObject paramsobj) throws IOException {
        this.controllersupplier = controllersupplier;
        name = StringParser.parse(paramsobj, "name")
                .orElseThrow(() -> new IOException("Malformed Definition file - <name> is a mandatory parameter"));
        location = Location.parse(paramsobj, "location").orElse(new Location(0, 0));
        windwardlaylines = BooleanParser.parse(paramsobj, "windwardlaylines").orElse(false);
        downwindlaylines = BooleanParser.parse(paramsobj, "downwindlaylines").orElse(false);
        laylinelength = DoubleParser.parse(paramsobj, "laylinelength").orElse(0.0);
        laylinecolor = ColorParser.parse(paramsobj, "laylinecolour").orElse(Color.BLACK);
        color = ColorParser.parse(paramsobj, "colour").orElse(Color.RED);
    }

    public Map<String, Object> properties() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("locataion", location);
        map.put("colour", color);
        map.put("windwardlaylines", windwardlaylines);
        map.put("downwindlaylines", downwindlaylines);
        map.put("laylinelength", laylinelength);
        map.put("laylinecolour", laylinecolor);
        return map;
    }

    private final static Angle WINDWARDLAYLINEANGLE = new Angle(135);
    private final static Angle LEEWARDLAYLINEANGLE = new Angle(45);

    public void draw(DisplaySurface canvas, double zoom) throws IOException {
        Controller controller = controllersupplier.get();
        Angle windAngle = controller.windflow.getFlow(location).getAngle();
        canvas.drawmark(location, SIZE, 6, javafx.scene.paint.Color.GOLD);
        // now draw the laylines - this are scale independent and set to 1 pixel line
//        if (windwardlaylines) {
//            pixelLine(gc, location,
//                    new DistancePolar(laylinelength, windAngle.add(WINDWARDLAYLINEANGLE)),
//                    laylinecolor, zoom);
//            pixelLine(gc, location,
//                    new DistancePolar(laylinelength, windAngle.sub(WINDWARDLAYLINEANGLE)),
//                    laylinecolor, zoom);
//        }
//        if (downwindlaylines) {
//            pixelLine(gc, location,
//                    new DistancePolar(laylinelength, windAngle.add(LEEWARDLAYLINEANGLE)),
//                    laylinecolor, zoom);
//            pixelLine(gc, location,
//                    new DistancePolar(laylinelength, windAngle.sub(LEEWARDLAYLINEANGLE)),
//                    laylinecolor, zoom);
//        }
    }

    private void pixelLine(DisplaySurface canvas, Location laylineBase, DistancePolar line,
            Color laylineColour, double zoom) {
//        BasicStroke stroke = new BasicStroke((float) (1f / zoom));
//        gc.setStroke(stroke);
//        gc.setColor(laylineColour);
//        Location end = line.polar2Location(laylineBase);
//        gc.draw(new Line2D.Double(laylineBase.getX(), laylineBase.getY(), end.getX(), end.getY()));
    }
}
