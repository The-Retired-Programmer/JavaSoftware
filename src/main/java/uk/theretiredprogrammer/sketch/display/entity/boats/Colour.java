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

import javafx.scene.paint.Color;

public enum Colour {
    BLACK (0.05f, 0.25f, Color.BLACK),
    BLUE(0.15f, 0.25f, Color.BLUE),
    BROWN(0.25f, 0.25f, Color.BROWN),
    CYAN(0.35f, 0.25f, Color.CYAN),
    GREEN(0.45f, 0.25f, Color.GREEN),
    MAGENTA(0.55f, 0.25f, Color.MAGENTA),
    ORANGE(0.65f, 0.25f, Color.ORANGE),
    PURPLE(0.75f, 0.25f, Color.PURPLE),
    RED(0.85f, 0.25f, Color.RED),
    YELLOW(0.95f, 0.25f, Color.YELLOW),
    //
    WHITE(0.05f, 0.75f, Color.WHITE),
    SILVER(0.15f, 0.75f, Color.SILVER),
    GOLD(0.25f, 0.75f, Color.GOLD);
    
    private final float yloc;
    private final float xloc;
    private final Color color;
    
    Colour(float yloc, float xloc, Color color){
        this.yloc = yloc;
        this.xloc = xloc;
        this.color = color;
    }
    
    float[] getTexCoords() {
        return new float[]{xloc, yloc};
    }
    
    Color getFXColour() {
        return color;
    }
}
