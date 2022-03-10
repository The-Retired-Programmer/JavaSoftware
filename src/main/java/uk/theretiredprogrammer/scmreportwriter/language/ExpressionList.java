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
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;

public class ExpressionList extends ArrayList<Operand> implements Operand {

    public static void reduce(int count, OperandStack operandstack) throws ParserException {
        ExpressionList elist = new ExpressionList();
        while (count > 0) {
            Operand operand = operandstack.pop();
            if (operand instanceof Property) {
                throw new ParserException("Property is not allowed in ExpressionList context");
            } else {
                elist.add(0, operand);
                count--;
            }
        }
        operandstack.push(elist);
    }
    
    @Override
    public List evaluate(DataSourceRecord datarecord) {
        return this.stream().map((item)-> item.evaluate(datarecord)).collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "Expression List";
    }
}
