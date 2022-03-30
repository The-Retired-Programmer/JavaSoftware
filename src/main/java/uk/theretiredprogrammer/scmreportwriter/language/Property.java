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
package uk.theretiredprogrammer.scmreportwriter.language;

import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;

public class Property implements Operand {

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws RPTWTRException {
        operatorstack.pop();
        Operand rhs = operandstack.pop();
        operandstack.push(new Property(DataTypes.isStringLiteral(operandstack.pop()), rhs));
    }

    private final String name;
    private final Operand expression;
    private TokenSourceLocator locator;

    public Property(String name, Operand expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public void setLocator(TokenSourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public TokenSourceLocator getLocator() {
        return locator;
    }

    public String getName() {
        return name;
    }

    public Operand getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(DataSourceRecord datarecord) throws RPTWTRException {
        return expression.evaluate(datarecord);
    }

    @Override
    public String toString() {
        return name;
    }
}
