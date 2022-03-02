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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import uk.theretiredprogrammer.scmreportwriter.reportdescriptor.S_Token.Tokentype;

public class Lexer {

    public List<S_Token> lex(Stream<String> lines) throws LexerException {
        return lex(flatten(lines.toArray(String[]::new)));
    }

    private char[] flatten(String[] lines) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", lines));
        sb.append('\n');
        char[] result = sb.toString().toCharArray();
        return result;
    }

    private enum LexState {
        INWHITESPACE, INTEXT, INDELIMITEDTEXT, INOPERATOR
    };

    private enum CharType {
        WHITESPACE, TEXT, SYMBOL, TEXTSTARTDELIMITER, TEXTENDDELIMITER
    };

    private enum OperatorMatchState {
        NOMATCH, MATCH, STARTOFMATCH
    };

    private CharType charType(char c) {
        if (Character.isLetter(c) || Character.isDigit(c)) {
            return CharType.TEXT;
        }
        if (c == '{') {
            return CharType.TEXTSTARTDELIMITER;
        }
        if (c == '}') {
            return CharType.TEXTENDDELIMITER;
        }
        if (c == '\n') {
            return CharType.SYMBOL;
        }
        if (Character.isWhitespace(c)) {
            return CharType.WHITESPACE;
        }
        return CharType.SYMBOL;
    }

    private List<S_Token> lex(char[] source) throws LexerException {
        List<S_Token> result = new ArrayList<>();
        result.add(new S_Token(Tokentype.START));
        StringBuilder tokenbuilder = new StringBuilder();
        LexState lstate = LexState.INWHITESPACE;
        for (char c : source) {
            switch (lstate) {
                case INWHITESPACE -> {
                    switch (charType(c)) {
                        case WHITESPACE -> {
                            // do nothing
                        }
                        case TEXT -> {
                            lstate = LexState.INTEXT;
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                            String op = tokenbuilder.toString();
                            switch (isOperator(op)) {
                                case MATCH -> {
                                    lstate = LexState.INWHITESPACE;
                                    result.add(new S_Token(getOperator(op)));
                                }
                                case STARTOFMATCH -> {
                                    lstate = LexState.INOPERATOR;
                                }
                                case NOMATCH -> {
                                    throw new LexerException("Bad Operator: " + op);
                                }

                            }
                        }
                        case TEXTSTARTDELIMITER -> {
                            tokenbuilder = new StringBuilder();
                            lstate = LexState.INDELIMITEDTEXT;
                        }
                        case TEXTENDDELIMITER -> {
                            throw new LexerException("Unexpected } symbol");
                        }

                    }
                }
                case INTEXT -> {
                    switch (charType(c)) {
                        case WHITESPACE -> {
                            result.add(getLiteralS_Token(tokenbuilder.toString()));
                            lstate = LexState.INWHITESPACE;
                        }
                        case TEXT -> {
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            result.add(getLiteralS_Token(tokenbuilder.toString()));
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                            String op = tokenbuilder.toString();
                            switch (isOperator(op)) {
                                case MATCH -> {
                                    lstate = LexState.INWHITESPACE;
                                    result.add(new S_Token(getOperator(op)));
                                }
                                case STARTOFMATCH -> {
                                    lstate = LexState.INOPERATOR;
                                }
                                case NOMATCH -> {
                                    throw new LexerException("Bad Operator: " + op);
                                }

                            }
                        }
                        case TEXTSTARTDELIMITER -> {
                            throw new LexerException("Unexpected { symbol");
                        }
                        case TEXTENDDELIMITER -> {
                            throw new LexerException("Unexpected } symbol");
                        }

                    }
                }
                case INDELIMITEDTEXT -> {
                    switch (charType(c)) {
                        case WHITESPACE -> {
                            tokenbuilder.append(c);
                        }
                        case TEXT -> {
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            tokenbuilder.append(c);
                        }
                        case TEXTSTARTDELIMITER -> {
                            throw new LexerException("Unexpected { symbol");
                        }
                        case TEXTENDDELIMITER -> {
                            result.add(getLiteralS_Token(tokenbuilder.toString()));
                            lstate = LexState.INWHITESPACE;
                        }
                    }
                }
                case INOPERATOR -> {
                    switch (charType(c)) {
                        case WHITESPACE -> {
                            throw new LexerException("Bad Operator - partial match terminated early: " + tokenbuilder.toString());
                        }
                        case TEXT -> {
                            throw new LexerException("Bad Operator - partial match terminated early: " + tokenbuilder.toString());
                        }
                        case SYMBOL -> {
                            tokenbuilder.append(c);
                            String op = tokenbuilder.toString();
                            switch (isOperator(op)) {
                                case MATCH -> {
                                    lstate = LexState.INWHITESPACE;
                                    result.add(new S_Token(getOperator(op)));
                                }
                                case STARTOFMATCH -> {
                                }
                                case NOMATCH -> {
                                    throw new LexerException("Bad Operator: " + op);
                                }

                            }
                        }
                        case TEXTSTARTDELIMITER -> {
                            throw new LexerException("Bad Operator - partial match terminated early by {");
                        }
                        case TEXTENDDELIMITER -> {
                            throw new LexerException("Bad Operator - partial match terminated early by {");
                        }

                    }
                }

            }
        }
        return result;
    }

    private S_Token getLiteralS_Token(String literal) {
        if (literal.equals("boolean")) {
            return new S_Token(Tokentype.STRING2BOOLEAN);
        }
        if (literal.equals("string")) {
            return new S_Token(Tokentype.BOOLEAN2STRING);
        }
        if (literal.equals("TRUE")) {
            return new S_Token<>(Tokentype.BOOLEAN, true);
        }
        if (literal.equals("FALSE")) {
            return new S_Token<>(Tokentype.BOOLEAN, false);
        }
        if (literal.equals("DATA")) {
            return new S_Token(Tokentype.DATACMD);
        }
        if (literal.equals("FILTER")) {
            return new S_Token(Tokentype.FILTERCMD);
        }
        if (literal.equals("FIELDS")) {
            return new S_Token(Tokentype.FIELDSCMD);
        }
        return new S_Token<>(Tokentype.STRING, literal);
    }

    private OperatorMatchState isOperator(String token) {
        for (String operator : alloperators) {
            if (token.equals(operator)) {
                return OperatorMatchState.MATCH;
            }
            if (operator.startsWith(token)) {
                return OperatorMatchState.STARTOFMATCH;
            }
        }
        return OperatorMatchState.NOMATCH;
    }

    private Tokentype getOperator(String token) throws LexerException {
        for (int i = 0; i < alloperators.length; i++) {
            if (alloperators[i].equals(token)) {
                return Tokentype.values()[i];
            }
        }
        throw new LexerException("Lexer Internal Failure: can't getOperator(" + token + ")");
    }

    private final String[] alloperators = new String[]{
        "¬", "+", "&&", "||",
        "==", "=~", "!=", "!~",
        "(", ")", "$",
        ",", ";", "\n"
    };
}
