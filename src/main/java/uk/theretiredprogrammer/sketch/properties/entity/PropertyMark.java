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
package uk.theretiredprogrammer.sketch.properties.entity;

import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyMark extends PropertyMap implements PropertyNamed {

    private final Config<PropertyString, String> name = new Config<>("name", MANDATORY, (s) -> new PropertyString(s, "<newname>"));
    private final Config<PropertyLocation, Location> location = new Config<>("location", OPTIONAL, (s) -> new PropertyLocation(s, LOCATIONZERO));
    private final Config<PropertyBoolean, Boolean> windwardlaylines = new Config<>("windwardlaylines", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> downwindlaylines = new Config<>("downwindlaylines", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyDouble, Double> laylinelength = new Config<>("laylinelength", OPTIONAL, (s) -> new PropertyDouble(s, 0.0));
    private final Config<PropertyColour, Color> laylinecolour = new Config<>("laylinecolour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final Config<PropertyColour, Color> colour = new Config<>("colour", OPTIONAL, (s) -> new PropertyColour(s, Color.RED));

    public PropertyMark() {
        this.addConfig(name, location, windwardlaylines, downwindlaylines, laylinelength, laylinecolour, colour);
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public PropertyMark get() {
        return this;
    }

    public String getName() {
        return name.get("PropertyMark name");
    }

    public PropertyString getNameProperty() {
        return name.getProperty("PropertyMark name");
    }

    public Location getLocation() {
        return location.get("PropertyMark location");
    }

    public boolean isWindwardlaylines() {
        return windwardlaylines.get("PropertyMark windwardlaylines");
    }

    public boolean isDownwindlaylines() {
        return downwindlaylines.get("PropertyMark downwindlaylines");
    }

    public double getLaylinelength() {
        return laylinelength.get("PropertyMark laylinelength");
    }

    public Color getLaylinecolour() {
        return laylinecolour.get("PropertyMark location");
    }

    public Color getColour() {
        return colour.get("PropertyMark colour");
    }
}
