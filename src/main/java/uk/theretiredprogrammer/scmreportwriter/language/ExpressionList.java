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
import java.util.stream.Collectors;
import uk.theretiredprogrammer.scmreportwriter.Configuration;
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;

public class ExpressionList extends ArrayList<Operand> implements Operand {

    private int location;
    private int length;

    @Override
    public void setLocation(int charoffset, int length) {
        location = charoffset;
        this.length = length;
    }

    @Override
    public int getLocation() {
        return location;
    }

    @Override
    public int getLength() {
        return length;
    }

    public static void reduce_s(Language language, OperatorStack operatorstack, OperandStack operandstack) throws InternalParserException {
        throw new InternalParserException(operatorstack.pop(), "Illegal to reduce on '[' operator");
    }

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws InternalParserException {
        ExpressionList elist = new ExpressionList();
        operatorstack.pop(); // this will be "]"
        while (operatorstack.peek().toString().equals(",")) {
            addExpression2List(operatorstack, operandstack, elist);
            
        }
        if (operatorstack.peek().toString().equals("[")) {
            addExpression2List(operatorstack, operandstack, elist);
        } else {
             throw new InternalParserException(operandstack.peek(), "'[' expected when generating a ExpressionList");
        }
        operandstack.push(elist);
    }

    private static void addExpression2List(OperatorStack operatorstack, OperandStack operandstack, ExpressionList elist) throws InternalParserException {
        Operand operand = operandstack.pop();
        if (operand instanceof Property) {
            throw new InternalParserException(operand, "Property is not allowed in ExpressionList context");
        } else {
            elist.add(0, operand);
        }
        operatorstack.pop();
    }

    @Override
    public List evaluate(Configuration configuration, DataSourceRecord datarecord) {
        return this.stream().map((item) -> item.evaluate(configuration, datarecord)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Expression List";
    }
}
