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

import javafx.stage.WindowEvent;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;

/**
 *
 * @author richard
 * @param <W>
 */
public abstract class AbstractController<W extends AbstractWindow> {

    protected enum ExternalCloseAction {
        CLOSE, HIDE, IGNORE
    }

    protected enum ClosingMode {
        PROGRAMMATICALLY, EXTERNALLY, HIDEONLY
    }

    private ClosingMode closingmode = ClosingMode.HIDEONLY;
    private ExternalCloseAction externalcloseaction;
    private W window;

    protected final void setWindow(W window, ExternalCloseAction externalcloseaction) {
        this.window = window;
        this.externalcloseaction = externalcloseaction;
    }

    protected final void setWindow(W window) {
        this.window = window;
        this.externalcloseaction = ExternalCloseAction.CLOSE;
    }

    protected final W getWindow() {
        return window;
    }

    public final void showWindow() {
        window.show();
    }

    public final void close() {
        closingmode = ClosingMode.PROGRAMMATICALLY;
        whenWindowIsClosingProgrammatically();
        whenWindowIsClosing();
        getWindow().close();
    }

    public final void windowHasExternalCloseRequest(WindowEvent e) {
        switch (externalcloseaction) {
            case CLOSE -> {
                closingmode = ClosingMode.EXTERNALLY;
                whenWindowIsClosingExternally();
                whenWindowIsClosing();
            }
            case HIDE -> {
                closingmode = ClosingMode.HIDEONLY;
            }
            case IGNORE ->
                e.consume();
        }
    }

    public final void windowIsHiding(WindowEvent e) {
        getWindow().saveWindowSizePreferences();
        whenWindowIsHiding();
    }

    public final void windowIsHidden(WindowEvent e) {
        switch (closingmode) {
            case PROGRAMMATICALLY -> {
                whenWindowIsClosedProgrammatically();
                whenWindowIsClosed();
                setWindow(null);
            }
            case EXTERNALLY -> {
                whenWindowIsClosedExternally();
                whenWindowIsClosed();
                setWindow(null);
            }
            case HIDEONLY ->
                whenWindowIsHiddenOnly();
        }
    }

    protected void whenWindowIsClosing() {
    }

    protected void whenWindowIsClosingProgrammatically() {
    }

    protected void whenWindowIsClosingExternally() {
    }

    protected void whenWindowIsHiding() {
    }

    protected void whenWindowIsHiddenOnly() {
    }

    protected void whenWindowIsClosed() {
    }

    protected void whenWindowIsClosedProgrammatically() {
    }

    protected void whenWindowIsClosedExternally() {
    }
}
