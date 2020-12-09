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
import uk.theretiredprogrammer.sketch.core.entity.Booln;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Intgr;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamed;

public class Mark extends ModelMap implements ModelNamed {

    private final Strg name;
    private final Location location;
    private final Booln windwardlaylines;
    private final Booln downwindlaylines;
    private final Dble laylinelength;
    private final Colour laylinecolour;
    private final Colour colour;
    private final Dble ladderspacing;
    private Intgr laddersteps;

    public Mark() {
        this(new Location());
    }

    public Mark(Location loc) {
        name = new Strg("<newname>");
        location = new Location(loc);
        windwardlaylines = new Booln(false);
        downwindlaylines = new Booln(false);
        laylinelength = new Dble(0.0);
        laylinecolour = new Colour(Color.BLACK);
        colour = new Colour(Color.RED);
        ladderspacing = new Dble(0.0);
        laddersteps = new Intgr(0);
        addProperty("name", name);
        addProperty("location", location);
        addProperty("windwardlaylines", windwardlaylines);
        addProperty("downwindlaylines", downwindlaylines);
        addProperty("laylinelength", laylinelength);
        addProperty("laylinecolour", laylinecolour);
        addProperty("colour", colour);
        addProperty("ladderspacing", ladderspacing);
        addProperty("laddersteps", laddersteps);
    }

    public Mark(Mark clonefrom) {
        name = new Strg(clonefrom.getNamed() + "-1");
        location = new Location(clonefrom.location);
        windwardlaylines = new Booln(clonefrom.windwardlaylines);
        downwindlaylines = new Booln(clonefrom.downwindlaylines);
        laylinelength = new Dble(clonefrom.laylinelength);
        laylinecolour = new Colour(clonefrom.laylinecolour);
        colour = new Colour(clonefrom.colour);
        ladderspacing = new Dble(clonefrom.ladderspacing);
        laddersteps = new Intgr(clonefrom.laddersteps);
        addProperty("name", name);
        addProperty("location", location);
        addProperty("windwardlaylines", windwardlaylines);
        addProperty("downwindlaylines", downwindlaylines);
        addProperty("laylinelength", laylinelength);
        addProperty("laylinecolour", laylinecolour);
        addProperty("colour", colour);
        addProperty("ladderspacing", ladderspacing);
        addProperty("laddersteps", laddersteps);
    }

    public Mark get() {
        return this;
    }

    @Override
    public String getNamed() {
        return name.get();
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
        parseOptionalProperty("ladderspacing", ladderspacing, jobj);
        parseOptionalProperty("laddersteps", laddersteps, jobj);
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
        ladderspacing.setOnChange(onchange);
        laddersteps.setOnChange(onchange);
    }

    public void addNameChangeListener(ChangeListener<String> childlistener) {
        name.addListener(childlistener);
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
        job.add("ladderspacing", ladderspacing.toJson());
        job.add("laddersteps", laddersteps.toJson());
        return job.build();
    }

    public boolean hasName(String name) {
        return getNamed().equals(name);
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

    public double getLadderspacing() {
        return ladderspacing.get();
    }

    public int getLaddersteps() {
        return laddersteps.get();
    }
}
