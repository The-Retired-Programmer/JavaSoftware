/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.awt.Graphics2D;
import java.io.IOException;

/**
 * The Element Class - represents the core class of every simulation object
 * class.
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public abstract class Element {
    
    
// TODO the action key and time based parameter changes have been disabled - needs to be fixed in future
//    private void setParameter(Entry<String, ParameterValue> e) throws IOException {
//        String key = e.getKey();
//        ParameterValue pval = paramstore.get(key);
//        if (pval == null) {
//            throw new IOException("Malformed Definition file - unknown key" + key);
//        }
//        paramstore.put(key, pvale.create(e.getValue().get());
//    }
//    
//     private final Map<String, Entry<String, ParameterValue>>keyactions = new HashMap<>();
//     private final Map<Integer, Entry<String, ParameterValue>>futureactions = new HashMap<>();
//     
//    public void actionKey(String key) throws IOException {
//       Entry<String, ParameterValue> e = keyactions.get(key);
//        if (e != null) {
//            setParameter(e);
//        }
//    }
//    
//    /**
//     * Process any future updates of parameter values which have to be made at
//     * this particular time.
//     *
//     * @param time the time
//     */
//    public void actionFutureParameters(int time) throws IOException {
//        Entry e = futureactions.get(time);
//        if (e != null) {
//            setParameter(e);
//        }
//    }
    
    public void finish(){
    }

    public void timerAdvance(int time) throws IOException{
    }

    public void draw(Graphics2D g2D, double pixelsPerMetre) {
    }
}
