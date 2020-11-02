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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;

public class Course extends ModelProperties {

    private static final ObservableList<String> roundings;

    static {
        roundings = FXCollections.observableArrayList();
        roundings.addAll("port", "starboard");
    }

    private final PropertyLocation start = new PropertyLocation("start", LOCATIONZERO);
    private final PropertyLegEndings legs;

    private Leg firstcourseleg;
    private final Marks marks;

    public Course(Marks marks, ObservableList<String> marknames) {
        legs = new PropertyLegEndings("legs", marknames, roundings);
        this.marks = marks;
        this.addProperty("start", start);
        this.addProperty("legs", legs);
        marks.setOnChange(() -> updatelegs());
    }

    private void updatelegs() {
        legs.getList().clear(); // not best way!!
        legs.getList().forEach(lv -> insertLeg(lv.get()));
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseMandatoryProperty("start", start, jobj);
        parseOptionalProperty("legs", legs, jobj);
        legs.getList().forEach(lv -> insertLeg(lv.get()));
    }

    @Override
    public void setOnChange(Runnable onchange) {
        start.setOnChange(onchange);
        legs.setOnChange(onchange);
    }

    @Override
    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("start", start.toJson());
        job.add("legs", legs.toJson());
        return job.build();
    }

    private void insertLeg(LegEnding legending) {
        if (firstcourseleg == null) {
            firstcourseleg = new Leg(start.get(), marks.get(legending.getMarkname()).getLocation(), legending.isPortRounding(), null);
        } else {
            Leg leg = firstcourseleg;
            while (leg.getFollowingLeg() != null) {
                leg = leg.getFollowingLeg();
            }
            Leg newleg = new Leg(leg.getEndLocation(), marks.get(legending.getMarkname()).getLocation(), legending.isPortRounding(), null);
            leg.setFollowingLeg(newleg);
        }
    }

    public Course get() {
        return this;
    }

    public Location getStart() {
        return start.get();
    }

    public PropertyLegEndings getPropertyLegValues() {
        return legs.get();
    }

    public Leg getFirstLeg() {
        return firstcourseleg;
    }
}
