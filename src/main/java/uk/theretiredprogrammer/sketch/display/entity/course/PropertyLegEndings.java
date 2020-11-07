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

import jakarta.json.JsonValue;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.ModelArray;

public class PropertyLegEndings extends ModelArray<PropertyLegEnding> {

    private final ObservableList<String> marknames;
    private final ObservableList<String> roundings;

    public PropertyLegEndings(ObservableList<String> marknames, ObservableList<String> roundings) {
        this.marknames = marknames;
        this.roundings = roundings;
    }

     @Override
    protected PropertyLegEnding createAndParse(JsonValue jval) {
        PropertyLegEnding p = new PropertyLegEnding(marknames, roundings);
        p.parse(jval);
        return p;
    }
    
    @Override
    public PropertyLegEnding get(String name) {
        throw new IllegalStateFailure("PropertyLegEnding cannot be selected by name");
    }
}
