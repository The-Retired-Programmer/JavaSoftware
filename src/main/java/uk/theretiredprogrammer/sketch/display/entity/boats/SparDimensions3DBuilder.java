/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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

public class SparDimensions3DBuilder {

    // default as SOLO
    private float boomLength = 2.693f;
    private float mastheightatdecklevel = 0.366f;
    private float gooseNeckHeight = 0.902f;
    private float diameter = 0.050f;
    private float mastHeight = 5.932f;
    private float bowtoMast = 0.857f;

    public SparDimensions3DBuilder setBoomLength(float boomLength) {
        this.boomLength = boomLength;
        return this;
    }

    public SparDimensions3DBuilder setGooseNeckHeight(float gooseNeckHeight) {
        this.gooseNeckHeight = gooseNeckHeight;
        return this;
    }

    public SparDimensions3DBuilder setDiameter(float diameter) {
        this.diameter = diameter;
        return this;
    }

    public SparDimensions3DBuilder setMastHeight(float mastHeight) {
        this.mastHeight = mastHeight;
        return this;
    }
    
    public SparDimensions3DBuilder settMastHeightatDeckLevel(float mastheightatdecklevel) {
        this.mastheightatdecklevel = mastheightatdecklevel;
        return this;
    }

    public SparDimensions3DBuilder setBowtoMast(float bowtoMast) {
        this.bowtoMast = bowtoMast;
        return this;
    }

    public SparDimensions3D build() {
        return new SparDimensions3D(bowtoMast, mastheightatdecklevel, mastHeight, diameter, gooseNeckHeight, boomLength);
    }
}
