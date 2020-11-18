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

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.control.strategy.GybingDownwindStrategy;
import uk.theretiredprogrammer.sketch.display.control.strategy.OffwindStrategy;
import uk.theretiredprogrammer.sketch.display.control.strategy.Strategy;
import uk.theretiredprogrammer.sketch.display.control.strategy.WindwardStrategy;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.boats.BoatMetrics;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class PropertyLeg implements ModelProperty<PropertyLeg> {

    private static final ObservableList<String> roundingdirections;

    static {
        roundingdirections = FXCollections.observableArrayList();
        roundingdirections.addAll("port", "starboard");
    }

    private final PropertyConstrainedString markname = new PropertyConstrainedString();
    private final PropertyConstrainedString passing = new PropertyConstrainedString(roundingdirections);

    ObservableList<String> marknames;
    private PropertyLocation startfrom;
    private PropertyLocation endat;
    private Marks marks;

    public PropertyLeg() {
        set(null, null);
    }

    public PropertyLeg(Marks marks, ObservableList<String> marknames) {
        setMarksAndNames(marks, marknames);
        set(null, null);
    }

    public PropertyLeg(String mark, String passing, Marks marks, ObservableList<String> marknames) {
        setMarksAndNames(marks, marknames);
        set(mark, passing);
    }

    public void setStartLegLocation(PropertyLocation startfrom) {
        this.startfrom = startfrom;
    }

    void update(PropertyLocation startfrom) {
        this.startfrom = startfrom;
        if (markname.get() != null) {
            this.endat = marks.get(markname.get()).getLocation();
        }
    }

    @Override
    public void setOnChange(Runnable onchange) {
        markname.setOnChange(onchange);
        passing.setOnChange(onchange);
    }

    final void setMarksAndNames(Marks marks, ObservableList<String> marknames) {
        this.marks = marks;
        this.marknames = marknames;
        markname.setConstraints(marknames);
    }

    public final void set(PropertyLeg value) {
        setMarksAndNames(value.marks, value.marknames);
        set(value.markname.get(), value.passing.get());
    }

    public final void set(String mark, String passing) {
        this.markname.set(mark);
        this.passing.set(passing == null ? null : passing.toLowerCase());
        if (markname.get() != null) {
            this.endat = marks.get(markname.get()).getLocation();
        }
    }

    @Override
    public final PropertyLeg parsevalue(JsonValue jvalue) {
        return FromJson.legProperty(jvalue, marks, marknames, roundingdirections);
    }

    @Override
    public JsonArray toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return UI.control(this, marknames, roundingdirections);
    }

    @Override
    public Node getControl(int size) {
        return getControl();
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }

    public String getRoundingdirection() {
        return passing.get();
    }

    public PropertyConstrainedString getRoundingdirectionProperty() {
        return passing;
    }

    public boolean isPortRounding() {
        return passing.get().equals("port");
    }

    public String getMarkname() {
        return markname.get();
    }

    public PropertyConstrainedString getMarknameProperty() {
        return markname;
    }

    @Override
    public String toString() {
        return markname.get() + " to " + passing.get();
    }

    public double getDistanceToEnd(PropertyLocation here) {
        return here.to(endat);
    }

    public PropertyLocation getEndLocation() {
        return endat;
    }

    public PropertyDegrees endLegMeanwinddirection(WindFlow windflow) {
        return windflow.getMeanFlowAngle(endat);
    }

    public PropertyDegrees getAngleofLeg() {
        return startfrom.angleto(endat);
    }
    
    //
    
    public static enum LegType {
        WINDWARD, OFFWIND, GYBINGDOWNWIND, NONE
    }

    public static Strategy get(Strategy clonefrom, Boat boat) {
        if (clonefrom instanceof WindwardStrategy windwardstrategy) {
            return new WindwardStrategy(windwardstrategy, boat);
        } else if (clonefrom instanceof OffwindStrategy offwindstrategy) {
            return new OffwindStrategy(offwindstrategy, boat);
        } else if (clonefrom instanceof GybingDownwindStrategy gybingdownwindstrategy) {
            return new GybingDownwindStrategy(gybingdownwindstrategy, boat);
        } else {
            throw new IllegalStateFailure("Illegal/unknown Strategy");
        }
    }

    public static Strategy get(Boat boat, CurrentLeg leg, WindFlow windflow, WaterFlow waterflow) {
        LegType legtype = getLegType(boat.metrics, leg.getAngleofLeg(), windflow, boat.isReachdownwind());
        switch (legtype) {
            case WINDWARD -> {
                return new WindwardStrategy(boat, leg, windflow, waterflow);
            }
            case OFFWIND -> {
                return new OffwindStrategy(boat, leg, windflow, waterflow);
            }
            case GYBINGDOWNWIND -> {
                return new GybingDownwindStrategy(boat, leg, windflow, waterflow);
            }
            default ->
                throw new IllegalStateFailure("Illegal/unknown LEGTYPE: " + legtype.toString());
        }
    }

    public static LegType getLegType(BoatMetrics metrics, PropertyDegrees legangle, WindFlow windflow, boolean reachesdownwind) {
        if (legangle == null) {
            return LegType.NONE;
        }
        PropertyDegrees legtowind = legangle.absDegreesDiff(windflow.getMeanFlowAngle());
        if (legtowind.lteq(metrics.upwindrelative)) {
            return LegType.WINDWARD;
        }
        if (reachesdownwind && legtowind.gteq(metrics.downwindrelative)) {
            return LegType.GYBINGDOWNWIND;
        }
        return LegType.OFFWIND;
    }
    
    static Optional<Double> getRefDistance(PropertyLocation location, PropertyLocation marklocation, PropertyDegrees refangle) {
        return getRefDistance(location, marklocation, refangle.get());
    }

    public static Optional<Double> getRefDistance(PropertyLocation location, PropertyLocation marklocation, double refangle) {
        PropertyDistanceVector tomark = new PropertyDistanceVector(location, marklocation);
        PropertyDegrees refangle2mark = tomark.getDegreesProperty().absDegreesDiff(refangle);
        if (refangle2mark.gt(90)) {
            return Optional.empty();
        }
        return Optional.of(refdistancetomark(tomark.getDistance(), refangle2mark));
    }

    private static double refdistancetomark(double distancetomark, PropertyDegrees refangle2mark) {
        return distancetomark * Math.cos(refangle2mark.getRadians());
    }

    private static PropertyDegrees refangletomark(PropertyDegrees tomarkangle, PropertyDegrees refangle) {
        return tomarkangle.absDegreesDiff(refangle);
    }
}
