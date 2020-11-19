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
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.display.control.strategy.RoundingDecisions;
import uk.theretiredprogrammer.sketch.display.control.strategy.SailingDecisions;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public abstract class Strategy {

    private SailingDecisions starboarddecisions;
    private SailingDecisions portdecisions;
    private RoundingDecisions roundingdecisions;
    private boolean useroundingdecisions = false;
    private PropertyDegrees portoffsetangle;
    private PropertyDegrees starboardoffsetangle;
    private double offset;
    private boolean isWindwardLeg = false;

    public Strategy() {
    }

    public Strategy(Strategy clonefrom) {
        this.setDecisions(clonefrom.starboarddecisions, clonefrom.portdecisions, clonefrom.roundingdecisions);
        this.useroundingdecisions = clonefrom.useroundingdecisions;
        this.setMarkOffset(clonefrom.offset, clonefrom.starboardoffsetangle, clonefrom.portoffsetangle);
    }

    public final void setDecisions(SailingDecisions starboarddecisions, SailingDecisions portdecisions, RoundingDecisions roundingdecisions) {
        this.starboarddecisions = starboarddecisions;
        this.portdecisions = portdecisions;
        this.roundingdecisions = roundingdecisions;
    }

    public final void setMarkOffset(double offset, PropertyDegrees starboardoffsetangle, PropertyDegrees portoffsetangle) {
        this.offset = offset;
        this.starboardoffsetangle = starboardoffsetangle;
        this.portoffsetangle = portoffsetangle;
    }

    public final void setIsWindwardLeg() {
        this.isWindwardLeg = true;
    }

    public PropertyDistanceVector getOffsetVector(boolean onPort) {
        return new PropertyDistanceVector(offset, onPort ? portoffsetangle : starboardoffsetangle);
    }

    public String strategyTimeInterval(Boat boat, Decision decision, CurrentLeg leg, SketchModel sketchproperty, WindFlow windflow, WaterFlow waterflow) {
        PropertyDegrees markMeanwinddirection = leg.endLegMeanwinddirection(windflow);
        PropertyDegrees winddirection = windflow.getFlow(boat.getLocation()).getDegreesProperty();
        if (useroundingdecisions) {
            return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
        }
        if (isWindwardLeg) {
            if (leg.isNear2WindwardMark(boat, markMeanwinddirection)) {
                useroundingdecisions = true;
                return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
            }
        } else {
            if (leg.isNear2LeewardMark(boat, markMeanwinddirection)) {
                useroundingdecisions = true;
                return roundingdecisions.nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);

            }
        }
        return (boat.isPort(winddirection) ? portdecisions : starboarddecisions).nextTimeInterval(boat, decision, sketchproperty, leg, this, windflow, waterflow);
    }
}
