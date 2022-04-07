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
package uk.theretiredprogrammer.reportwriter;

import java.io.File;
import uk.theretiredprogrammer.reportwriter.datasource.DataSetFromCSV;
import uk.theretiredprogrammer.reportwriter.configuration.Configuration;
import uk.theretiredprogrammer.reportwriter.datasource.DataSetStream;
import uk.theretiredprogrammer.reportwriter.datasource.DataSetToCSV;
import uk.theretiredprogrammer.reportwriter.datasource.DataSetToSysout;
import uk.theretiredprogrammer.reportwriter.datasource.DataSets;
import uk.theretiredprogrammer.reportwriter.datasource.StoredDataSet;
import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;

public class ReportWriter {

    private final ReportCompiler compiled;
    private final DataSets datasets = new DataSets();

    @SuppressWarnings("UseSpecificCatch")
    public ReportWriter(File f) throws RPTWTRException {
        compiled = new ReportCompiler(f);
    }

    public void loadDataFiles() throws RPTWTRException {
        try {
            ExpressionMap datadefs = compiled.getCompiledOutputDataStatements();
            datadefs.entrySet().stream()
                    .forEach(nameandparameters -> {
                        datasets.saveDataSet(nameandparameters.getKey(),
                                DataSetFromCSV.create(
                                        nameandparameters.getKey(),
                                        DataTypes.isExpressionMap(nameandparameters.getValue())));
                    });
        } catch (Throwable t) {
            throw new RPTWTRException(t);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    public void createAllGeneratedFiles() throws RPTWTRException {
        try {
            compiled.getCompiledOutputGeneratedDataStatements().entrySet()
                    .forEach(nameandparameters -> {
                        String toname = nameandparameters.getKey();
                        ExpressionMap parameters = DataTypes.isExpressionMap(nameandparameters.getValue());
                        String fromname = DataTypes.getRequiredString(parameters, "using", "generated_data");
                        StoredDataSet from = datasets.getDataSet(fromname);
                        DataSetStream datarecordsstream = from.createDataRecordStream();
                        BooleanExpression filterexpression = DataTypes.isBooleanExpression(parameters, "filter");
                        if (filterexpression != null) {
                            datarecordsstream = datarecordsstream.createDataSetStream(datarecordsstream.filter(filterexpression));
                        }
                        ExpressionList sortfields = DataTypes.isExpressionList(parameters, "sort_by");
                        if (sortfields != null) {
                            datarecordsstream = datarecordsstream.createDataSetStream(datarecordsstream.sort(sortfields));
                        }
                        datasets.saveDataSet(toname, datarecordsstream.createStoredDataSet());
                        if (Configuration.getDefault().getArgConfiguration().isListCmd()) {
                            System.out.println("generating " + toname + " from " + fromname);
                        }
                    });
        } catch (Throwable t) {
            throw new RPTWTRException(t);
        }
    }

    public void createAllReports() throws RPTWTRException {
        try {
            compiled.getCompiledOutputReportsStatements().stream()
                    .forEachOrdered((operand) -> {
                        ExpressionMap map = DataTypes.isExpressionMap(operand);
                        StoredDataSet primarydataset = datasets.getDataSet(DataTypes.getRequiredString(map, "using", "reports"));
                        DataSetStream primarystream = primarydataset.createDataRecordStream();
                        ExpressionList headers = DataTypes.isExpressionList(map, "headers");
                        BooleanExpression filterexpression = DataTypes.isBooleanExpression(map, "filter");
                        ExpressionList fields = DataTypes.getRequiredList(map, "fields", "reports");
                        String to = DataTypes.isStringLiteral(map, "to");
                        String title = DataTypes.isStringLiteral(map, "title");
                        if (filterexpression != null) {
                            primarystream = primarystream.createDataSetStream(primarystream.filter(filterexpression));
                        }
                        StoredDataSet dataset = primarystream.createStoredDataSet();
                        if (!dataset.isEmpty()) {
                            var headerfields = dataset.evaluateheaders(headers);
                            primarystream = dataset.createDataRecordStream(headerfields);
                            primarystream = primarystream.createDataSetStreamIncludeHeader(primarystream.buildNewDataRecordStream(fields));
                            if (to == null) {
                                DataSetToSysout.display(title, primarystream);
                            } else {
                                DataSetToCSV.save(to, primarystream);
                            }
                        }
                    });
        } catch (Throwable t) {
            throw new RPTWTRException(t);
        }
    }
}
