package uk.theretiredprogrammer.sketch;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.theretiredprogrammer.sketch.fileselector.control.FileSelectorController;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FileSelectorController fileselectorcontroller = new FileSelectorController();
        fileselectorcontroller.showFileSelectorWindow(stage);
    }
}
