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
package uk.theretiredprogrammer.sketch.flows;

import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.Area;
import uk.theretiredprogrammer.sketch.core.BooleanParser;
import uk.theretiredprogrammer.sketch.core.ColorParser;
import uk.theretiredprogrammer.sketch.core.DoubleParser;
import uk.theretiredprogrammer.sketch.core.IntegerParser;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.PropertyAngle;
import uk.theretiredprogrammer.sketch.core.PropertyBoolean;
import uk.theretiredprogrammer.sketch.core.PropertyColor;
import uk.theretiredprogrammer.sketch.core.PropertyDouble;
import uk.theretiredprogrammer.sketch.core.PropertyInteger;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;
import uk.theretiredprogrammer.sketch.timerlog.WindShiftLogEntry;
import uk.theretiredprogrammer.sketch.timerlog.WindSwingLogEntry;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.Displayable;
import uk.theretiredprogrammer.sketch.ui.Timerable;

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

    private final PropertyBoolean showflowproperty = new PropertyBoolean();
    private final PropertyDouble showflowintervalproperty = new PropertyDouble();
    private final PropertyColor showflowcolorproperty = new PropertyColor();
    private final PropertyAngle swingangleproperty = new PropertyAngle();
    private final PropertyInteger swingperiodproperty = new PropertyInteger();
    private final PropertyAngle shiftangleproperty = new PropertyAngle();
    private final PropertyInteger shiftperiodproperty = new PropertyInteger();
    private final PropertyBoolean randomshiftsproperty = new PropertyBoolean();

    private Angle meanflowangle;
    private final FlowComponentSet flowset;

    public Flow(Supplier<Controller> controllersupplier, JsonObject paramsobj, FlowComponentSet flowset) throws IOException {
        showflowproperty.set(BooleanParser.parse(paramsobj, "showflow").orElse(false));
        showflowintervalproperty.set(DoubleParser.parse(paramsobj, "showflowinterval").orElse(100.0));
        showflowcolorproperty.set(ColorParser.parse(paramsobj, "showflowcolour").orElse(Color.BLACK));
        swingangleproperty.setValue(Angle.parse(paramsobj, "swingangle").orElse(ANGLE0));
        swingperiodproperty.set(IntegerParser.parse(paramsobj, "swingperiod").orElse(0));
        shiftangleproperty.setValue(Angle.parse(paramsobj, "shiftangle").orElse(ANGLE0));
        shiftperiodproperty.set(IntegerParser.parse(paramsobj, "shiftperiod").orElse(0));
        randomshiftsproperty.set(BooleanParser.parse(paramsobj, "randomshifts").orElse(false));
        //
        this.area = controllersupplier.get().displayparameters.getSailingArea();
        hstepsize = area.getHeight() / HEIGHTSTEPS;
        wstepsize = area.getWidth() / WIDTHSTEPS;
        this.flowset = flowset;
        setFlows();
    }

    public void change(JsonObject paramsobj) throws IOException {
        showflowproperty.set(BooleanParser.parse(paramsobj, "showflow").orElse(showflowproperty.get()));
        showflowintervalproperty.set(DoubleParser.parse(paramsobj, "showflowinterval").orElse(showflowintervalproperty.get()));
        showflowcolorproperty.set(ColorParser.parse(paramsobj, "showflowcolour").orElse(showflowcolorproperty.get()));
        swingangleproperty.setValue(Angle.parse(paramsobj, "swingangle").orElse(swingangleproperty.getValue()));
        swingperiodproperty.set(IntegerParser.parse(paramsobj, "swingperiod").orElse(swingperiodproperty.get()));
        shiftangleproperty.setValue(Angle.parse(paramsobj, "shiftangle").orElse(shiftangleproperty.getValue()));
        shiftperiodproperty.set(IntegerParser.parse(paramsobj, "shiftperiod").orElse(shiftperiodproperty.get()));
        randomshiftsproperty.set(BooleanParser.parse(paramsobj, "randomshifts").orElse(randomshiftsproperty.get()));
    }

    void properties(LinkedHashMap<String, Object> map) {
        map.put("showflow", showflowproperty);
        map.put("showflowinterval", showflowintervalproperty);
        map.put("showflowcolor", showflowcolorproperty);
        map.put("swingangle", swingangleproperty);
        map.put("swingperiod", swingperiodproperty);
        map.put("shiftangle", shiftangleproperty);
        map.put("shiftperiod", shiftperiodproperty);
        map.put("randomshifts", randomshiftsproperty);
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

    public Angle getMeanFlowAngle(Location pos) throws IOException {
        return getFlowwithoutswing(pos).getAngle();
    }

    public Angle getMeanFlowAngle() throws IOException {
        return meanflowangle;
    }

    private Angle calcMeanFlowAngle() throws IOException {
        return SpeedPolar.meanAngle(flowarray);
    }

    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog) {
        if (swingperiodproperty.get() != 0) {
            // as we are using a sine rule for swing - convert to an angle (in radians)
            double radians = Math.toRadians(((double) simulationtime % swingperiodproperty.get()) / swingperiodproperty.get() * 360);
            swingNow = swingangleproperty.getValue().mult(Math.sin(radians));
            timerlog.add(new WindSwingLogEntry(swingNow));
        } else {
            swingNow = ANGLE0;
        }
        // now deal with shifts
        Angle shiftval = ANGLE0;
        boolean shifting = false;
        if (shiftperiodproperty.get() != 0) {
            double delta = randomshiftsproperty.get() ? Math.random() * shiftperiodproperty.get() : simulationtime % shiftperiodproperty.get();
            double quarterPeriod = shiftperiodproperty.get() / 4;
            if (delta < quarterPeriod) {
                shiftval = ANGLE0;
            } else if (delta < quarterPeriod * 2) {
                shiftval = shiftangleproperty.getValue().negate();
            } else if (delta < quarterPeriod * 3) {
                shiftval = ANGLE0;
            } else {
                shiftval = shiftangleproperty.getValue();
            }
            shifting = true;
        }
        if (randomshiftsproperty.get()) {
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

    @Override
    public void draw(SketchPane canvas) throws IOException {
        Location sw = area.getBottomleft();
        double westedge = sw.getX();
        double eastedge = westedge + area.getWidth();
        double southedge = sw.getY();
        double northedge = southedge + area.getHeight();
        if (showflowproperty.get()) { // draw the flow arrows
            //
            double x = westedge + showflowintervalproperty.get();
            while (x < eastedge) {
                double y = southedge + showflowintervalproperty.get();
                while (y < northedge) {
                    Location here = new Location(x, y);
                    canvas.displayWindGraphic(here, getFlow(here), showflowcolorproperty.get());
                    y += showflowintervalproperty.get();
                }
                x += showflowintervalproperty.get();
            }
        }
    }

//    private void displayWindGraphic(SketchWindow canvas, double zoom, double x, double y) throws IOException {
//        GeneralPath p = new GeneralPath();
//        p.moveTo(0, 15);
//        p.lineTo(0, -15);
//        p.moveTo(4, 7);
//        p.lineTo(0, 15);
//        p.lineTo(-4, 7);
//        //
//        AffineTransform xform = gc.getTransform();
//        gc.translate(x, y);
//        gc.scale(1 / pixelsPerMetre, -1 / pixelsPerMetre);
//        SpeedPolar flow = getFlow(new Location(x, y));
//        gc.rotate(flow.getAngle().getRadians());
//        gc.setColor(showflowcolor);
//        gc.setStroke(new BasicStroke(1));
//        gc.draw(p);
//        //
//        gc.setFont(new Font("Sans Serif", Font.PLAIN, 10));
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(1);
//        nf.setMinimumFractionDigits(1);
//        String windspeedText = nf.format(flow.getSpeed());
//        if (flow.getAngle().isPositive()) {
//            gc.translate(-2, 4);
//            gc.rotate(-Math.PI / 2);
//        } else {
//            gc.translate(+2, -15);
//            gc.rotate(Math.PI / 2);
//        }
//        gc.drawString(windspeedText, 0, 0);
//        gc.setTransform(xform);
//    }
    public SpeedPolar getFlow(Location pos) throws IOException {
        SpeedPolar f = getFlowwithoutswing(pos);
        if (swingperiodproperty.get() > 0) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(swingNow));
        }
        if (shiftperiodproperty.get() > 0 || randomshiftsproperty.get()) {
            f = new SpeedPolar(f.getSpeed(), f.getAngle().add(shiftNow));
        }
        return f;
    }
}
