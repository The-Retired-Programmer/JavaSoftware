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

import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;

public class DataSource extends ArrayList<DataSourceRecord> {

    private List<String> columnnames = new ArrayList<>();

    public DataSource(DataSource copyfrom) {
        super(copyfrom);
        this.columnnames = copyfrom.columnnames;
    }
    
    public DataSource(List<String> columnnames, List<DataSourceRecord> datarecords) {
        super(datarecords);
        this.columnnames = columnnames;
    }

    public DataSource() {
        super();
        this.columnnames = new ArrayList<>();
    }

    public void setColumnNames(List<String> columnnames) {
        this.columnnames = columnnames;
    }
    
    public List<String> getColumnNames(){
        return columnnames;
    }

    protected int getColumnIndex(String columnname) throws RPTWTRException {
        int i = 0;
        for (String name : columnnames) {
            if (name.equals(columnname)) {
                return i;
            }
            i++;
        }
        throw new RPTWTRException("Can't lookup column name");
    }
}
