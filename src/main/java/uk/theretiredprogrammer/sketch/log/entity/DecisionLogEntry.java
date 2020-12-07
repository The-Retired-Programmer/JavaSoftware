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
package uk.theretiredprogrammer.sketch.log.entity;

import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.display.entity.boats.Boat;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision.DecisionAction;
import uk.theretiredprogrammer.sketch.display.entity.course.Decision.Importance;
import uk.theretiredprogrammer.sketch.display.entity.flows.WindFlow;

public class DecisionLogEntry extends TimerLogEntry {

    private final DecisionAction decisionaction;
    private final double decisionangle;
    private final boolean decisionPORT;
    private final String boatname;
    private final double boatangle;
    private final double boatx;
    private final double boaty;
    private final Importance decisionimportance;
    private final String reason;

    public DecisionLogEntry(Boat boat, WindFlow windflow, Decision decision) {
        this.boatname = boat.getNamed();
        Location loc = boat.getLocation();
        this.boatx = loc.getX();
        this.boaty = loc.getY();
        this.boatangle = boat.getDirection().get();
        this.decisionimportance = decision.getImportance();
        this.decisionaction = decision.getAction();
        this.decisionangle = decision.getDegrees();
        this.decisionPORT = decision.isPort();
        this.reason = decision.getReason();
    }

    @Override
    public boolean hasName(String name) {
        return boatname.equals(name);
    }

    @Override
    public boolean hasMajorImportance() {
        return decisionimportance == Importance.MAJOR;
    }

    @Override
    public boolean hasMajorMinorImportance() {
        return decisionimportance == Importance.MAJOR || decisionimportance == Importance.MINOR;
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
            case TURN, MARKROUNDING -> {
                sb.append(" to ");
                sb.append(format1dp(decisionangle));
                sb.append("° ");
                sb.append(decisionPORT ? "PORT" : "STARBOARD");
            }
        }
        sb.append("  BOAT (");
        sb.append(boatname);
        sb.append("): [");
        sb.append(format2dp(boatx));
        sb.append(",");
        sb.append(format2dp(boaty));
        sb.append("] ");
        sb.append(format1dp(boatangle));
        sb.append("°");
        sb.append("  REASON (");
        sb.append(boatname);
        sb.append("): ");
        sb.append(reason);
        return sb.toString();
    }
}
