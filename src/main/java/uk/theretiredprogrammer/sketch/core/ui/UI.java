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
package uk.theretiredprogrammer.sketch.core.ui;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.text.FontWeight.NORMAL;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import uk.theretiredprogrammer.sketch.core.control.ExecuteAndCatch;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.ConstrainedString;
import uk.theretiredprogrammer.sketch.core.entity.DistanceVector;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import uk.theretiredprogrammer.sketch.core.entity.Model;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamed;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamedList;
import uk.theretiredprogrammer.sketch.core.entity.Obj;
import uk.theretiredprogrammer.sketch.core.entity.SpeedVector;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import uk.theretiredprogrammer.sketch.display.entity.course.Mark;
import uk.theretiredprogrammer.sketch.display.entity.course.Marks;
import uk.theretiredprogrammer.sketch.display.entity.flows.Gradient;

public class UI {

    public static Button toolbarButton(String imagename, String tooltip, EventHandler<ActionEvent> action) {
        Button button = new Button("", image(imagename));
        button.setDisable(false);
        button.setOnAction((e) -> new ExecuteAndCatch(() -> action.handle(e)));
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    public static Menu menu(String name, MenuItem... menuitems) {
        Menu menu = new Menu(name);
        menu.getItems().addAll(menuitems);
        return menu;
    }

    public static MenuItem menuitem(String itemtext, EventHandler<ActionEvent> action) {
        MenuItem menuitem = new MenuItem(itemtext);
        menuitem.setOnAction((e) -> new ExecuteAndCatch(() -> action.handle(e)));
        return menuitem;
    }

    public static MenuItem menuitem(String itemtext) {
        return menuitem(itemtext, (e) -> {
        });
    }

    public static ImageView image(String name) {
        return new ImageView(new Image(UI.class.getResourceAsStream(name)));
    }

    public static ContextMenu contextMenu(MenuItem... menuitems) {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(menuitems);
        return menu;
    }

    public static DisplayContextMenu displayContextMenu(MenuItem... menuitems) {
        DisplayContextMenu menu = new DisplayContextMenu();
        menu.getItems().addAll(menuitems);
        return menu;
    }

    public static MenuItem contextMenuitem(String itemtext, BiConsumer<ActionEvent, ContextMenu> action) {
        MenuItem menuitem = new MenuItem(itemtext);
        menuitem.setOnAction((e) -> new ExecuteAndCatch(() -> action.accept(e, menuitem.getParentPopup())));
        return menuitem;
    }

    // 
    public static TextField control(int size, SimpleIntegerProperty property) {
        TextField intfield = new TextField(Integer.toString(property.get()));
        intfield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, integerFilter);
        intfield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return intfield;
    }

    public static TextField control(int size, SimpleDoubleProperty property) {
        TextField doublefield = new TextField(Double.toString(property.doubleValue()));
        doublefield.setPrefColumnCount(size);
        TextFormatter<Number> textformatter = new TextFormatter<>(new NumberStringConverter(), 0.0, doubleFilter);
        doublefield.setTextFormatter(textformatter);
        textformatter.valueProperty().bindBidirectional(property);
        return doublefield;
    }

    public static CheckBox control(SimpleBooleanProperty property) {
        CheckBox booleanfield = new CheckBox();
        booleanfield.setSelected(property.get());
        booleanfield.selectedProperty().bindBidirectional(property);
        return booleanfield;
    }

    public static TextFieldWithFocusManagement control(SimpleStringProperty property) {
        TextFieldWithFocusManagement stringfield = new TextFieldWithFocusManagement(property.get());
        stringfield.textProperty().bindBidirectional(property);
        return stringfield;
    }
    
    public static ComboBox<String> control(ConstrainedString property, ObservableList<String> constraints) {
        ComboBox<String> combofield = new ComboBox(constraints);
        combofield.valueProperty().bindBidirectional(property);
        return combofield;
    }

    public static ColorPicker control(SimpleObjectProperty<Color> property) {
        ColorPicker picker = new ColorPicker();
        picker.setValue(property.get());
        picker.setOnAction(actionEvent -> {
            property.set(picker.getValue());
        });
        return picker;
    }

    public static TextFlow control(int size, Location property) {
        return new TextFlow(
                createTextFor("["),
                control(size, property.getXProperty()),
                createTextFor(","),
                control(size, property.getYProperty()),
                createTextFor("]")
        );
    }

    public static TextFlow control(int distancesize, int directionsize, DistanceVector property) {
        return new TextFlow(
                control(distancesize, property.getDistanceProperty()),
                createTextFor("@"),
                control(directionsize, property.getAngle()),
                createTextFor("˚"));
    }

    public static TextFlow control(int speedsize, int directionsize, SpeedVector property) {
        return new TextFlow(
                control(speedsize, property.getSpeedProperty()),
                createTextFor("@"),
                control(directionsize, property.getAngle()),
                createTextFor("˚"));
    }

    public static HBox control(int size, Area property) {
        Location bottomleft = property.getLocationProperty();
        return new HBox(
                createTextFor("["),
                control(size, bottomleft.getXProperty()),
                createTextFor(","),
                control(size, bottomleft.getYProperty()),
                createTextFor("] "),
                control(size, property.getWidthProperty()),
                createTextFor("x"),
                control(size, property.getHeightProperty())
        );
    }

    public static HBox control(Leg property, Marks marks, ObservableList<String> roundings) {
        ComboBoxFactory<Obj<Mark>, Mark> markcombofactory = new ComboBoxFactory<>();
        return new HBox(
                markcombofactory.create(property.getMarkProperty(), marks),
                control(property.getRoundingdirectionProperty(), roundings)
        );
    }

    public static HBox control(int size, Gradient property, ObservableList<String> typeconstraints) {
        HBox hbox = new HBox(control(property.getTypeProperty(), typeconstraints));
        property.getSpeeds().forEach(speedproperty -> hbox.getChildren().add(control(size, speedproperty)));
        return hbox;
    }

    static UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)(\\.[0-9]*)?")) {
            return change;
        }
        return null;
    };

    static UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        if (change.getControlNewText().matches("-?([0-9]*)?")) {
            return change;
        }
        return null;
    };

    static Text createTextFor(String input) {
        Text text = new Text(input);
        text.setFont(Font.font("System", NORMAL, 28));
        return text;
    }

    public static class ComboBoxFactory<P extends Property, T extends ModelNamed & Model> {

        public ComboBox<T> create(P property, ModelNamedList<T> constraints) {
            ComboBox<T> combofield = new ComboBox(constraints.get());

            combofield.setConverter(
                    new StringConverter<T>() {
                @Override
                public String toString(T object) {
                    return object == null ? "" : object.getNamed();
                }

                @Override
                public T fromString(String s) {
                    return constraints.get(s);
                }
            });
            combofield.valueProperty().bindBidirectional(property);
            return combofield;
        }
    }
}
