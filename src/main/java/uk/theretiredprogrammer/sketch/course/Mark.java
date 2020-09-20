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

import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.BooleanParser;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.Angle;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.PropertyColor;
import uk.theretiredprogrammer.sketch.core.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.core.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.PropertyString;
import uk.theretiredprogrammer.sketch.core.StringParser;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.Displayable;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Mark implements Displayable {

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private final PropertyString nameproperty = new PropertyString();
    public String getName() {
        return nameproperty.get();
    }
    
    private final PropertyLocation locationproperty = new PropertyLocation();

    Location getLocation() {
        return locationproperty.get();
    }
    //
    private final PropertyBoolean windwardlaylinesproperty = new PropertyBoolean();
    private final PropertyBoolean downwindlaylinesproperty = new PropertyBoolean();
    private final PropertyDouble laylinelengthproperty = new PropertyDouble();
    private final PropertyColor laylinecolorproperty = new PropertyColor();
    private final PropertyColor colourproperty = new PropertyColor();

    private final Supplier<Controller> controllersupplier;

    public Mark(Supplier<Controller> controllersupplier, JsonObject paramsobj) throws IOException {
        this.controllersupplier = controllersupplier;
        nameproperty.set(StringParser.parse(paramsobj, "name")
                .orElseThrow(() -> new IOException("Malformed Definition file - <name> is a mandatory parameter")));
        locationproperty.set(Location.parse(paramsobj, "location").orElse(new Location(0, 0)));
        windwardlaylinesproperty.set(BooleanParser.parse(paramsobj, "windwardlaylines").orElse(false));
        downwindlaylinesproperty.set(BooleanParser.parse(paramsobj, "downwindlaylines").orElse(false));
        laylinelengthproperty.set(DoubleParser.parse(paramsobj, "laylinelength").orElse(0.0));
        laylinecolorproperty.set(ColorParser.parse(paramsobj, "laylinecolour").orElse(Color.BLACK));
        colourproperty.set(ColorParser.parse(paramsobj, "colour").orElse(Color.RED));
    }

    @Override
    public Map<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        map.put("name", nameproperty);
        map.put("location", locationproperty);
        map.put("colour", colourproperty);
        map.put("windwardlaylines", windwardlaylinesproperty);
        map.put("downwindlaylines", downwindlaylinesproperty);
        map.put("laylinelength", laylinelengthproperty);
        map.put("laylinecolour", laylinecolorproperty);
        return map;
    }

    @Override
    public void draw(SketchPane canvas) throws IOException {
        Controller controller = controllersupplier.get();
        canvas.drawmark(getLocation(), SIZE, 6, colourproperty.get());
        Angle windAngle = controller.windflow.getFlow(getLocation()).getAngle();
        if (windwardlaylinesproperty.get()) {
            canvas.drawwindwardlaylines(getLocation(), windAngle,
                    laylinelengthproperty.get(), laylinecolorproperty.get());
        }
        if (downwindlaylinesproperty.get()) {
            canvas.drawleewardlaylines(getLocation(), windAngle,
                    laylinelengthproperty.get(), laylinecolorproperty.get());
        }
    }
}
