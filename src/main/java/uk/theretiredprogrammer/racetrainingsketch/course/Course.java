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
package uk.theretiredprogrammer.racetrainingsketch.course;

import java.io.IOException;
import java.util.List;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.ListOf;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Leg;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Course {

    private final CourseLeg firstcourseleg;

    public Course(JsonObject paramsobj, ScenarioElement scenario) throws IOException {
        Location start = Location.parse(paramsobj, "start").orElse(new Location(0, 0));
        List<Leg> legs = ListOf.<Leg>parse(paramsobj, "legs", (jval) -> Leg.parseElement(jval))
                .orElseThrow(() -> new IOException("Malformed Definition file - <legs> is a mandatory parameter"));
        CourseLeg following = null;
        int i = legs.size() - 1;
        while (i >= 0) {
            following = new CourseLeg(
                    i == 0 ? start : scenario.getMark(legs.get(i-1).getMarkname()).getLocation(),
                    scenario.getMark(legs.get(i).getMarkname()).getLocation(),
                    legs.get(i).getTurn(),
                    following);
            i--;
        }
        firstcourseleg = following;
    }

    public CourseLeg getFirstCourseLeg() {
        return firstcourseleg;
    }
}
