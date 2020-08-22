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
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.IntegerParser;

/**
 * The Information to describe the Simulation "Field of play
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DisplayParameters  {
    
    public static final double ZOOM_DEFAULT = 1;

    public final double zoom;
    public final int secondsperdisplay;
    public final double speedup;

    public DisplayParameters(JsonObject parsedjson) throws IOException {
        JsonObject paramsobj = parsedjson.getJsonObject("DISPLAY");
        if (paramsobj == null) {
            throw new IOException("Malformed Definition File - missing DISPLAY object");
        }
        zoom = DoubleParser.parse(paramsobj, "zoom").orElse(ZOOM_DEFAULT);
        secondsperdisplay = IntegerParser.parse(paramsobj, "secondsperdisplay").orElse(1);
        speedup = DoubleParser.parse(paramsobj, "speedup").orElse(1.0);
    }
    
    public Map properties() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("zoom", zoom);
        map.put("secondsperdisplay", secondsperdisplay);
        map.put("speedup", speedup);
        return map;
    }
}
