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
package uk.theretiredprogrammer.lafe;

public class ProbeDefinitions {

    public final String probename;
    public final int minPin;
    public final long minFrequence;
    public final int maxPin;
    public final long maxFrequency;
    public final long maxSamplesize;
    
    public ProbeDefinitions(
            String probename,
            int minPin, int maxPin,
            long minFrequency, long maxFrequency,
            long maxSamplesize
    ){
        this.probename = probename;
        this.minPin = minPin;
        this.maxPin   = maxPin;
        this.minFrequence = minFrequency;
        this.maxFrequency = maxFrequency;
        this.maxSamplesize = maxSamplesize;
    }
}
