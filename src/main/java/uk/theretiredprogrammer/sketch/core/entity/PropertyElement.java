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
package uk.theretiredprogrammer.sketch.core.entity;

import uk.theretiredprogrammer.sketch.core.entity.PropertyAny;
import jakarta.json.JsonValue;
import java.util.function.UnaryOperator;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import static javafx.scene.text.FontWeight.NORMAL;
import javafx.scene.text.Text;

/**
 *
 * @author richard
 * @param <C>
 */
public abstract class PropertyElement<C extends Object> extends PropertyAny<C> {

    public final C getValue() {
        return get();
    }

    public final void setValue(C newvalue) {
        set(newvalue);
    }

    public abstract void set(C newvalue);

    public abstract C parsevalue(JsonValue jvalue);

    public abstract Node getField(int size);

    public abstract Node getField();

    //
    UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)(\\.[0-9]*)?")) {
            return change;
        }
        return null;
    };

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)?")) {
            return change;
        }
        return null;
    };

    Text createTextFor(String input) {
        Text text = new Text(input);
        text.setFont(Font.font("System", NORMAL, 28));
        return text;
    }
}
