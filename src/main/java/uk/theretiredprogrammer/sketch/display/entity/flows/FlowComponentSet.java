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
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.ModelList;
import uk.theretiredprogrammer.sketch.core.entity.PropertyArea;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertySpeedVector;

public class FlowComponentSet extends ModelList<FlowComponent> {

    final static int WIDTHSTEPS = 100;
    final static int HEIGHTSTEPS = 100;

    private final Supplier<PropertyArea> getdisplayarea;
    private final PropertySpeedVector[][] flowarray = new PropertySpeedVector[WIDTHSTEPS + 1][HEIGHTSTEPS + 1];
    private double wstepsize;
    private double hstepsize;

    private PropertyDegrees meanflowangle = new PropertyDegrees();

    public FlowComponentSet(Supplier<PropertyArea> getdisplayarea) {
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
                flowarray[w][h] = getFlow(new PropertyLocation(x, y));
            }
        }
        meanflowangle = meanWindAngle(); // check if we are using a forced mean
        if (meanflowangle == null) {
            meanflowangle = meanAngle(flowarray); // if not then calculate it
        }
    }

    private PropertySpeedVector getFlow(PropertyLocation pos) {
        return this.stream()
                .filter(flowcomponent -> flowcomponent.getArea().isWithinArea(pos))
                .sorted(Comparator.comparingInt(FlowComponent::getZlevel).reversed())
                .findFirst()
                .map(flowcomponent -> flowcomponent.getFlow(pos))
                .orElse(new PropertySpeedVector());
    }

    private PropertyDegrees meanWindAngle() {
        return stream().map(flowcomponent -> flowcomponent.meanWindAngle())
                .filter(angle -> angle != null)
                .findFirst().orElse(null);
    }

    private PropertyDegrees meanAngle(PropertySpeedVector[][] array) {
        double x = 0;
        double y = 0;
        for (PropertySpeedVector[] column : array) {
            for (PropertySpeedVector cell : column) {
                double r = cell.getDegreesProperty().getRadians();
                x += Math.sin(r);
                y += Math.cos(r);
            }
        }
        return new PropertyDegrees(Math.toDegrees(Math.atan2(x, y)));
    }

    PropertySpeedVector getFlowwithoutswing(PropertyLocation pos) {
        return stream().count() == 0? new PropertySpeedVector() : calculateFlowwithoutswing(pos);
    }
    
    private PropertySpeedVector calculateFlowwithoutswing(PropertyLocation pos) {
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

    PropertyDegrees getMeanFlowAngle(PropertyLocation pos) {
        return getFlowwithoutswing(pos).getDegreesProperty();
    }

    PropertyDegrees getMeanFlowAngle() {
        return meanflowangle;
    }
}
