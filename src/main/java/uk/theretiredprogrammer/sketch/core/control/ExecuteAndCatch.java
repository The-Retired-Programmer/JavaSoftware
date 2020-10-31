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
package uk.theretiredprogrammer.sketch.core.control;

import jakarta.json.JsonException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 *
 * @author richard
 */
public class ExecuteAndCatch {

    private Consumer<Exception> illegalstatefailurereporting = (ex) -> catchDialog("Program Failure", ex);
    private Consumer<Exception> parsefailurereporting = (ex) -> catchDialog("Problem parsing Config file", ex);
    private Consumer<Exception> otherexceptionsreporting = (ex) -> catchDialog("Program Failure", ex);
    private Runnable exceptionHandler = () -> {};
    private Runnable parsefailureHandler = () -> exceptionHandler.run();

    public static void runLater(Runnable work) {
        Platform.runLater(() -> new ExecuteAndCatch(work));
    }

    public ExecuteAndCatch() {
    }

    public ExecuteAndCatch(Runnable work) {
        run(work);
    }

    public ExecuteAndCatch reportOnIllegalStateFailure(Consumer<Exception> illegalstatefailurereporting) {
        this.illegalstatefailurereporting = illegalstatefailurereporting;
        return this;
    }

    public ExecuteAndCatch reportOnParseFailure(Consumer<Exception> parsefailurereporting) {
        this.parsefailurereporting = parsefailurereporting;
        return this;
    }

    public ExecuteAndCatch reportOnOtherExceptions(Consumer<Exception> otherexceptionsreporting) {
        this.otherexceptionsreporting = otherexceptionsreporting;
        return this;
    }

    public ExecuteAndCatch setExceptionHandler(Runnable exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public ExecuteAndCatch setParseFailureHandler(Runnable parsefailureHandler) {
        this.parsefailureHandler = parsefailureHandler;
        return this;
    }

    public final void run(Runnable work) {
        try {
            work.run();
        } catch (IllegalStateFailure ex) {
            exceptionHandler.run();
            Platform.runLater(() -> illegalstatefailurereporting.accept(ex));
        } catch (JsonException | ParseFailure ex) {
            parsefailureHandler.run();
            Platform.runLater(() -> parsefailurereporting.accept(ex));
        } catch (Exception ex) {
            exceptionHandler.run();
            Platform.runLater(() -> otherexceptionsreporting.accept(ex));
        }
    }

    public void catchDialog(String title, Exception ex) {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        ex.printStackTrace(pwriter);
        ButtonType loginButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        dialog.setTitle(title + ex.getLocalizedMessage());
        dialog.setContentText(writer.toString());
        dialog.setWidth(1000);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
