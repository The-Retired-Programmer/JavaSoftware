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

    // default to SOLO
    private int numsurfaces = 3;
    private float[] sectionlocations = new float[]{0f, 0.728f, 1.338f, 1.948f, 2.558f, 3.168f, 3.778f};
    private float[][] sectiondimensions = new float[][]{
        {0f, 00.0f, 0f, -00.1f, 0f, -00.2f, 0f, -00.31f},
        {0f, 0.061f, 0.241f, -0.038f, 0.363f, -0.159f, 0.469f, -0.324f},
        {0f, 0.105f, 0.398f, 0.014f, 0.506f, -0.097f, 00.619f, -00.334f},
        {0f, 0.115f, 0.489f, 0.045f, 0.641f, -0.076f, 0.749f, -0.345f},
        {0f, 0.096f, 0.503f, 0.025f, 00.640f, -00.096f, 0.744f, -00.345f},// need estimates for upper chine and sheerline
        {0f, 0.051f, 0.458f, -0.013f, 00.550f, -00.116f, 00.630f, -00.305f}, // need estimates for upper chine and sheerline
        {0f, 0f, 0.33f, -0.05f, 0.448f, -0.136f, 0.505f, -0.28f}
    };
    private float[] deckclheights = new float[]{-00.31f, -00.36f, -00.39f, -00.3f, -00.3f, -000.3f, -0.3f};
    private boolean hardchines = true;
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

    public void setNumsurfaces(int numsurfaces) {
        this.numsurfaces = numsurfaces;
    }

    public void setSectionlocations(float[] sectionlocations) {
        this.sectionlocations = sectionlocations;
    }

    public void setSectiondimensions(float[][] sectiondimensions) {
        this.sectiondimensions = sectiondimensions;
    }

    public void setDeckclheights(float[] deckclheights) {
        this.deckclheights = deckclheights;
    }

    public void setHardchines(boolean hardchines) {
        this.hardchines = hardchines;
    }

    public HullDimensions3D build() {
        return new HullDimensions3D(numsurfaces, sectionlocations, sectiondimensions, deckclheights, hardchines);
    }
}
