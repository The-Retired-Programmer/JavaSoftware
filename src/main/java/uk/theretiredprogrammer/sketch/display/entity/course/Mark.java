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
import javafx.beans.value.ChangeListener;
import uk.theretiredprogrammer.sketch.core.entity.Strg;
import uk.theretiredprogrammer.sketch.core.entity.Dble;
import uk.theretiredprogrammer.sketch.core.entity.Colour;
import uk.theretiredprogrammer.sketch.core.entity.Bool;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.Location;

public class Mark extends ModelMap {

    private final Strg name = new Strg("<newname>");
    private final Location location;
    private final Bool windwardlaylines = new Bool(false);
    private final Bool downwindlaylines = new Bool(false);
    private final Dble laylinelength = new Dble(0.0);
    private final Colour laylinecolour = new Colour(Color.BLACK);
    private final Colour colour = new Colour(Color.RED);

    public Mark() {
        location = new Location();
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

    public Mark(Location loc) {
        location = new Location(loc);
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
    
    public void addNameChangeListener(ChangeListener<String> listener) {
        name.addListener(listener);
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

    public Strg getNameProperty() {
        return name;
    }

    public Location getLocation() {
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
