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

public class HullDimensions3DBuilder {

    // alternative for testing - imaginary
//        int numsurfaces = 2;
//        float[] sectionlocations = new float[] { 0f, 1.2f, 2.5f, 3f};
//        float[][] sectiondimensions = new float[][]{
//            { 0f, 0f, 0f, 0f, 0f, 0.5f},
//            { 0f, -0.05f, 0.3f, 0f, 0.6f, 0.4f},
//            { 0f, -0.1f, 0.4f, 0f, 0.75f, 0.35f},
//            { 0f, 0f, 0.4f, 0.1f, 0.6f, 0.3f}
//        };
//        float[] deckclheights = new float[]{ 0.5f, 0f ,  0f ,0.15f};
//        boolean hardchines = false;
//
    public HullDimensions3D build() {
        return new HullDimensions3D(true, Colour.ORANGE, Colour.BROWN,
                new HullSection3D(0f, 0f, 0.31f, new Point(0f, 0.10f), new Point(0f, 0.2f), new Point(0f, 0.31f)),
                new HullSection3D(0.728f, -0.061f, 0.36f, new Point(0.241f, 0.038f), new Point(0.363f, 0.159f), new Point(0.469f, 0.324f)),
                new HullSection3D(1.338f, -0.105f, 0.39f, new Point(0.398f, -0.014f), new Point(0.506f, 0.097f), new Point(0.619f, 0.334f)),
                new HullSection3D(1.948f, -0.115f, 0.3f, new Point(0.489f, -0.045f), new Point(0.641f, 0.076f), new Point(0.749f, 0.345f)),
                new HullSection3D(2.558f, -0.096f, 0.3f, new Point(0.503f, -0.025f), new Point(0.640f, 0.096f), new Point(0.744f, 0.345f)),
                new HullSection3D(3.168f, -0.051f, 0.3f, new Point(0.458f, 0.013f), new Point(0.550f, 0.116f), new Point(0.630f, 0.305f)),
                new HullSection3D(3.778f, 0f, 0.3f, new Point(0.33f, 0.05f), new Point(0.448f, 0.136f), new Point(0.505f, 0.28f))
        );
    }
}
