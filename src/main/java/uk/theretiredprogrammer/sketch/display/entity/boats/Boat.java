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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE90;
import uk.theretiredprogrammer.sketch.core.entity.DistancePolar;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Channel;
import static uk.theretiredprogrammer.sketch.core.entity.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;
import uk.theretiredprogrammer.sketch.core.entity.PropertyAngle;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.display.control.strategy.Strategy;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;

public abstract class Boat extends ModelProperties {

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

    private final PropertyString name;
    private final PropertyConstrainedString type;
    private final PropertyAngle heading = new PropertyAngle("heading", ANGLE0);
    private final PropertyLocation location;
    private final PropertyColour colour = new PropertyColour("colour", Color.BLACK);
    private final PropertyColour trackcolour = new PropertyColour("trackcolour", Color.BLACK);
    private final PropertyBoolean upwindsailonbesttack = new PropertyBoolean("upwindsailonbesttack", false);
    private final PropertyBoolean upwindtackifheaded = new PropertyBoolean("upwindtackifheaded", false);
    private final PropertyBoolean upwindbearawayifheaded = new PropertyBoolean("upwindbearawayifheaded", false);
    private final PropertyBoolean upwindluffupiflifted = new PropertyBoolean("upwindluffupiflifted", false);
    private final PropertyBoolean reachdownwind = new PropertyBoolean("reachdownwind", false);
    private final PropertyBoolean downwindsailonbestgybe = new PropertyBoolean("downwindsailonbestgybe", false);
    private final PropertyBoolean downwindbearawayifheaded = new PropertyBoolean("downwindbearawayifheaded", false);
    private final PropertyBoolean downwindgybeiflifted = new PropertyBoolean("downwindgybeiflifted", false);
    private final PropertyBoolean downwindluffupiflifted = new PropertyBoolean("downwindluffupiflifted", false);
    public final Color sailcolor = Color.WHITE;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());
    private Strategy strategy; // current leg strategy

    public Boat(Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", "laser2", LOCATIONZERO, firstleg, windflow, waterflow, metrics);
    }

    public Boat(String classtype, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, LOCATIONZERO, firstleg, windflow, waterflow, metrics);
    }

    public Boat(Location loc, Leg firstleg,WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", "laser2", loc, firstleg, windflow, waterflow, metrics);
    }
    
    public Boat(String classtype, Location loc, Leg firstleg,WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, loc, firstleg, windflow, waterflow, metrics);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Boat(String newname, String classtype, Location loc, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        name = new PropertyString("name", newname);
        location = new PropertyLocation("location", loc);
        type = new PropertyConstrainedString("type", classes, classtype);
        this.metrics = metrics;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
        this.strategy = Strategy.get(this, firstleg, windflow, waterflow);
        addProperty("name", name);
        addProperty("type", type);
        addProperty("heading", heading);
        addProperty("location", location);
        addProperty("colour", colour);
        addProperty("trackcolour", trackcolour);
        addProperty("upwindsailonbesttack", upwindsailonbesttack);
        addProperty("upwindtackifheaded", upwindtackifheaded);
        addProperty("upwindbearawayifheaded", upwindbearawayifheaded);
        addProperty("upwindluffupiflifted", upwindluffupiflifted);
        addProperty("reachdownwind", reachdownwind);
        addProperty("downwindsailonbestgybe", downwindsailonbestgybe);
        addProperty("downwindbearawayifheaded", downwindbearawayifheaded);
        addProperty("downwindgybeiflifted", downwindgybeiflifted);
        addProperty("downwindluffupiflifted", downwindluffupiflifted);
    }

    public Boat(String newname, Boat clonefrom) {
        name = new PropertyString("name", newname);
        location = new PropertyLocation("location", clonefrom.location.get());
        type = new PropertyConstrainedString("type", classes, clonefrom.type.get());
        metrics = clonefrom.metrics;
        rotationAnglePerSecond = clonefrom.rotationAnglePerSecond;
        strategy = clonefrom.strategy;
        this.boatspeed = clonefrom.boatspeed;
        this.heading.set(clonefrom.heading.get());
        this.colour.set(clonefrom.colour.get());
        this.trackcolour.set(clonefrom.trackcolour.get());
        this.upwindsailonbesttack.set(clonefrom.upwindsailonbesttack.get());
        this.upwindtackifheaded.set(clonefrom.upwindtackifheaded.get());
        this.upwindbearawayifheaded.set(clonefrom.upwindbearawayifheaded.get());
        this.upwindluffupiflifted.set(clonefrom.upwindluffupiflifted.get());
        this.reachdownwind.set(clonefrom.reachdownwind.get());
        this.downwindsailonbestgybe.set(clonefrom.downwindsailonbestgybe.get());
        this.downwindbearawayifheaded.set(clonefrom.downwindbearawayifheaded.get());
        this.downwindgybeiflifted.set(clonefrom.downwindgybeiflifted.get());
        this.downwindluffupiflifted.set(clonefrom.downwindluffupiflifted.get());
    }
    
    @Override
    protected void parseValues(JsonObject jobj) {
        parseMandatoryProperty("name", name, jobj);
        parseMandatoryProperty("type", type, jobj);
        parseOptionalProperty("heading", heading, jobj);
        parseOptionalProperty("location", location, jobj);
        parseOptionalProperty("colour", colour, jobj);
        parseOptionalProperty("trackcolour", trackcolour, jobj);
        parseOptionalProperty("upwindsailonbesttack", upwindsailonbesttack, jobj);
        parseOptionalProperty("upwindtackifheaded", upwindtackifheaded, jobj);
        parseOptionalProperty("upwindbearawayifheaded", upwindbearawayifheaded, jobj);
        parseOptionalProperty("upwindluffupiflifted", upwindluffupiflifted, jobj);
        parseOptionalProperty("reachdownwind", reachdownwind, jobj);
        parseOptionalProperty("downwindsailonbestgybe", downwindsailonbestgybe, jobj);
        parseOptionalProperty("downwindbearawayifheaded", downwindbearawayifheaded, jobj);
        parseOptionalProperty("downwindgybeiflifted", downwindgybeiflifted, jobj);
        parseOptionalProperty("downwindluffupiflifted", downwindluffupiflifted, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("name", name.toJson());
        job.add("type", type.toJson());
        job.add("heading", heading.toJson());
        job.add("location", location.toJson());
        job.add("colour", colour.toJson());
        job.add("trackcolour", trackcolour.toJson());
        job.add("upwindsailonbesttack", upwindsailonbesttack.toJson());
        job.add("upwindtackifheaded", upwindtackifheaded.toJson());
        job.add("upwindbearawayifheaded", upwindbearawayifheaded.toJson());
        job.add("upwindluffupiflifted", upwindluffupiflifted.toJson());
        job.add("reachdownwind", reachdownwind.toJson());
        job.add("downwindsailonbestgybe", downwindsailonbestgybe.toJson());
        job.add("downwindbearawayifheaded", downwindbearawayifheaded.toJson());
        job.add("downwindgybeiflifted", downwindgybeiflifted.toJson());
        job.add("downwindluffupiflifted", downwindluffupiflifted.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        name.setOnChange(onchange);
        type.setOnChange(onchange);
        heading.setOnChange(onchange);
        location.setOnChange(onchange);
        colour.setOnChange(onchange);
        trackcolour.setOnChange(onchange);
        upwindsailonbesttack.setOnChange(onchange);
        upwindtackifheaded.setOnChange(onchange);
        upwindbearawayifheaded.setOnChange(onchange);
        upwindluffupiflifted.setOnChange(onchange);
        reachdownwind.setOnChange(onchange);
        downwindsailonbestgybe.setOnChange(onchange);
        downwindbearawayifheaded.setOnChange(onchange);
        downwindgybeiflifted.setOnChange(onchange);
        downwindluffupiflifted.setOnChange(onchange);
    }
    
    public Boat get() {
        return this;
    }

    public final String getName() {
        return name.get();
    }

    public final PropertyString getNameProperty() {
        return name;
    }

    public final String getType() {
        return type.get();
    }

    public final Angle getDirection() {
        return heading.get();
    }

    public final void setDirection(Angle newdirection) {
        heading.set(newdirection);
    }

    public final Location getLocation() {
        return location.get();
    }

    public final void setLocation(Location newlocation) {
        location.set(newlocation);
    }

    public final Color getColour() {
        return colour.get();
    }

    public final Color getTrackcolour() {
        return trackcolour.get();
    }

    public final boolean isUpwindsailonbesttack() {
        return upwindsailonbesttack.get();
    }

    public final boolean isUpwindtackifheaded() {
        return upwindtackifheaded.get();
    }

    public final boolean isUpwindbearawayifheaded() {
        return upwindbearawayifheaded.get();
    }

    public final boolean isUpwindluffupiflifted() {
        return upwindluffupiflifted.get();
    }

    public final boolean isReachdownwind() {
        return reachdownwind.get();
    }

    public final boolean isDownwindsailonbestgybe() {
        return downwindsailonbestgybe.get();
    }

    public final boolean isDownwindbearawayifheaded() {
        return downwindbearawayifheaded.get();
    }

    public final boolean isDownwindgybeiflifted() {
        return downwindgybeiflifted.get();
    }

    public final boolean isDownwindluffupiflifted() {
        return downwindluffupiflifted.get();
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public boolean isPort(Angle winddirection) {
        return getDirection().gteq(winddirection);
    }

    public Angle getStarboardCloseHauledCourse(Angle winddirection) {
        return winddirection.sub(metrics.upwindrelative);
    }

    public Angle getPortCloseHauledCourse(Angle winddirection) {
        return winddirection.add(metrics.upwindrelative);
    }

    public Angle getStarboardReachingCourse(Angle winddirection) {
        return winddirection.sub(metrics.downwindrelative);
    }

    public Angle getPortReachingCourse(Angle winddirection) {
        return winddirection.add(metrics.downwindrelative);
    }

    public boolean isPortRear90Quadrant(Location location) {
        return isQuadrant(location, getDirection().inverse(), getDirection().sub(ANGLE90));
    }

    public boolean isStarboardRear90Quadrant(Location location) {
        return isQuadrant(location, getDirection().add(ANGLE90), getDirection().inverse());
    }

    public boolean isPortTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getDirection().inverse(), getStarboardCloseHauledCourse(winddirection));
    }

    public boolean isStarboardTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getPortCloseHauledCourse(winddirection), getDirection().inverse());
    }

    public boolean isPortGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getDirection().inverse(), getPortReachingCourse(winddirection));
    }

    public boolean isStarboardGybingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, getStarboardReachingCourse(winddirection), getDirection().inverse());
    }

    private boolean isQuadrant(Location location, Angle min, Angle max) {
        return getLocation().angleto(location).between(min, max);
    }

    public boolean moveUsingDecision(WindFlow windflow, WaterFlow waterflow, Decision decision) {
        SpeedPolar windpolar = windflow.getFlow(getLocation());
        SpeedPolar waterpolar = waterflow.getFlow(getLocation());
        switch (decision.getAction()) {
            case SAILON -> {
                moveBoat(getDirection(), windpolar, waterpolar);
                return false;
            }
            case STOP -> {
                return false;
            }
            case MARKROUNDING -> {
                return turn(decision, windpolar, waterpolar);
            }
            case TURN -> {
                turn(decision, windpolar, waterpolar);
                return false;
            }
            default ->
                throw new IllegalStateFailure("Illegal sailing Action when moving boat");
        }
    }

    private boolean turn(Decision decision, SpeedPolar windflow, SpeedPolar waterflow) {
        Angle newdirection = decision.getAngle();
        if (getDirection().absAngleDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON();
            return true;
        }
        moveBoat(getDirection().add(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
        return false;
    }

    /**
     * Move the boat in the required directionproperty.
     *
     * @param nextdirection the required directionproperty
     */
    void moveBoat(Angle nextdirection, SpeedPolar windflow, SpeedPolar waterflow) {
        // calculate the potential boat speed - based on wind speed and relative angle 
        double potentialBoatspeed = SpeedPolar.convertKnots2MetresPerSecond(
                metrics.getPotentialBoatSpeed(nextdirection.absAngleDiff(windflow.getAngle()),
                        windflow.getSpeed()));
        boatspeed += metrics.getInertia() * (potentialBoatspeed - boatspeed);
        // start by calculating the vector components of the boats movement
        DistancePolar move = new DistancePolar(boatspeed, nextdirection)
                .subtract(new DistancePolar(waterflow.getSpeedMetresPerSecond(), waterflow.getAngle()));
        setLocation(move.polar2Location(getLocation())); // updated position calculated
        track.add(getLocation()); // record it in track
        setDirection(nextdirection); // and update the directionproperty
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }
}
