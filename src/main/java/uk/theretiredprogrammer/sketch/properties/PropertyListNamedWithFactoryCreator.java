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
package uk.theretiredprogrammer.sketch.properties;

import java.util.function.Function;

/**
 *
 * @author richard
 * @param <P>
 */
public abstract class PropertyListNamedWithFactoryCreator<P extends PropertyAny & PropertyNamed> extends PropertyListWithFactoryCreator<P> {

    public PropertyListNamedWithFactoryCreator(Function<String, P> creator) {
        super(creator);
    }

    public PropertyListNamedWithFactoryCreator(Function<String, P> creator, String parametername) {
        super(creator, parametername);
    }

    public final P get(String key) {
        for (P property : propertylist) {
            if (property.hasName(key)) {
                return property;
            }
        }
        return null;
    }

    public final void remove(String name) {
        remove(get(name));
    }
}
