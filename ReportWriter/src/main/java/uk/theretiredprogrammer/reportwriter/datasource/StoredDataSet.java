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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;

public class StoredDataSet extends DataSet {

    private final List<DataRecord> dataset = new ArrayList<>();

    public StoredDataSet(List<String> headers) {
        super(headers);
    }

    public StoredDataSet(Stream<String> headers) {
        super(headers);
    }

    public DataSetStream createDataRecordStream() {
        return new DataSetStream(this);
    }

    public DataSetStream createDataRecordStream(List<String> headers) {
        return new DataSetStream(headers, this.getStream());
    }

    public Stream<DataRecord> getStream() {
        return dataset.stream();
    }

    public void insertDataRecord(Stream<String> fields) {
        dataset.add(new DataRecord(getHeaders(), fields));
    }

    public void insertDataRecord(List<String> fields) {
        dataset.add(new DataRecord(getHeaders(), fields));
    }

    public StoredDataSet insertDataRecords(Stream<DataRecord> datarecords) {
        datarecords.forEach(dr -> dataset.add(dr));
        return this;
    }

    public List<String> evaluateheaders(ExpressionList headerexpressions) {
        DataRecord datarecord = dataset.get(0);
        return headerexpressions.stream()
                .map(operand -> DataTypes.isStringExpression(operand).evaluate(datarecord))
                .collect(Collectors.toList());
    }
    
    public boolean isEmpty() {
        return dataset.isEmpty();
    }
}
