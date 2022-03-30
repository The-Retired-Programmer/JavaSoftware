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

import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;

public class ExpressionList extends ArrayList<Operand> implements Operand {

    private TokenSourceLocator locator;

    @Override
    public void setLocator(TokenSourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public TokenSourceLocator getLocator() {
        return locator;
    }

    public static void reduce_s(Language language, OperatorStack operatorstack, OperandStack operandstack) throws RPTWTRException {
        throw new RPTWTRException("Illegal to reduce on '[' operator", operatorstack.pop());
    }

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws RPTWTRException {
        ExpressionList elist = new ExpressionList();
        operatorstack.pop(); // this will be "]"
        while (operatorstack.peek().toString().equals(",")) {
            addExpression2List(operatorstack, operandstack, elist);

        }
        if (operatorstack.peek().toString().equals("[")) {
            addExpression2List(operatorstack, operandstack, elist);
        } else {
            throw new RPTWTRException("'[' expected when generating a ExpressionList", operandstack.peek());
        }
        operandstack.push(elist);
    }

    private static void addExpression2List(OperatorStack operatorstack, OperandStack operandstack, ExpressionList elist) throws RPTWTRException {
        Operand operand = operandstack.pop();
        if (operand instanceof Property) {
            throw new RPTWTRException("Property is not allowed in ExpressionList context", operand);
        } else {
            elist.add(0, operand);
        }
        operatorstack.pop();
    }

    @Override
    public List evaluate(DataSourceRecord datarecord) throws RPTWTRException {
        List result = new ArrayList();
        for (Operand operand : this) {
            result.add(operand.evaluate(datarecord));
        }
        return result;
        //return this.stream().map((item) -> item.evaluate( datarecord)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Expression List";
    }
}
