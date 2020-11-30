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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Channel;
import static uk.theretiredprogrammer.sketch.core.entity.Channel.CHANNELOFF;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.Booln;
import uk.theretiredprogrammer.sketch.core.entity.Colour;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamed;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.core.entity.Strg;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.MARKROUNDING;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction.SAILON;
import uk.theretiredprogrammer.sketch.display.entity.course.CurrentLeg;
import uk.theretiredprogrammer.sketch.display.entity.course.Params;
import uk.theretiredprogrammer.sketch.log.control.LogController;

public abstract class Boat extends ModelMap implements ModelNamed {

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

    private final Strg name;
    private final ConstrainedString type;
    private final Angle heading = new Angle(0);
    private final Location location;
    private final Colour colour = new Colour(Color.BLACK);
    private final Colour trackcolour = new Colour(Color.BLACK);
    private final Booln upwindsailonbesttack = new Booln(false);
    private final Booln upwindtackifheaded = new Booln(false);
    private final Booln upwindbearawayifheaded = new Booln(false);
    private final Booln upwindluffupiflifted = new Booln(false);
    private final Booln reachdownwind = new Booln(false);
    private final Booln downwindsailonbestgybe = new Booln(false);
    private final Booln downwindbearawayifheaded = new Booln(false);
    private final Booln downwindgybeiflifted = new Booln(false);
    private final Booln downwindluffupiflifted = new Booln(false);
    public final Color sailcolor = Color.WHITE;
    public final BoatMetrics metrics;
    //
    private double boatspeed = 0;
    private Angle rotationAnglePerSecond;
    private final List<Location> track = Collections.synchronizedList(new ArrayList<Location>());

    private final CurrentLeg currentleg;

    public Boat(String classtype, CurrentLeg currentleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, new Location(), currentleg, windflow, waterflow, metrics);
    }

    public Boat(String classtype, Location loc, CurrentLeg currentleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        this("<newname>", classtype, loc, currentleg, windflow, waterflow, metrics);
    }

    public Boat(String newname, String classtype, Location loc, CurrentLeg currentleg, WindFlow windflow, WaterFlow waterflow, BoatMetrics metrics) {
        name = new Strg(newname);
        location = new Location(loc);
        type = new ConstrainedString(classtype, classes);
        this.metrics = metrics;
        this.rotationAnglePerSecond = metrics.getMaxTurningAnglePerSecond().div(2);
        this.currentleg = currentleg;
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
        name = new Strg(newname);
        location = new Location(clonefrom.location);
        type = new ConstrainedString(clonefrom.type.get(), classes);
        metrics = clonefrom.metrics;
        rotationAnglePerSecond = clonefrom.rotationAnglePerSecond;
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
        this.currentleg = new CurrentLeg(clonefrom.currentleg);
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

    public final CurrentLeg getCurrentLeg() {
        return currentleg;
    }

    @Override
    public final String getNamed() {
        return name.get();
    }

    public final Strg getNameProperty() {
        return name;
    }

    public final String getType() {
        return type.get();
    }

    public final Angle getDirection() {
        return heading;
    }

    public final void setDirection(Angle newdirection) {
        heading.set(newdirection);
    }

    public final Location getLocation() {
        return location;
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

    public boolean isPort(Angle winddirection) {
        return heading.gteq(winddirection);
    }

    public Angle getStarboardCloseHauledCourse(Angle winddirection) {
        return winddirection.sub(metrics.upwindrelative);
    }

    public Angle getPortCloseHauledCourse(Angle winddirection) {
        return winddirection.plus(metrics.upwindrelative);
    }

    public Angle getStarboardReachingCourse(Angle winddirection) {
        return winddirection.sub(metrics.downwindrelative);
    }

    public Angle getPortReachingCourse(Angle winddirection) {
        return winddirection.plus(metrics.downwindrelative);
    }

    public boolean isPortRear90Quadrant(Location location) {
        return isQuadrant(location, heading.inverse(), heading.sub(90));
    }

    public boolean isStarboardRear90Quadrant(Location location) {
        return isQuadrant(location, heading.plus(90), heading.inverse());
    }

    public boolean isPortTackingQuadrant(Location location, Angle winddirection) {
        return isQuadrant(location, heading.inverse(), getStarboardCloseHauledCourse(winddirection));
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

    public void tick(SketchModel model, int simulationtime, LogController logcontroller) {
        currentleg.tick(new Params(model, this), simulationtime, logcontroller);
    }

    public boolean moveUsingDecision(Params params) {
        SpeedVector windpolar = params.windflow.getFlow(getLocation());
        SpeedVector waterpolar = params.waterflow.getFlow(getLocation());
        switch (params.decision.getAction()) {
            case SAILON -> {
                moveBoat(heading, windpolar, waterpolar);
                return false;
            }
            case STOP -> {
                return false;
            }
            case MARKROUNDING -> {
                return turn(params.decision, windpolar, waterpolar);
            }
            case TURN -> {
                turn(params.decision, windpolar, waterpolar);
                return false;
            }
            default ->
                throw new IllegalStateFailure("Illegal sailing Action when moving boat");
        }
    }

    private boolean turn(Decision decision, SpeedVector windflow, SpeedVector waterflow) {
        Angle newdirection = currentleg.getDecision().getDegreesProperty();
        if (heading.absDegreesDiff(newdirection).lteq(rotationAnglePerSecond)) {
            moveBoat(newdirection, windflow, waterflow);
            decision.setSAILON(heading);
            return true;
        }
        moveBoat(getDirection().plus(rotationAnglePerSecond.negateif(decision.isPort())), windflow, waterflow);
        return false;
    }

    void moveBoat(Angle nextdirection, SpeedVector windspeedvector, SpeedVector waterspeedvector) {
        // calculate the potential boat speed - based on wind speed and relative angle 
        double potentialBoatspeed = SpeedVector.convertKnots2MetresPerSecond(
                metrics.getPotentialBoatSpeed(nextdirection.absDegreesDiff(windspeedvector.getAngle()),
                        windspeedvector.getSpeed()));
        boatspeed += metrics.getInertia() * (potentialBoatspeed - boatspeed);
        // start by calculating the vector components of the boats movement
        DistanceVector move = new DistanceVector(boatspeed, nextdirection)
                .sub(new DistanceVector(waterspeedvector.getSpeedMetresPerSecond(), waterspeedvector.getAngle()));
        setLocation(move.toLocation(getLocation())); // updated position calculated
        track.add(getLocation()); // record it in track
        setDirection(nextdirection); // and update the directionproperty
        rotationAnglePerSecond = boatspeed < 1 ? metrics.getMaxTurningAnglePerSecond().div(2) : metrics.getMaxTurningAnglePerSecond();
    }
}
