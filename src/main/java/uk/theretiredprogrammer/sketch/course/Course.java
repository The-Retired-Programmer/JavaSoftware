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
package uk.theretiredprogrammer.sketch.course;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.strategy.Leg;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.ListOf;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.LegValue;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.core.PropertyLocation;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.Displayable;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Course implements Displayable {

    private final Leg firstcourseleg;
    private final ObservableList<Mark> marks = FXCollections.observableArrayList();
    private final PropertyLocation startproperty = new PropertyLocation();

    public Course(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray markarray = parsedjson.getJsonArray("marks");
        if (markarray != null) {
            for (JsonValue markv : markarray) {
                if (markv.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject markparms = (JsonObject) markv;
                    Mark mark = new Mark(controllersupplier, markparms);
                    marks.add(mark);
                } else {
                    throw new IOException("Malformed Definition File - <marks> array contains items other that mark objects");
                }
            }
        }
        //
        JsonObject courseobj = parsedjson.getJsonObject("course");
        if (courseobj != null) {
            Location start = Location.parse(courseobj, "start").orElse(new Location(0, 0));
            startproperty.set(start);
            List<LegValue> legvalues = ListOf.<LegValue>parse(courseobj, "legs", (jval) -> LegValue.parseElement(jval))
                    .orElseThrow(() -> new IOException("Malformed Definition file - <legs> is a mandatory parameter"));
            Leg following = null;
            int i = legvalues.size() - 1;
            while (i >= 0) {
                Mark endmark = getMark(legvalues.get(i).getMarkname());
                following = new Leg(controllersupplier,
                        i == 0 ? start : getMark(legvalues.get(i - 1).getMarkname()).getLocation(),
                        endmark, legvalues.get(i).isPortRounding(),
                        following);
                i--;
            }
            firstcourseleg = following;
        } else {
            firstcourseleg = null;
        }
    }

    @Override
    public Map<String, PropertyItem> properties() {
        LinkedHashMap<String, PropertyItem> map = new LinkedHashMap<>();
        map.put("start", startproperty);
        Leg leg = firstcourseleg;
        int count = 1;
        while (leg != null) {
            map.put("Leg" + count++, leg.getProperty());
            leg = leg.getFollowingLeg();
        }
        return map;
    }

    public final Mark getMark(String name) {
        for (Mark mark : marks) {
            if (mark.getName().equals(name)) {
                return mark;
            }
        }
        return null;
    }

    public void addMark(Mark mark) {
        marks.add(mark);
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public ObservableList<String> getMarknames() {
        ObservableList<String> marknames = FXCollections.observableArrayList();
        marks.forEach(mark -> marknames.add(mark.getName()));
        return marknames;
    }

    public void setOnMarksChange(ListChangeListener<Mark> ml) {
        marks.addListener(ml);
    }

    public Leg getFirstCourseLeg() {
        return firstcourseleg;
    }

    @Override
    public void draw(SketchPane canvas) throws IOException {
        for (Mark mark : marks) {
            mark.draw(canvas);
        }
    }
}
