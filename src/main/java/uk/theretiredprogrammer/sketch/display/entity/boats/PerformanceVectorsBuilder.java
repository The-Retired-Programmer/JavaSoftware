/*
 * Copyright 2020 rRichard Linsdale (richard at theretiredprogrammer.uk).
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

public class PerformanceVectorsBuilder {

    private final Map<Double, double[]> vectors = new HashMap<>();
    private PropertyDegrees[] angles;
    private int anglescount = 0;

    public PerformanceVectorsBuilder defineAngles(int[] inputangles) {
        if (anglescount != 0) {
            throw new IllegalStateFailure("PerformanceVectorsBuilder: duplicate angles definition");
        }
        anglescount = inputangles.length;
        angles = new PropertyDegrees[anglescount];
        for (int i = 0; i < anglescount; i++) {
            angles[i] = new PropertyDegrees(inputangles[i]);
        }
        return this;
    }

    public PerformanceVectorsBuilder vector(double windspeed, double[] vector) {
        if (anglescount == 0) {
            throw new IllegalStateFailure("PerformanceVectorsBuilder: can't define vectors prior to defining angles");
        }
        if (vector.length != anglescount) {
            throw new IllegalStateFailure("PerformanceVectorsBuilder: vector length not equal to angles length");
        }
        vectors.put(windspeed, vector);
        return this;
    }

    public PerformanceVectors build() {
        if (anglescount == 0) {
            throw new IllegalStateFailure("PerformanceVectorsBuilder: no angles defined");
        }
        int vectorssize = vectors.size();
        if (vectors.isEmpty()) {
            throw new IllegalStateFailure("PerformanceVectorsBuilder: no vectors defined");
        }
        double[][] vectorsarray = new double[vectorssize + 2][anglescount];
        double[] windspeeds = new double[vectorssize + 2];
        windspeeds[0] = 0;
        windspeeds[vectorssize + 1] = 100; //backstop
        List<Double> speeds = vectors.keySet().stream().sorted().collect(Collectors.toList());
        int i = 1;
        for (double speed : speeds) {
            windspeeds[i++] = speed;
        }
        // zero wind
        int j;
        for (j = 0; j < anglescount; j++) {
            vectorsarray[0][j] = 0;
        }
        for (i = 0; i < vectorssize; i++) {
            double speed = speeds.get(i);
            double[] vector = vectors.get(speed);
            for (j = 0; j < anglescount; j++) {
                vectorsarray[i + 1][j] = vector[j];
            }
        }
        //backstop
        double speed = speeds.get(vectorssize - 1);
        double[] vector = vectors.get(speed);
        for (j = 0; j < anglescount; j++) {
            vectorsarray[vectorssize + 1][j] = vector[j];
        }

        return new PerformanceVectors(vectorsarray, angles, windspeeds);
    }

}
