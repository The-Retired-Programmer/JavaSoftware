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
package uk.theretiredprogrammer.sketch.decisionslog.entity;

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;

public class DecisionLogEntry extends TimerLogEntry {

    private final Decision.DecisionAction decisionaction;
    private final double decisionangle;
    private final boolean decisionPORT;
    private final String boatname;

    public DecisionLogEntry(String boatname, Decision decision) {
        this.boatname = boatname;
        this.decisionaction = decision.getAction();
        this.decisionangle = decision.getDegrees();
        this.decisionPORT = decision.isPort();
    }

    @Override
    public boolean hasName(String name) {
        return boatname.equals(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("  DECISION (");
        sb.append(boatname);
        sb.append("): ");
        sb.append(decisionaction);
        switch (decisionaction) {
            case TURN:
            case MARKROUNDING:
                sb.append(" to ");
                sb.append(format1dp(decisionangle));
                sb.append("° ");
                sb.append(decisionPORT ? "PORT" : "STARBOARD");
        }
        return sb.toString();
    }
}
