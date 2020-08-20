/*
 * Copyright 2020 richard linsdale.
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
package uk.theretiredprogrammer.sketch.boats;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.sketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PerformanceVectorsBuilder {

    private final Map<Double, double[]> vectors = new HashMap<>();
    private Angle[] angles;
    private int anglescount = 0;

    public PerformanceVectorsBuilder defineAngles(int[] inputangles) throws IOException {
        if (anglescount != 0) {
            throw new IOException("PerformanceVectorsBuilder: duplicate angles definition");
        }
        anglescount = inputangles.length;
        angles = new Angle[anglescount];
        for (int i = 0; i < anglescount; i++) {
            angles[i] = new Angle(inputangles[i]);
        }
        return this;
    }

    public PerformanceVectorsBuilder vector(double windspeed, double[] vector) throws IOException {
        if (anglescount == 0) {
            throw new IOException("PerformanceVectorsBuilder: can't define vectors prior to defining angles");
        }
        if (vector.length != anglescount) {
            throw new IOException("PerformanceVectorsBuilder: vector length not equal to angles length");
        }
        vectors.put(windspeed, vector);
        return this;
    }

    public PerformanceVectors build() throws IOException {
        if (anglescount == 0) {
            throw new IOException("PerformanceVectorsBuilder: no angles defined");
        }
        int vectorssize = vectors.size();
        if (vectors.isEmpty()) {
            throw new IOException("PerformanceVectorsBuilder: no vectors defined");
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
