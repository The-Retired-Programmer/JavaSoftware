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

import uk.theretiredprogrammer.scmreportwriter.language.SyntaxTreeItem;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.Operator;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.functions.And;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Boolean2String;
import uk.theretiredprogrammer.scmreportwriter.language.functions.BooleanLiteral;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Concatonate;
import uk.theretiredprogrammer.scmreportwriter.language.functions.DataRecordField;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Equals;
import uk.theretiredprogrammer.scmreportwriter.language.functions.EqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Not;
import uk.theretiredprogrammer.scmreportwriter.language.functions.NotEquals;
import uk.theretiredprogrammer.scmreportwriter.language.functions.NotEqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.language.functions.Or;
import uk.theretiredprogrammer.scmreportwriter.language.Property;
import uk.theretiredprogrammer.scmreportwriter.language.functions.CmdParamValue;
import uk.theretiredprogrammer.scmreportwriter.language.functions.String2Boolean;
import uk.theretiredprogrammer.scmreportwriter.language.functions.EnvValue;
import uk.theretiredprogrammer.scmreportwriter.language.functions.SysValue;

public class SCM_ExpressionLanguage extends Language {

    public SCM_ExpressionLanguage() {
        setSyntaxTreeSymbols(new SyntaxTreeItem[]{
            new SyntaxTreeItem("boolean", new Operator("Boolean cast", PrecedenceGroup.MONADIC, String2Boolean::reduce)),
            new SyntaxTreeItem("string", new Operator("String cast", PrecedenceGroup.MONADIC, Boolean2String::reduce)),
            new SyntaxTreeItem("FALSE", new BooleanLiteral(false)),
            new SyntaxTreeItem("TRUE", new BooleanLiteral(true)),
            new SyntaxTreeItem("parameter", new Operator("Parameter value", PrecedenceGroup.MONADIC, CmdParamValue::reduce)),
            new SyntaxTreeItem("env", new Operator("Env value", PrecedenceGroup.MONADIC, EnvValue::reduce)),
            new SyntaxTreeItem("sys", new Operator("System Property value", PrecedenceGroup.MONADIC, SysValue::reduce)),});
        setSyntaxTreeOperators(new SyntaxTreeItem[]{
            new SyntaxTreeItem("!=~", new Operator("!=~", PrecedenceGroup.EQ, NotEqualsIgnoreCase::reduce)),
            new SyntaxTreeItem("&&", new Operator("&&", PrecedenceGroup.AND, And::reduce)),
            new SyntaxTreeItem("||", new Operator("||", PrecedenceGroup.OR, Or::reduce)),
            new SyntaxTreeItem("==", new Operator("==", PrecedenceGroup.EQ, Equals::reduce)),
            new SyntaxTreeItem("=~", new Operator("=~", PrecedenceGroup.EQ, EqualsIgnoreCase::reduce)),
            new SyntaxTreeItem("!=", new Operator("!=", PrecedenceGroup.EQ, NotEquals::reduce)),
            new SyntaxTreeItem("!", new Operator("!", PrecedenceGroup.MONADIC, Not::reduce)),
            new SyntaxTreeItem("+", new Operator("+", PrecedenceGroup.DIADIC, Concatonate::reduce)),
            new SyntaxTreeItem("[", new Operator("[", PrecedenceGroup.EXPBRA, ExpressionList::reduce_s)),
            new SyntaxTreeItem("]", new Operator("]", PrecedenceGroup.EXPKET, ExpressionList::reduce)),
            new SyntaxTreeItem("{", new Operator("{", PrecedenceGroup.EXPBRA, ExpressionMap::reduce_s)),
            new SyntaxTreeItem("}", new Operator("}", PrecedenceGroup.EXPKET, ExpressionMap::reduce)),
            new SyntaxTreeItem("(", new Operator("(", PrecedenceGroup.BRA, this::reduceBRA)),
            new SyntaxTreeItem(")", new Operator(")", PrecedenceGroup.KET, this::reduceKET)),
            new SyntaxTreeItem("$", new Operator("$", PrecedenceGroup.MONADIC, DataRecordField::reduce)),
            new SyntaxTreeItem(",", new Operator(",", PrecedenceGroup.EXPSEP, this::reduceEXPRESSIONSEPARATOR)),
            new SyntaxTreeItem(":", new Operator(":", PrecedenceGroup.PROPERTY, Property::reduce))
        });
    }
}
