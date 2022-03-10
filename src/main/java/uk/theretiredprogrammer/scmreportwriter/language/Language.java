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

import uk.theretiredprogrammer.scmreportwriter.language.functions.StringLiteral;

public abstract class Language {

    private AST[] operators;

    private AST[] symbols;

    private Precedence[][] precedencetable;

    public void setASTSymbols(AST[] symbols) {
        this.symbols = symbols;
    }

    public void setASTOperators(AST[] operators) {
        this.operators = operators;
    }

    public void setPrecedenceTable(Precedence[][] precedencetable) {
        this.precedencetable = precedencetable;
    }

    // Lexer support
    public enum CharType {
        WHITESPACE, TEXT, SYMBOL, TEXTSTARTDELIMITER, TEXTENDDELIMITER
    };

    public static CharType charType(char c) {
        if (Character.isLetter(c) || Character.isDigit(c)) {
            return CharType.TEXT;
        }
        if (c == '{') {
            return CharType.TEXTSTARTDELIMITER;
        }
        if (c == '}') {
            return CharType.TEXTENDDELIMITER;
        }
        if (Character.isWhitespace(c)) {
            return CharType.WHITESPACE;
        }
        return CharType.SYMBOL;
    }

    private Operator extractedoperator;

    public String extractOperator(String input) throws LexerException {
        extractedoperator = null;
        for (AST terminal : operators) {
            if (terminal.token instanceof Operator operator) {
                if (input.startsWith(terminal.tokenstring)) {
                    extractedoperator = operator;
                    return input.substring(terminal.tokenstring.length());
                }
            }
        }
        throw new LexerException("Illegal Operator: " + input);
    }

    public Operator getExtractedOperator() {
        return extractedoperator;
    }

    public S_Token getToken(String tokenstring) {
        for (AST symbol : symbols) {
            if (symbol.tokenstring.equals(tokenstring)) {
                return symbol.token;
            }
        }
        return new StringLiteral(tokenstring);
    }

    public enum PrecedenceGroup {
        START, END, COMMAND, EXPSEP,
        BRA, KET,
        PROPERTY, OR, AND, EQ, DIADIC, MONADIC
    }

    public enum Precedence {
        SHIFT, EQUAL, REDUCE, ERROR
    };

    public Precedence getPrecedence(Operator loperator, Operator roperator) {
        return precedencetable[roperator.operatorgroup.ordinal()][loperator.operatorgroup.ordinal()];
    }

    public final Operator OPERATOR_START = new Operator("START", PrecedenceGroup.START, this::reduceSTART);
    public final Operator OPERATOR_END = new Operator("END", PrecedenceGroup.END, this::reduceEND);

    private void reduceSTART(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        throw new ParserException("illegal to reduce on 'START' operator");
    }

    private void reduceEND(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        if (operatorstack.size() != 2) { // ie not end and start
            throw new ParserException("operator stack not empty when reduction completed");
        }
        if (operandstack.size() != 1) {
            throw new ParserException("single operand expected (the program) - wrong number of operands remain");
        }
    }

    public void reduceEXPRESSIONSEPARATOR(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        throw new ParserException("illegal to reduce on ',' operator");
    }

    public void reduceBRA(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        throw new ParserException("illegal to reduce on '(' operator");
    }

    public void reduceKET(OperatorStack operatorstack, OperandStack operandstack) throws ParserException {
        Operator operator = operatorstack.pop(); // this will be ")"
        if (operatorstack.peek().name.equals("(")) {
            operatorstack.pop();
            return;
        }
        int count = 0;
        while (getPrecedence(operatorstack.peek(), operator) == Precedence.EQUAL) {
            operator = operatorstack.pop();
            count++;
        }
        if (operandstack.peek() instanceof Property) {
            ExpressionMap.reduce(count, operandstack);
        } else {
            ExpressionList.reduce(count, operandstack);
        }
    }
}
