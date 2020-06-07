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
package uk.theretiredprogrammer.racetrainingsketch.flows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.text.NumberFormat;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.racetrainingsketch.core.BooleanParser;
import uk.theretiredprogrammer.racetrainingsketch.core.ColorParser;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.IntegerParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;

/**
 * The ComplexFlow Class - represents a flow which is described by flows (speed
 * and direction) at the four corners points. Flows within the described area
 * are interpolated.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class CoreFlowElement extends FlowElement {

    private Angle swingNow = new Angle(0);
    private Angle shiftNow = new Angle(0);
    
    private boolean showflow;
    private double showflowinterval;
    private Color showflowcolor;
    private Angle swingangle;
    private int swingperiod;
    private Angle shiftangle;
    private int shiftperiod;
    private boolean randomshifts;

    private final ScenarioElement scenario;

    public CoreFlowElement(JsonObject paramsobj, ScenarioElement scenario) throws IOException {
        showflow = BooleanParser.parse(paramsobj, "showflow").orElse(false);
        showflowinterval = DoubleParser.parse(paramsobj, "showflowinterval").orElse(100.0);
        showflowcolor = ColorParser.parse(paramsobj, "showflowcolour").orElse(Color.black);
        swingangle = Angle.parse(paramsobj, "swingangle").orElse(ANGLE0);
        swingperiod = IntegerParser.parse(paramsobj, "swingperiod").orElse(0);
        shiftangle = Angle.parse(paramsobj, "shiftangle").orElse(ANGLE0);
        shiftperiod = IntegerParser.parse(paramsobj, "shiftperiod").orElse(0);
        randomshifts = BooleanParser.parse(paramsobj, "randomshifts").orElse(false);
        //
        this.scenario = scenario;
    }
    
    @Override
    public void change(JsonObject paramsobj) throws IOException {
        showflow = BooleanParser.parse(paramsobj, "showflow").orElse(showflow);
        showflowinterval = DoubleParser.parse(paramsobj, "showflowinterval").orElse(showflowinterval);
        showflowcolor = ColorParser.parse(paramsobj, "showflowcolour").orElse(showflowcolor);
        swingangle = Angle.parse(paramsobj, "swingangle").orElse(swingangle);
        swingperiod = IntegerParser.parse(paramsobj, "swingperiod").orElse(swingperiod);
        shiftangle = Angle.parse(paramsobj, "shiftangle").orElse(shiftangle);
        shiftperiod = IntegerParser.parse(paramsobj, "shiftperiod").orElse(shiftperiod);
        randomshifts = BooleanParser.parse(paramsobj, "randomshifts").orElse(randomshifts);
    }
    
    private boolean isShowflow() {
        return showflow;
    }

    private double getShowflowinterval() {
        return showflowinterval;
    }

    private Color getShowflowcolour() {
        return showflowcolor;
    }

    private int getSwingperiod() {
        return swingperiod;
    }

    private Angle getSwingangle() {
        return swingangle;
    }

    private int getShiftperiod() {
        return shiftperiod;
    }

    private Angle getShiftangle() {
        return shiftangle;
    }

    private boolean isRandomshifts() {
        return randomshifts;
    }

   

    /**
     * Advance time. Recalculate the flow.
     *
     * @param time the new time
     */
    @Override
    public void timerAdvance(int time) {
        int swingperiod = getSwingperiod();
        Angle swingangle = getSwingangle();
        int shiftperiod = getShiftperiod();
        Angle shift = getShiftangle();
        boolean randomshifts = isRandomshifts();
        if (swingperiod != 0) {
        // as we are using a sine rule for swing - convert to an angle (in radians)
        double radians = Math.toRadians(((double) time % swingperiod) / swingperiod * 360);
        swingNow = swingangle.mult(Math.sin(radians));
        } else {
            swingNow = ANGLE0;
        }
        // now deal with shifts
        Angle shiftval = ANGLE0;
        if (shiftperiod != 0){
            double delta = randomshifts ? Math.random() * shiftperiod : time % shiftperiod;
            double quarterPeriod = shiftperiod / 4;
            if (delta < quarterPeriod) {
                shiftval = ANGLE0;
            } else if (delta < quarterPeriod * 2) {
                shiftval = shift.negate();
            } else if (delta < quarterPeriod * 3) {
                shiftval = ANGLE0;
            } else {
                shiftval = shift;
            }
        }
        if (randomshifts) {
            // only apply the random shift in 2% of cases - otherwise leave alone
            if (Math.random() <= 0.02) {
                shiftNow = shiftval;
            }
        } else {
            shiftNow = shiftval; // apply the shift
        }
    }

    /**
     * Draw the Flow arrows on the display canvas.
     *
     * @param g2D the 2D graphics object
     * @param pixelsPerMetre the scale factor
     */
    @Override
    public void draw(Graphics2D g2D, double pixelsPerMetre) {
        double westedge = scenario.getWest();
        double eastedge = scenario.getEast();
        double northedge = scenario.getNorth();
        double southedge = scenario.getSouth();
        double showflowinterval = getShowflowinterval();
        if (isShowflow()) { // draw the flow arrows
            //
            double x = westedge + showflowinterval;
            while (x < eastedge) {
                double y = southedge + showflowinterval;
                while (y < northedge) {
                    displayWindGraphic(g2D, pixelsPerMetre, x, y);
                    y += showflowinterval;
                }
                x += showflowinterval;
            }
        }
    }

    private void displayWindGraphic(Graphics2D g2D, double pixelsPerMetre, double x, double y) {
        GeneralPath p = new GeneralPath();
        p.moveTo(0, 15);
        p.lineTo(0, -15);
        p.moveTo(4, 7);
        p.lineTo(0, 15);
        p.lineTo(-4, 7);
        //
        AffineTransform xform = g2D.getTransform();
        g2D.translate(x, y);
        g2D.scale(1 / pixelsPerMetre, -1 / pixelsPerMetre);
        SpeedPolar flow = getFlow(new Location(x, y));
        g2D.rotate(flow.getAngle().getRadians());
        g2D.setColor(getShowflowcolour());
        g2D.setStroke(new BasicStroke(1));
        g2D.draw(p);
        //
        g2D.setFont(new Font("Sans Serif", Font.PLAIN, 10));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String windspeedText = nf.format(flow.getSpeed());
        if (flow.getAngle().isPositive()) {
            g2D.translate(-2, 4);
            g2D.rotate(-Math.PI / 2);
        } else {
            g2D.translate(+2, -15);
            g2D.rotate(Math.PI / 2);
        }
        g2D.drawString(windspeedText, 0, 0);
        g2D.setTransform(xform);
    }

    /**
     * Get the Flow at the current time, at the requested location.
     *
     * @param pos location
     * @return the flow
     */
    @Override
    public SpeedPolar getFlow(Location pos) {
        SpeedPolar f = getFlowWithoutSwing(pos);
        if (getSwingperiod() > 0) {
            f = new SpeedPolar(f.getSpeed(),f.getAngle().add(swingNow));
        }
        if (getShiftperiod() > 0 || isRandomshifts()) {
            f = new SpeedPolar(f.getSpeed(),f.getAngle().add(shiftNow));
        }
        return f;
    }
    
    abstract SpeedPolar getFlowWithoutSwing(Location pos);
}
