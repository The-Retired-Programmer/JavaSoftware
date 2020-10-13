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
package uk.theretiredprogrammer.sketch.core;

/**
 * A Polar location is the relative value (distance, flow etc from a logical
 * origin with direction
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class DistancePolar extends Polar<DistancePolar> {

    private final double distance;

    public DistancePolar(double distance, Angle angle) {
        super(angle);
        this.distance = distance;
    }

    public DistancePolar(Location origin, Location pos) {
        super(new Angle(Math.toDegrees(Math.atan2(pos.getX() - origin.getX(), pos.getY() - origin.getY()))));
        double deltax = (pos.getX() - origin.getX());
        double deltay = (pos.getY() - origin.getY());
        distance = Math.sqrt(deltax * deltax + deltay * deltay);
    }

    public Location polar2Location(Location origin) {
        return new Location(origin.getX() + distance * Math.sin(getAngle().getRadians()),
                origin.getY() + distance * Math.cos(getAngle().getRadians()));
    }

    public double getDistance() {
        return distance;
    }

    @Override
    double getDimension() {
        return distance;
    }

    @Override
    DistancePolar create(double dimension, Angle angle) {
        return new DistancePolar(dimension, angle);
    }
}
