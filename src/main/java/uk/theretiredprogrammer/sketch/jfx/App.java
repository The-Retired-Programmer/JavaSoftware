package uk.theretiredprogrammer.sketch.jfx;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.jfx.fileselectordisplay.FileSelectorWindow;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FileSelectorWindow.create(stage);
    }
}
