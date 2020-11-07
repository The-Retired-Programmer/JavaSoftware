/*
 * Copyright 2020 Richard Linsdale (richard@theretiredprogrammer.uk).
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

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.ParseHelper;
import uk.theretiredprogrammer.sketch.core.ui.FieldBuilder;

public class PropertyLegEnding extends SimpleObjectProperty<LegEnding>  implements ModelProperty<LegEnding> {

    private final ObservableList<String> marknames;
    private final ObservableList<String> roundings;

    public PropertyLegEnding(ObservableList<String> marknames, ObservableList<String> roundings) {
        this.marknames = marknames;
        this.roundings = roundings;
    }

    public PropertyLegEnding(LegEnding defaultvalue, ObservableList<String> marknames, ObservableList<String> roundings) {
        this.marknames = marknames;
        this.roundings = roundings;
        set(defaultvalue);
    }
    
    @Override
    public void setOnChange(Runnable onchange) {
    }
    
    @Override
    public final LegEnding parsevalue(JsonValue jvalue) {
        return ParseHelper.legEndingParse(jvalue, marknames, roundings);
    }

    @Override
    public JsonArray toJson() {
        return ParseHelper.legEndingToJson(get());
    }

    @Override
    public Node getField() {
        return FieldBuilder.getLegEndingField(this, marknames, roundings);
    }

    @Override
    public Node getField(int size) {
        return getField();
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }
}
