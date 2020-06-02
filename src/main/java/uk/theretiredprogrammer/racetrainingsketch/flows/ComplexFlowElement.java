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

import java.io.IOException;
import javax.json.JsonObject;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import static uk.theretiredprogrammer.racetrainingsketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.racetrainingsketch.core.DoubleParser;
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
public class ComplexFlowElement extends CoreFlowElement {
    
    // TODO - should this be a complex object in UI / system
    private Location northeastposition;
    private Angle northeastfrom;
    private double northeastspeed;
    private Location northwestposition;
    private Angle northwestfrom;
    private double northwestspeed;
    private Location southeastposition;
    private Angle southeastfrom;
    private double southeastspeed;
    private Location southwestposition;
    private Angle southwestfrom;
    private double southwestspeed;
    
    
    public ComplexFlowElement(JsonObject paramsobj, ScenarioElement scenario) throws IOException {
        super(paramsobj, scenario);
        // TODO default positions could be calculated from corners of field of play limits
        northeastposition=Location.parse(paramsobj, "northeastposition").orElse(new Location(0,0));
        northeastfrom=Angle.parse(paramsobj, "northeastfrom").orElse(ANGLE0);
        northeastspeed = DoubleParser.parse(paramsobj, "northeastspeed").orElse(0.0);
        northwestposition=Location.parse(paramsobj, "northwestposition").orElse(new Location(0,0));
        northwestfrom=Angle.parse(paramsobj, "northwestfrom").orElse(ANGLE0);
        northwestspeed = DoubleParser.parse(paramsobj, "northwestspeed").orElse(0.0);
        southeastposition=Location.parse(paramsobj, "southeastposition").orElse(new Location(0,0));
        southeastfrom=Angle.parse(paramsobj, "southeastfrom").orElse(ANGLE0);
        southeastspeed = DoubleParser.parse(paramsobj, "southeastspeed").orElse(0.0);
        southwestposition=Location.parse(paramsobj, "southwestposition").orElse(new Location(0,0));
        southwestfrom=Angle.parse(paramsobj, "southwestfrom").orElse(ANGLE0);
        southwestspeed = DoubleParser.parse(paramsobj, "nsouthwestspeed").orElse(0.0);        
    }
    
    @Override
    public void change(JsonObject paramsobj) throws IOException {
        super.change(paramsobj);
        northeastposition=Location.parse(paramsobj, "northeastposition").orElse(northeastposition);
        northeastfrom=Angle.parse(paramsobj, "northeastfrom").orElse(northeastfrom);
        northeastspeed = DoubleParser.parse(paramsobj, "northeastspeed").orElse(northeastspeed);
        northwestposition=Location.parse(paramsobj, "northwestposition").orElse(northwestposition);
        northwestfrom=Angle.parse(paramsobj, "northwestfrom").orElse(northwestfrom);
        northwestspeed = DoubleParser.parse(paramsobj, "northwestspeed").orElse(northwestspeed);
        southeastposition=Location.parse(paramsobj, "southeastposition").orElse(southeastposition);
        southeastfrom=Angle.parse(paramsobj, "southeastfrom").orElse(southeastfrom);
        southeastspeed = DoubleParser.parse(paramsobj, "southeastspeed").orElse(southeastspeed);
        southwestposition=Location.parse(paramsobj, "southwestposition").orElse(southwestposition);
        southwestfrom=Angle.parse(paramsobj, "southwestfrom").orElse(southwestfrom);
        southwestspeed = DoubleParser.parse(paramsobj, "nsouthwestspeed").orElse(southwestspeed); 
    }
    
    private Location getNortheastposition() {
        return northeastposition;
    }
    
    private Location getNorthwestposition() {
        return northwestposition;
    }
    
    private Location getSoutheastposition() {
        return southeastposition;
    }
    
    private Location getSouthwestposition() {
        return southwestposition;
    }
    
    private double getNortheastspeed() {
        return northeastspeed;
    }
    
    private double getNorthwestspeed() {
        return northwestspeed;
    }
    
    private double getSoutheastspeed() {
        return southeastspeed;
    }
    
    private double getSouthwestspeed() {
        return southwestspeed;
    }
    
    private Angle getNortheastfrom() {
        return northeastfrom;
    }
    
    private Angle getNorthwestfrom() {
        return northwestfrom;
    }
    
    private Angle getSoutheastfrom() {
        return southeastfrom;
    }
    
