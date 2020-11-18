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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Decision {

    public static final boolean PORT = true;
    public static final boolean STARBOARD = false;

    public enum DecisionAction {
        SAILON, STOP, MARKROUNDING, TURN
    }

    private final Boat boat;

    private DecisionAction action = DecisionAction.SAILON;
    private PropertyDegrees degrees = null;
    private boolean turndirection = STARBOARD;

    public Decision(Boat boat) {
        this.boat = boat;
    }
    
    public Decision(Boat boat, Decision clonefrom) {
        this.boat = boat;
        this.action = clonefrom.action;
        this.degrees = clonefrom.degrees;
        this.turndirection = clonefrom.turndirection;
    }

    public void setSAILON() {
        set(DecisionAction.SAILON, null, STARBOARD);
    }

    public void setTURN(PropertyDegrees degrees, boolean turndirection) {
        set(DecisionAction.TURN, degrees, turndirection);
    }

    public void setMARKROUNDING(PropertyDegrees degrees, boolean turndirection) {
        set(DecisionAction.MARKROUNDING, degrees, turndirection);
    }

    public void setSTOP() {
        set(DecisionAction.STOP, null, STARBOARD);
    }

    private void set(DecisionAction action, PropertyDegrees degrees, boolean turndirection) {
        this.action = action;
        this.degrees = degrees;
        this.turndirection = turndirection;
    }

    public DecisionAction getAction() {
        return action;
    }

    private boolean isRotating() {
        return action.equals(DecisionAction.TURN) || action.equals(DecisionAction.MARKROUNDING);
    }

    public PropertyDegrees getDegreesProperty() {
        return isRotating() ? degrees : boat.getDirection();
    }

    public double getDegrees() {
        return getDegreesProperty().get();
    }

    public boolean isPort() {
        return turndirection;
    }
}
