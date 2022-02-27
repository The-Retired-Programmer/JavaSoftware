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

import java.io.IOException;
import uk.theretiredprogrammer.scmreportwriter.DataSource;
import uk.theretiredprogrammer.scmreportwriter.DataSources;
import uk.theretiredprogrammer.scmreportwriter.Fields;
import uk.theretiredprogrammer.scmreportwriter.expression.And;
import uk.theretiredprogrammer.scmreportwriter.expression.DataRecordField;
import uk.theretiredprogrammer.scmreportwriter.expression.Equals;
import uk.theretiredprogrammer.scmreportwriter.expression.EqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Expression;
import uk.theretiredprogrammer.scmreportwriter.expression.Literal;
import uk.theretiredprogrammer.scmreportwriter.expression.Not;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEquals;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Or;
import uk.theretiredprogrammer.scmreportwriter.expression.String2Boolean;

public class ReportDescriptor {

    private DataSources datasources;
    private Expression<Boolean> filter;
    private Fields fields;

//    public ReportDescriptor(DataSources datasources, Expression<Boolean> filter, Fields fields) {
//        this.datasources = datasources;
//        this.filter = filter;
//        this.fields = fields;
//    }

    public DataSource getDataSource(String key) {
        return datasources.get(key);
    }

    public Expression<Boolean> getFilter() {
        return filter;
    }

    public Fields getFields() {
        return fields;
    }
    
    //[] is a boolean expression {} is a string expression (<,,> is use in syntax to mean either boolean or string expression (operator dependent)
    // non terminal boolean expression is [operator<expression>..]
    // terminal boolean expressions are [TRUE] | [FALSE]
    //
    // non terminal string expression is {operator<expression>..}
    // terminal String expressions are {!fieldname} | {"literaltext}
    //
    
    String[] serialisedbooleanoperators = new String[] {
        "and","or",
        "equals","equalsignorecase","notequals","notequalsignorecase",
        "string2boolean", "not"};
    
    private enum ParameterType { S, SS, B, BB };
    ParameterType[] bptype = new ParameterType[] { ParameterType.BB, ParameterType.BB,
        ParameterType.SS, ParameterType.SS, ParameterType.SS, ParameterType.SS,
       ParameterType.S, ParameterType.B};
    
    String[] serialisedStringoperators = new String[] {
    "concatonate","boolean2string"};
    ParameterType[] sptype = new ParameterType[] {   
         ParameterType.SS, ParameterType.B};

    // SERIALISATION - is three lines of text
    //{filepathexpression} ....
    //[expression]
    //{{headingexpression},{valueexpression}} ...
    
    public String[] serialise() {
        
    }

    public void deserialise(String[] serialiseddata) throws IOException {
       datasources = extractdatasources(serialiseddata[0]);
       filter = extractbooleanexpression(serialiseddata[1]);
       fields = extractfields(serialiseddata[2]);
    }
    
    private DataSources extractdatasources(String serialised) {
        
    }
    
    
    private Fields extractfields(String serialised) {
        
    }
    
    private Expression<Boolean> extractbooleanexpression(String serialised) throws IOException {
        String slice = sliceexpressionbody(serialised, "[", "]");
        if (slice.equals("TRUE")) {
            return new Literal<>(true);
        }
        if (slice.equals("FALSE")) {
            return new Literal<>(false);
        }
        int endofoperator = findendofoperator(slice);
        String operator = slice.substring(0, endofoperator);
        slice = slice.substring(endofoperator);
        int operatorindex = lookupoperator(serialisedbooleanoperators, operator);
        ParameterType pt = bptype[operatorindex];
        switch (pt) {
            case SS -> {
                int endofexpression = findendofexpression(slice);
                String expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<String> left = extractstringexpression(expression);
                //
                endofexpression = findendofexpression(slice);
                expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<String> right = extractstringexpression(expression);
                //
                if (!slice.isEmpty()) {
                    throw new IOException("Malformed serialised data - excess data after required parameters" + slice);
                }
                return makebooleanoperatorSSexpression(operatorindex, left, right);
            }
            case BB -> {
                int endofexpression = findendofexpression(slice);
                String expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<Boolean> left = extractbooleanexpression(expression);
                //
                endofexpression = findendofexpression(slice);
                expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<Boolean> right = extractbooleanexpression(expression);
                //
                if (!slice.isEmpty()) {
                    throw new IOException("Malformed serialised data - excess data after required parameters" + slice);
                }
                return makebooleanoperatorBBexpression(operatorindex, left, right);
            }
            case S -> {
                int endofexpression = findendofexpression(slice);
                String expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<String> left = extractstringexpression(expression);
                if (!slice.isEmpty()) {
                    throw new IOException("Malformed serialised data - excess data after required parameters" + slice);
                }
                return makebooleanoperatorSexpression(operatorindex, left);
            }
            case B -> {
                int endofexpression = findendofexpression(slice);
                String expression = slice.substring(0, endofexpression);
                slice = slice.substring(endofoperator);
                Expression<Boolean> left = extractbooleanexpression(expression);
                //
                if (!slice.isEmpty()) {
                    throw new IOException("Malformed serialised data - excess data after required parameters" + slice);
                }
                return makebooleanoperatorBexpression(operatorindex, left);
            }
        }
        throw new IOException("Deserialisation Faiure - unexcepted state 1");
    }
    
