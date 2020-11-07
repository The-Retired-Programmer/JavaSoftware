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
package uk.theretiredprogrammer.sketch.display.entity.boats;

import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

public class BoatMetricsBuilder {

    private double length = 3;// metres
    private double width = 1.5;// metres
    private double inertia = 0.5;// fraction of speed lost or gained in changing winds
    private PropertyDegrees maxTurningAnglePerSecond = new PropertyDegrees(20);
    private PropertyDegrees upwindrelative = new PropertyDegrees(50);
    private PropertyDegrees downwindrelative = new PropertyDegrees(180);
    private PerformanceVectors performancevectors;

    public BoatMetricsBuilder length(double length) {
        this.length = length;
        return this;
    }

    public BoatMetricsBuilder width(double width) {
        this.width = width;
        return this;
    }

    public BoatMetricsBuilder inertia(double inertia) {
        this.inertia = inertia;
        return this;
    }

    public BoatMetricsBuilder maxTurningAnglePerSecond(int maxTurningAnglePerSecond) {
        this.maxTurningAnglePerSecond = new PropertyDegrees(maxTurningAnglePerSecond);
        return this;
    }

    public BoatMetricsBuilder upwindrelative(int upwindrelative) {
        this.upwindrelative = new PropertyDegrees(upwindrelative);
        return this;
    }

    public BoatMetricsBuilder downwindrelative(int downwindrelative) {
        this.downwindrelative = new PropertyDegrees(downwindrelative);
        return this;
    }

    public BoatMetricsBuilder performancevectors(PerformanceVectors performancevectors) {
        this.performancevectors = performancevectors;
        return this;
    }

    public BoatMetrics build() {
        if (performancevectors == null) {
            throw new IllegalStateFailure("BoatMetricsBuilder: no performance vectors defined");
        }
        return new BoatMetrics(length, width, inertia, maxTurningAnglePerSecond,
                upwindrelative, downwindrelative, performancevectors);
    }

}
