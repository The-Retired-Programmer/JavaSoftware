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
package uk.theretiredprogrammer.racetrainingsketch.strategy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import uk.theretiredprogrammer.racetrainingsketch.boats.BoatElement;
import uk.theretiredprogrammer.racetrainingsketch.core.Angle;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Decision {
    
    public enum TurnDirection {
        CLOCKWISE , ANTICLOCKWISE
    }
    
    public enum DecisionAction {
        SAILON, STOP, MARKROUNDING, TURN
    }
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final BoatElement boat;
    
    private DecisionAction action = DecisionAction.SAILON;
    private Angle angle = null;
    private TurnDirection turndirection = TurnDirection.CLOCKWISE;
    
    public Decision(BoatElement boat) {
        this.boat = boat;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
        pcs.removePropertyChangeListener(listener);
    }
    
    public void setSAILON(){
        set(DecisionAction.SAILON, null, TurnDirection.CLOCKWISE);
    }
    
    public void setTURN(Angle angle, TurnDirection turndirection ){
        set(DecisionAction.TURN, angle, turndirection);
    }
    
    public void setMARKROUNDING(Angle angle, TurnDirection turndirection){
        set(DecisionAction.MARKROUNDING, angle, turndirection);
    }
    
    public void setSTOP(){
        set(DecisionAction.STOP, null, TurnDirection.CLOCKWISE);
    }
    
    private void set(DecisionAction action, Angle angle, TurnDirection turndirection) {
        DecisionAction oldaction = this.action;
        this.action = action;
        this.angle = angle;
        this.turndirection = turndirection;
        if (!action.equals(oldaction)) {
            pcs.firePropertyChange("ACTION", oldaction, action);
        }
    }

    public DecisionAction getAction() {
        return action;
    }
    
    private boolean isRotating() {
        return action.equals(DecisionAction.TURN) || action.equals(DecisionAction.MARKROUNDING);
    }
    
    public void UpdateAngle(Angle angle) {
        this.angle = angle;
    }

    
    public Angle getAngle() {
        return isRotating() ? angle: boat.getDirection();
    }

    public boolean isClockwise() {
        return turndirection.equals(TurnDirection.CLOCKWISE);
    }
}
