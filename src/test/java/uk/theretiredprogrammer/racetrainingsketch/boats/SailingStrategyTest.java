/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.boats;

import java.io.IOException;
import java.util.function.Supplier;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import static javax.json.JsonValue.FALSE;
import static javax.json.JsonValue.TRUE;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;
import uk.theretiredprogrammer.racetrainingsketch.flows.FlowElement;
import uk.theretiredprogrammer.racetrainingsketch.ui.DefFile;
import uk.theretiredprogrammer.racetrainingsketch.ui.ScenarioElement;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class SailingStrategyTest {
    
    private BoatElement boat;
    private FlowElement wind;

    
    public Decision makeDecision(String filename, Supplier<String>... configs) throws IOException {
        ScenarioElement scenario = new DefFile(filename).parse();
        boat = scenario.getBoat(0);
        wind = scenario.getWind();
        Decision decision = new Decision(boat);
        //
        for (var config : configs) {
            String error = config.get();
            if (error != null) {
                throw new IOException(error);
            }
        }
        //
        Angle winddirection = scenario.getWindflow(boat.getLocation()).getAngle();
        CourseLegWithStrategy leg = new CourseLegWithStrategy(scenario.getCourse().getFirstCourseLeg(),
                scenario.getWindmeanflowangle(),boat.getMetrics(), boat);
        leg.nextTimeInterval(decision, boat, winddirection);
        return decision;
    }
    
   String setwindfrom(int degrees) {
        try {
            wind.change(Json.createObjectBuilder().add("from", degrees).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    String setboatparam(String name, boolean value) {
        try {
            boat.change(Json.createObjectBuilder().add(name, value?TRUE:FALSE).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    String setboatparamstrue(String... names) {
        try {
            JsonObjectBuilder job = Json.createObjectBuilder();
            for (String name: names){
                job.add(name, TRUE);
            }
            boat.change(job.build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    String setboatintvalue(String name, int value) {
        try {
            boat.change(Json.createObjectBuilder().add(name, value).build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    String setboatlocationvalue(String name, double x, double y) {
        try {
            boat.change(Json.createObjectBuilder()
                    .add(name, Json.createArrayBuilder().add(x).add(y).build())
                    .build());
            return null;
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
}
