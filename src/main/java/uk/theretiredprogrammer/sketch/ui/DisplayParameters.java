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

import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.IntegerParser;
import uk.theretiredprogrammer.sketch.core.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.PropertyInteger;

/**
 * The Information to describe the Simulation "Field of play
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DisplayParameters {

    public static final double ZOOM_DEFAULT = 1;

    private final PropertyDouble zoomproperty = new PropertyDouble();

    public final double getZoom() {
        return zoomproperty.get();
    }
    private final PropertyInteger secondsperdisplayproperty = new PropertyInteger();

    public final int getSecondsperdisplay() {
        return secondsperdisplayproperty.get();
    }
    private final PropertyDouble speedupproperty = new PropertyDouble();

    public final double getSpeedup() {
        return speedupproperty.get();
    }

    public DisplayParameters(JsonObject parsedjson) throws IOException {
        JsonObject paramsobj = parsedjson.getJsonObject("DISPLAY");
        if (paramsobj == null) {
            throw new IOException("Malformed Definition File - missing DISPLAY object");
        }
        zoomproperty.set(DoubleParser.parse(paramsobj, "zoom").orElse(ZOOM_DEFAULT));
        secondsperdisplayproperty.set(IntegerParser.parse(paramsobj, "secondsperdisplay").orElse(1));
        speedupproperty.set(DoubleParser.parse(paramsobj, "speedup").orElse(1.0));
    }

    public Map properties() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("zoom", zoomproperty);
        map.put("secondsperdisplay", secondsperdisplayproperty);
        map.put("speedup", speedupproperty);
        return map;
    }
}
