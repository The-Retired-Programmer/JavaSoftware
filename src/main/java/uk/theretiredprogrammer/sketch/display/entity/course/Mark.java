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
package uk.theretiredprogrammer.sketch.display.entity.course;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;

public class Mark extends ModelMap {

    private final PropertyString name = new PropertyString("<newname>");
    private final PropertyLocation location;
    private final PropertyBoolean windwardlaylines = new PropertyBoolean(false);
    private final PropertyBoolean downwindlaylines = new PropertyBoolean(false);
    private final PropertyDouble laylinelength = new PropertyDouble(0.0);
    private final PropertyColour laylinecolour = new PropertyColour(Color.BLACK);
    private final PropertyColour colour = new PropertyColour(Color.RED);

    public Mark() {
        location = new PropertyLocation();
        registerproperties();
    }

    private void registerproperties() {
        addProperty("name", name);
        addProperty("location", location);
        addProperty("windwardlaylines", windwardlaylines);
        addProperty("downwindlaylines", downwindlaylines);
        addProperty("laylinelength", laylinelength);
        addProperty("laylinecolour", laylinecolour);
        addProperty("colour", colour);
    }

    public Mark(PropertyLocation loc) {
        location = new PropertyLocation(loc);
        registerproperties();
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseMandatoryProperty("name", name, jobj);
        parseOptionalProperty("location", location, jobj);
        parseOptionalProperty("windwardlaylines", windwardlaylines, jobj);
        parseOptionalProperty("downwindlaylines", downwindlaylines, jobj);
        parseOptionalProperty("laylinelength", laylinelength, jobj);
        parseOptionalProperty("laylinecolour", laylinecolour, jobj);
        parseOptionalProperty("colour", colour, jobj);
    }

    @Override
    public void setOnChange(Runnable onchange) {
        name.setOnChange(onchange);
        location.setOnChange(onchange);
        windwardlaylines.setOnChange(onchange);
        downwindlaylines.setOnChange(onchange);
        laylinelength.setOnChange(onchange);
        laylinecolour.setOnChange(onchange);
        colour.setOnChange(onchange);
    }

    @Override
    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("name", name.toJson());
        job.add("location", location.toJson());
        job.add("windwardlaylines", windwardlaylines.toJson());
        job.add("downwindlaylines", downwindlaylines.toJson());
        job.add("laylinelength", laylinelength.toJson());
        job.add("laylinecolour", laylinecolour.toJson());
        job.add("colour", colour.toJson());
        return job.build();
    }

    public boolean hasName(String name) {
        return getName().equals(name);
    }

    public Mark get() {
        return this;
    }

    public String getName() {
        return name.get();
    }

    public PropertyString getNameProperty() {
        return name;
    }

    public PropertyLocation getLocation() {
        return location;
    }

    public boolean isWindwardlaylines() {
        return windwardlaylines.get();
    }

    public boolean isDownwindlaylines() {
        return downwindlaylines.get();
    }

    public double getLaylinelength() {
        return laylinelength.get();
    }

    public Color getLaylinecolour() {
        return laylinecolour.get();
    }

    public Color getColour() {
        return colour.get();
    }
}
