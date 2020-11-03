/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.base;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.core.entity.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import static uk.theretiredprogrammer.sketch.core.entity.Area.AREAZERO;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;

public class DisplayModel extends ModelProperties{

    public static final Area DISPLAYAREADEFAULT = new Area(new Location(-500, -500), 1000, 1000);

    private final PropertyDouble zoom = new PropertyDouble("zoom", 1.0);
    private final PropertyInteger secondsperdisplay = new PropertyInteger("secondsperdisplay", 1);
    private final PropertyDouble speedup = new PropertyDouble("speedup", 1.0);
    private final PropertyArea displayarea = new PropertyArea("displayarea", DISPLAYAREADEFAULT);
    private final PropertyArea sailingarea = new PropertyArea("sailingarea", AREAZERO);

    public DisplayModel() {
        addProperty("zoom", zoom);
        addProperty("secondsperdisplay", secondsperdisplay);
        addProperty("speedup", speedup);
        addProperty("displayarea", displayarea);
        addProperty("sailingarea", sailingarea);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseOptionalProperty("zoom", zoom, jobj);
        parseOptionalProperty("secondsperdisplay", secondsperdisplay, jobj);
        parseOptionalProperty("speedup", speedup, jobj);
        parseOptionalProperty("displayarea", displayarea, jobj);
        parseOptionalProperty("sailingarea", sailingarea, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("zoom", zoom.toJson());
        job.add("secondsperdisplay", secondsperdisplay.toJson());
        job.add("speedup", speedup.toJson());
        job.add("displayarea", displayarea.toJson());
        job.add("sailingarea", sailingarea.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        zoom.setOnChange(onchange);
        secondsperdisplay.setOnChange(onchange);
        speedup.setOnChange(onchange);
        displayarea.setOnChange(onchange);
        sailingarea.setOnChange(onchange);
    }
    
    public DisplayModel get() {
        return this;
    }

    public double getZoom() {
        return zoom.get();
    }

    public int getSecondsperdisplay() {
        return secondsperdisplay.get();
    }

    public double getSpeedup() {
        return speedup.get();
    }

    public Area getSailingarea() {
        Area area = sailingarea.get();
        return area.equals(AREAZERO) ? getDisplayarea() : area;
    }

    public Area getDisplayarea() {
        return displayarea.get();
    }
}
