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
package uk.theretiredprogrammer.sketch.properties.ui;

import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Model;
import uk.theretiredprogrammer.sketch.core.entity.PropertyAny;
import uk.theretiredprogrammer.sketch.core.entity.PropertyElement;
import uk.theretiredprogrammer.sketch.core.entity.PropertyList;
import uk.theretiredprogrammer.sketch.core.entity.PropertyMap;
import uk.theretiredprogrammer.sketch.core.entity.PropertyString;

/**
 *
 * @author richard
 */
public class PropertyMapPane extends TitledPane {

    public PropertyMapPane(ObservableMap<String, PropertyAny> properties, String title) {
        this.setText(title);
        this.setContent(new ScrollPane(createpropertiescontent(properties)));
    }

    public PropertyMapPane(ObservableMap<String, PropertyAny> properties, String titleroot, PropertyString propertyname) {
        this.textProperty().bind(new SimpleStringProperty(titleroot)
                .concat(propertyname.propertyString())
        );
        this.setContent(createpropertiescontent(properties));
    }

    private ScrollPane createpropertiescontent(ObservableMap<String, PropertyAny> properties) {
        GridPane propertiestable = new GridPane();
        int row = 0;
        createpropertymapcontent(propertiestable, row, properties);
        return new ScrollPane(propertiestable);
    }

    private int createpropertymapcontent(GridPane propertiestable, int row, ObservableMap<String, PropertyAny> properties) {
        for (var pe : properties.entrySet()) {
            Object value = pe.getValue();
            if (value instanceof Model model) {
                row = createpropertymapcontent(propertiestable, row, model.getProperties());
            } else if (value instanceof PropertyList propertylist) {
                row = createpropertylistcontent(propertiestable, row, propertylist);
            } else if (value instanceof PropertyMap propertymap) {
                row = createpropertymapcontent(propertiestable, row, propertymap.propertymap);
            } else if (value instanceof PropertyElement propertyelement) {
                row = createpropertyelementcontent(propertiestable, row, propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        }
        return row;
    }

    private int createpropertylistcontent(GridPane propertiestable, int row, PropertyList<? extends PropertyAny> properties) {
        for (PropertyAny value : properties.getList()) {
            if (value instanceof PropertyList propertylist) {
                row = createpropertylistcontent(propertiestable, row, propertylist);
            } else if (value instanceof PropertyMap propertymap) {
                row = createpropertymapcontent(propertiestable, row, propertymap.propertymap);
            } else if (value instanceof PropertyElement propertyelement) {
                row = createpropertyelementcontent(propertiestable, row, propertyelement);
            } else {
                throw new IllegalStateFailure("PropertiesPane: Unknown Property instance");
            }
        }
        return row;
    }

    private int createpropertyelementcontent(GridPane propertiestable, int row, PropertyElement propertyelement) {
        Optional<String> okey = propertyelement.getOptionalKey();
        propertiestable.add(new Label(okey.orElse("")), 0, row, 1, 1);
        propertiestable.add(propertyelement.getField(), 1, row++, 1, 1);
        return row;
    }
}
