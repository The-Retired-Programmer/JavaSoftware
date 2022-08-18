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
package uk.theretiredprogrammer.sketch.core.ui;

import javafx.scene.control.TextField;

public class TextFieldWithFocusManagement extends TextField {
    
    private String valueonfocus;
    private Runnable onchanged;
    
    public TextFieldWithFocusManagement(String initialvalue) {
        focusedProperty().addListener((obj,oldv,newv) -> focusmanagement(newv));
    }
    
    private void focusmanagement(boolean newfocusstate) {
        if (newfocusstate) {
            valueonfocus = this.getText();
        } else {
            if (!this.getText().equals(valueonfocus) && onchanged != null) {
                onchanged.run();
            }
        }
    }
    
    public void setOnChanged(Runnable onchanged){
        this.onchanged = onchanged;
    }
}
