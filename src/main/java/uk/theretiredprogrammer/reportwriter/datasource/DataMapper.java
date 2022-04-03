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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;
import uk.theretiredprogrammer.reportwriter.configuration.Configuration;
import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.StringExpression;

public class DataMapper {

    public static void addTransformedDataSource(Map<String, DataSource> datasources, String name, ExpressionMap parameters) throws RPTWTRException {
        new DataMapper().transform(datasources, name, parameters);
    }

    public void transform(Map<String, DataSource> datasources, String name, ExpressionMap parameters) throws RPTWTRException {
        String sourcename = getRequiredString(parameters, "using");
        DataSource data = datasources.get(sourcename);
        //
        BooleanExpression filterexpression = DataTypes.isBooleanExpression(parameters, "filter");
        Predicate<DataSourceRecord> predicate = filterexpression == null ? null :(dr)-> filterpredicate(dr, filterexpression);
        //
        ExpressionList sortfields = DataTypes.isExpressionList(parameters, "sort_by");
        List<String> fieldnames = sortfields == null? null: getSortFieldNames(sortfields);
        Comparator<DataSourceRecord> comparator = sortfields == null ? null : (dr1,dr2) -> sortcomparator(dr1,dr2,fieldnames);
        //
        if (Configuration.getDefault().getArgConfiguration().isListCmd()) {
            System.out.println("generating "+ name + " from " + sourcename);
        }
        DataSourceRecordStream result = DataSourceRecordStream.of(data.stream())
                        .filter(predicate)
                        .sort(comparator)
                        .map(null);
        if (result.isFailure()) {
            throw new RPTWTRException(result.getThrownMessage());
        }
        DataSource resultds = new DataSource(
                data.getColumnNames(),
                result.get().collect(Collectors.toList())
        );
        datasources.put(name, resultds);
    }
    
    private boolean filterpredicate(DataSourceRecord dr, BooleanExpression filterexpression) {
            return filterexpression.evaluate(dr);
    }
    
    private  int sortcomparator(DataSourceRecord dr1, DataSourceRecord dr2, List<String> fieldnames) {
        for (var fn : fieldnames){
            int cmp = dr1.getFieldValue(fn).compareTo(dr2.getFieldValue(fn));
            if (cmp != 0) return cmp;
        }
        return 0;
    }
   
    private String getRequiredString(ExpressionMap parameters, String key) throws RPTWTRException {
        StringExpression keyparameter = DataTypes.isStringExpression(parameters, key);
        if (keyparameter != null) {
            return keyparameter.evaluate(new DataSourceRecord());
        }
        throw new RPTWTRException(key + " parameter missing in generated_data statement", parameters);
    }

    private List<String> getSortFieldNames(ExpressionList sortfields) throws RPTWTRException {
        List<String> res = new ArrayList<>();
        for (var operand: sortfields){
            StringExpression stringexp = DataTypes.isStringExpression(operand);
            res.add(stringexp.evaluate(new DataSourceRecord()));
        }
        return res;
    }

}
