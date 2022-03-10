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
package uk.theretiredprogrammer.scmreportwriter;

import uk.theretiredprogrammer.scmreportwriter.DataSource;
import uk.theretiredprogrammer.scmreportwriter.DataSources;
import uk.theretiredprogrammer.scmreportwriter.Fields;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;

public class ReportDescriptor {

    private final DataSources datasources;
    private final BooleanExpression filter;
    private final Fields fields;

    public ReportDescriptor(DataSources datasources, BooleanExpression filter, Fields fields) {
        this.datasources = datasources;
        this.filter = filter;
        this.fields = fields;
    }

    public DataSource getDataSource(String key) {
        return datasources.get(key);
    }

    public BooleanExpression getFilter() {
        return filter;
    }

    public Fields getFields() {
        return fields;
    }

}
