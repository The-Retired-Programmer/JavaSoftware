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
package uk.theretiredprogrammer.scmreportwriter.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;

public class DataSourceRecord {

    private final Map<String, String> datarecord = new HashMap<>();

    public DataSourceRecord() {
    }

    public DataSourceRecord(String fieldname, String fieldvalue) {
        datarecord.put(fieldname, fieldvalue);
    }

    public DataSourceRecord(String... fieldnamesandvalues) throws RPTWTRException {
        if ((fieldnamesandvalues.length & 0x1) > 0) {
            throw new RPTWTRException("DataSourceRecord - fieldnamesandvalues not multiple of 2");
        }
        int numberofentries = fieldnamesandvalues.length / 2;
        for (int i = 0; i < numberofentries; i++) {
            datarecord.put(fieldnamesandvalues[i], fieldnamesandvalues[numberofentries + i]);
        }
    }

    public DataSourceRecord(List<String> fieldnames, List<String> fieldvalues) throws RPTWTRException {
        if (fieldnames.size() != fieldvalues.size()) {
            throw new RPTWTRException("DataSourceRecord - fieldnames & fieldvalues not equal in length");
        }
        for (int i = 0; i < fieldvalues.size(); i++) {
            datarecord.put(fieldnames.get(i), fieldvalues.get(i));
        }
    }

    public String getFieldValue(String fieldname) {
        return datarecord.get(fieldname);
    }
}
