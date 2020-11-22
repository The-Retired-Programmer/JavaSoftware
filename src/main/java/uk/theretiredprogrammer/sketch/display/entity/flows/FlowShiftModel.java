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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import uk.theretiredprogrammer.sketch.core.entity.Int;
import uk.theretiredprogrammer.sketch.core.entity.Dble;
import uk.theretiredprogrammer.sketch.core.entity.Bool;
import uk.theretiredprogrammer.sketch.core.entity.Colour;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.log.control.LogController;
import uk.theretiredprogrammer.sketch.log.entity.WindShiftLogEntry;
import uk.theretiredprogrammer.sketch.log.entity.WindSwingLogEntry;

public class FlowShiftModel extends ModelMap {

    private final Bool showflow = new Bool(false);
    private final Dble showflowinterval = new Dble(100.0);
    private final Colour showflowcolour = new Colour(Color.BLACK);
    private final Angle swingangle = new Angle(0);
    private final Int swingperiod = new Int(0);
    private final Angle shiftangle = new Angle(0);
    private final Int shiftperiod = new Int(0);
    private final Bool randomshifts = new Bool(false);

    private double shiftNow = 0;
    private double swingNow = 0;

    public FlowShiftModel() {
        addProperty("showflow", showflow);
        addProperty("showflowinterval", showflowinterval);
        addProperty("showflowcolour", showflowcolour);
        addProperty("swingangle", swingangle);
        addProperty("swingperiod", swingperiod);
        addProperty("shiftangle", shiftangle);
        addProperty("shiftperiod", shiftperiod);
        addProperty("randomshifts", randomshifts);
    }

    @Override
    protected void parseValues(JsonObject jobj) {
        parseOptionalProperty("showflow", showflow, jobj);
        parseOptionalProperty("showflowinterval", showflowinterval, jobj);
        parseOptionalProperty("showflowcolour", showflowcolour, jobj);
        parseOptionalProperty("swingangle", swingangle, jobj);
        parseOptionalProperty("swingperiod", swingperiod, jobj);
        parseOptionalProperty("shiftangle", shiftangle, jobj);
        parseOptionalProperty("shiftperiod", shiftperiod, jobj);
        parseOptionalProperty("randomshifts", randomshifts, jobj);
    }

    @Override
    public JsonValue toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("showflow", showflow.toJson());
        job.add("showflowinterval", showflowinterval.toJson());
        job.add("showflowcolour", showflowcolour.toJson());
        job.add("swingangle", swingangle.toJson());
        job.add("swingperiod", swingperiod.toJson());
        job.add("shiftangle", shiftangle.toJson());
        job.add("shiftperiod", shiftperiod.toJson());
        job.add("randomshifts", randomshifts.toJson());
        return job.build();
    }

    @Override
    public void setOnChange(Runnable onchange) {
        showflow.setOnChange(onchange);
        showflowinterval.setOnChange(onchange);
        showflowcolour.setOnChange(onchange);
        swingangle.setOnChange(onchange);
        swingperiod.setOnChange(onchange);
        shiftangle.setOnChange(onchange);
        shiftperiod.setOnChange(onchange);
        randomshifts.setOnChange(onchange);
    }

    public boolean isShowflow() {
        return showflow.get();
    }

    public double getShowflowinterval() {
        return showflowinterval.get();
    }

    public Color getShowflowcolour() {
        return showflowcolour.get();
    }

    void timerAdvance(int simulationtime, LogController timerlog) {
        if (swingperiod.get() != 0) {
            // as we are using a sine rule for swing - convert to an angle (in radians)
            double radians = Math.toRadians(((double) simulationtime % swingperiod.get()) / swingperiod.get() * 360);
            swingNow = swingangle.mult(Math.sin(radians)).get();
            timerlog.add(new WindSwingLogEntry(swingNow));
        } else {
            swingNow = 0;
        }
        // now deal with shifts
        double shiftval = 0;
        boolean shifting = false;
        if (shiftperiod.get() != 0) {
            double delta = randomshifts.get()
                    ? Math.random() * shiftperiod.get()
                    : simulationtime % shiftperiod.get();
            double quarterPeriod = shiftperiod.get() / 4;
            if (delta < quarterPeriod) {
                shiftval = 0;
            } else if (delta < quarterPeriod * 2) {
                shiftval = shiftangle.negative().get();
            } else if (delta < quarterPeriod * 3) {
                shiftval = 0;
            } else {
                shiftval = shiftangle.get();
            }
            shifting = true;
        }
        if (randomshifts.get()) {
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

    SpeedVector addShiftandSwing(SpeedVector flow) {
        if (swingperiod.get() > 0) {
            flow = new SpeedVector(flow.getSpeed(), flow.getDegreesProperty().plus(swingNow));
        }
        if (shiftperiod.get() > 0 || randomshifts.get()) {
            flow = new SpeedVector(flow.getSpeed(), flow.getDegreesProperty().plus(shiftNow));
        }
        return flow;
    }
}
