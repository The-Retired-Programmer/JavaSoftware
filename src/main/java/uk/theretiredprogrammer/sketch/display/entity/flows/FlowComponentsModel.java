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
package uk.theretiredprogrammer.sketch.display.entity.flows;

import jakarta.json.JsonObject;
import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;
import uk.theretiredprogrammer.sketch.core.entity.Area;
import uk.theretiredprogrammer.sketch.core.entity.Model;
import uk.theretiredprogrammer.sketch.core.entity.ModelArray;

public class FlowComponentsModel extends ModelArray<FlowComponentModel> {

    private final Supplier<Area> getdisplayarea;

    public FlowComponentsModel(Supplier<Area> getdisplayarea) {
        this.getdisplayarea = getdisplayarea;
    }

    @Override
    protected FlowComponentModel createAndParse(JsonObject jobj) {
        FlowComponentModel flowcm = FlowComponentModel.factory(
                jobj.getString( "type", "<undefined>"),
                getdisplayarea
        );
        flowcm.parse(jobj);
        return flowcm;
    }

    @Override
    public FlowComponentModel get(String name) {
        throw new IllegalStateFailure("get(name) in not to be used");
    }

}
