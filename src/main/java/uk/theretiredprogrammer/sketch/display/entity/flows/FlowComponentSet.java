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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.Comparator;
import java.util.function.Supplier;
import javafx.collections.ListChangeListener;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.ModelList;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;

public class FlowComponentSet extends ModelList<FlowComponent> {

    final static int WIDTHSTEPS = 100;
    final static int HEIGHTSTEPS = 100;

    private final Supplier<Area> getdisplayarea;
    private final SpeedVector[][] flowarray = new SpeedVector[WIDTHSTEPS + 1][HEIGHTSTEPS + 1];
    private double wstepsize;
    private double hstepsize;

    private Angle meanflowangle = new Angle();

    public FlowComponentSet(Supplier<Area> getdisplayarea) {
        this.getdisplayarea = getdisplayarea;
        setOnChange(() -> flowchange());
        addListChangeListener((ListChangeListener<FlowComponent>) (c) -> flowchange());
    }

    @Override
    protected FlowComponent createAndParse(JsonValue jval) {
        if (jval.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new ParseFailure("Malformed Definition File - array contains items other than Objects");
        }
        JsonObject jobj = (JsonObject) jval;
        FlowComponent flowc = FlowComponent.factory(
                jobj.getString("type", "<undefined>"),
                getdisplayarea
        );
        flowc.parse(jobj);
        return flowc;
    }

    private void flowchange() {
        double hpos = getdisplayarea.get().getLocationProperty().getY();
        double wpos = getdisplayarea.get().getLocationProperty().getX();
        hstepsize = getdisplayarea.get().getHeight() / HEIGHTSTEPS;
        wstepsize = getdisplayarea.get().getWidth() / WIDTHSTEPS;
        for (int h = 0; h < HEIGHTSTEPS + 1; h++) {
            double y = hpos + hstepsize * h;
            for (int w = 0; w < WIDTHSTEPS + 1; w++) {
                double x = wpos + wstepsize * w;
                flowarray[w][h] = getFlow(new Location(x, y));
            }
        }
        meanflowangle = meanWindAngle(); // check if we are using a forced mean
        if (meanflowangle == null) {
            meanflowangle = meanAngle(flowarray); // if not then calculate it
        }
    }

    private SpeedVector getFlow(Location pos) {
        return this.stream()
                .filter(flowcomponent -> flowcomponent.getArea().isWithinArea(pos))
                .sorted(Comparator.comparingInt(FlowComponent::getZlevel).reversed())
                .findFirst()
                .map(flowcomponent -> flowcomponent.getFlow(pos))
                .orElse(new SpeedVector());
    }

    private Angle meanWindAngle() {
        return stream().map(flowcomponent -> flowcomponent.meanWindAngle())
                .filter(angle -> angle != null)
                .findFirst().orElse(null);
    }

    private Angle meanAngle(SpeedVector[][] array) {
        double x = 0;
        double y = 0;
        for (SpeedVector[] column : array) {
            for (SpeedVector cell : column) {
                double r = cell.getAngle().getRadians();
                x += Math.sin(r);
                y += Math.cos(r);
            }
        }
        return new Angle(Math.toDegrees(Math.atan2(x, y)));
    }

    SpeedVector getFlowwithoutswing(Location pos) {
        return stream().count() == 0? new SpeedVector() : calculateFlowwithoutswing(pos);
    }
    
    private SpeedVector calculateFlowwithoutswing(Location pos) {
        int w = (int) Math.floor((pos.getX() - getdisplayarea.get().getLocationProperty().getX()) / wstepsize);
        if (w < 0) {
            w = 0;
        }
        if (w > WIDTHSTEPS) {
            w = WIDTHSTEPS;
        }
        int h = (int) Math.floor((pos.getY() - getdisplayarea.get().getLocationProperty().getY()) / hstepsize);
        if (h < 0) {
            h = 0;
        }
        if (h > HEIGHTSTEPS) {
            h = HEIGHTSTEPS;
        }
        return flowarray[w][h];
    }

    Angle getMeanFlowAngle(Location pos) {
        return getFlowwithoutswing(pos).getAngle();
    }

    Angle getMeanFlowAngle() {
        return meanflowangle;
    }
}
