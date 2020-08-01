/*
 * Copyright 2020 Richard Linsdale
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
import uk.theretiredprogrammer.racetrainingsketch.core.Area;
import uk.theretiredprogrammer.racetrainingsketch.core.BooleanParser;
import uk.theretiredprogrammer.racetrainingsketch.core.ColorParser;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.IntegerParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.SpeedPolar;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.TimerLog;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.WindShiftLogEntry;
import uk.theretiredprogrammer.racetrainingsketch.timerlog.WindSwingLogEntry;
import uk.theretiredprogrammer.racetrainingsketch.ui.Scenario;
import uk.theretiredprogrammer.racetrainingsketch.ui.Displayable;
import uk.theretiredprogrammer.racetrainingsketch.ui.Timerable;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class Flow implements Displayable, Timerable {

    final static int WIDTHSTEPS = 100;
    final static int HEIGHTSTEPS = 100;
    private final SpeedPolar[][] flowarray = new SpeedPolar[WIDTHSTEPS + 1][HEIGHTSTEPS + 1];
    private final Area area;
    private final double wstepsize;
    private final double hstepsize;

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

    private final Scenario scenario;
    private Angle meanflowangle;
    private final FlowComponentSet flowset;

    public Flow(JsonObject paramsobj, Scenario scenario, FlowComponentSet flowset) throws IOException {
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
        this.area = new Area(new Location(scenario.getWest(), scenario.getSouth()),
                scenario.getEast() - scenario.getWest(), scenario.getNorth() - scenario.getSouth());
        wstepsize = area.getWidth() / WIDTHSTEPS;
        hstepsize = area.getHeight() / HEIGHTSTEPS;
        this.flowset = flowset;
        setFlows();
    }

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

    public FlowComponentSet getFlowComponentSet() {
        return flowset;
    }

    public final void setFlows() throws IOException {
        double hpos = area.getBottomleft().getY();
        double wpos = area.getBottomleft().getX();
        for (int h = 0; h < HEIGHTSTEPS + 1; h++) {
            double y = hpos + hstepsize * h;
            for (int w = 0; w < WIDTHSTEPS + 1; w++) {
                double x = wpos + wstepsize * w;
                flowarray[w][h] = flowset.getFlow(new Location(x, y));
            }
        }
        meanflowangle = flowset.meanWindAngle(); // check if we are using a forced mean
        if (meanflowangle == null) {
            meanflowangle = calcMeanFlowAngle(); // if not then calculate it
        }
    }

    private SpeedPolar getFlowwithoutswing(Location pos) throws IOException {
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

    public Angle getMeanFlowAngle() throws IOException {
        return meanflowangle;
    }

    private Angle calcMeanFlowAngle() throws IOException {
        return SpeedPolar.meanAngle(flowarray);
    }

    /**
     * Advance time. Recalculate the flow.
     *
     * @param simulationtime the new setTime
     */
    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog) {
        if (swingperiod != 0) {
            // as we are using a sine rule for swing - convert to an angle (in radians)
            double radians = Math.toRadians(((double) simulationtime % swingperiod) / swingperiod * 360);
            swingNow = swingangle.mult(Math.sin(radians));
            timerlog.add(new WindSwingLogEntry(swingNow));
        } else {
            swingNow = ANGLE0;
        }
        // now deal with shifts
        Angle shiftval = ANGLE0;
        boolean shifting = false;
        if (shiftperiod != 0) {
            double delta = randomshifts ? Math.random() * shiftperiod : simulationtime % shiftperiod;
            double quarterPeriod = shiftperiod / 4;
            if (delta < quarterPeriod) {
                shiftval = ANGLE0;
            } else if (delta < quarterPeriod * 2) {
                shiftval = shiftangle.negate();
            } else if (delta < quarterPeriod * 3) {
                shiftval = ANGLE0;
            } else {
                shiftval = shiftangle;
            }
            shifting = true;
        }
        if (randomshifts) {
            // only apply the random shift in 2% of cases - otherwise leave alone
            if (Math.random() <= 0.02) {
                shiftNow = shiftval;
            }
            shifting = true;
        } else {
            shiftNow = shiftval; // apply the shift
        }
        if (shifting){
            timerlog.add(new WindShiftLogEntry(shiftNow));
        }
    }

    /**
     * Draw the Flow arrows on the display canvas.
     *
     * @param g2D the 2D graphics object
     * @param zoom the scale factor (pixels/metre)
     */
    @Override
    public void draw(Graphics2D g2D, double zoom) throws IOException {
        Location sw = area.getBottomleft();
        double westedge = sw.getX();
        double eastedge = westedge + area.getWidth();
        double southedge = sw.getY();
        double northedge = southedge + area.getHeight();
        if (showflow) { // draw the flow arrows
            //
            double x = westedge + showflowinterval;
            while (x < eastedge) {
                double y = southedge + showflowinterval;
                while (y < northedge) {
                    displayWindGraphic(g2D, zoom, x, y);
                    y += showflowinterval;
                }
                x += showflowinterval;
            }
        }
    }

    private void displayWindGraphic(Graphics2D g2D, double pixelsPerMetre, double x, double y) throws IOException {
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
        g2D.setColor(showflowcolor);
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
     * Get the Flow at the current setTime, at the requested location.
     *
     * @param pos location
     * @return the flow
     */
    public SpeedPolar getFlow(Location pos) throws IOException {
        SpeedPolar f = getFlowwithoutswing(pos);
        if (swingperiod > 0) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(swingNow));
        }
        if (shiftperiod > 0 || randomshifts) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(shiftNow));
        }
        return f;
    }
}
