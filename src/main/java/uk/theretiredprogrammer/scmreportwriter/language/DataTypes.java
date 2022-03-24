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

public class DataTypes  {
    
    public static ExpressionMap isExpressionMap(Operand operand) throws InternalReportWriterException {
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new InternalReportWriterException(operand, "Requires an ExpressionMap");
    }
    
    public static ExpressionMap isExpressionMap(ExpressionMap parent, String key) throws InternalReportWriterException {
        Operand operand = parent.get(key);
        if (operand == null) return null;   
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new InternalReportWriterException(parent.get(key), "Requires an ExpressionMap");
    }
    
    public static ExpressionList isExpressionList(ExpressionMap parent, String key) throws InternalReportWriterException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof ExpressionList list){
            return list;
        }
        throw new InternalReportWriterException(parent.get(key), "Requires an ExpressionList");
    }

    public static BooleanExpression isBooleanExpression(Operand operand) throws InternalParserException {
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new InternalParserException(operand, "Requires a boolean value");
    }
    
    public static BooleanExpression isBooleanExpression(ExpressionMap parent, String key) throws InternalReportWriterException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new InternalReportWriterException(parent.get(key), "requires a boolean value");
    }

    public static StringExpression isStringExpression(Operand operand) throws InternalParserException {
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new InternalParserException(operand, "Requires a String value");
    }
    
    public static StringExpression isStringExpression(ExpressionList parent, int index) throws InternalReportWriterException {
        Operand operand = parent.get(index);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new InternalReportWriterException(parent.get(index), "Requires a String value");
    }
    
    public static StringExpression isStringExpression(ExpressionMap parent, String key) throws InternalReportWriterException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new InternalReportWriterException(parent.get(key), "Requires a String value");
    }
    
    public static String isStringLiteral(Operand operand) throws InternalParserException {
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new InternalParserException(operand, "Requires a String literal value");
    }
    
    public static String isStringLiteral(ExpressionMap parent, String key) throws InternalReportWriterException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new InternalReportWriterException(parent.get(key), "requires a String literal value");
    }
}
