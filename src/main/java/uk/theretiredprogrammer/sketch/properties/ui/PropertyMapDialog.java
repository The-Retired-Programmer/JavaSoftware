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
package uk.theretiredprogrammer.sketch.properties.ui;

import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class PropertyMapDialog {

    public static boolean showAndWait(String title, PropertyMapPane pane) {
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneButtonType = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        Dialog dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, doneButtonType);
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);
        Optional<ButtonType> res = dialog.showAndWait();
        return res.isPresent() ? res.get() == doneButtonType : false;
    }
}
