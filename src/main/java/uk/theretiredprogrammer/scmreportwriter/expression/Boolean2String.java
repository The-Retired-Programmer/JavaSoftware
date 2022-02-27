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
package uk.theretiredprogrammer.scmreportwriter.expression;

import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;

public class Boolean2String implements Expression<String> {

    private final Expression<Boolean> node;

    public Boolean2String(Expression<Boolean> node) {
        this.node = node;
    }

    @Override
    public String evaluate(DataSourceRecord datarecord) {
        return node.evaluate(datarecord) ? "Yes" : "No";
    }
}
