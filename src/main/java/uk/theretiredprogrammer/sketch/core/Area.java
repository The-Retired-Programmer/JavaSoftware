/*
 * Copyright 2020 Richard Linsdale
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
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Area {
    
    private final Location bottomleft;
    private final double width;
    private final double height;
    
    public Area(Location bottomleft, double width, double height){
        this.bottomleft = bottomleft;
        this.width = width;
        this.height = height;
    }
    
    public Location getBottomleft(){
        return bottomleft;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight(){
        return height;
    }
    
    public boolean isWithinArea(Location location) {
        return location.getX() >= bottomleft.getX() && location.getX() <= bottomleft.getX() + width
                && location.getY() >= bottomleft.getY() && location.getY() <= bottomleft.getY() + height;
    }
}
