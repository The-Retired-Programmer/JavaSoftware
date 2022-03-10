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
package uk.theretiredprogrammer.scmreportwriter;

import uk.theretiredprogrammer.scmreportwriter.language.AST;
import uk.theretiredprogrammer.scmreportwriter.language.OperandStack;
import uk.theretiredprogrammer.scmreportwriter.language.OperatorStack;
import uk.theretiredprogrammer.scmreportwriter.language.Operator;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.functions.And;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Boolean2String;
import uk.theretiredprogrammer.scmreportwriter.language.functions.BooleanLiteral;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Concatonate;
import uk.theretiredprogrammer.scmreportwriter.language.functions.DataRecordField;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Equals;
import uk.theretiredprogrammer.scmreportwriter.language.functions.EqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Not;
import uk.theretiredprogrammer.scmreportwriter.language.functions.NotEquals;
import uk.theretiredprogrammer.scmreportwriter.language.functions.NotEqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Or;
import uk.theretiredprogrammer.scmreportwriter.language.Property;
import uk.theretiredprogrammer.scmreportwriter.language.functions.String2Boolean;
import uk.theretiredprogrammer.scmreportwriter.language.Language.Precedence;
import uk.theretiredprogrammer.scmreportwriter.language.Language.PrecedenceGroup;

public class SCM_Language extends Language{
    
    public SCM_Language() {
        setASTSymbols(new AST[]{
                    new AST("boolean", new Operator("Boolean cast", PrecedenceGroup.MONADIC, String2Boolean::reduce)),
                    new AST("string", new Operator("String cast", PrecedenceGroup.MONADIC, Boolean2String::reduce)),
                    new AST("FALSE", new BooleanLiteral(false)),
                    new AST("TRUE", new BooleanLiteral(true)),
                    new AST("DATA", new Operator("DATA", PrecedenceGroup.COMMAND, this::reduceCOMMAND)),
                    new AST("FILTER", new Operator("FILTER", PrecedenceGroup.COMMAND, this::reduceCOMMAND)),
                    new AST("FIELDS", new Operator("FIELDS", PrecedenceGroup.COMMAND, this::reduceCOMMAND))
                });
        setASTOperators(new AST[]{
                    new AST("!=~", new Operator("!=~", PrecedenceGroup.EQ, NotEqualsIgnoreCase::reduce)),
                    new AST("&&", new Operator("&&", PrecedenceGroup.AND, And::reduce)),
                    new AST("||", new Operator("||", PrecedenceGroup.OR, Or::reduce)),
                    new AST("==", new Operator("==", PrecedenceGroup.EQ, Equals::reduce)),
                    new AST("=~", new Operator("=~", PrecedenceGroup.EQ, EqualsIgnoreCase::reduce)),
                    new AST("!=", new Operator("!=", PrecedenceGroup.EQ, NotEquals::reduce)),
                    new AST("!", new Operator("!", PrecedenceGroup.MONADIC, Not::reduce)),
                    new AST("+", new Operator("+", PrecedenceGroup.DIADIC, Concatonate::reduce)),
                    new AST("(", new Operator("(", PrecedenceGroup.BRA, this::reduceBRA)),
                    new AST(")", new Operator(")", PrecedenceGroup.KET, this::reduceKET)),
                    new AST("$", new Operator("$", PrecedenceGroup.MONADIC, DataRecordField::reduce)),
                    new AST(",", new Operator(",", PrecedenceGroup.EXPSEP, this::reduceEXPRESSIONSEPARATOR)),
                    new AST(":", new Operator(":", PrecedenceGroup.PROPERTY, Property::reduce))
                });

        setPrecedenceTable(
                new Precedence[][]{
                    { //rhs symbol START
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR,
                        //BRA, KET,
                        Precedence.ERROR, Precedence.ERROR,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR
                    },
                    { //rhs symbol END
                        //START, END, COMMAND, EXPSEP,
                        Precedence.SHIFT, Precedence.ERROR, Precedence.REDUCE, Precedence.ERROR,
                        //BRA, KET,
                        Precedence.ERROR, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol COMMAND
                        //START, END, COMMAND, EXPSEP, EXPEND,
                        Precedence.SHIFT, Precedence.ERROR, Precedence.EQUAL, Precedence.ERROR,
                        //BRA, KET,
                        Precedence.ERROR, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.ERROR
                    },
                    { //rhs symbol EXPSEP
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.EQUAL, 
                        //BRA, KET,
                        Precedence.EQUAL, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol BRA
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.ERROR,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT
                    },
                    { //rhs symbol KET
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.ERROR, Precedence.EQUAL, 
                        //BRA, KET,
                        Precedence.EQUAL, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol PROPERTY
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.ERROR, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol OR
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol AND
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.SHIFT, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol EQ
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.REDUCE, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol DIADIC
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.REDUCE, Precedence.REDUCE
                    },
                    { //rhs symbol MONADIC
                        //START, END, COMMAND, EXPSEP,
                        Precedence.ERROR, Precedence.ERROR, Precedence.SHIFT, Precedence.SHIFT, 
                        //BRA, KET,
                        Precedence.SHIFT, Precedence.REDUCE,
                        //PROPERTY, OR, AND, EQ, DIADIC, MONADIC,
                        Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT, Precedence.SHIFT
                    }
                });
    }
    
    private void reduceCOMMAND(OperatorStack operatorstack, OperandStack operandstack) {
        ExpressionMap map = new ExpressionMap();
        Operator cmd;
        do {
            cmd = operatorstack.pop();
            map.put(cmd.toString(), operandstack.pop());
        } while (getPrecedence(operatorstack.peek(), cmd) == Precedence.EQUAL);
        operandstack.push(map);
    }
}
