/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.properties.entity;

import javafx.scene.paint.Color;
import uk.theretiredprogrammer.sketch.core.entity.Angle;
import static uk.theretiredprogrammer.sketch.core.entity.Angle.ANGLE0;

/**
 *
 * @author richard
 */
public class PropertyFlowShifts extends PropertyMap {

    private final Config<PropertyBoolean, Boolean> showflow = new Config<>("showflow", OPTIONAL, (s) -> new PropertyBoolean(s, false));
    private final Config<PropertyDouble, Double> showflowinterval = new Config<>("showflowinterval", OPTIONAL, (s) -> new PropertyDouble(s, 100.0));
    private final Config<PropertyColour, Color> showflowcolour = new Config<>("showflowcolour", OPTIONAL, (s) -> new PropertyColour(s, Color.BLACK));
    private final Config<PropertyAngle, Angle> swingangle = new Config<>("swingangle", OPTIONAL, (s) -> new PropertyAngle(s, ANGLE0));
    private final Config<PropertyInteger, Integer> swingperiod = new Config<>("swingperiod", OPTIONAL, (s) -> new PropertyInteger(s, 0));
    private final Config<PropertyAngle, Angle> shiftangle = new Config<>("shiftangle", OPTIONAL, (s) -> new PropertyAngle(s, ANGLE0));
    private final Config<PropertyInteger, Integer> shiftperiod = new Config<>("shiftperiod", OPTIONAL, (s) -> new PropertyInteger(s, 0));
    private final Config<PropertyBoolean, Boolean> randomshifts = new Config<>("randomshifts", OPTIONAL, (s) -> new PropertyBoolean(s, false));

    public PropertyFlowShifts(String key) {
        setKey(key);
        addConfig(showflow, showflowinterval, showflowcolour,
                swingangle, swingperiod, shiftangle, shiftperiod, randomshifts);
    }

    @Override
    public PropertyFlowShifts get() {
        return this;
    }

    public boolean isShowflow() {
        return showflow.get("PropertyFlowShifts showflow");
    }

    public double getShowflowinterval() {
        return showflowinterval.get("PropertyFlowShifts showflowinterval");
    }

    public Color getShowflowcolour() {
        return showflowcolour.get("PropertyFlowShifts showflowcolour");
    }

    public Angle getSwingangle() {
        return swingangle.get("PropertyFlowShifts swingangle");
    }

    public int getSwingperiod() {
        return swingperiod.get("PropertyFlowShifts swingperiod");
    }

    public Angle getShiftangle() {
        return shiftangle.get("PropertyFlowShifts shiftangle");
    }

    public int getShiftperiod() {
        return shiftperiod.get("PropertyFlowShifts shiftperiod");
    }

    public boolean isRandomshifts() {
        return randomshifts.get("PropertyFlowShifts randomshifts");
    }
}
