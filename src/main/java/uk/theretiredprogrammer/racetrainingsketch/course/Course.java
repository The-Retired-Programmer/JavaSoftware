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
import java.util.ArrayList;
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

    private final List<CourseLeg> courselegs = new ArrayList<>();

    public Course(JsonObject paramsobj, ScenarioElement scenario) throws IOException {
        Location start = Location.parse(paramsobj, "start").orElse(new Location(0, 0));
        List<Leg> legs = ListOf.<Leg>parse(paramsobj, "legs", (jval) -> Leg.parseElement(jval))
                .orElseThrow(() -> new IOException("Malformed Definition file - <legs> is a mandatory parameter"));
        List<CourseLeg> partcourselegs = new ArrayList<>();
        Location startofleg = start;
        int i = 0;
        for (Leg leg : legs) {
            MarkElement mark = scenario.getMark(leg.getMarkname());
            partcourselegs.add(new CourseLeg(startofleg, mark.getLocation(), leg.getTurn()));
            startofleg = mark.getLocation();
            i++;
        }
        for (i = 0; i < partcourselegs.size() - 1; i++) {
            courselegs.add(partcourselegs.get(i).addFollowingleg(partcourselegs.get(i + 1)));
        }
    }

    public CourseLeg getFirstCourseLeg() {
        return courselegs.get(0);
    }
}
