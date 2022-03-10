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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lexer {

    private final Language language;
    
    public Lexer(Language language) {
        this.language = language;
    }

    public List<S_Token> lex(Stream<String> lines) throws LexerException {
        return lex(lines.collect(Collectors.joining("\n","","\n")).toCharArray());
    }
   
    private enum LexState {
        INWHITESPACE, INTEXT, INDELIMITEDTEXT, INOPERATOR
    };
    
//    private LexState[][] nextState = new LexState[][] { // [state],[chartype]
//        { // inwhitespace
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INTEXT, LexState.INOPERATOR, LexState.INDELIMITEDTEXT, //fail //
//        },
//        { // intext
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INTEXT, LexState.INOPERATOR, //fail , //fail //
//        },
//        { // indelimitedtext
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INDELIMITEDTEXT, LexState.INDELIMITEDTEXT, //fail// , LexState.INWHITESPACE
//        },
//        { // inoperator
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INTEXT, LexState.INOPERATOR, LexState.INDELIMITEDTEXT , // fail
//        }
//    };
//    
//    private Consumer<Character>[][] onCharaction = new Consumer<Character>[][] { // [state],[chartype]
//        { // inwhitespace
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INTEXT, LexState.INOPERATOR, LexState.INDELIMITEDTEXT, //fail //
//        },
//        { // intext
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INTEXT, LexState.INOPERATOR, //fail , //fail //
//        },
//        { // indelimitedtext
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            LexState.INWHITESPACE, LexState.INDELIMITEDTEXT, LexState.INDELIMITEDTEXT, //fail// , LexState.INWHITESPACE
//        },
//        { // inoperator
//            // whitespace, text, symbol, textstartdelimiter, textenddlimiter
//            this:processSymbols(result, tokenbuilder.toString()), LexState.INTEXT, this::insertintotoken, LexState.INDELIMITEDTEXT , // fail
//        }
//    };

    private List<S_Token> lex(char[] source) throws LexerException {
        List<S_Token> result = new ArrayList<>();
        StringBuilder tokenbuilder = new StringBuilder();
        LexState lstate = LexState.INWHITESPACE;
        for (char c : source) {
            switch (lstate) {
                case INWHITESPACE -> {
                    switch (Language.charType(c)) {
                        case WHITESPACE -> {
                            // do nothing
                        }
                        case TEXT -> {
                            lstate = LexState.INTEXT;
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            lstate = LexState.INOPERATOR;
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
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
                    switch (Language.charType(c)) {
                        case WHITESPACE -> {
                            result.add(language.getToken(tokenbuilder.toString()));
                            lstate = LexState.INWHITESPACE;
                        }
                        case TEXT -> {
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            result.add(language.getToken(tokenbuilder.toString()));
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                            lstate = LexState.INOPERATOR;
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
                    switch (Language.charType(c)) {
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
                            result.add(language.getToken(tokenbuilder.toString()));
                            lstate = LexState.INWHITESPACE;
                        }
                    }
                }
                case INOPERATOR -> {
                    switch (Language.charType(c)) {
                        case WHITESPACE -> {
                            processSymbols(result, tokenbuilder.toString());
                            lstate = LexState.INWHITESPACE;
                        }
                        case TEXT -> {
                            processSymbols(result, tokenbuilder.toString());
                            lstate = LexState.INTEXT;
                            tokenbuilder = new StringBuilder();
                            tokenbuilder.append(c);
                        }
                        case SYMBOL -> {
                            tokenbuilder.append(c);
                        }
                        case TEXTSTARTDELIMITER -> {
                            processSymbols(result, tokenbuilder.toString());
                            lstate = LexState.INDELIMITEDTEXT;
                            tokenbuilder = new StringBuilder();
                        }
                        case TEXTENDDELIMITER -> {
                            processSymbols(result, tokenbuilder.toString());
                            throw new LexerException("Unexpected } symbol");
                        }

                    }
                }

            }
        }
        return result;
    }
    
//    private void insertintotoken(char c) {
//        
//    }
//        
//    private void processoperators(char c){
//        
//    }
    
    // handles concatonated operators (symbols) - matches longest first (operator table must be correctly ordered)
    private void processSymbols(List<S_Token> result, String symbolstring) throws LexerException{
        while (!symbolstring.isEmpty()) {
            symbolstring = language.extractOperator(symbolstring);
            result.add(language.getExtractedOperator());
        }
    }
}
