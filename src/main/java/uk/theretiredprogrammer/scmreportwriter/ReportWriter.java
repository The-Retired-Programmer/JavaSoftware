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

import uk.theretiredprogrammer.scmreportwriter.datasource.DataMapper;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceCSV;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;

public class ReportWriter {

    private final ReportDefinition definition;
    private final Map<String, DataSource> datasources = new HashMap<>();

    public ReportWriter() {
        definition = new ReportDefinition();
    }

    public boolean buildReportDefinition() throws IOException, RPTWTRException {
        return definition.buildReportDefinition();
    }

    public void loadDataFiles() throws IOException, RPTWTRException {
        ExpressionMap datadefs = definition.getDatadefinitions();
        for (Entry<String, Operand> nameandparameters : datadefs.entrySet()) {
            ExpressionMap parameters = DataTypes.isExpressionMap(nameandparameters.getValue());
            datasources.put(nameandparameters.getKey(), DataSourceCSV.read(nameandparameters.getKey(), parameters));
        }
    }

    public void createAllGeneratedFiles() throws RPTWTRException {
        ExpressionMap generatedfiles = definition.getGeneratedFilesdefinitions();
        for (Entry<String, Operand> nameandparameters : generatedfiles.entrySet()) {
            ExpressionMap parameters = DataTypes.isExpressionMap(nameandparameters.getValue());
            DataMapper.addTransformedDataSource(datasources, nameandparameters.getKey(), parameters);
        }
    }

    public void createAllReports() throws RPTWTRException, IOException {
        for (Operand operand : definition.getReportdefinitions()) {
            ExpressionMap map = DataTypes.isExpressionMap(operand);
            DataSource primaryds = datasources.get(DataTypes.isStringLiteral(map, "using"));
            ExpressionList headers = DataTypes.isExpressionList(map, "headers");
            BooleanExpression filter = DataTypes.isBooleanExpression(map, "filter");
            ExpressionList fields = DataTypes.isExpressionList(map, "fields");
            String to = DataTypes.isStringLiteral(map, "to");
            String title = DataTypes.isStringLiteral(map, "title");
            // create report output
            List<List<String>> outputlines = new ArrayList<>();
            DataSourceRecord firstrecord = primaryds.get(0);
            outputlines.add(evaluate(headers, firstrecord));
            for (DataSourceRecord datarecord : primaryds) {
                if (filter == null || filter.evaluate(datarecord)) {
                    outputlines.add(evaluate(fields, datarecord));
                }
            }
            if (to == null) {
                DataSourceCSV.sysout(title, outputlines);
            } else {
                DataSourceCSV.write(to, outputlines);
            }
        }
    }

    private List<String> evaluate(ExpressionList fieldexpressions, DataSourceRecord datarecord) throws RPTWTRException {
        List<String> fields = new ArrayList<>();
        for (Operand operand : fieldexpressions) {
            fields.add(DataTypes.isStringExpression(operand).evaluate(datarecord));
        }
        return fields;
    }
}
