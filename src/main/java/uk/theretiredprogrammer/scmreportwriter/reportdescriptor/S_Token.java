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

import java.util.Objects;

public class S_Token<T> {

    public static enum Tokentype {
        NOT, CONCATONATE, AND, OR,
        EQUALS, EQUALSIGNORECASE, NOTEQUALS, NOTEQUALSIGNORECASE,
        BRA, KET, FIELDOP,
        LISTSEPARATOR, LISTTERMINATOR, END, START,
        //
        STRING2BOOLEAN, BOOLEAN2STRING, DATACMD, FILTERCMD, FIELDSCMD,
        
        BOOLEAN, STRING
    }
    
    public static boolean isOperatorToken(S_Token token) {
        return token.getOperator().ordinal() < OPCOUNT;
    }
    
    private static final int OPCOUNT = 20;


    private final T literal;
    private final Tokentype operator;

    public S_Token(Tokentype operator) {
        this(operator, null);
    }

    public S_Token(Tokentype literaltype, T literalvalue) {
        this.operator = literaltype;
        this.literal = literalvalue;
    }

    public Tokentype getOperator() {
        return operator;
    }

    public T getOperand() {
        return literal;
    }

    public char getOperandType() throws ParserException {
        if (literal instanceof String) {
            return 's';
        }
        if (literal instanceof Boolean) {
            return 'b';
        }
        throw new ParserException("Unknown data type for literal data");
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.literal);
        hash = 29 * hash + Objects.hashCode(this.operator);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final S_Token<?> other = (S_Token<?>) obj;
        if (!Objects.equals(this.literal, other.literal)) {
            return false;
        }
        return this.operator == other.operator;
    }

}
