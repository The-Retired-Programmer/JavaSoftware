/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

public class Decision {

    public static final boolean PORT = true;
    public static final boolean STARBOARD = false;

    public enum DecisionAction {
        SAILON, STOP, MARKROUNDING, TURN
    }

    private DecisionAction action = DecisionAction.SAILON;
    private PropertyDegrees degrees = new PropertyDegrees(0);
    private boolean turndirection = STARBOARD;

    public void setSAILON(PropertyDegrees currentdirection) {
        set(DecisionAction.SAILON, currentdirection, STARBOARD);
    }

    public void setTURN(PropertyDegrees degrees, boolean turndirection) {
        set(DecisionAction.TURN, degrees, turndirection);
    }

    public void setMARKROUNDING(PropertyDegrees degrees, boolean turndirection) {
        set(DecisionAction.MARKROUNDING, degrees, turndirection);
    }

    public void setSTOP(PropertyDegrees currentdirection) {
        set(DecisionAction.STOP, currentdirection, STARBOARD);
    }

    private void set(DecisionAction action, PropertyDegrees degrees, boolean turndirection) {
        this.action = action;
        this.degrees.set(degrees);
        this.turndirection = turndirection;
    }

    public DecisionAction getAction() {
        return action;
    }

    public PropertyDegrees getDegreesProperty() {
        return degrees;
    }

    public double getDegrees() {
        return degrees.get();
    }

    public boolean isPort() {
        return turndirection;
    }
}
