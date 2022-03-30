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
import java.util.Map;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;

public class DataMapper {

    public static void addTransformedDataSource(Map<String, DataSource> datasources, String name, ExpressionMap parameters) throws RPTWTRException {
        new DataMapper().transform(datasources, name, parameters);
    }

    public void transform(Map<String, DataSource> datasources, String name, ExpressionMap parameters) throws RPTWTRException {
        DataSource data = datasources.get(getRequiredString(parameters, "using"));
        // filter <boolean expression>  [optional]
        BooleanExpression filterexpression = DataTypes.isBooleanExpression(parameters, "filter");
        if (filterexpression != null) {
            data = applyFilter(data, filterexpression);
        }
        ExpressionList sortfields = DataTypes.isExpressionList(parameters, "sort_by");
        if (sortfields != null) {
            data = applySort(data, sortfields);
        }
        // this is temporarly a simple copy through transform
        datasources.put(name, data);
    }

    private String getRequiredString(ExpressionMap parameters, String key) throws RPTWTRException {
        StringExpression keyparameter = DataTypes.isStringExpression(parameters, key);
        if (keyparameter != null) {
            return keyparameter.evaluate(new DataSourceRecord());
        }
        throw new RPTWTRException(key + " parameter missing in generated_data statement", parameters);
    }

    private DataSource applyFilter(DataSource data, BooleanExpression filter) throws RPTWTRException {
        DataSource filtered = new DataSource();
        boolean headerrecord = true;
        for (DataSourceRecord datarecord : data) {
            if (headerrecord || filter == null || filter.evaluate(datarecord)) {
                filtered.add(datarecord);
            }
            headerrecord = false;
        }
        return filtered;
    }

    private DataSource applySort(DataSource data, ExpressionList sortfields) throws RPTWTRException {
        List<String> sortfieldnames = getSortFieldNames(sortfields);
        //return data.stream().sort(Comparator.comparing(DataSourceIO::getField(sortfieldnames.get(0)))).collect(Collectors.toList);
        return data;  // needs proper implementation
    }

    private List<String> getSortFieldNames(ExpressionList sortfields) throws RPTWTRException {
        List<String> fieldnames = new ArrayList<>();
        for (Operand operand : sortfields) {
            StringExpression stringexp = DataTypes.isStringExpression(operand);
            fieldnames.add(stringexp.evaluate(new DataSourceRecord()));
        }
        return fieldnames;
    }

}
