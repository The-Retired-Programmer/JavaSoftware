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

import uk.theretiredprogrammer.scmreportwriter.RPTWTRRuntimeException;
import uk.theretiredprogrammer.scmreportwriter.configuration.Configuration;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.OperandStack;
import uk.theretiredprogrammer.scmreportwriter.language.OperatorStack;

public class CmdParamValue extends StringExpression {

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        operatorstack.pop();
        operandstack.push(new CmdParamValue(DataTypes.isStringExpression(operandstack.pop())));
    }

    private final StringExpression expression;

    public CmdParamValue(StringExpression expression) {
        super("Command Parameter value");
        this.expression = expression;
    }

    @Override
    public String evaluate(DataSourceRecord datarecord) {
        String pval;
        try {
            pval = Configuration.getDefault().getArgConfiguration().getCommandParameter(Integer.parseInt(expression.evaluate(datarecord)));
        } catch (NumberFormatException ex) {
            throw new RPTWTRRuntimeException("Integer expected after \"parameter\"", expression);
        }
        if (pval == null) {
            throw new RPTWTRRuntimeException("No such parameter on command line", expression);
        }
        return pval;
    }
}
