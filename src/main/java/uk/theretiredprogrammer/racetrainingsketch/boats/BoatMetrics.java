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

import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class BoatMetrics {

    private final double length;// metres
    private final double width;// metres
    private final double inertia;// fraction of speed lost or gained in changing winds
    private final Angle maxTurningAnglePerSecond;
    private final Angle upwindrelative;
    private final Angle downwindrelative;
    private final PerformanceVectors performancevectors;

    BoatMetrics(double length, double width, double inertia, Angle maxTurningAnglePerSecond,
            Angle upwindrelative, Angle downwindrelative, PerformanceVectors performancevectors) {
        this.length = length;
        this.width = width;
        this.inertia = inertia;
        this.maxTurningAnglePerSecond = maxTurningAnglePerSecond;
        this.upwindrelative = upwindrelative;
        this.downwindrelative = downwindrelative;
        this.performancevectors = performancevectors;
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

    public double getPotentialBoatSpeed(Angle angle, double windSpeed) {
        return performancevectors.getPotentialBoatSpeed(angle, windSpeed);
    }
}
