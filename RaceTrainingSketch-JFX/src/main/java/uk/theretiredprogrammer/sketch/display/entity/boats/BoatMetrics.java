/*
 * Copyright 2020-2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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

import uk.theretiredprogrammer.sketch.core.entity.Angle;

public class BoatMetrics {

    public final double length;// metres
    public final double width;// metres
    private final double inertia;// fraction of speed lost or gained in changing winds
    private final Angle maxTurningAnglePerSecond;
    public final Angle upwindrelative;
    public final Angle downwindrelative;
    private final PerformanceVectors performancevectors;
    private final Dimensions3D dimensions3d;

    BoatMetrics(double length, double width, double inertia, Angle maxTurningAnglePerSecond,
            Angle upwindrelative, Angle downwindrelative, PerformanceVectors performancevectors,
            Dimensions3D dimensions3d) {
        this.length = length;
        this.width = width;
        this.inertia = inertia;
        this.maxTurningAnglePerSecond = maxTurningAnglePerSecond;
        this.upwindrelative = upwindrelative;
        this.downwindrelative = downwindrelative;
        this.performancevectors = performancevectors;
        this.dimensions3d = dimensions3d;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getInertia() {
        return inertia;
    }

    public Angle getMaxTurningAnglePerSecond() {
        return maxTurningAnglePerSecond;
    }

    public Angle getUpwindrelative() {
        return upwindrelative;
    }

    public Angle getDownwindrelative() {
        return downwindrelative;
    }

    public PerformanceVectors getPerformancevectors() {
        return performancevectors;
    }

    public double getPotentialBoatSpeed(Angle degrees, double windSpeed) {
        return performancevectors.getPotentialBoatSpeed(degrees, windSpeed);
    }
    
    public Dimensions3D getDimensions3D() {
        return dimensions3d;
    }
}
