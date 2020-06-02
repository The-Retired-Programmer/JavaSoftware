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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.BooleanParser;
import uk.theretiredprogrammer.racetrainingsketch.core.ColorParser;
import uk.theretiredprogrammer.racetrainingsketch.core.Location;
import uk.theretiredprogrammer.racetrainingsketch.core.Element;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
import uk.theretiredprogrammer.racetrainingsketch.core.DistancePolar;
import uk.theretiredprogrammer.racetrainingsketch.core.StringParser;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE90MINUS;

/**
 * The Mark Class - represent course marks.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class MarkElement extends Element {

    private static final double SIZE = 1; // set up as 1 metre diameter object

    private final ScenarioElement scenario;

    private final String name;
    private final Location location;
    private final boolean windwardlaylines;
    private final boolean downwindlaylines;

    private final double laylinelength;
    private final Color laylinecolor;
    private final Color color;
    private final boolean leavetoport;
    private final String nextmark;

    /**
     * Constructor
     *
     * @param name the name
     * @param wind the wind flow to be applied
     */
    public MarkElement(JsonObject paramsobj, ScenarioElement scenario) throws IOException {
        name = StringParser.parse(paramsobj, "name")
                .orElseThrow(() -> new IOException("Malformed Definition file - <name> is a mandatory parameter"));
        location = Location.parse(paramsobj, "location").orElse(new Location(0, 0));
        windwardlaylines = BooleanParser.parse(paramsobj, "windwardlaylines").orElse(false);
        downwindlaylines = BooleanParser.parse(paramsobj, "downwindlaylines").orElse(false);
        laylinelength = DoubleParser.parse(paramsobj, "laylinelength").orElse(0.0);
        laylinecolor = ColorParser.parse(paramsobj, "laylinecolour").orElse(Color.black);
        color = ColorParser.parse(paramsobj, "colour").orElse(Color.red);
        leavetoport = BooleanParser.parse(paramsobj, "leavetoport").orElse(false);
        nextmark = StringParser.parse(paramsobj, "name").orElse(null);
        //
        this.scenario = scenario;
    }

    public final String getName() {
        return name;
    }

    public final Location getLocation() {
        return location;
    }

    private boolean getWindwardlaylines() {
        return windwardlaylines;
    }

    private boolean getDownwindlaylines() {
        return downwindlaylines;
    }

    private double getLaylinelength() {
        return laylinelength;
    }

    private Color getLaylinecolor() {
        return laylinecolor;
    }

    private Color getColor() {
        return color;
    }

    public final boolean isPort() {
        return leavetoport;
    }

    private String getNextmark() {
        return nextmark;
    }

    public MarkElement nextMark() {
        return scenario.getMark(nextmark);
    }

    private final static Angle WINDWARDLAYLINEANGLE = new Angle(135);
    private final static Angle LEEWARDLAYLINEANGLE = new Angle(45);

    @Override
    public void draw(Graphics2D g2D, double pixelsPerMetre) throws IOException {
        Angle windAngle = scenario.getWindflow(getLocation()).getAngle();
        DistancePolar toPt = getDownwindlaylines()
                ? new DistancePolar(2 * SIZE, windAngle.add(isPort() ? ANGLE90MINUS : ANGLE90))
                : new DistancePolar(2 * SIZE, windAngle.add(isPort() ? ANGLE90 : ANGLE90MINUS));
        Location laylineBase = toPt.polar2Location(getLocation());
        double radius = pixelsPerMetre > 6
                ? SIZE / 2 * pixelsPerMetre // if it will be visible then draw circle to scale
                : 3; // create visible object
        Ellipse2D s = new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2);
        AffineTransform xform = g2D.getTransform();
        g2D.translate(getLocation().getX(), getLocation().getY());
        g2D.scale(1 / pixelsPerMetre, 1 / pixelsPerMetre);
        g2D.setColor(getColor());
        g2D.draw(s);
        g2D.fill(s);
        g2D.setTransform(xform);
        // now draw the laylines - this are scale independent and set to 1 pixel line
        if (getWindwardlaylines()) {
            pixelLine(g2D, laylineBase,
                    new DistancePolar(getLaylinelength(), windAngle.add(WINDWARDLAYLINEANGLE)),
                    getLaylinecolor(), pixelsPerMetre);
            pixelLine(g2D, laylineBase,
                    new DistancePolar(getLaylinelength(), windAngle.add(WINDWARDLAYLINEANGLE)),
                    getLaylinecolor(), pixelsPerMetre);
        }
        if (getDownwindlaylines()) {
            pixelLine(g2D, laylineBase,
                    new DistancePolar(getLaylinelength(), windAngle.add(LEEWARDLAYLINEANGLE)),
                    getLaylinecolor(), pixelsPerMetre);
            pixelLine(g2D, laylineBase,
                    new DistancePolar(getLaylinelength(), windAngle.sub(LEEWARDLAYLINEANGLE)),
                    getLaylinecolor(), pixelsPerMetre);
        }
    }

    private void pixelLine(Graphics2D g2D, Location laylineBase, DistancePolar line,
            Color laylineColour, double pixelsPerMetre) {
        BasicStroke stroke = new BasicStroke((float) (1f / pixelsPerMetre));
        g2D.setStroke(stroke);
        g2D.setColor(laylineColour);
        Location end = line.polar2Location(laylineBase);
        g2D.draw(new Line2D.Double(laylineBase.getX(), laylineBase.getY(), end.getX(), end.getY()));
    }
}
