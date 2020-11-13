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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.FromJson;
import uk.theretiredprogrammer.sketch.core.entity.PropertyConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.ToJson;
import uk.theretiredprogrammer.sketch.core.ui.UI;

public class PropertyLegEnding implements ModelProperty<PropertyLegEnding> {

    private static final ObservableList<String> roundingdirections;

    static {
        roundingdirections = FXCollections.observableArrayList();
        roundingdirections.addAll("port", "starboard");
    }

    private final PropertyConstrainedString mark = new PropertyConstrainedString();
    private final PropertyConstrainedString passing = new PropertyConstrainedString(roundingdirections);

    ObservableList<String> marknames;

    public PropertyLegEnding() {
        set(null, null);
    }

    public PropertyLegEnding(ObservableList<String> marknames) {
        this.marknames = marknames;
        set(null, null);
    }

    public PropertyLegEnding(PropertyLegEnding defaultvalue) {
        set(defaultvalue);
    }
    
    public PropertyLegEnding(String mark, String passing, ObservableList<String> marknames) {
        setMarknames(marknames);
        set(mark, passing);
    }

    public PropertyLegEnding(String mark, String passing) {
        set(mark, passing);
    }

    @Override
    public void setOnChange(Runnable onchange) {
    }

    void setMarknames(ObservableList<String> marknames) {
        this.marknames = marknames;
        mark.setConstraints(marknames);
    }

    public final void set(PropertyLegEnding value) {
        setMarknames(value.marknames);
        set(value.mark.get(), value.passing.get());
    }

    public final void set(String mark, String passing) {
        this.mark.set(mark);
        this.passing.set(passing == null ? null : passing.toLowerCase());
    }

    @Override
    public final PropertyLegEnding parsevalue(JsonValue jvalue) {
        return FromJson.legEndingProperty(jvalue, marknames, roundingdirections);
    }

    @Override
    public JsonArray toJson() {
        return ToJson.serialise(this);
    }

    @Override
    public Node getControl() {
        return UI.control(this, marknames, roundingdirections);
    }

    @Override
    public Node getControl(int size) {
        return getControl();
    }

    @Override
    public final void parse(JsonValue jvalue) {
        set(parsevalue(jvalue));
    }

    public String getRoundingdirection() {
        return passing.get();
    }

    public PropertyConstrainedString getRoundingdirectionProperty() {
        return passing;
    }

    public boolean isPortRounding() {
        return passing.get().equals("port");
    }

    public String getMarkname() {
        return mark.get();
    }

    public PropertyConstrainedString getMarknameProperty() {
        return mark;
    }

    @Override
    public String toString() {
        return mark.get() + " to " + passing.get();
    }
}
