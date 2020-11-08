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
import uk.theretiredprogrammer.sketch.core.entity.ModelArray;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperties;
import uk.theretiredprogrammer.sketch.core.entity.ModelProperty;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;

public class PropertyMapPane extends TitledPane {

    public PropertyMapPane(ModelProperties properties, String title) {
        this.setText(title);
        this.setContent(new ScrollPane(createpropertiescontent(properties)));
    }

    public PropertyMapPane(ModelProperties properties, String titleroot, PropertyString propertyname) {
        this.textProperty().bind(new SimpleStringProperty(titleroot)
                .concat(propertyname.get())
        );
        this.setContent(createpropertiescontent(properties));
    }

    private ScrollPane createpropertiescontent(ModelProperties properties) {
        GridPane propertiestable = new GridPane();
        int row = 0;
        createpropertymapcontent(propertiestable, row, properties);
        return new ScrollPane(propertiestable);
    }

    private int createpropertymapcontent(GridPane propertiestable, int row, ModelProperties properties) {
        for (var pe : properties.getProperties().entrySet()) {
            Object value = pe.getValue();
            if (value instanceof ModelProperties model) {
                row = createpropertymapcontent(propertiestable, row, model);
            } else if (value instanceof ModelArray propertylist) {
                row = createpropertylistcontent(propertiestable, row, propertylist);
            } else if (value instanceof ModelProperty propertyelement) {
                row = createpropertyelementcontent(propertiestable, row, pe.getKey(), propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        }
        return row;
    }

    private int createpropertylistcontent(GridPane propertiestable, int row, ModelArray<? extends Model> properties) {
        for (Model value : properties.getProperties()) {
            if (value instanceof ModelArray propertylist) {
                row = createpropertylistcontent(propertiestable, row, propertylist);
            } else if (value instanceof ModelProperties propertymap) {
                row = createpropertymapcontent(propertiestable, row, propertymap);
            } else if (value instanceof ModelProperty propertyelement) {
                row = createpropertyelementcontent(propertiestable, row, propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        }
        return row;
    }

    private int createpropertyelementcontent(GridPane propertiestable, int row, ModelProperty propertyelement) {
        return createpropertyelementcontent(propertiestable, row, "", propertyelement);
    }
    
    private int createpropertyelementcontent(GridPane propertiestable, int row, String key, ModelProperty propertyelement) {
        propertiestable.add(new Label(key), 0, row, 1, 1);
        propertiestable.add(propertyelement.getControl(), 1, row++, 1, 1);
        return row;
    }
}
