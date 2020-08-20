/*
 * Copyright 2020 richard.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE0;
import uk.theretiredprogrammer.sketch.core.Location;
import uk.theretiredprogrammer.sketch.core.SpeedPolar;

/**
 *
 * @author richard
 */
public class FlowComponentSet  {
    
    private final List<FlowComponent> flows = new ArrayList<>();
    
    public void change(JsonObject params, String name) throws IOException {
        for (FlowComponent flow : flows) {
            if (flow.getName().equals(name)) {
                flow.change(params);
            }
        }
    }
    
    public void change(JsonObject params) throws IOException {
        change(params,0);
    }
        
    public void change(JsonObject params, int zlevel) throws IOException {        
        for (FlowComponent flow : flows) {
            if (flow.getZlevel() == zlevel) {
                flow.change(params);
            }
        }
    }
    
    public void add(FlowComponent flow){
        flows.add(flow);
    }
    
    public SpeedPolar getFlow(Location pos) throws IOException {
        int zlevel  = -1;
        FlowComponent flowtouse = null;
        for (FlowComponent flow : flows) {
            if (flow.getArea().isWithinArea(pos)) {
                if (flow.getZlevel() > zlevel) {
                    zlevel = flow.getZlevel();
                    flowtouse = flow;
                }
            }
        }
        if (flowtouse == null) {
            throw new IOException("Undefined flow component as location "+pos);
        }
        return flowtouse.getFlow(pos);
    }
    
    public Angle meanWindAngle() {
        for (FlowComponent flow : flows) {
            Angle res = flow.meanWindAngle();
            if (res != null) {
                return res;
            }
        }
        return null;
    }
}
