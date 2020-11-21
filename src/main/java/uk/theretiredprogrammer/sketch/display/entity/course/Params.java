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

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance;

public class Params {

    public final Boat boat;
    public final SketchModel model;
    public final WindFlow windflow;
    public final WaterFlow waterflow;
    //
    public Decision decision;
    public CurrentLeg leg;
    public PropertyDegrees winddirection;
    public PropertyDegrees starboardCloseHauled;
    public PropertyDegrees portCloseHauled;
    public PropertyDegrees starboardReaching;
    public PropertyDegrees portReaching;
    public PropertyDegrees heading;
    public PropertyLocation location;
    public PropertyDegrees angletowind;
    public PropertyDegrees meanwinddirection;
    public PropertyDegrees downwindrelative;
    public PropertyDegrees upwindrelative;
    public boolean isPort;
    public PropertyLocation marklocation;
    public PropertyDegrees markmeanwinddirection;

    public Params(SketchModel model, Boat boat) {
        this.model = model;
        windflow = model.getWindFlow();
        waterflow = model.getWaterFlow();
        this.boat = boat;
        refresh();
    }

    public final void refresh() {
        leg = boat.getCurrentLeg();
        //
        marklocation = leg.getMarkLocation();
        decision = leg.decision;
        //
        winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        meanwinddirection = windflow.getMeanFlowAngle();
        markmeanwinddirection = windflow.getMeanFlowAngle(marklocation);
        //
        heading = boat.getDirection();
        location = boat.getLocation();
        downwindrelative = boat.metrics.downwindrelative;
        upwindrelative = boat.metrics.upwindrelative;
        isPort = boat.isPort(winddirection);
        starboardCloseHauled = boat.getStarboardCloseHauledCourse(winddirection);
        portCloseHauled = boat.getPortCloseHauledCourse(winddirection);
        starboardReaching = boat.getStarboardReachingCourse(winddirection);
        portReaching = boat.getPortReachingCourse(winddirection);
        angletowind = heading.absDegreesDiff(winddirection);
    }

    public final void setSAILON() {
        decision.setSAILON(heading);
    }

    public final void setTURN(PropertyDegrees degrees, boolean turndirection, Importance importance, String reason) {
        if (degrees.absDegreesDiff(heading).lt(0.1)) {
            decision.setSAILON(heading);
        } else {
            decision.setTURN(degrees, turndirection, importance, reason);
        }
    }

    public final void setMARKROUNDING(PropertyDegrees degrees, boolean turndirection, Importance importance, String reason) {
        decision.setMARKROUNDING(degrees, turndirection, importance, reason);
    }

    public final void setSTOP() {
        decision.setSTOP(heading);
    }
}
