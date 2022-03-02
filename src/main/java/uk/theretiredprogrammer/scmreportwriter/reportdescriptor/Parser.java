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
package uk.theretiredprogrammer.scmreportwriter.reportdescriptor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.expression.And;
import uk.theretiredprogrammer.scmreportwriter.expression.Boolean2String;
import uk.theretiredprogrammer.scmreportwriter.expression.Concatonate;
import uk.theretiredprogrammer.scmreportwriter.expression.DataRecordField;
import uk.theretiredprogrammer.scmreportwriter.expression.Equals;
import uk.theretiredprogrammer.scmreportwriter.expression.EqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Expression;
import uk.theretiredprogrammer.scmreportwriter.expression.ListSeparator;
import uk.theretiredprogrammer.scmreportwriter.expression.Literal;
import uk.theretiredprogrammer.scmreportwriter.expression.Not;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEquals;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Or;
import uk.theretiredprogrammer.scmreportwriter.expression.String2Boolean;
import uk.theretiredprogrammer.scmreportwriter.reportdescriptor.S_Token.Tokentype;


public class Parser {

    private final Deque<ParseStackValue> parsestack = new ArrayDeque<>();

    public Expression<?> parse(List<S_Token> tokens) throws ParserException {
        for (S_Token token : tokens) {
            if (S_Token.isOperatorToken(token)) {
                addOperator(token.getOperator());
            } else {
                addOperand(token);
            }
        }
        return getExpression();
    }

    private void addOperator(Tokentype operator) throws ParserException {
        if (operator == Tokentype.START) {
            leftHandle(operator);
        } else {
            switch (getPrecidence(parsestack.peek().operator, operator)){
                case LEFTH -> leftHandle(operator);
                case RIGHTH -> rightHandle(operator);
                case EQUALH -> equalHandle(operator);
                case ERRORH -> throw new ParserException("Bad Syntax");
            }
        }
    }

    private void addOperand(S_Token token) throws ParserException{
        switch (token.getOperandType()) {
            case 's' ->
                parsestack.peek().addOperand(new Literal<>((String)token.getOperand()));
            case 'b' ->
                parsestack.peek().addOperand(new Literal<>((Boolean)token.getOperand()));
            default ->
                throw new ParserException("unknown Operand datatype");
        }
    }

    private Expression<?> getExpression() throws ParserException {
        if (parsestack.size() == 2 && parsestack.pop().getOperator() == Tokentype.END) {
            ParseStackValue root = parsestack.peek();
            if (root.getOperator() == Tokentype.START) {
                Expression<?> operand = root.getOperand();
                if (operand != null) {
                    return operand;
                }
            }
        }
        throw new ParserException("Illegal syntax");
    }

    private void leftHandle(Tokentype tt) {
        parsestack.push(new ParseStackValue(tt));
    }

    private void rightHandle(Tokentype tt) throws ParserException {
        while (getPrecidence(parsestack.peek().operator, tt) == Precidence.RIGHTH) {
            ParseStackValue popped = parsestack.pop();
            ParseStackValue peeked = parsestack.peek();
            while (getPrecidence(peeked.operator, popped.operator) == Precidence.EQUALH) {
                popped = parsestack.pop();
                peeked = parsestack.peek();
            }
            Expression exp = getExpression(popped.getOperator(), peeked.getOperand(), popped.getOperand());
            peeked.addOperand(exp);
        }
        addOperator(tt);
    }

    private void equalHandle(Tokentype tt) {
        parsestack.push(new ParseStackValue(tt));
    }
    
    private enum Precidence { LEFTH, EQUALH, RIGHTH, ERRORH };
    
    private Precidence getPrecidence(Tokentype loperator, Tokentype roperator) {
        return precidencetable[loperator.ordinal()][roperator.ordinal()];
    }
    
    private final Precidence[][] precidencetable = new Precidence[][] {
        {   // not
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // concatonate
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // and
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // or
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // equals
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // equalsignorecase
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // notequals
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // notequalsignorecase
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // bra
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.EQUALH, Precidence.LEFTH, Precidence.ERRORH, Precidence.ERRORH,
            // end, start, string2boolean, boolean2string
            Precidence.ERRORH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // ket
            // not, concatonate, and, or,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.ERRORH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.RIGHTH, Precidence.RIGHTH
        },
        {   // fieldop
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // listseparator
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // listterminator
            // not, concatonate, and, or,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH
        },
        {   // end
           // not, concatonate, and, or,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH,
            // end, start, string2boolean, boolean2string
            Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH, Precidence.ERRORH
        },
        {   // start
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH,
            // end, start, string2boolean, boolean2string
            Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // string2boolean
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
        {   // boolean2string
            // not, concatonate, and, or,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // equals, equalsignorecase, notequals, notequalsignorecase,
            Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // bra, ket, fieldop, listseparator,listterminator,
            Precidence.LEFTH, Precidence.RIGHTH, Precidence.LEFTH, Precidence.RIGHTH, Precidence.RIGHTH,
            // end, start, string2boolean, boolean2string
            Precidence.RIGHTH, Precidence.ERRORH, Precidence.LEFTH, Precidence.LEFTH
        },
    };

    private Expression getExpression(Tokentype operator, Expression left, Expression right) throws ParserException {
        return switch (operator.ordinal()) {
            case 0 -> new Not(right); // NOT
            case 1 -> new Concatonate(left,right); // CONCATONATE
            case 2 -> new And(left, right); // AND
            case 3 -> new Or(left, right); // OR
            case 4 -> new Equals(left, right); // EQUALS
            case 5 -> new EqualsIgnoreCase(left, right); // EQUALSIGNORECASE
            case 6 -> new NotEquals(left, right); // NOTEQUALS
            case 7 -> new NotEqualsIgnoreCase(left, right); // NOTEQUALSIGNORECASE
            case 8 -> right; // BRA
            case 9 -> right; // KET
            case 10 -> new DataRecordField(right); // FIELDOP
            case 11 -> new ListSeparator(left,right);  // LISTSEPARATOR
            case 12 -> new ListSeparator(left,right); // LISTTERMINATOR
            case 13 -> throw new ParserException("attempting to create an END object on syntax tree"); // END
            case 14 -> throw new ParserException("attempting to create a START object on syntax tree"); // START
            case 15 -> new String2Boolean(right); //STRING2BOOLEAN
            case 16 -> new Boolean2String(right); //BOOLEAN2STRING
            case 17 -> throw new ParserException("attempting to create a BOOLEAN OPERAND on syntax tree"); //BOOLEAn operand
            case 18 -> throw new ParserException("attempting to create a STRING OPERAND on syntax tree"); //STRING (operands)
            default -> throw new ParserException("attempting to create a unknown object on syntax tree"); // ???? operator
        };
    }

    private class ParseStackValue<T> {

        public final Tokentype operator;
        public Expression<?> operand = null;

        public ParseStackValue(Tokentype operator) {
            this.operator = operator;
        }

        public void addOperand(Expression<?> operand) {
            this.operand = operand;
        }

        public Tokentype getOperator() {
            return operator;
        }

        public Expression<?> getOperand() {
            return operand;
        }
    }
}
