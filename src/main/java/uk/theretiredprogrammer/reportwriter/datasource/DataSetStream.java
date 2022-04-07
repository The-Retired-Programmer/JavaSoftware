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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;

public class DataSetStream extends DataSet {

    private Stream<DataRecord> stream;

    DataSetStream(StoredDataSet dataset) {
        super(dataset.getHeaders());
        this.stream = dataset.getStream();
    }

    DataSetStream(List<String> headers, Stream<DataRecord> stream) {
        super(headers);
        this.stream = stream;
    }

    DataSetStream(List<String> headers) {
        super(headers);
        this.stream = Stream.empty();
    }

    public StoredDataSet createStoredDataSet() {
        return new StoredDataSet(getHeaders()).insertDataRecords(stream);
    }

    public DataSetStream createDataSetStream(Stream<DataRecord> stream) {
        return new DataSetStream(getHeaders(), stream);
    }
    
    public DataSetStream createDataSetStreamIncludeHeader(Stream<DataRecord> stream) {
        return new DataSetStream(getHeaders(), Stream.concat(getHeaderAsDataRecord(), stream));
    }

    Stream<DataRecord> getStream() {
        return stream;
    }

    public Stream<DataRecord> filter(BooleanExpression filterexpression) {
        try {
            return this.stream.filter(dr -> filterexpression.evaluate(dr));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Stream<DataRecord> sort(ExpressionList sortfields) {
        try {
            List<String> sortfieldnames = sortfields.stream()
                    .map(operand -> DataTypes.isStringExpression(operand).evaluate(DataRecord.EMPTY))
                    .collect(Collectors.toList());
            return this.stream.sorted((dr1, dr2) -> sortfieldnames.stream()
                    .map(fn -> dr1.get(fn).compareTo(dr2.get(fn)))
                    .filter(cmp -> cmp != 0).findFirst().orElse(0));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public Stream<DataRecord> buildNewDataRecordStream(ExpressionList fields) {
        try {
            return this.stream.map(datarecord -> new DataRecord(
                    this.getHeaders(),
                    fields.stream().map(operand -> DataTypes.isStringExpression(operand).evaluate(datarecord))
            )
            );
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void forEachOrdered(Consumer<DataRecord> action) {
        try {
            this.stream.forEachOrdered(dr -> action.accept(dr));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private Stream<DataRecord> getHeaderAsDataRecord() {
        return Stream.of(new DataRecord(getHeaders(), getHeaders()));
    }
}
