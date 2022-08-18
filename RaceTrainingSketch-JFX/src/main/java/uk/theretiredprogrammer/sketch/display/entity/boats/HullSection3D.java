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

public class HullSection3D {

    public final float distancefrombow;
    public final float depthatkeel;
    public final float depthatdeckcentreline;
    public final Point[] sectionpoints;

    public HullSection3D(float distancefrombow, float depthatkeel, float depthatdeckcentreline, Point... sectionpoints) {
        this.distancefrombow = distancefrombow;
        this.depthatkeel = depthatkeel;
        this.depthatdeckcentreline = depthatdeckcentreline;
        this.sectionpoints = sectionpoints;
    }
}
