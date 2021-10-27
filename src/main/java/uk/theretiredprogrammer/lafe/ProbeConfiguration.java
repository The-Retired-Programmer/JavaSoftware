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

import java.text.MessageFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static uk.theretiredprogrammer.lafe.ProbeConfiguration.HzUnits.HZ;
import static uk.theretiredprogrammer.lafe.ProbeConfiguration.SampleEndMode.MANUAL;
import static uk.theretiredprogrammer.lafe.ProbeConfiguration.Trigger.FALLING;
import static uk.theretiredprogrammer.lafe.ProbeConfiguration.Trigger.RISING;

public class ProbeConfiguration {

    public final IntegerProperty firstpin = new SimpleIntegerProperty(15);
    public final IntegerProperty pins = new SimpleIntegerProperty(1);
    public final IntegerProperty speed = new SimpleIntegerProperty(9600);
    public final ObjectProperty<HzUnits> speedunit = new SimpleObjectProperty<HzUnits>(HZ);
    public final IntegerProperty speedmultiplier = new SimpleIntegerProperty(2);
    public final BooleanProperty st_enabled = new SimpleBooleanProperty(true);
    public final IntegerProperty st_pin = new SimpleIntegerProperty(15);
    public final ObjectProperty<Trigger> st_level = new SimpleObjectProperty<Trigger>(FALLING);
    public final BooleanProperty et_enabled = new SimpleBooleanProperty(false);
    public final IntegerProperty et_pin = new SimpleIntegerProperty(15);
    public final ObjectProperty<Trigger> et_level = new SimpleObjectProperty<Trigger>(RISING);
    public final ObjectProperty<SampleEndMode> sampleendmode = new SimpleObjectProperty<SampleEndMode>(MANUAL);
    public final IntegerProperty samplesize = new SimpleIntegerProperty(1280);
    public final StringProperty probetype = new SimpleStringProperty("Unknown");
    public final StringProperty probeversion = new SimpleStringProperty("???");
    
    public final IntegerProperty sqw_firstpin = new SimpleIntegerProperty(19);
    public final IntegerProperty sqw_speed = new SimpleIntegerProperty(1);
    public final ObjectProperty<HzUnits> sqw_speedunit = new SimpleObjectProperty<HzUnits>(HZ);
    

    public String getprobecommand(String command) {
        return MessageFormat.format("{0}-{1,number,#}-{2,number,#}-"
                + "{3,number,#}-"
                + "{4,number,#}-{5,number,#}-{6,number,#}-"
                + "{7,number,#}-{8,number,#}-{9,number,#}-"
                + "{10,number,#}-{11,number,#}",
                command, firstpin.get(), pins.get(),
                speedcalculation(),
                st_enabled.get() ? 1 : 0, st_pin.get(), st_level.get().ordinal(),
                et_enabled.get() ? 1 : 0, et_pin.get(), et_level.get().ordinal(),
                sampleendmode.get().ordinal(), samplesize.get());
    }

    private int speedcalculation() {
        return speed.get() * speedmultiplier.get() * speedunit.get().getMultiplier();
    }
    
    public String getSquareWaveCommand(String command, boolean on) {
        return MessageFormat.format("{0}-{1,number,#}-"
                + "{2,number,#}-{3,number,#}",
                command, on ? 1: 0,
                sqw_firstpin.get(), sqw_speedcalculation());
    }
    
    private int sqw_speedcalculation() {
        return sqw_speed.get() * sqw_speedunit.get().getMultiplier();
    }

    public enum HzUnits {
        HZ(1, "Hz"),
        KHZ(1000, "KHz"),
        MHZ(1000000, "MHz");

        private final String fordisplay;
        private final int multiplier;

        HzUnits(int multiplier, String fordisplay) {
            this.multiplier = multiplier;
            this.fordisplay = fordisplay;
        }

        @Override
        public String toString() {
            return fordisplay;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    public enum Trigger {
        LOW("Low"),
        HIGH("High"),
        FALLING("Falling"),
        RISING("Rising");

        private final String fordisplay;

        Trigger(String fordisplay) {
            this.fordisplay = fordisplay;
        }

        @Override
        public String toString() {
            return fordisplay;
        }
    }

    public enum SampleEndMode {
        MANUAL("Manual"),
        BUFFERFULL("Buffer Full"),
        EVENTINWINDOW1("Event in Window 1"),
        EVENTINWINDOW2("Event in Window 2"),
        EVENTINWINDOW3("Event in Window 3"),
        EVENTINWINDOW4("Event in Window 4");
    
        private final String fordisplay;

        SampleEndMode(String fordisplay) {
            this.fordisplay = fordisplay;
        }

        @Override
        public String toString() {
            return fordisplay;
        }
    }
}
