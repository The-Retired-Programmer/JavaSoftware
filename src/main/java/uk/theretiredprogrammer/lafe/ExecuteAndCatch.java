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
        Platform.runLater(() -> runworker(work, null));
    }
    
    public static void run(Runnable work) {
        runworker(work, null);
    }
    
    public static void run(Runnable work, Runnable exceptionaction) {
        runworker(work, exceptionaction);
    }
    
    private static void runworker(Runnable work, Runnable exceptionaction) {
        try {
            work.run();
        } catch (IllegalProgramStateFailure ex) {
            catchDialog("Illegal Program State Failure", ex, exceptionaction);
        } catch (Exception ex) {
            catchDialog("Program Failure", ex, exceptionaction);
        }
    }

    private static void catchDialog(String title, Exception ex, Runnable exceptionaction) {
        if (exceptionaction != null) {
            exceptionaction.run();
        }
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        ex.printStackTrace(pwriter);
        if (Platform.isFxApplicationThread()) {
            buildAndShowCatchDialog(title, ex, writer);
        } else {
            Platform.runLater( () -> buildAndShowCatchDialog(title, ex, writer));
        }
    }
        
    private static void buildAndShowCatchDialog(String title, Exception ex, StringWriter writer) {
        ButtonType loginButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        dialog.setTitle(title + " - " + ex.getLocalizedMessage());
        dialog.setContentText(writer.toString());
        dialog.getDialogPane().setMinWidth(900);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
