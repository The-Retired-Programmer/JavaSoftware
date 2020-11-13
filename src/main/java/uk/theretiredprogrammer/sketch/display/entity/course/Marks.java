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
import javafx.beans.value.ChangeListener;
import uk.theretiredprogrammer.sketch.core.entity.ModelNamedList;

public class Marks extends ModelNamedList<Mark> {
    
     public Marks() {
        super("Mark", (mark)-> mark.getName());
    }

    @Override
    protected Mark createAndParse(JsonValue jobj) {
        Mark mark = new Mark();
        mark.parse(jobj);
        return mark;
    }
    
    public void addNameChangeListener(ChangeListener<String> childlistener) {
        addChildChangeListener(mark-> mark.addNameChangeListener(childlistener));
    }
}
