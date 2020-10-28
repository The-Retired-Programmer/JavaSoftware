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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Channel;
import static uk.theretiredprogrammer.sketch.core.entity.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;

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

    private final Config<PropertyString, String> name = new Config<>("name", MANDATORY, (s) -> new PropertyString(s, "<newname>"));
    private final Config<PropertyConstrainedString, String> type;
    private final Config<PropertyAngle, Angle> heading = new Config<PropertyAngle, Angle>("heading", OPTIONAL, (s) -> new PropertyAngle(s, ANGLE0));
    private final Config<PropertyLocation, Location> location;
    private final Config<PropertyColour, Color> colour = new Config<PropertyColour, Color>("colour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final Config<PropertyColour, Color> trackcolour = new Config<PropertyColour, Color>("trackcolour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final Config<PropertyBoolean, Boolean> upwindsailonbesttack = new Config<PropertyBoolean, Boolean>("upwindsailonbesttack", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> upwindtackifheaded = new Config<PropertyBoolean, Boolean>("upwindtackifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> upwindbearawayifheaded = new Config<PropertyBoolean, Boolean>("upwindbearawayifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> upwindluffupiflifted = new Config<PropertyBoolean, Boolean>("upwindluffupiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> reachdownwind = new Config<PropertyBoolean, Boolean>("reachdownwind", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> downwindsailonbestgybe = new Config<PropertyBoolean, Boolean>("downwindsailonbestgybe", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> downwindbearawayifheaded = new Config<PropertyBoolean, Boolean>("downwindbearawayifheaded", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> downwindgybeiflifted = new Config<PropertyBoolean, Boolean>("downwindgybeiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyBoolean, Boolean> downwindluffupiflifted = new Config<PropertyBoolean, Boolean>("downwindluffupiflifted", OPTIONAL, (s) -> new PropertyBoolean(s, false));

    public PropertyBoat() {
        this("laser2");
    }

    public PropertyBoat(String classtype) {
        location = new Config<>("location", OPTIONAL, (s) -> new PropertyLocation(s, LOCATIONZERO));
        type = new Config<>("type", MANDATORY, (s) -> new PropertyConstrainedString(s, classes, classtype));
        this.addConfig(name, type, heading, location, colour, trackcolour, upwindsailonbesttack,
                upwindtackifheaded, upwindbearawayifheaded, upwindluffupiflifted, reachdownwind,
                downwindsailonbestgybe, downwindbearawayifheaded, downwindgybeiflifted, downwindluffupiflifted
        );
    }

    public PropertyBoat(Location loc) {
        this("laser2", loc);
    }

    public PropertyBoat(String classtype, Location loc) {
        location = new Config<>("location", OPTIONAL, (s) -> new PropertyLocation(s, loc));
        type = new Config<>("type", MANDATORY, (s) -> new PropertyConstrainedString(s, classes, classtype));
        this.addConfig(name, type, heading, location, colour, trackcolour, upwindsailonbesttack,
                upwindtackifheaded, upwindbearawayifheaded, upwindluffupiflifted, reachdownwind,
                downwindsailonbestgybe, downwindbearawayifheaded, downwindgybeiflifted, downwindluffupiflifted
        );
    }

    @Override
    public boolean hasName(String name) {
        return getName().equals(name);
    }

    @Override
    public PropertyBoat get() {
        return this;
    }

    public String getName() {
        return name.get("PropertyBoat name");
    }

    public PropertyString getNameProperty() {
        return name.getProperty("PropertyBoat name");
    }

    public String getType() {
        return type.get("PropertyBoat type");
    }

    public Angle getDirection() {
        return heading.get("PropertyBoat heading");
    }

    public void setDirection(Angle newdirection) {
        heading.getProperty("PropertyBoat heading").set(newdirection);
    }

    public Location getLocation() {
        return location.get("PropertyBoat location");
    }

    public void setLocation(Location newlocation) {
        location.getProperty("PropertyBoat location").set(newlocation);
    }

    public Color getColour() {
        return colour.get("PropertyBoat colour");
    }

    public Color getTrackcolour() {
        return trackcolour.get("PropertyBoat trackcolour");
    }

    public boolean isUpwindsailonbesttack() {
        return upwindsailonbesttack.get("PropertyBoat upwindsailonbesttackr");
    }

    public boolean isUpwindtackifheaded() {
        return upwindtackifheaded.get("PropertyBoat upwindtackifheaded");
    }

    public boolean isUpwindbearawayifheaded() {
        return upwindbearawayifheaded.get("PropertyBoat upwindbearawayifheadedr");
    }

    public boolean isUpwindluffupiflifted() {
        return upwindluffupiflifted.get("PropertyBoat upwindluffupiflifted");
    }

    public boolean isReachdownwind() {
        return reachdownwind.get("PropertyBoat reachdownwind");
    }

    public boolean isDownwindsailonbestgybe() {
        return downwindsailonbestgybe.get("PropertyBoat downwindsailonbestgybe");
    }

    public boolean isDownwindbearawayifheaded() {
        return downwindbearawayifheaded.get("PropertyBoat downwindbearawayifheaded");
    }

    public boolean isDownwindgybeiflifted() {
        return downwindgybeiflifted.get("PropertyBoat downwindgybeiflifted");
    }

    public boolean isDownwindluffupiflifted() {
        return downwindluffupiflifted.get("PropertyBoat downwindluffupiflifted");
    }
}
