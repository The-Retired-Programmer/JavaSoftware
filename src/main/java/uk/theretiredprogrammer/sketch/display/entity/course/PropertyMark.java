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
package uk.theretiredprogrammer.sketch.display.entity.course;

import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyNamed;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyMap;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConfig;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyConfig.MANDATORY;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyConfig.OPTIONAL;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyMark extends PropertyMap implements PropertyNamed {

    private final PropertyConfig<PropertyString, String> name = new PropertyConfig<>("name", MANDATORY, (s) -> new PropertyString(s, "<newname>"));
    private final PropertyConfig<PropertyLocation, Location> location;
    private final PropertyConfig<PropertyBoolean, Boolean> windwardlaylines = new PropertyConfig<>("windwardlaylines", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> downwindlaylines = new PropertyConfig<>("downwindlaylines", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyDouble, Double> laylinelength = new PropertyConfig<>("laylinelength", OPTIONAL, (s) -> new PropertyDouble(s, 0.0));
    private final PropertyConfig<PropertyColour, Color> laylinecolour = new PropertyConfig<>("laylinecolour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final PropertyConfig<PropertyColour, Color> colour = new PropertyConfig<>("colour", OPTIONAL, (s) -> new PropertyColour(s, Color.RED));

    public PropertyMark() {
        location = new PropertyConfig<>("location", OPTIONAL, (s) -> new PropertyLocation(s, LOCATIONZERO));
        this.addConfig(name, location, windwardlaylines, downwindlaylines, laylinelength, laylinecolour, colour);
    }

    public PropertyMark(Location loc) {
        location = new PropertyConfig<>("location", OPTIONAL, (s) -> new PropertyLocation(s, loc));
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
        return name.get(this, "PropertyMark name");
    }

    public PropertyString getNameProperty() {
        return name.getProperty(this, "PropertyMark name");
    }

    public Location getLocation() {
        return location.get(this, "PropertyMark location");
    }

    public boolean isWindwardlaylines() {
        return windwardlaylines.get(this, "PropertyMark windwardlaylines");
    }

    public boolean isDownwindlaylines() {
        return downwindlaylines.get(this, "PropertyMark downwindlaylines");
    }

    public double getLaylinelength() {
        return laylinelength.get(this, "PropertyMark laylinelength");
    }

    public Color getLaylinecolour() {
        return laylinecolour.get(this, "PropertyMark location");
    }

    public Color getColour() {
        return colour.get(this, "PropertyMark colour");
    }
}
