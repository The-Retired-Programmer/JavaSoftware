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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.SpeedPolar;
import uk.theretiredprogrammer.sketch.decisionslog.control.DecisionController;
import uk.theretiredprogrammer.sketch.decisionslog.entity.WindShiftLogEntry;
import uk.theretiredprogrammer.sketch.decisionslog.entity.WindSwingLogEntry;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;

public abstract class Flow {

    final static int WIDTHSTEPS = 100;
    final static int HEIGHTSTEPS = 100;
    private final SpeedPolar[][] flowarray = new SpeedPolar[WIDTHSTEPS + 1][HEIGHTSTEPS + 1];
    private final Area area;
    private final double wstepsize;
    private final double hstepsize;
    private Angle shiftNow = new Angle(0);
    private Angle swingNow = new Angle(0);
    private Angle meanflowangle;

    private final FlowShiftModel flowshiftsproperty;
    private final FlowComponentSet flowcomponents;

    public Flow(SketchModel sketchproperty, FlowShiftModel flowshiftsproperty, FlowComponentSet flowcomponents) {
        this.flowshiftsproperty = flowshiftsproperty;
        this.flowcomponents = flowcomponents;
        //
        this.area = sketchproperty.getDisplay().getSailingarea();
        hstepsize = area.getHeight() / HEIGHTSTEPS;
        wstepsize = area.getWidth() / WIDTHSTEPS;
        setFlows();
    }

    public final void setFlows() {
        double hpos = area.getBottomleft().getY();
        double wpos = area.getBottomleft().getX();
        for (int h = 0; h < HEIGHTSTEPS + 1; h++) {
            double y = hpos + hstepsize * h;
            for (int w = 0; w < WIDTHSTEPS + 1; w++) {
                double x = wpos + wstepsize * w;
                flowarray[w][h] = flowcomponents.getFlow(new Location(x, y));
            }
        }
        meanflowangle = flowcomponents.meanWindAngle(); // check if we are using a forced mean
        if (meanflowangle == null) {
            meanflowangle = calcMeanFlowAngle(); // if not then calculate it
        }
    }

    private SpeedPolar getFlowwithoutswing(Location pos) {
        int w = (int) Math.floor((pos.getX() - area.getBottomleft().getX()) / wstepsize);
        if (w < 0) {
            w = 0;
        }
        if (w > WIDTHSTEPS) {
            w = WIDTHSTEPS;
        }
        int h = (int) Math.floor((pos.getY() - area.getBottomleft().getY()) / hstepsize);
        if (h < 0) {
            h = 0;
        }
        if (h > HEIGHTSTEPS) {
            h = HEIGHTSTEPS;
        }
        return flowarray[w][h];
    }

    public Angle getMeanFlowAngle(Location pos) {
        return getFlowwithoutswing(pos).getAngle();
    }

    public Angle getMeanFlowAngle() {
        return meanflowangle;
    }

    private Angle calcMeanFlowAngle() {
        return SpeedPolar.meanAngle(flowarray);
    }

    public void timerAdvance(int simulationtime, DecisionController timerlog) {
        if (flowshiftsproperty.getSwingperiod() != 0) {
            // as we are using a sine rule for swing - convert to an angle (in radians)
            double radians = Math.toRadians(((double) simulationtime % flowshiftsproperty.getSwingperiod()) / flowshiftsproperty.getSwingperiod() * 360);
            swingNow = flowshiftsproperty.getSwingangle().mult(Math.sin(radians));
            timerlog.add(new WindSwingLogEntry(swingNow));
        } else {
            swingNow = ANGLE0;
        }
        // now deal with shifts
        Angle shiftval = ANGLE0;
        boolean shifting = false;
        if (flowshiftsproperty.getShiftperiod() != 0) {
            double delta = flowshiftsproperty.isRandomshifts()
                    ? Math.random() * flowshiftsproperty.getShiftperiod()
                    : simulationtime % flowshiftsproperty.getShiftperiod();
            double quarterPeriod = flowshiftsproperty.getShiftperiod() / 4;
            if (delta < quarterPeriod) {
                shiftval = ANGLE0;
            } else if (delta < quarterPeriod * 2) {
                shiftval = flowshiftsproperty.getShiftangle().negate();
            } else if (delta < quarterPeriod * 3) {
                shiftval = ANGLE0;
            } else {
                shiftval = flowshiftsproperty.getShiftangle();
            }
            shifting = true;
        }
        if (flowshiftsproperty.isRandomshifts()) {
            // only apply the random shift in 2% of cases - otherwise leave alone
            if (Math.random() <= 0.02) {
                shiftNow = shiftval;
            }
            shifting = true;
        } else {
            shiftNow = shiftval; // apply the shift
        }
        if (shifting) {
            timerlog.add(new WindShiftLogEntry(shiftNow));
        }
    }

    public SpeedPolar getFlow(Location pos) {
        SpeedPolar f = getFlowwithoutswing(pos);
        if (flowshiftsproperty.getSwingperiod() > 0) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(swingNow));
        }
        if (flowshiftsproperty.getShiftperiod() > 0 || flowshiftsproperty.isRandomshifts()) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(shiftNow));
        }
        return f;
    }
}
