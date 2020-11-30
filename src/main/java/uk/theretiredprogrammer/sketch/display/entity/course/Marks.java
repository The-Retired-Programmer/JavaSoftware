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
package uk.theretiredprogrammer.sketch.display.entity.course;

import jakarta.json.JsonValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamedList;

public class Marks extends ModelNamedList<Mark> {

    private final ObservableList<SimpleStringProperty> marknames = FXCollections.observableArrayList();

    public Marks() {
        super("Mark");
//        addListChangeListener((ListChangeListener<Mark>) (c) -> marklistchanged((ListChangeListener.Change<Mark>) c));
//        addNameChangeListener((mark, oldval, newval) -> marknamechanged(mark, oldval, newval));
    }

//    private void marknamechanged(Mark mark, String oldval, String newval) {
//        for (int i = 0; i < marknames.size(); i++) {
//            if (marknames.get(i).equals(oldval)) {
//                marknames.set(i, newval);
//                return;
//            }
//        }
//        throw new IllegalStateFailure("Marks: Can't find a Mark with name " + oldval + " to change to " + newval);
//    }
//
//    private void marklistchanged(ListChangeListener.Change<Mark> c) {
//        while (c.next()) {
//            c.getRemoved().forEach(remitem -> {
//                marknames.remove(remitem.getName());
//            });
//            c.getAddedSubList().forEach(additem -> {
//                marknames.add(additem.getName());
//            });
//        }
//        if (marknames.size() != stream().count()) {
//            // trap - maybe removed later
//            throw new IllegalStateFailure("Number of Marks does not equal number of Marknames");
//        }
//    }

    @Override
    protected Mark createAndParse(JsonValue jobj) {
        Mark mark = new Mark();
        mark.parse(jobj);
        return mark;
    }

//    public final void addNameChangeListener(NameChangeListener<Mark> childlistener) {
//        addChildChangeListener(mark -> mark.addNameChangeListener(childlistener));
//    }

//    public final ObservableList<SimpleStringProperty> getMarkNames() {
//        return marknames;
//    }
}
