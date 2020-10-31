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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Channel;
import static uk.theretiredprogrammer.sketch.core.entity.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.PropertyAngle;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyNamed;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.PropertyMap;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyMap.PropertyConfig.MANDATORY;
import static uk.theretiredprogrammer.sketch.core.entity.PropertyMap.PropertyConfig.OPTIONAL;

/*
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyBoat extends PropertyMap implements PropertyNamed {

    private static final ObservableList<String> classes;

    static {
        classes = FXCollections.observableArrayList();
        classes.addAll("laser2");
    }

    public static ObservableList<String> getClasses() {
        return classes;
    }

    public final Channel upwindchannel = CHANNELOFF;
    public final Channel downwindchannel = CHANNELOFF;

    private final PropertyConfig<PropertyString, String> name;
    private final PropertyConfig<PropertyConstrainedString, String> type;
    private final PropertyConfig<PropertyAngle, Angle> heading = new PropertyConfig<PropertyAngle, Angle>("heading", OPTIONAL, (s) -> new PropertyAngle(s, ANGLE0));
    private final PropertyConfig<PropertyLocation, Location> location;
    private final PropertyConfig<PropertyColour, Color> colour = new PropertyConfig<PropertyColour, Color>("colour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final PropertyConfig<PropertyColour, Color> trackcolour = new PropertyConfig<PropertyColour, Color>("trackcolour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final PropertyConfig<PropertyBoolean, Boolean> upwindsailonbesttack = new PropertyConfig<PropertyBoolean, Boolean>("upwindsailonbesttack", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> upwindtackifheaded = new PropertyConfig<PropertyBoolean, Boolean>("upwindtackifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> upwindbearawayifheaded = new PropertyConfig<PropertyBoolean, Boolean>("upwindbearawayifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> upwindluffupiflifted = new PropertyConfig<PropertyBoolean, Boolean>("upwindluffupiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> reachdownwind = new PropertyConfig<PropertyBoolean, Boolean>("reachdownwind", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> downwindsailonbestgybe = new PropertyConfig<PropertyBoolean, Boolean>("downwindsailonbestgybe", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> downwindbearawayifheaded = new PropertyConfig<PropertyBoolean, Boolean>("downwindbearawayifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> downwindgybeiflifted = new PropertyConfig<PropertyBoolean, Boolean>("downwindgybeiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final PropertyConfig<PropertyBoolean, Boolean> downwindluffupiflifted = new PropertyConfig<PropertyBoolean, Boolean>("downwindluffupiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));

    public PropertyBoat() {
        this("<newname>", "laser2", LOCATIONZERO);
    }

    public PropertyBoat(String classtype) {
        this("<newname>", classtype, LOCATIONZERO);
    }

    public PropertyBoat(Location loc) {
        this("<newname>", "laser2", loc);
    }

    public PropertyBoat(String newname, String classtype, Location loc) {
        name = new PropertyConfig<>("name", MANDATORY, (s) -> new PropertyString(s, newname));
        location = new PropertyConfig<>("location", OPTIONAL, (s) -> new PropertyLocation(s, loc));
        type = new PropertyConfig<>("type", MANDATORY, (s) -> new PropertyConstrainedString(s, classes, classtype));
        this.addConfig(name, type, heading, location, colour, trackcolour, upwindsailonbesttack,
                upwindtackifheaded, upwindbearawayifheaded, upwindluffupiflifted, reachdownwind,
                downwindsailonbestgybe, downwindbearawayifheaded, downwindgybeiflifted, downwindluffupiflifted
        );
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public PropertyBoat(String name, PropertyBoat clonefrom) {
        this(name, clonefrom.getType(), clonefrom.getLocation());
        this.heading.getProperty("PropertyBoat heading").set(clonefrom.heading.get("PropertyBoat heading"));
        this.colour.getProperty("PropertyBoat colour").set(clonefrom.colour.get("PropertyBoat colour"));
        this.trackcolour.getProperty("PropertyBoat trackcolour").set(clonefrom.trackcolour.get("PropertyBoat trackcolour"));
        this.upwindsailonbesttack.getProperty("PropertyBoat upwindsailonbesttack").set(clonefrom.upwindsailonbesttack.get("PropertyBoat upwindsailonbesttack"));
        this.upwindtackifheaded.getProperty("PropertyBoat upwindtackifheaded").set(clonefrom.upwindtackifheaded.get("PropertyBoat upwindtackifheaded"));
        this.upwindbearawayifheaded.getProperty("PropertyBoat upwindbearawayifheaded").set(clonefrom.upwindbearawayifheaded.get("PropertyBoat upwindbearawayifheaded"));
        this.upwindluffupiflifted.getProperty("PropertyBoat upwindluffupiflifted").set(clonefrom.upwindluffupiflifted.get("PropertyBoat upwindluffupiflifted"));
        this.reachdownwind.getProperty("PropertyBoat lreachdownwind").set(clonefrom.reachdownwind.get("PropertyBoat reachdownwind"));
        this.downwindsailonbestgybe.getProperty("PropertyBoat downwindsailonbestgybe").set(clonefrom.downwindsailonbestgybe.get("PropertyBoat downwindsailonbestgybe"));
        this.downwindbearawayifheaded.getProperty("PropertyBoat downwindbearawayifheaded").set(clonefrom.downwindbearawayifheaded.get("PropertyBoat downwindbearawayifheaded"));
        this.downwindgybeiflifted.getProperty("PropertyBoat downwindgybeiflifted").set(clonefrom.downwindgybeiflifted.get("PropertyBoat downwindgybeiflifted"));
        this.downwindluffupiflifted.getProperty("PropertyBoat downwindluffupiflifted").set(clonefrom.downwindluffupiflifted.get("PropertyBoat downwindluffupiflifted"));
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public PropertyBoat get() {
        return this;
    }

    public final String getName() {
        return name.get("PropertyBoat name");
    }

    public final PropertyString getNameProperty() {
        return name.getProperty("PropertyBoat name");
    }

    public final String getType() {
        return type.get("PropertyBoat type");
    }

    public final Angle getDirection() {
        return heading.get("PropertyBoat heading");
    }

    public final void setDirection(Angle newdirection) {
        heading.getProperty("PropertyBoat heading").set(newdirection);
    }

    public final Location getLocation() {
        return location.get("PropertyBoat location");
    }

    public final void setLocation(Location newlocation) {
        location.getProperty("PropertyBoat location").set(newlocation);
    }

    public final Color getColour() {
        return colour.get("PropertyBoat colour");
    }

    public final Color getTrackcolour() {
        return trackcolour.get("PropertyBoat trackcolour");
    }

    public final boolean isUpwindsailonbesttack() {
        return upwindsailonbesttack.get("PropertyBoat upwindsailonbesttackr");
    }

    public final boolean isUpwindtackifheaded() {
        return upwindtackifheaded.get("PropertyBoat upwindtackifheaded");
    }

    public final boolean isUpwindbearawayifheaded() {
        return upwindbearawayifheaded.get("PropertyBoat upwindbearawayifheadedr");
    }

    public final boolean isUpwindluffupiflifted() {
        return upwindluffupiflifted.get("PropertyBoat upwindluffupiflifted");
    }

    public final boolean isReachdownwind() {
        return reachdownwind.get("PropertyBoat reachdownwind");
    }

    public final boolean isDownwindsailonbestgybe() {
        return downwindsailonbestgybe.get("PropertyBoat downwindsailonbestgybe");
    }

    public final boolean isDownwindbearawayifheaded() {
        return downwindbearawayifheaded.get("PropertyBoat downwindbearawayifheaded");
    }

    public final boolean isDownwindgybeiflifted() {
        return downwindgybeiflifted.get("PropertyBoat downwindgybeiflifted");
    }

    public final boolean isDownwindluffupiflifted() {
        return downwindluffupiflifted.get("PropertyBoat downwindluffupiflifted");
    }
}
