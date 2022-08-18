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
package uk.theretiredprogrammer.reportwriter.datasource;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

public class DataRecord {

    public static final DataRecord EMPTY = new DataRecord();

    private LinkedHashMap<String, String> datarecord = new LinkedHashMap<>();

    private DataRecord() {
    }

    public DataRecord(List<String> headers, Stream<String> fields) {
        Iterator<String> fieldI = fields.iterator();
        Iterator<String> headerI = headers.iterator();
        createDataRecord(headerI, fieldI);
    }

    public DataRecord(List<String> headers, List<String> fields) {
        Iterator<String> fieldI = fields.iterator();
        Iterator<String> headerI = headers.iterator();
        createDataRecord(headerI, fieldI);
    }

    private void createDataRecord(Iterator<String> headerI, Iterator<String> fieldI) {
        while (headerI.hasNext()) {
            if (!fieldI.hasNext()) {
                throw new RuntimeException("Data Record: number of headers greater than number of fields");
            }
            datarecord.put(headerI.next(), fieldI.next());
        }
        if (fieldI.hasNext()) {
            throw new RuntimeException("Data Record: number of headers less than number of fields");
        }
    }

    public String get(String name) {
        String value = datarecord.get(name);
        if (value != null) {
            return value;
        }
        throw new RuntimeException("DataRecord::get: missing value - name was " + name);
    }
    
    public Collection<String> getAll() {
        return datarecord.values();
    }
}
