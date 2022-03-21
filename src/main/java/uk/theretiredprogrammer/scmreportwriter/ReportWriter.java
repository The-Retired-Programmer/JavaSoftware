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
import uk.theretiredprogrammer.scmreportwriter.language.InternalParserException;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;
import uk.theretiredprogrammer.scmreportwriter.language.LexerException;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;
import uk.theretiredprogrammer.scmreportwriter.language.ParserException;

public class ReportWriter {

    private final ReportDefinition definition;
    private final Map<String, DataSource> datasources = new HashMap<>();
    private final Configuration configuration;

    public ReportWriter(Configuration configuration) throws IOException, LexerException, ParserException, ReportWriterException {
        this.configuration = configuration;
        definition = new ReportDefinition();
        try {
            definition.buildReportDefinition(configuration.getDefinitionFile());
        } catch (InternalReportWriterException ex) {
            throw definition.getLanguageSource().newReportWriterException(ex);
        }
    }

    public void loadDataFiles() throws IOException, ReportWriterException {
        try {
            ExpressionMap datadefs = definition.getDatadefinitions();
            for (Entry<String, Operand> nameandparameters : datadefs.entrySet()) {
                ExpressionMap parameters = DataTypes.isExpressionMap(nameandparameters.getValue());
                datasources.put(nameandparameters.getKey(), DataSourceCSV.read(configuration, parameters));
            }
        } catch (InternalReportWriterException ex) {
            throw definition.getLanguageSource().newReportWriterException(ex);
        }
    }

    public void createAllReports() throws ReportWriterException, IOException, ConfigurationException {
        for (String reportname : definition.getAllReportNames()) {
            createReport(reportname);
        }
    }

    public void createReport(String reportname) throws ReportWriterException, IOException, ConfigurationException {
        try {
            ExpressionMap map = definition.getReportdefinitions(reportname);
            DataSource primaryds = datasources.get(DataTypes.isStringLiteral(map, "using"));
            ExpressionList headers = DataTypes.isExpressionList(map, "headers");
            BooleanExpression filter = DataTypes.isBooleanExpression(map, "filter");
            ExpressionList fields = DataTypes.isExpressionList(map, "fields");
            String output = DataTypes.isStringLiteral(map, "output");
            String title  = DataTypes.isStringLiteral(map, "title");
            // create report output
            List<List<String>> outputlines = new ArrayList<>();
            DataSourceRecord firstrecord = primaryds.get(0);
            outputlines.add(evaluate(headers, firstrecord));
            for (DataSourceRecord datarecord : primaryds) {
                if (filter == null || filter.evaluate(datarecord)) {
                    outputlines.add(evaluate(fields, datarecord));
                }
            }
            if (output == null){
                DataSourceCSV.sysout(title, outputlines);
            } else {
                DataSourceCSV.write(configuration, output, outputlines);
            }
        } catch (InternalReportWriterException ex) {
            throw definition.getLanguageSource().newReportWriterException(ex);
        } catch (InternalParserException ex) {
            throw definition.getLanguageSource().newReportWriterException(ex);
        }
    }

    private List<String> evaluate(ExpressionList fieldexpressions, DataSourceRecord datarecord) throws InternalParserException {
        List<String> fields = new ArrayList<>();
        for (Operand operand : fieldexpressions) {
            fields.add(DataTypes.isStringExpression(operand).evaluate(datarecord));
        }
        return fields;
    }
}
