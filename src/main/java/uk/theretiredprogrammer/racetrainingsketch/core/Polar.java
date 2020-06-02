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
package uk.theretiredprogrammer.racetrainingsketch.core;

/**
 * A Polar
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
abstract class Polar<P extends Polar> {

    private final Angle angle;
    
    abstract P create(double dimension, Angle angle);
    
    abstract double getDimension();
    
    Polar(Angle angle){
        this.angle = angle;
    }
    
    public P add(P p) {
        double dimension = getDimension();
        double pdimension = p.getDimension();
        double x = dimension * Math.sin(angle.getRadians()) + pdimension * Math.sin(p.getAngle().getRadians());
        double y = dimension* Math.cos(angle.getRadians()) + pdimension * Math.cos(p.getAngle().getRadians());
        //
        
        return create(
                Math.sqrt(x * x + y * y),
                new Angle( (int)Math.toDegrees(Math.atan2(x, y)))); 
    }

    public P subtract(P p) {
        double dimension = getDimension();
        double pdimension = p.getDimension();
        double x = dimension * Math.sin(angle.getRadians()) - pdimension * Math.sin(p.getAngle().getRadians());
        double y = dimension* Math.cos(angle.getRadians()) - pdimension * Math.cos(p.getAngle().getRadians());
        //
        return create(
                Math.sqrt(x * x + y * y),
                new Angle( (int)Math.toDegrees(Math.atan2(x, y))));
    }

    public P mult(double multiplier) {
        return multiplier < 0
                ? create(-getDimension() * multiplier, angle.inverse())
                : create(getDimension() * multiplier, angle);
    }
    
    public Angle angleDiff(Angle angle) {
        return this.angle.angleDiff(angle);
    }

    public Angle angleDiff(Polar p) {
        return angle.angleDiff(p.angle);
    }

    public Angle getAngle() {
        return angle;
    }
}
