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

import java.util.List;
import java.util.stream.Collectors;

public class ReportWriter {
    
    public void create(ReportDescriptor reportdescriptor) {
        DataSource datasource = reportdescriptor.getDataSource("first");
        List<DataSourceRecord> filteredsource = datasource.stream().filter((datarecord)->reportdescriptor.getFilter().evaluate(datarecord)).collect(Collectors.toList());
        System.out.println(String.join(", ", reportdescriptor.getFields().getHeaders(filteredsource.get(0))));
        for (DataSourceRecord record : filteredsource){
            System.out.println(String.join(", ", reportdescriptor.getFields().getValues(record)));
        }
    }
    
}
