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
package uk.theretiredprogrammer.sketch.display.entity.course;

import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.INSIGNIFICANT;
import static uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance.MAJOR;

public class Decision {

    public static final boolean PORT = true;
    public static final boolean STARBOARD = false;

    public enum Importance {
        MAJOR, MINOR, INSIGNIFICANT
    }

    public enum DecisionAction {
        SAILON, STOP, MARKROUNDING, TURN
    }

    private DecisionAction action = DecisionAction.SAILON;
    private PropertyDegrees degrees = new PropertyDegrees(0);
    private boolean turndirection = STARBOARD;
    private Importance importance = INSIGNIFICANT;
    private String reason;

    public void setSAILON(PropertyDegrees currentdirection) {
        set(DecisionAction.SAILON, currentdirection, STARBOARD, INSIGNIFICANT, "Sail On");
    }

    public void setTURN(PropertyDegrees degrees, boolean turndirection, Importance importance, String reason) {
        set(DecisionAction.TURN, degrees, turndirection, importance, reason);
    }

    public void setMARKROUNDING(PropertyDegrees degrees, boolean turndirection, Importance importance, String reason) {
        set(DecisionAction.MARKROUNDING, degrees, turndirection, importance, reason);
    }

    public void setSTOP(PropertyDegrees currentdirection) {
        set(DecisionAction.STOP, currentdirection, STARBOARD, MAJOR, "Stop");
    }

    private void set(DecisionAction action, PropertyDegrees degrees, boolean turndirection,
            Importance importance, String reason) {
        this.action = action;
        this.degrees.set(degrees);
        this.turndirection = turndirection;
        this.importance = importance;
        this.reason = reason;
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

    public Importance getImportance() {
        return importance;
    }

    public String getReason() {
        return reason;
    }
}
