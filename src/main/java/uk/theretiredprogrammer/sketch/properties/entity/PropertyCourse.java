/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.sketch.properties.entity;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.Location;
import static uk.theretiredprogrammer.sketch.core.entity.Location.LOCATIONZERO;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyCourse extends PropertyMap {

    private static final ObservableList<String> roundings;

    static {
        roundings = FXCollections.observableArrayList();
        roundings.addAll("port", "starboard");
    }

    private final Config<PropertyLocation, Location> start = new Config<>("start", MANDATORY, (s) -> new PropertyLocation(s, LOCATIONZERO));
    private final Config<PropertyLegEndings, PropertyLegEndings> legs;

    public PropertyCourse(String key, ObservableList<String> marknames) {
        setKey(key);
        legs = new Config<>("legs", OPTIONAL, (s) -> new PropertyLegEndings(s, marknames, roundings));
        this.addConfig(start, legs);
    }
    
    @Override
    public void setOnChange(Runnable onchange) {
        super.setOnChange(onchange);
        getPropertyLegValues().setOnChange(onchange);
    }

    @Override
    public PropertyCourse get() {
        return this;
    }

    public Location getStart() {
        return start.get("PropertyCourse start");
    }

    public PropertyLegEndings getPropertyLegValues() {
        return legs.get("PropertyCourse legs");
    }
}
