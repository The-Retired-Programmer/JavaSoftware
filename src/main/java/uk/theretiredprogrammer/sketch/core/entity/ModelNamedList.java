/*
 * Copyright 2014-2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.core.entity;

import uk.theretiredprogrammer.sketch.core.control.IllegalStateFailure;

public abstract class ModelNamedList<P extends Model & ModelNamed> extends ModelList<P>  {

    private final String childclassname;

    public ModelNamedList(String childclassname) {
        this.childclassname = childclassname;
    }

    public final P get(String name) {
        return stream().filter(property -> name.equals(property.getNamed())).findFirst()
                .orElseThrow(() -> new IllegalStateFailure("ModelNamedList: can't find " + childclassname + " with name " + name));
    }
}
