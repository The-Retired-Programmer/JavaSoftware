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

package uk.theretiredprogrammer.sketch;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.core.control.ExecuteAndCatch;
import uk.theretiredprogrammer.sketch.fileselector.control.FileSelectorController;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new ExecuteAndCatch(() -> new FileSelectorController(stage));
    }
}
