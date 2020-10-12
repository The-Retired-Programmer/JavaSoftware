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
package uk.theretiredprogrammer.sketch.properties;

import java.util.function.Supplier;
import uk.theretiredprogrammer.sketch.core.Area;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertyFlowComponents extends PropertyListNamedWithFactoryCreator<PropertyFlowComponent> {

    public PropertyFlowComponents(String key, Supplier<Area> getdisplayarea) {
        super((type) -> PropertyFlowComponent.factory(type, getdisplayarea));
        setKey(key);
    }

    @Override
    public PropertyFlowComponents get() {
        return this;
    }
}
