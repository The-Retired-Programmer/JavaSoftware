/*
 * Copyright 2022 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.scmreportwriter.language;

import uk.theretiredprogrammer.scmreportwriter.language.Language.PrecedenceGroup;

public class Operator implements S_Token {

    public final PrecedenceGroup operatorgroup;
    public final Reduction reduction;
    public final String name;
    private TokenSourceLocator locator;

    public Operator(String name, PrecedenceGroup operatorgroup, Reduction reduction) {
        this.name = name;
        this.operatorgroup = operatorgroup;
        this.reduction = reduction;
    }

    @Override
    public void setLocator(TokenSourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public TokenSourceLocator getLocator() {
        return locator;
    }

    @Override
    public String toString() {
        return name;
    }
}
