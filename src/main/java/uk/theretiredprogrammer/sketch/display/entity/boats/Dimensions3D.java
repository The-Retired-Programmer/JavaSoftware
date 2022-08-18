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

public class Dimensions3D {

    private final HullDimensions3D hull;
    private final SparDimensions3D spars;

    public Dimensions3D(HullDimensions3D hull, SparDimensions3D spars) {
        this.hull = hull;
        this.spars = spars;
    }

    public HullDimensions3D getHulldimensions() {
        return hull;
    }

    public SparDimensions3D getSpardimensions() {
        return spars;
    }
}
