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
package uk.theretiredprogrammer.reportwriter.language;

import uk.theretiredprogrammer.reportwriter.RPTWTRRuntimeException;
import uk.theretiredprogrammer.reportwriter.language.functions.StringLiteral;

public class DataTypes  {
    
    public static ExpressionMap isExpressionMap(Operand operand) {
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new RPTWTRRuntimeException("Requires an ExpressionMap",operand);
    }
    
    public static ExpressionMap isExpressionMap(ExpressionMap parent, String key) {
        Operand operand = parent.get(key);
        if (operand == null) return null;   
        if (operand instanceof ExpressionMap map){
            return map;
        }
        throw new RPTWTRRuntimeException("Requires an ExpressionMap", parent.get(key));
    }
    
    public static ExpressionList isExpressionList(ExpressionMap parent, String key) {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof ExpressionList list){
            return list;
        }
        throw new RPTWTRRuntimeException("Requires an ExpressionList", parent.get(key));
    }

    public static BooleanExpression isBooleanExpression(Operand operand) {
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new RPTWTRRuntimeException("Requires a boolean value", operand);
    }
    
    public static BooleanExpression isBooleanExpression(ExpressionMap parent, String key) {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof BooleanExpression bexp) {
            return bexp;
        }
        throw new RPTWTRRuntimeException("requires a boolean value", parent.get(key));
    }

    public static StringExpression isStringExpression(Operand operand)  {
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRRuntimeException("Requires a String value", operand);
    }
    
    public static StringExpression isStringExpression(ExpressionList parent, int index){
        Operand operand = parent.get(index);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRRuntimeException("Requires a String value", parent.get(index));
    }
    
    public static StringExpression isStringExpression(ExpressionMap parent, String key){
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringExpression sexp) {
            return sexp;
        }
        throw new RPTWTRRuntimeException("Requires a String value", parent.get(key));
    }
    
    public static String isStringLiteral(Operand operand) {
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new RPTWTRRuntimeException("Requires a String literal value", operand);
    }
    
    public static String isStringLiteral(ExpressionMap parent, String key) {
        Operand operand = parent.get(key);
        if (operand == null) return null; 
        if (operand instanceof StringLiteral slit) {
            return slit.toString();
        }
        throw new RPTWTRRuntimeException("requires a String literal value", parent.get(key));
    }
}
