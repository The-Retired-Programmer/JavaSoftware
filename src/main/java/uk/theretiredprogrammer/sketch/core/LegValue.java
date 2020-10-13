/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.sketch.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A leg is a mark name and passing (port / starboard)
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class LegValue {

    private final String mark;
    private final String passing;

    public LegValue(String mark, String passing) {
        this.mark = mark;
        passing = passing.toLowerCase();
        if (passing.equals("port") || passing.equals("starboard")) {
            this.passing = passing;
        } else {
            throw new IllegalStateFailure("Malformed Definition file - 2nd parameter in leg must be port or starboard");
        }
    }

    public String getRoundingdirection() {
        return passing;
    }

    public boolean isPortRounding() {
        return passing.equals("port");
    }

    public static ObservableList<String> getRoundingdirections() {
        ObservableList<String> values = FXCollections.observableArrayList();
        values.addAll("port", "starboard");
        return values;
    }

    public String getMarkname() {
        return mark;
    }

    @Override
    public String toString() {
        return mark + " to " + passing;
    }
}
