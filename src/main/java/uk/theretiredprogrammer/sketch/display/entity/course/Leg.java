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
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class Leg implements ModelProperty<Leg> {

    private static final ObservableList<String> roundingdirections;

    static {
        roundingdirections = FXCollections.observableArrayList();
        roundingdirections.addAll("port", "starboard");
    }

    //private final ConstrainedString markname;
    private final SimpleObjectProperty<Mark> mark;
    private final ConstrainedString passing;

    private Location startfrom;
    private Location endat;
    private final Marks marks;

    public Leg(Marks marks) {
        mark = new SimpleObjectProperty<>();
        passing = new ConstrainedString(roundingdirections);
        this.marks = marks;
        mark.set(null);
        passing.set(null);
    }

    public SimpleObjectProperty<Mark> getMarkProperty() {
        return mark;
    }

    public Mark getMark() {
        return mark.get();
    }

    public void setMark(Mark mark) {
        this.mark.set(mark);
        if (mark != null) {
            this.endat = mark.getLocation();
        }
    }

    public void setStartLegLocation(Location startfrom) {
        this.startfrom = startfrom;
    }

    void update(Location startfrom) {
        this.startfrom = startfrom;
        if (mark != null) {
            this.endat = mark.get().getLocation();
        }
    }

    @Override
    public void setOnChange(Runnable onchange) {
        // not listening to mark changes at the moment
        passing.setOnChange(onchange);
    }

    public final void set(Leg value) {
        set(value.mark.get(), value.passing.get());
    }

    public final void set(Mark mark, String passing) {
        this.mark.set(mark);
        this.passing.set(passing);
        if (mark != null) {
            this.endat = mark.getLocation();
        }
    }

    @Override
    public final void parse(JsonValue jvalue) {
        LegValues legvalues = FromJson.legvalues(jvalue);
        mark.set(marks.get(legvalues.markname));
        if (mark.get() == null) {
            throw new ParseFailure("Leg: markname requested is not in set of defined marks");
        }
        passing.set(legvalues.passing);
    }

    @Override
    public final Leg parsevalue(JsonValue jvalue) {
        throw new IllegalStateFailure("Leg: parsevalue should not be used");
    }

    @Override
    public JsonArray toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return UI.control(this, marks, roundingdirections);
    }

    @Override
    public Node getControl(int size) {
        return getControl();
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
        return mark.get().getNamed();
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
