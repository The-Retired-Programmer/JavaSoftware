/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class Leg implements ModelProperty<Leg> {

    private static final ObservableList<String> roundingdirections;

    static {
        roundingdirections = FXCollections.observableArrayList();
        roundingdirections.addAll("port", "starboard");
    }

    private final ConstrainedString markname = new ConstrainedString();
    private final ConstrainedString passing = new ConstrainedString(roundingdirections);

    ObservableList<String> marknames;
    private Location startfrom;
    private Location endat;
    private Marks marks;

    public Leg() {
        set(null, null);
    }

    public Leg(Marks marks, ObservableList<String> marknames) {
        setMarksAndNames(marks, marknames);
        set(null, null);
    }

    public Leg(String mark, String passing, Marks marks, ObservableList<String> marknames) {
        setMarksAndNames(marks, marknames);
        set(mark, passing);
    }

    public void setStartLegLocation(Location startfrom) {
        this.startfrom = startfrom;
    }

    void update(Location startfrom) {
        this.startfrom = startfrom;
        if (markname.get() != null) {
            this.endat = marks.get(markname.get()).getLocation();
        }
    }

    @Override
    public void setOnChange(Runnable onchange) {
        markname.setOnChange(onchange);
        passing.setOnChange(onchange);
    }

    final void setMarksAndNames(Marks marks, ObservableList<String> marknames) {
        this.marks = marks;
        this.marknames = marknames;
        markname.setConstraints(marknames);
    }

    public final void set(Leg value) {
        setMarksAndNames(value.marks, value.marknames);
        set(value.markname.get(), value.passing.get());
    }

    public final void set(String mark, String passing) {
        this.markname.set(mark);
        this.passing.set(passing == null ? null : passing.toLowerCase());
        if (markname.get() != null) {
            this.endat = marks.get(markname.get()).getLocation();
        }
    }

    @Override
    public final Leg parsevalue(JsonValue jvalue) {
        return FromJson.legProperty(jvalue, marks, marknames, roundingdirections);
    }

    @Override
    public JsonArray toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return UI.control(this, marknames, roundingdirections);
    }

    @Override
    public Node getControl(int size) {
        return getControl();
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }

    public String getRoundingdirection() {
        return passing.get();
    }

    public ConstrainedString getRoundingdirectionProperty() {
        return passing;
    }

    public boolean isPortRounding() {
        return passing.get().equals("port");
    }

    public String getMarkname() {
        return markname.get();
    }

    public ConstrainedString getMarknameProperty() {
        return markname;
    }

    @Override
    public String toString() {
        return markname.get() + " to " + passing.get();
    }

    public double getDistanceToEnd(Location here) {
        return here.to(endat);
    }

    public Location getEndLocation() {
        return endat;
    }

    public Angle getAngleofLeg() {
        return startfrom.angleto(endat);
    }
}
