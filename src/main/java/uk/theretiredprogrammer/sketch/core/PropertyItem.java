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
package uk.theretiredprogrammer.sketch.core;

import java.util.function.UnaryOperator;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import static javafx.scene.text.FontWeight.NORMAL;
import javafx.scene.text.Text;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 *
 * @author richard
 */
public abstract class PropertyItem {

    public abstract Node createPropertySheetItem(Controller controller);

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
