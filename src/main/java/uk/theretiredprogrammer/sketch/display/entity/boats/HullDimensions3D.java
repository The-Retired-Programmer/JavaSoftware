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

    public final boolean hashardchines;
    public final HullSection3D[] sections;
    private final Colour hullcolour;
    private final Colour deckcolour;
    private boolean useportstarboardcolours = false;
    
    
    public HullDimensions3D(boolean hashardchines, Colour hullcolour, Colour deckcolour, HullSection3D... sections) {
        this.sections = sections;
        this.hashardchines = hashardchines;
        this.hullcolour = hullcolour;
        this.deckcolour = deckcolour;
    }
    
    public void setUsePortStarboardColours(boolean setting) {
        this.useportstarboardcolours = setting;
    }
    
    public Colour getTransomColour() {
        return hullcolour;
    }
    
    public Colour getPortHullColour() {
        return useportstarboardcolours? Colour.RED : hullcolour;
    }
    
     public Colour getStarboardHullColour() {
        return useportstarboardcolours? Colour.GREEN : hullcolour;
    }
     
    public Colour getPortDeckColour() {
        return useportstarboardcolours? Colour.RED : deckcolour;
    }
    
     public Colour getStarboardDeckColour() {
        return useportstarboardcolours? Colour.GREEN : deckcolour;
    }
}
