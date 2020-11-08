/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import uk.theretiredprogrammer.sketch.core.entity.PropertyLocation;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import uk.theretiredprogrammer.sketch.display.entity.flows.WaterFlow;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class Laser2 extends Boat {

    public Laser2(Leg firstleg, WindFlow windflow, WaterFlow waterflow) {
        super("laser2",firstleg, windflow, waterflow,getMetrics());
    }
    
    public Laser2(PropertyLocation location, Leg firstleg, WindFlow windflow, WaterFlow waterflow) {
        super("laser2",location, firstleg, windflow, waterflow,getMetrics());
    }
    
    private static BoatMetrics getMetrics() {
               return  new BoatMetricsBuilder()
                        .length(4)
                        .width(1.5)
                        .inertia(0.25)
                        .maxTurningAnglePerSecond(30)
                        .upwindrelative(45)
                        .downwindrelative(135)
                        .performancevectors(
                                new PerformanceVectorsBuilder()
                                        .defineAngles(new int[]{0, 40, 45, 60, 75, 90, 110, 120, 150, 180, 190})
                                        .vector(2, new double[]{-0.3, 0, 1.1, 1.1, 1.5, 2, 2, 2, 1.2, 0.8, 0.8})
                                        .vector(4, new double[]{-0.8, 0, 2.5, 2.9, 2.9, 3.8, 3.5, 3, 2.3, 1.8, 1.8})
                                        .vector(6, new double[]{-1.0, 0, 3.1, 3.1, 4.1, 5.6, 5, 4.2, 3.2, 2.5, 2.5})
                                        .vector(8, new double[]{-1.1, 0, 3.5, 3.5, 4.6, 6.9, 6.5, 5.8, 4.2, 3.6, 3.6})
                                        .vector(10, new double[]{-1.2, 0, 3.9, 3.9, 5.1, 10.9, 10.0, 8, 5, 4.5, 4.5})
                                        .vector(12, new double[]{-1.5, 0, 4.3, 4.3, 5.7, 12.4, 13, 10, 6, 5, 5})
                                        .vector(14, new double[]{-1.5, 0, 5.1, 5.1, 6.5, 13.5, 14.5, 12, 7.5, 5.5, 5.5})
                                        .vector(16, new double[]{-1.5, 0, 5.2, 5.2, 7.0, 14, 17, 14, 9, 7.1, 7.1})
                                        .vector(18, new double[]{-1.5, 0, 5.3, 5.3, 7.1, 14.5, 18, 15.8, 10.2, 7.8, 7.8})
                                        .vector(20, new double[]{-1.5, 0, 5.3, 5.3, 7.1, 15, 19, 17.5, 12, 9, 9})
                                        .build()
                        ).build();
    }
    
    public Laser2(String name, Laser2 clonefrom) {
        super(name, clonefrom);
    }
}
