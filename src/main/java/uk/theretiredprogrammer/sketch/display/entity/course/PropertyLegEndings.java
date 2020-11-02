/*
 * Copyright 2020 richard Linsdale.
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

import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.PropertyListWithSimpleCreator;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyLegEndings extends PropertyListWithSimpleCreator<PropertyLegEnding> {

    private final ObservableList<String> marknames;
    private final ObservableList<String> roundings;

    public PropertyLegEndings(String key, ObservableList<String> marknames, ObservableList<String> roundings) {
        super(() -> new PropertyLegEnding(marknames, roundings));
        setKey(key);
        this.marknames = marknames;
        this.roundings = roundings;
    }

    @Override
    public PropertyLegEndings get() {
        return this;
    }

    public void add() {
        super.add(new PropertyLegEnding(marknames, roundings));
    }

    public void add(LegEnding legvalue) {
        super.add(new PropertyLegEnding(legvalue, marknames, roundings));
    }
}
