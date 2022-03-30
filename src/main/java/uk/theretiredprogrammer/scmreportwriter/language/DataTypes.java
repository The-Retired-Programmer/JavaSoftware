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
import uk.theretiredprogrammer.scmreportwriter.language.functions.StringLiteral;

public class DataTypes  {
    
    public static ExpressionMap isExpressionMap(Operand operand) throws RPTWTRException {
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new RPTWTRException("Requires an ExpressionMap",operand);
    }
    
    public static ExpressionMap isExpressionMap(ExpressionMap parent, String key) throws RPTWTRException {
        Operand operand = parent.get(key);
        if (operand == null) return null;   
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new RPTWTRException("Requires an ExpressionMap", parent.get(key));
    }
    
    public static ExpressionList isExpressionList(ExpressionMap parent, String key) throws RPTWTRException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof ExpressionList list){
            return list;
        }
        throw new RPTWTRException("Requires an ExpressionList", parent.get(key));
    }

    public static BooleanExpression isBooleanExpression(Operand operand) throws RPTWTRException {
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new RPTWTRException("Requires a boolean value", operand);
    }
    
    public static BooleanExpression isBooleanExpression(ExpressionMap parent, String key) throws RPTWTRException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new RPTWTRException("requires a boolean value", parent.get(key));
    }

    public static StringExpression isStringExpression(Operand operand) throws RPTWTRException {
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRException("Requires a String value", operand);
    }
    
    public static StringExpression isStringExpression(ExpressionList parent, int index) throws RPTWTRException {
        Operand operand = parent.get(index);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRException("Requires a String value", parent.get(index));
    }
    
    public static StringExpression isStringExpression(ExpressionMap parent, String key) throws RPTWTRException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRException("Requires a String value", parent.get(key));
    }
    
    public static String isStringLiteral(Operand operand) throws RPTWTRException {
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new RPTWTRException("Requires a String literal value", operand);
    }
    
    public static String isStringLiteral(ExpressionMap parent, String key) throws RPTWTRException {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new RPTWTRException("requires a String literal value", parent.get(key));
    }
}
