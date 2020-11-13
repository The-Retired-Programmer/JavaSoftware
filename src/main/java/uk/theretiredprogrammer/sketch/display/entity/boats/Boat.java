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
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Channel;
import static uk.theretiredprogrammer.sketch.core.entity.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.entity.PropertyColour;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;
import uk.theretiredprogrammer.sketch.display.control.strategy.Strategy;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.control.strategy.Decision;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.display.control.strategy.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;

public abstract class Boat extends ModelMap {

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
    private final PropertyDegrees heading = new PropertyDegrees(0);
    private final PropertyLocation location;
    private final PropertyColour colour = new PropertyColour(Color.BLACK);
    private final PropertyColour trackcolour = new PropertyColour(Color.BLACK);
    private final PropertyBoolean upwindsailonbesttack = new PropertyBoolean(false);
    private final PropertyBoolean upwindtackifheaded = new PropertyBoolean(false);
    private final PropertyBoolean upwindbearawayifheaded = new PropertyBoolean(false);
    private final PropertyBoolean upwindluffupiflifted = new PropertyBoolean(false);
    private final PropertyBoolean reachdownwind = new PropertyBoolean(false);
    private final PropertyBoolean downwindsailonbestgybe = new PropertyBoolean(false);
    private final PropertyBoolean downwindbearawayifheaded = new PropertyBoolean(false);
    private final PropertyBoolean downwindgybeiflifted = new PropertyBoolean(false);
    private final PropertyBoolean downwindluffupiflifted = new PropertyBoolean(false);
    public final Color sailcolor = Color.WHITE;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private PropertyDegrees rotationAnglePerSecond;
    private final List<PropertyLocation> track = Collections.synchronizedList(new ArrayList<PropertyLocation>());
    private Strategy strategy; // current leg strategy

    public Boat(Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", "laser2", new PropertyLocation(), firstleg, windflow, waterflow, metrics);
    }

    public Boat(String classtype, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, new PropertyLocation(), firstleg, windflow, waterflow, metrics);
    }

    public Boat(PropertyLocation loc, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", "laser2", loc, firstleg, windflow, waterflow, metrics);
    }

    public Boat(String classtype, PropertyLocation loc, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, loc, firstleg, windflow, waterflow, metrics);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Boat(String newname, String classtype, PropertyLocation loc, Leg firstleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        name = new PropertyString(newname);
        location = new PropertyLocation(loc);
        type = new PropertyConstrainedString(classtype, classes);
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
        name = new PropertyString(newname);
        location = new PropertyLocation(clonefrom.location);
        type = new PropertyConstrainedString(clonefrom.type.get(), classes);
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

    public final PropertyDegrees getDirection() {
        return heading;
    }

    public final void setDirection(PropertyDegrees newdirection) {
        heading.set(newdirection);
    }

    public final PropertyLocation getLocation() {
        return location;
    }

    public final void setLocation(PropertyLocation newlocation) {
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

    public boolean isPort(PropertyDegrees winddirection) {
        return heading.gteq(winddirection);
    }

    public PropertyDegrees getStarboardCloseHauledCourse(PropertyDegrees winddirection) {
        return winddirection.sub(metrics.upwindrelative);
    }

    public PropertyDegrees getPortCloseHauledCourse(PropertyDegrees winddirection) {
        return winddirection.plus(metrics.upwindrelative);
    }

    public PropertyDegrees getStarboardReachingCourse(PropertyDegrees winddirection) {
        return winddirection.sub(metrics.downwindrelative);
    }

    public PropertyDegrees getPortReachingCourse(PropertyDegrees winddirection) {
        return winddirection.plus(metrics.downwindrelative);
    }

    public boolean isPortRear90Quadrant(PropertyLocation location) {
        return isQuadrant(location, heading.inverse(), heading.sub(90));
    }

    public boolean isStarboardRear90Quadrant(PropertyLocation location) {
        return isQuadrant(location, heading.plus(90), heading.inverse());
    }

    public boolean isPortTackingQuadrant(PropertyLocation location, PropertyDegrees winddirection) {
        return isQuadrant(location, heading.inverse(), getStarboardCloseHauledCourse(winddirection));
    }

    public boolean isStarboardTackingQuadrant(PropertyLocation location, PropertyDegrees winddirection) {
        return isQuadrant(location, getPortCloseHauledCourse(winddirection), getDirection().inverse());
    }

    public boolean isPortGybingQuadrant(PropertyLocation location, PropertyDegrees winddirection) {
        return isQuadrant(location, getDirection().inverse(), getPortReachingCourse(winddirection));
    }

    public boolean isStarboardGybingQuadrant(PropertyLocation location, PropertyDegrees winddirection) {
        return isQuadrant(location, getStarboardReachingCourse(winddirection), getDirection().inverse());
    }

    private boolean isQuadrant(PropertyLocation location, PropertyDegrees min, PropertyDegrees max) {
        return getLocation().angleto(location).between(min, max);
    }

    public boolean moveUsingDecision(WindFlow windflow, WaterFlow waterflow, Decision decision) {
        PropertySpeedVector windpolar = windflow.getFlow(getLocation());
        PropertySpeedVector waterpolar = waterflow.getFlow(getLocation());
        switch (decision.getAction()) {
            case SAILON -> {
                moveBoat(heading, windpolar, waterpolar);
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

    private boolean turn(Decision decision, PropertySpeedVector windflow, PropertySpeedVector waterflow) {
        PropertyDegrees newdirection = decision.getDegreesProperty();
        if (heading.absDegreesDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON();
            return true;
        }
        moveBoat(getDirection().plus(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
        return false;
    }

    /**
     * Move the boat in the required directionproperty.
     *
     * @param nextdirection the required directionproperty
     */
    void moveBoat(PropertyDegrees nextdirection, PropertySpeedVector windspeedvector, PropertySpeedVector waterspeedvector) {
        // calculate the potential boat speed - based on wind speed and relative angle 
        double potentialBoatspeed = PropertySpeedVector.convertKnots2MetresPerSecond(
                metrics.getPotentialBoatSpeed(nextdirection.absDegreesDiff(windspeedvector.getDegreesProperty()),
                        windspeedvector.getSpeed()));
        boatspeed += metrics.getInertia() * (potentialBoatspeed - boatspeed);
        // start by calculating the vector components of the boats movement
        PropertyDistanceVector move = new PropertyDistanceVector(boatspeed, nextdirection)
                .sub(new PropertyDistanceVector(waterspeedvector.getSpeedMetresPerSecond(), waterspeedvector.getDegreesProperty()));
        setLocation(move.toLocation(getLocation())); // updated position calculated
        track.add(getLocation()); // record it in track
        setDirection(nextdirection); // and update the directionproperty
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }
}