    private Expression<Boolean> makebooleanoperatorSSexpression(int operatorindex, Expression<String> left, Expression<String> right) throws IOException {
        switch (operatorindex) {
            case 2 -> {//equals
                return new Equals(left, right);
            }
            case 3 -> {//equals
                return new EqualsIgnoreCase(left, right);
            }
            case 4 -> {//equals
                return new NotEquals(left, right);
            }
            case 5 -> {//equals
                return new NotEqualsIgnoreCase(left, right);
            }
            default -> {
                throw new IOException("Malformed internal data structures: operatorindex=" + operatorindex + "; parameterstyle is SS");
            }
        }
    }
    
    private Expression<Boolean> makebooleanoperatorBBexpression(int operatorindex, Expression<Boolean> left, Expression<Booolean> right) throws IOException {
        switch (operatorindex) {
            case 0 -> {//and
                return new And(left, right);
            }
            case 1 -> {//or
                return new Or(left, right);
            }
            default -> {
                throw new IOException("Malformed internal data structures: operatorindex=" + operatorindex + "; parameterstyle is BB");
            }
        }
    }
    
    private Expression<Boolean> makebooleanoperatorSexpression(int operatorindex, Expression<String> left) throws IOException {
        switch (operatorindex) {
            case 6 -> {//string2boolean
                return new String2Boolean(left);
            }
            default -> {
                throw new IOException("Malformed internal data structures: operatorindex=" + operatorindex + "; parameterstyle is S");
            }
        }
    }
    
    private Expression<Boolean> makebooleanoperatorBexpression(int operatorindex, Expression<Boolean> left) throws IOException {
        switch (operatorindex) {
            case 7 -> {//not
                return new Not(left);
            }
            default -> {
                throw new IOException("Malformed internal data structures: operatorindex=" + operatorindex + "; parameterstyle is S");
            }
        }
    }
    
    private Expression<String> extractstringexpression(String serialised) throws IOException {
                String slice = sliceexpressionbody(serialised, "{", "]}");
                if (slice.startsWith("!")){
                    return new DataRecordField(slice.substring(1));
                }
                if (slice.startsWith("\"")){
                    return new Literal<>(slice.substring(1));
                }
                //cut and paste code to finish this
    }
    
    // 4 builder methods here - cut and paste
    
    private String sliceexpressionbody(String serialised, String starts, String ends)  throws IOException {
        if (serialised.startsWith(starts) && serialised.endsWith(ends)) {
                return serialised.substring(1, serialised.length()-1);
        }
        throw new IOException("Malformed serialised data - expression type is incorrect: "+ serialised);
    }
    
    private int findendofoperator(String slice) throws IOException{
        for (int i = 0; i< slice.length(); i++) {
            char c = slice.charAt(i);
            if (c=='[' || c=='{' ){
                return i;
            }
        }
        throw new IOException("Malformed serialised data - operator is not followed by expression: "+ slice);
    }
    
    private int lookupoperator(String[] operators, String operator) throws IOException {
        for (int i = 0; i < operators.length; i++) {
            if (operator.equals(operators[i])) {
                return i;
            }
        }
        throw new IOException("Malformed serialised data - illegal Operator: "+ operator);
    }
    
    private int findendofexpression(String slice) throws IOException{
        if (slice.startsWith("{")) {
            return findendofexpression(slice,'}');
        }
        if (slice.startsWith("[")) {
            return findendofexpression(slice,']');
        }
        throw new IOException("Malformed serialised data - expected expression open brackets: "+ slice);
    }
    
    private int findendofexpression(String slice, char endchar) throws IOException{
        for (int i = 1; i < slice.length(); i++) {
            if (slice.charAt(i) == endchar) {
                return i;
            }
        }
        throw new IOException("Malformed serialised data - closing expression bracket missing: "+slice);
    }
    
}
