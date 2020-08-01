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
package uk.theretiredprogrammer.racetrainingsketch.timerlog;

import java.util.ArrayList;
import java.util.List;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class TimerLog {

    private String mmsstime;
    private final List<TimerLogEntry> log = new ArrayList<>();

    public void setTime(String mmsstime) {
        this.mmsstime = mmsstime;
    }

    public void add(TimerLogEntry entry) {
        entry.setTime(mmsstime);
        log.add(entry);
    }

    public void write2output(String title) {
        InputOutput io = IOProvider.getDefault().getIO(title, false);
        io.select();
        try ( OutputWriter msg = io.getOut()) {
            log.stream().forEach(entry -> msg.println(entry.toString()));
        }
    }
    
    public void writefiltered2output(String title, String boatname) {
        InputOutput io = IOProvider.getDefault().getIO(title, false);
        io.select();
        try ( OutputWriter msg = io.getOut()) {
            log.stream()
                    .filter(entry -> entry.hasName(boatname))
                    .forEach(entry -> msg.println(entry.toString()));
        }
    }
}
