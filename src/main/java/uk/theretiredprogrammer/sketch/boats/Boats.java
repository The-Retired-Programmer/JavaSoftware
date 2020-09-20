/*
 * Copyright 2020 richard Linsdale.
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
package uk.theretiredprogrammer.sketch.boats;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.PropertyItem;
import uk.theretiredprogrammer.sketch.jfx.SketchWindow.SketchPane;
import uk.theretiredprogrammer.sketch.timerlog.TimerLog;
import uk.theretiredprogrammer.sketch.ui.Controller;
import uk.theretiredprogrammer.sketch.ui.Displayable;
import uk.theretiredprogrammer.sketch.ui.Timerable;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class Boats implements Displayable, Timerable {

    private final ObservableList<Boat> boats = FXCollections.observableArrayList();

    public Boats(Supplier<Controller> controllersupplier, JsonObject parsedjson) throws IOException {
        JsonArray boatarray = parsedjson.getJsonArray("boats");
        if (boatarray == null) {
            throw new IOException("Malformed Definition File - missing <boats> array");
        }
        for (JsonValue boatv : boatarray) {
            if (boatv.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject boatparams = (JsonObject) boatv;
                Boat boat = BoatFactory.createboatelement(controllersupplier, boatparams);
                boats.add(boat);
            } else {
                throw new IOException("Malformed Definition File - <boats> array contains items other that boat objects");
            }
        }

    }

    public final Boat getBoat(String name) {
        for (Boat boat : boats) {
            if (boat.getName().equals(name)) {
                return boat;
            }
        }
        return null;
    }
    
    public void addBoat(Boat boat) {
        boats.add(boat);
    }

    public List<Boat> getBoats() {
        return boats;
    }
    
    public void setOnBoatsChange(ListChangeListener<Boat> ml) {
        boats.addListener(ml);
    }

    @Override
    public void timerAdvance(int simulationtime, TimerLog timerlog) throws IOException {
    }

    @Override
    public void draw(SketchPane canvas) throws IOException {
        for (var boat : boats) {
            boat.draw(canvas);
        }
    }

    @Override
    public Map<String, PropertyItem> properties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
