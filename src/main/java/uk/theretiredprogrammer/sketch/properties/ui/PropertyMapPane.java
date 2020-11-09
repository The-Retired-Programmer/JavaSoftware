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

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Model;
import uk.theretiredprogrammer.sketch.core.entity.ModelList;
import uk.theretiredprogrammer.sketch.core.entity.ModelMap;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;

public class PropertyMapPane extends TitledPane {
    
    private int row = 0;

    public PropertyMapPane(ModelMap properties, String title) {
        this.setText(title);
        this.setContent(new ScrollPane(createpropertiescontent(properties)));
    }

    public PropertyMapPane(ModelMap properties, String titleroot, PropertyString propertyname) {
        this.textProperty().bind(new SimpleStringProperty(titleroot)
                .concat(propertyname.get())
        );
        this.setContent(createpropertiescontent(properties));
    }

    private ScrollPane createpropertiescontent(ModelMap properties) {
        GridPane propertiestable = new GridPane();
        row = 0;
        createpropertymapcontent(propertiestable, properties);
        return new ScrollPane(propertiestable);
    }

    private void createpropertymapcontent(GridPane propertiestable, ModelMap properties) {
        properties.stream().forEach( entry -> {
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
                createpropertyelementcontent(propertiestable, propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        });
    }

    private void createpropertyelementcontent(GridPane propertiestable, ModelProperty propertyelement) {
        createpropertyelementcontent(propertiestable,  "", propertyelement);
    }
    
    private void createpropertyelementcontent(GridPane propertiestable, String key, ModelProperty propertyelement) {
        propertiestable.add(new Label(key), 0, row, 1, 1);
        propertiestable.add(propertyelement.getControl(), 1, row++, 1, 1);
    }
}
