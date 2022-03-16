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
package uk.theretiredprogrammer.scmreportwriter.oldstuff;

import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Fields extends ArrayList<Field> {
    
    public List<String> getHeaders(DataSourceRecord record) {
        return this.stream().map((field)-> field.getHeading(record)).collect(Collectors.toList());
    }
    
    public List<String> getValues(DataSourceRecord record) {
        return this.stream().map((field)-> field.getValue(record)).collect(Collectors.toList());
    }
}
