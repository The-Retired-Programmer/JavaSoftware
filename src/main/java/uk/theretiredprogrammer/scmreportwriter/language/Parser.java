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
import uk.theretiredprogrammer.scmreportwriter.language.Language.Precedence;

public class Parser {

    private final Language language;
    private final DefinitionSource source;

    public Parser(DefinitionSource source, Language language) {
        this.language = language;
        this.source = source;
    }

    private final OperatorStack operatorstack = new OperatorStack();
    private final OperandStack operandstack = new OperandStack();

    public Operand parse() throws RPTWTRException {
        operatorstack.push(language.OPERATOR_START);
        for (S_Token token : source.getS_Tokens()) {
            if (token instanceof Operator operator) {
                addOperator(operator);
            } else {
                if (token instanceof Operand operand) {
                    operandstack.push(operand);
                } else {
                    throw new RPTWTRException("Parser PANIC - unknown token type - not Operator or Operand", token);
                }
            }
        }
        addOperator(language.OPERATOR_END);
        return operandstack.pop();
    }

    private void addOperator(Operator operator) throws RPTWTRException {
        switch (language.getPrecedence(operatorstack.peek(), operator)) {
            case SHIFT ->
                operatorstack.push(operator);
            case REDUCE -> {
                while (language.getPrecedence(operatorstack.peek(), operator) == Precedence.REDUCE) {
                    operatorstack.peek().reduction.accept(language, operatorstack, operandstack);
                }
                operatorstack.push(operator);
            }
            case EQUAL ->
                operatorstack.push(operator);
            case ERROR ->
                throw new RPTWTRException("Bad Syntax", operator);
        }
    }
}
