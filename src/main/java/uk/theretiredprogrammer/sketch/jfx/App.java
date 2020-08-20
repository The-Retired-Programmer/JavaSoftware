package uk.theretiredprogrammer.sketch.jfx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class App extends Application {
    
    public enum SplitPaneNodeName { FILESELECTOR, FILECONTENT, PROPERTIES};
    
    private SplitPane splitPane;

    @Override
    public void start(Stage stage) {
        FileContentPane filecontentpane = new FileContentPane();
        splitPane = new SplitPane();
        splitPane.getItems().addAll(
                new FileSelectorPane((p)-> filecontentpane.fileSelected(this, p)),
                filecontentpane,
                PropertiesPane.create()
        );
        //
        var scene = new Scene(splitPane, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Race Training SKETCH Application");
        stage.show();
    }
    
    public void replaceSplitPaneNode(SplitPaneNodeName nodename, Node replacementNode) {
        int offset = nodename.ordinal();
        ObservableList<Node> splitPanelNodes = splitPane.getItems();
        splitPanelNodes.remove(offset);
        splitPanelNodes.add(offset, replacementNode);
    }

    public static void main(String[] args) {
        launch(args);
    }
}