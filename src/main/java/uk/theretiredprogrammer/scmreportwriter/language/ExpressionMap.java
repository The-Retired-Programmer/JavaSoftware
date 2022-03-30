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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;

public class ExpressionMap extends HashMap<String, Operand> implements Operand {

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
        throw new RPTWTRException("Illegal to reduce on '{' operator", operatorstack.pop());
    }

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws RPTWTRException {
        ExpressionMap emap = new ExpressionMap();
        operatorstack.pop(); // this will be "}"
        while (operatorstack.peek().toString().equals(",")) {
            addProperty2Map(operatorstack, operandstack, emap);
        }
        if (operatorstack.peek().toString().equals("{")) {
            addProperty2Map(operatorstack, operandstack, emap);
        } else {
            throw new RPTWTRException("'{' expected when generating a ExpressionMap", operandstack.peek());
        }
        operandstack.push(emap);
    }

    private static void addProperty2Map(OperatorStack operatorstack, OperandStack operandstack, ExpressionMap emap) throws RPTWTRException {
        Operand operand = operandstack.pop();
        if (operand instanceof Property property) {
            emap.put(property.getName(), property.getExpression());
        } else {
            throw new RPTWTRException("Expression is not allowed in ExpressionMap context", operand);
        }
        operatorstack.pop();
    }

    @Override
    public Map<String, Object> evaluate(DataSourceRecord datarecord) throws RPTWTRException {
        Map<String, Object> result = new HashMap<>();
        for (Entry<String, Operand> e : entrySet()) {
            result.put(e.getKey(), e.getValue().evaluate(datarecord));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Expression Map";
    }
}
