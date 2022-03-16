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

public class Lexer {

    private final LanguageSource source;
    private final Language language;

    public Lexer(LanguageSource source, Language language) {
        this.source = source;
        this.language = language;
    }
    
    private enum LexState {
        INWHITESPACE, INTEXT, INDELIMITEDTEXT, INOPERATOR
    };

    public void lex() throws LexerException {
        try {
            StringBuilder tokenbuilder = new StringBuilder();
            LexState lstate = LexState.INWHITESPACE;
            while (true) {
                char c = source.getchar();
                switch (lstate) {
                    case INWHITESPACE -> {
                        switch (language.charType(c)) {
                            case WHITESPACE -> {
                                // do nothing
                            }
                            case TEXT -> {
                                source.mark();
                                lstate = LexState.INTEXT;
                                tokenbuilder = new StringBuilder();
                                tokenbuilder.append(c);
                            }
                            case SYMBOL -> {
                                source.mark();
                                lstate = LexState.INOPERATOR;
                                tokenbuilder = new StringBuilder();
                                tokenbuilder.append(c);
                            }
                            case TEXTDELIMITER -> {
                                source.mark();
                                tokenbuilder = new StringBuilder();
                                lstate = LexState.INDELIMITEDTEXT;
                            }
                            case EOF -> {
                                return;
                            }
                        }
                    }
                    case INTEXT -> {
                        switch (language.charType(c)) {
                            case WHITESPACE -> {
                                source.add(language.getToken(tokenbuilder.toString()));
                                lstate = LexState.INWHITESPACE;
                            }
                            case TEXT -> {
                                tokenbuilder.append(c);
                            }
                            case SYMBOL -> {
                                source.add(language.getToken(tokenbuilder.toString()));
                                tokenbuilder = new StringBuilder();
                                tokenbuilder.append(c);
                                lstate = LexState.INOPERATOR;
                            }
                            case TEXTDELIMITER -> {
                                source.add(language.getToken(tokenbuilder.toString()));
                                source.mark();
                                tokenbuilder = new StringBuilder();
                                lstate = LexState.INDELIMITEDTEXT;
                            }
                            case EOF -> {
                                source.add(language.getToken(tokenbuilder.toString()));
                                return;
                            }
                        }
                    }
                    case INDELIMITEDTEXT -> {
                        switch (language.charType(c)) {
                            case WHITESPACE -> {
                                tokenbuilder.append(c);
                            }
                            case TEXT -> {
                                tokenbuilder.append(c);
                            }
                            case SYMBOL -> {
                                tokenbuilder.append(c);
                            }
                            case TEXTDELIMITER -> {
                                source.add(language.getToken(tokenbuilder.toString()));
                                lstate = LexState.INWHITESPACE;
                            }
                            case EOF -> {
                                throw source.newInternalLexerException("Unexpected EOF when in delimited text");
                            }
                        }
                    }
                    case INOPERATOR -> {
                        switch (language.charType(c)) {
                            case WHITESPACE -> {
                                processSymbols(tokenbuilder.toString());
                                lstate = LexState.INWHITESPACE;
                            }
                            case TEXT -> {
                                processSymbols(tokenbuilder.toString());
                                lstate = LexState.INTEXT;
                                tokenbuilder = new StringBuilder();
                                tokenbuilder.append(c);
                            }
                            case SYMBOL -> {
                                tokenbuilder.append(c);
                            }
                            case TEXTDELIMITER -> {
                                processSymbols(tokenbuilder.toString());
                                lstate = LexState.INDELIMITEDTEXT;
                                tokenbuilder = new StringBuilder();
                            }
                            case EOF -> {
                                processSymbols(tokenbuilder.toString());
                                return;
                            }
                        }
                    }
                }
            }
        } catch (InternalLexerException ex) {
            throw source.newLexerException(ex);
        }
    }
    
    // handles concatonated operators (symbols) - matches longest first (operator table must be correctly ordered)
    private void processSymbols(String symbolstring) throws InternalLexerException {
        if (symbolstring.isEmpty()) {
            return;
        }
        String remainingsymbols = language.extractOperator(symbolstring);
        if (remainingsymbols == null) {
            throw source.newInternalLexerException("Illegal Operator: " + symbolstring);
        }
        source.add(language.getExtractedOperator());
        processSymbols(remainingsymbols);
    }
}
