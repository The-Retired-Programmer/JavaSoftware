package uk.theretiredprogrammer.sketch.jfx;

import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uk.theretiredprogrammer.sketch.ui.Controller;

public class App extends Application {

    private static final int FILECONTENTPANEOFFSET = 1;
    private static final int PROPERTIESPANEOFFSET = 2;

    public static void main(String[] args) {
        launch(args);
    }

    private SplitPane splitPane;
    private Controller controller;
    private ToolBar toolbar;
    private Button runbutton;
    private Text statusbar;
    private Path selectedpath;

    @Override
    public void start(Stage stage) {
        splitPane = new SplitPane();
        splitPane.getItems().addAll(
                new FileSelectorPane((p) -> fileSelected(p)),
                new FileContentPane(),
                new PropertiesPane()
        );
        runbutton = new Button("Run");
        runbutton.setDisable(true);
        runbutton.setOnAction(actionEvent ->  {
            new DisplayStage(selectedpath);    
        });
        toolbar = new ToolBar();
        toolbar.getItems().addAll(runbutton);
        statusbar = new Text("");
        VBox vbox = new VBox();
        vbox.getChildren().addAll(toolbar, splitPane, statusbar);
        //
        var scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Race Training SKETCH Application");
        stage.show();
    }

    private void fileSelected(TreeItem<PathWithShortName> p) {
        selectedpath = p.getValue().getPath();
        ObservableList<Node> splitPanelNodes = splitPane.getItems();
        FileContentPane filecontentnode = (FileContentPane) splitPanelNodes.get(FILECONTENTPANEOFFSET);
        filecontentnode.fileSelected(this, p.getValue());
        //
        controller = new Controller(selectedpath, null, null, null);
        PropertiesPane propertiesnode = (PropertiesPane) splitPanelNodes.get(PROPERTIESPANEOFFSET);
        propertiesnode.updateAllproperties(controller);
        //
        runbutton.setDisable(false);
    }

    public void replaceSplitPaneNode(Node replacementNode) throws IOException {
        int offset = getoffsetofNode(replacementNode);
        ObservableList<Node> splitPanelNodes = splitPane.getItems();
        splitPanelNodes.remove(offset);
        splitPanelNodes.add(offset, replacementNode);
    }

    private int getoffsetofNode(Node node) throws IOException {
        if (node instanceof FileContentPane) {
            return FILECONTENTPANEOFFSET;
        }
        throw new IOException("Can't find target for provided replacement node");
    }
}
