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
package uk.theretiredprogrammer.sketch.boats;

import java.io.IOException;
import uk.theretiredprogrammer.sketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatMetricsBuilder {

    private double length = 3;// metres
    private double width = 1.5;// metres
    private double inertia = 0.5;// fraction of speed lost or gained in changing winds
    private Angle maxTurningAnglePerSecond = new Angle(20);
    private Angle upwindrelative = new Angle(50);
    private Angle downwindrelative = new Angle(180);
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
        this.maxTurningAnglePerSecond = new Angle(maxTurningAnglePerSecond);
        return this;
    }

    public BoatMetricsBuilder upwindrelative(int upwindrelative) {
        this.upwindrelative = new Angle(upwindrelative);
        return this;
    }

    public BoatMetricsBuilder downwindrelative(int downwindrelative) {
        this.downwindrelative = new Angle(downwindrelative);
        return this;
    }

    public BoatMetricsBuilder performancevectors(PerformanceVectors performancevectors) {
        this.performancevectors = performancevectors;
        return this;
    }

    public BoatMetrics build() throws IOException {
        if (performancevectors == null) {
            throw new IOException("BoatMetricsBuilder: no performance vectors defined");
        }
        return new BoatMetrics(length, width, inertia, maxTurningAnglePerSecond,
                upwindrelative, downwindrelative, performancevectors);
    }

}
