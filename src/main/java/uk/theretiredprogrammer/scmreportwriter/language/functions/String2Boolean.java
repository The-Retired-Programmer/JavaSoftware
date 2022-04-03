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
package uk.theretiredprogrammer.scmreportwriter.language.functions;

import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.OperandStack;
import uk.theretiredprogrammer.scmreportwriter.language.OperatorStack;

public class String2Boolean extends BooleanExpression {

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        operatorstack.pop();
        operandstack.push(new String2Boolean(DataTypes.isStringExpression(operandstack.pop())));
    }

    private final StringExpression expression;

    public String2Boolean(StringExpression expression) {
        super("Boolean cast");
        this.expression = expression;
    }

    @Override
    public Boolean evaluate(DataSourceRecord datarecord) {
        return expression.evaluate(datarecord).equalsIgnoreCase("Yes") || expression.evaluate(datarecord).equalsIgnoreCase("True");
    }
}