    private Angle getSouthwestfrom() {
        return southwestfrom;
    }


    @Override
        SpeedPolar getFlowWithoutSwing(Location pos) {
        Location northwest = getNorthwestposition();
        Location northeast = getNortheastposition();
        Location southwest = getSouthwestposition();
        Location southeast = getSoutheastposition();
        SpeedPolar northwestFlow = new SpeedPolar(getNorthwestspeed(), getNorthwestfrom());
        SpeedPolar northeastFlow = new SpeedPolar( getNortheastspeed(), getNortheastfrom());
        SpeedPolar southwestFlow = new SpeedPolar( getSouthwestspeed(), getSouthwestfrom());
        SpeedPolar southeastFlow = new SpeedPolar(getSoutheastspeed(), getSoutheastfrom());
        // the flow is modelled over 9 sectors
        // first we can sort out the corner 4 sectors
        if (pos.getX() <= northwest.getX() && pos.getY() >= northwest.getY()) {
            return northwestFlow;
        }
        if (pos.getX() >= northeast.getX() && pos.getY() >= northeast.getY()) {
            return northeastFlow;
        }
        if (pos.getX() <= southwest.getX() && pos.getY() <= southwest.getY()) {
            return southwestFlow;
        }
        if (pos.getX() >= southeast.getX() && pos.getY() <= southeast.getY()) {
            return southeastFlow;
        }
        // calculate the locations and flows at the edges of the centre sector
        Location west = new Location(getX(northwest, southwest, pos.getY()), pos.getY());
        SpeedPolar westFlow = flowAtY(northwest, northwestFlow, southwest, southwestFlow, pos.getY());
        Location east = new Location(getX(northeast, southeast, pos.getY()), pos.getY());
        SpeedPolar eastFlow = flowAtY(northeast, northeastFlow, southeast, southeastFlow, pos.getY());
        Location north = new Location(pos.getX(), getY(northeast, northwest, pos.getX()));
        SpeedPolar northFlow = flowAtX(northeast, northeastFlow, northwest, northwestFlow, pos.getX());
        Location south = new Location(pos.getX(), getY(southeast, southwest, pos.getX()));
        SpeedPolar southFlow = flowAtX(southeast, southeastFlow, southwest, southwestFlow, pos.getX());
        // now look for the 4 centre out sectors
        if (pos.getY() > north.getY()) {
            return northFlow;
        }
        if (pos.getY() < south.getY()) {
            return southFlow;
        }
        if (pos.getX() < west.getX()) {
            return westFlow;
        }
        if (pos.getX() > east.getX()) {
            return eastFlow;
        }
        // must be the cental sector
        return flowAtY(north, northFlow, south, southFlow, pos.getY());
    }
    
    private double getX(Location to, Location from, double y) {
        return from.getX() + (to.getX() - from.getX()) * (y - from.getY()) / (to.getY() - from.getY());
    }

    private double getY(Location to, Location from, double x) {
        return from.getY() + (to.getY() - from.getY()) * (x - from.getX()) / (to.getX() - from.getX());
    }
    // TODO  use polar maths
    private SpeedPolar flowAtX(Location to, SpeedPolar toFlow, Location from, SpeedPolar fromFlow, double x) {
        double ratio = (x - from.getX()) / (to.getX() - from.getX());
        return new SpeedPolar(
                fromFlow.getSpeed() + ratio * (toFlow.getSpeed() - fromFlow.getSpeed()),
                fromFlow.getAngle().add(toFlow.getAngle().sub(fromFlow.getAngle()).mult(ratio))
        );
    }
    // TODO  use polar maths
    private SpeedPolar flowAtY(Location to, SpeedPolar toFlow, Location from, SpeedPolar fromFlow, double y) {
        double ratio = (y - from.getY()) / (to.getY() - from.getY());
        return new SpeedPolar(
                fromFlow.getSpeed() + ratio * (toFlow.getSpeed() - fromFlow.getSpeed()),
                fromFlow.getAngle().add(toFlow.getAngle().sub(fromFlow.getAngle()).mult(ratio))
        );
    }

    /**
     * Get the Average flow across the total area.
     * 
     * @return the average flow
     */
    @Override
    public Angle getMeanFlowAngle() {
        return getNortheastfrom().add(getNorthwestfrom())
                .add(getSoutheastfrom()).add(getSouthwestfrom().div(4));
    }
}
