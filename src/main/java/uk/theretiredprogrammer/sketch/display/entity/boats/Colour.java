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

public enum Colour {
    BLACK (0.05f, 0.25f),
    BLUE(0.15f, 0.25f),
    BROWN(0.25f, 0.25f),
    CYAN(0.35f, 0.25f),
    GREEN(0.45f, 0.25f),
    MAGENTA(0.55f, 0.25f),
    ORANGE(0.65f, 0.25f),
    PURPLE(0.75f, 0.25f),
    RED(0.85f, 0.25f),
    YELLOW(0.95f, 0.25f),
    //
    WHITE(0.05f, 0.75f);
    
    private final float yloc;
    private final float xloc;
    
    Colour(float yloc, float xloc){
        this.yloc = yloc;
        this.xloc = xloc;
    }
    
    float[] getTexCoords() {
        return new float[]{xloc, yloc};
    }
}
