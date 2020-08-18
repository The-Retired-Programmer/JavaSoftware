package uk.theretiredprogrammer.sketch.jfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // control buttons
        var controlButton = new Button("Start");
        controlButton.setOnAction((e)-> DisplayActions.control(controlButton, e));
        //
        var resetButton = new Button("Reset to Start");
        resetButton.setOnAction((e)-> DisplayActions.reset(controlButton, e));
        //
        var controls = new HBox();
        controls.getChildren().add(resetButton);
        controls.getChildren().add(controlButton);
        // main area
        TreeView directoryview = (new FileExplorer()).getExplorerView();
        directoryview.setShowRoot(false);
        TitledPane filestitledpane = new TitledPane(FileExplorer.FILEROOT, new VBox(directoryview));
        VBox midControl = new VBox(new Label("Display"));
        midControl.getChildren().add(controls);
        VBox rightControl = new VBox(new Label("Properties"));
        //
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(filestitledpane, midControl, rightControl);
        //
        var scene = new Scene(splitPane, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Race Training SKETCH Application");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}