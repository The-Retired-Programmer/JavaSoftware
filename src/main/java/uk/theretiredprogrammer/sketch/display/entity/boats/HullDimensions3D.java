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

public class HullDimensions3D {

    private final int numsurfaces;
    private final float[] sectionlocations;
    private final float[][] sectiondimensions;
    private final float[] deckclheights;
    private final boolean hardchines;
    
    public HullDimensions3D(int numsurfaces, float[] sectionlocations, float[][] sectiondimensions, float[] deckclheights, boolean hardchines) {
        this.numsurfaces = numsurfaces;
        this.sectionlocations = sectionlocations;
        this.sectiondimensions = sectiondimensions;
        this.deckclheights = deckclheights;
        this.hardchines = hardchines;
    }

    public int getNumsurfaces() {
        return numsurfaces;
    }

    public float[] getSectionlocations() {
        return sectionlocations;
    }

    public float[][] getSectiondimensions() {
        return sectiondimensions;
    }

    public float[] getDeckclheights() {
        return deckclheights;
    }

    public boolean isHardchines() {
        return hardchines;
    }
}
