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
package uk.theretiredprogrammer.sketch.display.entity.course;

import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.LegEnding;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyLegEndings;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMarks;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Course {

    private Leg firstcourseleg;
    private final PropertyLegEndings legvaluesproperty;
    private final PropertyMarks marksproperty;
    private final Location startproperty;

    public Course(PropertySketch sketchproperty) {
        this.marksproperty = sketchproperty.getMarks();
        this.legvaluesproperty = sketchproperty.getCourse().getPropertyLegValues();
        this.startproperty = sketchproperty.getCourse().getStart();
        //
        //legvaluesproperty.stream().forEach(lv -> insertLeg(lv.get()));
        for (var lv : legvaluesproperty.getList()){
            insertLeg(lv.get());
        }
    }

    private void insertLeg(LegEnding legending) {
        if (firstcourseleg == null) {
            firstcourseleg = new Leg(startproperty, marksproperty.get(legending.getMarkname()).getLocation(), legending.isPortRounding(), null);
        } else {
            Leg leg = firstcourseleg;
            while (leg.getFollowingLeg() != null) {
                leg = leg.getFollowingLeg();
            }
            Leg newleg = new Leg(leg.getEndLocation(), marksproperty.get(legending.getMarkname()).getLocation(), legending.isPortRounding(), null);
            leg.setFollowingLeg(newleg);
        }
    }

    public Leg getFirstLeg() {
        return firstcourseleg;
    }
}
