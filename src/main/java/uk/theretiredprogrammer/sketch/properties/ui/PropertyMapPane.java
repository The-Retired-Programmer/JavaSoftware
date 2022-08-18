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
package uk.theretiredprogrammer.sketch.properties.ui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Model;
import uk.theretiredprogrammer.sketch.core.entity.ModelList;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.Strg;
import uk.theretiredprogrammer.sketch.core.ui.UI;
import uk.theretiredprogrammer.sketch.display.entity.course.Leg;
import uk.theretiredprogrammer.sketch.display.entity.course.Legs;

public class PropertyMapPane extends TitledPane {

    private int row = 0;

    public PropertyMapPane(ModelMap properties, String title) {
        this.setText(title);
        this.setContent(new ScrollPane(createpropertiescontent(properties)));
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public PropertyMapPane(ModelMap properties, String titleroot, Strg name) {
        UI.insertTitle(this, titleroot, name);
        this.setContent(createpropertiescontent(properties));
    }

    public void addTitleButton(String imagefilename, String tooltip, EventHandler<ActionEvent> action) {
        ((HBox) getGraphic()).getChildren().add(UI.toolbarButton(imagefilename, tooltip, action));
    }

    private ScrollPane createpropertiescontent(ModelMap properties) {
        GridPane propertiestable = new GridPane();
        row = 0;
        createpropertymapcontent(propertiestable, properties);
        return new ScrollPane(propertiestable);
    }

    private void createpropertymapcontent(GridPane propertiestable, ModelMap properties) {
        properties.stream().forEach(entry -> {
            Model value = entry.getValue();
            if (value instanceof ModelMap propertymap) {
                createpropertymapcontent(propertiestable, propertymap);
            } else if (value instanceof ModelList propertylist) {
                createpropertylistcontent(propertiestable, propertylist);
            } else if (value instanceof ModelProperty propertyelement) {
                createpropertyelementcontent(propertiestable, entry.getKey(), propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        });
    }

    private void createpropertylistcontent(GridPane propertiestable, ModelList<? extends Model> properties) {
        properties.stream().forEach(value -> {
            if (value instanceof ModelList propertylist) {
                createpropertylistcontent(propertiestable, propertylist);
            } else if (value instanceof ModelMap propertymap) {
                createpropertymapcontent(propertiestable, propertymap);
            } else if (value instanceof ModelProperty propertyelement) {
                if (propertyelement instanceof Leg leg) {
                    createpropertyelementcontent(propertiestable, leg, (Legs) properties);
                } else {
                    createpropertyelementcontent(propertiestable, propertyelement);
                }
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        });
    }

    private void createpropertyelementcontent(GridPane propertiestable, Leg propertyelement, Legs propertylist) {
        propertiestable.add(new Label(""), 0, row, 1, 1);
        HBox elements = (HBox) propertyelement.getControl();
        //elements.minWidthProperty().bind(titledpane.widthProperty());
        elements.getChildren().add(UI.toolbarButton("add.png", "Duplicate", (ev) -> duplicate(propertylist, propertyelement)));
        elements.getChildren().add(UI.toolbarButton("delete.png", "Delete", (ev) -> delete(propertylist, propertyelement)));
        elements.getChildren().add(UI.toolbarButton("arrow_up.png", "Move Up", (ev) -> moveup(propertylist, propertyelement)));
        elements.getChildren().add(UI.toolbarButton("arrow_down.png", "Move Down", (ev) -> movedown(propertylist, propertyelement)));
        propertiestable.add(elements, 1, row++, 1, 1);
    }

    private void duplicate(Legs legs, Leg leg) {
        legs.add(new Leg(leg));
    }

    private void delete(Legs legs, Leg leg) {
        legs.remove(leg);
    }

    private void moveup(Legs legs, Leg leg) {
        ObservableList<Leg> list = legs.get();
        int index = list.indexOf(leg);
        if (index == 0) {
            return;
        }
        list.remove(index);
        list.add(index - 1, leg);
    }

    private void movedown(Legs legs, Leg leg) {
        ObservableList<Leg> list = legs.get();
        int index = list.indexOf(leg);
        if (index >= list.size() - 1) {
            return;
        }
        list.remove(index);
        list.add(index + 1, leg);
    }

    private void createpropertyelementcontent(GridPane propertiestable, ModelProperty propertyelement) {
        createpropertyelementcontent(propertiestable, "", propertyelement);
    }

    private void createpropertyelementcontent(GridPane propertiestable, String key, ModelProperty propertyelement) {
        propertiestable.add(new Label(key), 0, row, 1, 1);
        propertiestable.add(propertyelement.getControl(), 1, row++, 1, 1);
    }
}
