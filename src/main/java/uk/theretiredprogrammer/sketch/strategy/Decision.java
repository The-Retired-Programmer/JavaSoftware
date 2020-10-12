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
package uk.theretiredprogrammer.sketch.strategy;

import uk.theretiredprogrammer.sketch.boats.Boat;
import uk.theretiredprogrammer.sketch.core.Angle;

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
    private Angle angle = null;
    private boolean turndirection = STARBOARD;

    public Decision(Boat boat) {
        this.boat = boat;
    }

    public void setSAILON() {
        set(DecisionAction.SAILON, null, STARBOARD);
    }

    public void setTURN(Angle angle, boolean turndirection) {
        set(DecisionAction.TURN, angle, turndirection);
    }

    public void setMARKROUNDING(Angle angle, boolean turndirection) {
        set(DecisionAction.MARKROUNDING, angle, turndirection);
    }

    public void setSTOP() {
        set(DecisionAction.STOP, null, STARBOARD);
    }

    private void set(DecisionAction action, Angle angle, boolean turndirection) {
        this.action = action;
        this.angle = angle;
        this.turndirection = turndirection;
    }

    public DecisionAction getAction() {
        return action;
    }

    private boolean isRotating() {
        return action.equals(DecisionAction.TURN) || action.equals(DecisionAction.MARKROUNDING);
    }

    public Angle getAngle() {
        return isRotating() ? angle : boat.getProperty().getDirection();
    }

    public boolean isPort() {
        return turndirection;
    }
}
