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

import uk.theretiredprogrammer.scmreportwriter.SCM_Language;
import uk.theretiredprogrammer.scmreportwriter.language.S_Token;
import uk.theretiredprogrammer.scmreportwriter.language.LexerException;
import uk.theretiredprogrammer.scmreportwriter.language.Lexer;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.scmreportwriter.language.Language;

public class LexerTest {
    
    private final Language language;

    public LexerTest() {
        language = new SCM_Language();
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testLex1() throws Exception {
        System.out.println("lex - empty");
        lextest("");
    }

    @Test
    public void testLex2() throws Exception {
        System.out.println("lex - text");
        lextest("   ABCDEF  ",
                "ABCDEF"
        );
    }

    @Test
    public void testLex3() throws Exception {
        System.out.println("lex - delimitedtext");
        lextest("   {ABCDEF}  ",
                "ABCDEF"
        );
    }

    @Test
    public void testLex4() throws Exception {
        System.out.println("lex - delimitedtext2");
        lextest("   {A B C D.E.F..}  ",
                "A B C D.E.F.."
        );
    }

    @Test
    public void testLex5() throws Exception {
        System.out.println("lex - boolean true");
        lextest("   TRUE  ",
                "true"
        );
    }

    @Test
    public void testLex6() throws Exception {
        System.out.println("lex - boolean false");
        lextest("   {FALSE}  ",
                "false"
        );
    }

    @Test
    public void testLex7() throws Exception {
        System.out.println("lex - operators");
        lextest("{boolean} {string} ! + && || == =~ != !=~ ( ) $ , ",
                "Boolean cast",
                "String cast",
                "!",
                "+",
                "&&",
                "||",
                "==",
                "=~",
                "!=",
                "!=~",
                "(",
                ")",
                "$",
                ","
        );
    }

    @Test
    public void testLex8() throws Exception {
        System.out.println("lex - operators packed");
        lextest("{boolean}{string}!+&&||===~!=!=~()$,",
                "Boolean cast",
                "String cast",
                "!",
                "+",
                "&&",
                "||",
                "==",
                "=~",
                "!=",
                "!=~",
                "(",
                ")",
                "$",
                ","
        );
    }

    @Test
    public void testLex9() throws Exception {
        System.out.println("lex - operators and literals");
        lextest("{boolean} s {string} b ! b s + s b && b b || b s == s s =~ s s != s s !=~ s (s) $a x , x  ",
                "Boolean cast",
                "s",
                "String cast",
                "b",
                "!",
                "b",
                "s",
                "+",
                "s",
                "b",
                "&&",
                "b",
                "b",
                "||",
                "b",
                "s",
                "==",
                "s",
                "s",
                "=~",
                "s",
                "s",
                "!=",
                "s",
                "s",
                "!=~",
                "s",
                "(",
                "s",
                ")",
                "$",
                "a",
                "x",
                ",",
                "x"
        );
    }

    @Test
    public void testLex10() throws Exception {
        System.out.println("lex - operators and literals packed");
        lextest("{boolean}s {string}b!b s+s b&&b b||b s==s s=~s s!=s s!=~s(s)$a x,x",
                "Boolean cast",
                "s",
                "String cast",
                "b",
                "!",
                "b",
                "s",
                "+",
                "s",
                "b",
                "&&",
                "b",
                "b",
                "||",
                "b",
                "s",
                "==",
                "s",
                "s",
                "=~",
                "s",
                "s",
                "!=",
                "s",
                "s",
                "!=~",
                "s",
                "(",
                "s",
                ")",
                "$",
                "a",
                "x",
                ",",
                "x"
        );
    }

    @Test
    public void testLex11() throws Exception {
        System.out.println("lex - data cmd");
        lextest("   DATA  ",
                "DATA"
        );
    }

    @Test
    public void testLex12() throws Exception {
        System.out.println("lex - filter cmd");
        lextest("   FILTER  ",
                "FILTER"
        );
    }

    @Test
    public void testLex13() throws Exception {
        System.out.println("lex - fields cmd");
        lextest("   FIELDS  ",
                "FIELDS"
        );
    }
    
    @Test
    public void testLex14() throws Exception {
        System.out.println("lex - FILTER and datarecordfield");
        lextest("FILTER ${abc}",
                "FILTER",
                "$",
                "abc"
        );
    }
    
     @Test
    public void testLex15() throws Exception {
        System.out.println("lex - FILTER and property");
        lextest("FILTER abc:def+hij",
                "FILTER",
                "abc",
                ":",
                "def",
                "+",
                "hij"
        );
    }
    
    private void lextest(String input, String... results) throws LexerException {
        Lexer instance = new Lexer(language);
        List<S_Token> lexresult = instance.lex(input.lines());
        assertEquals(results.length, lexresult.size());
        for (int i = 0; i < results.length; i++) {
            assertEquals(results[i], lexresult.get(i).toString());
        }
    }
}
