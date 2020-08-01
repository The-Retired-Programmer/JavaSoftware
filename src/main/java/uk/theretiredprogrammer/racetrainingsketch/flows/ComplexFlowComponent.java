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
import uk.theretiredprogrammer.racetrainingsketch.ui.Scenario;

/**
 * The ComplexFlow Class - represents a flow which is described by flows (speed
 * and direction) at the four corners points. Flows within the described area
 * are interpolated.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class ComplexFlowComponent extends FlowComponent {
    
    private SpeedPolar nwflow;
    private SpeedPolar neflow;
    private SpeedPolar seflow;
    private SpeedPolar swflow;
    
    
    public ComplexFlowComponent(JsonObject paramsobj, Scenario scenario) throws IOException {
        super(paramsobj, scenario);
        nwflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "northwestspeed").orElse(0.0),
                Angle.parse(paramsobj, "northwestfrom").orElse(ANGLE0)
        );
        neflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "northeastspeed").orElse(0.0),
                Angle.parse(paramsobj, "northeastfrom").orElse(ANGLE0)
        );
        seflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "southeastspeed").orElse(0.0),
                Angle.parse(paramsobj, "southeastfrom").orElse(ANGLE0)
        );
        swflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "southwestspeed").orElse(0.0),
                Angle.parse(paramsobj, "southwestfrom").orElse(ANGLE0)
        );
    }
    
    @Override
    public void change(JsonObject paramsobj) throws IOException {
        super.change(paramsobj);
        nwflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "northwestspeed").orElse(nwflow.getSpeed()),
                Angle.parse(paramsobj, "northwestfrom").orElse(ANGLE0)
        );
        neflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "northeastspeed").orElse(neflow.getSpeed()),
                Angle.parse(paramsobj, "northeastfrom").orElse(ANGLE0)
        );
        seflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "southeastspeed").orElse(seflow.getSpeed()),
                Angle.parse(paramsobj, "southeastfrom").orElse(ANGLE0)
        );
        swflow = new SpeedPolar(
                DoubleParser.parse(paramsobj, "southwestspeed").orElse(swflow.getSpeed()),
                Angle.parse(paramsobj, "southwestfrom").orElse(ANGLE0)
        );
    }

    @Override
    public SpeedPolar getFlow(Location pos) throws IOException {
        testLocationWithinArea(pos);
        Location bottomleft = getArea().getBottomleft();
        double xfraction = (pos.getX()-bottomleft.getX())/getArea().getWidth();
        double yfraction = (pos.getY()-bottomleft.getY())/getArea().getHeight();
        Location fractions = new Location(xfraction, yfraction);
        return swflow.extrapolate(nwflow, neflow, seflow, fractions);
    }
}
