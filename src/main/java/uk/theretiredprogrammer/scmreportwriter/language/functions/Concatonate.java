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

import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.OperandStack;
import uk.theretiredprogrammer.scmreportwriter.language.OperatorStack;
import uk.theretiredprogrammer.scmreportwriter.language.ParserException;

public class Concatonate extends StringExpression{
    
    public static void reduce(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        operatorstack.pop();
        StringExpression rhs = DataTypes.stringExpression(operandstack.pop());
        operandstack.push(new Concatonate(DataTypes.stringExpression(operandstack.pop()), rhs));
    }
    
    private final StringExpression lhs;
    private final StringExpression rhs;
    
    public Concatonate(StringExpression lhs, StringExpression rhs) {
        super("Concatonate expression");
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String evaluate(DataSourceRecord datarecord) {
        return lhs.evaluate(datarecord)+rhs.evaluate(datarecord);
    }
}
