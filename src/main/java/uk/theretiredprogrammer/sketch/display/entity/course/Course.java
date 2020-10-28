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

import uk.theretiredprogrammer.sketch.display.control.strategy.Leg;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.LegValue;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyLegValues;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMarks;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Course {

    private Leg firstcourseleg;
    private final PropertyLegValues legvaluesproperty;
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

//    public void addLeg() {
//        if (marksproperty.getList().isEmpty()) {
//            throw new IllegalStateFailure("No marks defined - so leg cannot be defined");
//        }
//        LegValue leg = new LegValue(marksproperty.getList().get(0).getName(), "port");
//        insertLeg(leg);
//        legvaluesproperty.add(leg);
//    }

    private void insertLeg(LegValue leg) {
        if (firstcourseleg == null) {
            firstcourseleg = new Leg(startproperty, marksproperty.get(leg.getMarkname()), leg.isPortRounding(), null);
        } else {
            Leg l = firstcourseleg;
            while (l.getFollowingLeg() != null) {
                l = l.getFollowingLeg();
            }
            Leg newl = new Leg(l.getEndLocation(), marksproperty.get(leg.getMarkname()), leg.isPortRounding(), null);
            l.setFollowingLeg(newl);
        }
    }

    public Leg getFirstCourseLeg() {
        return firstcourseleg;
    }
}
