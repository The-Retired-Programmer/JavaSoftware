/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.lafe;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ExecuteAndCatch {

    public static void runLater(Runnable work) {
        Platform.runLater(() -> runworker(work));
    }
    
    public static void run(Runnable work) {
        runworker(work);
    }

    private static void runworker(Runnable work) {
        try {
            work.run();
        } catch (IllegalProgramStateFailure ex) {
            catchDialog("Illegal Program State Failure", ex);
        } catch (Exception ex) {
            catchDialog("Program Failure", ex);
        }
    }

    private static void catchDialog(String title, Exception ex) {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        ex.printStackTrace(pwriter);
        ButtonType loginButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        dialog.setTitle(title + ex.getLocalizedMessage());
        dialog.setContentText(writer.toString());
        dialog.getDialogPane().setMinWidth(900);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
