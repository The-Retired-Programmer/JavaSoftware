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

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.scmreportwriter.reportdescriptor.S_Token.Tokentype;

public class LexerTest {
    
    public LexerTest() {
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
        lextest("",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex2() throws Exception {
        System.out.println("lex - text");
        lextest("   ABCDEF  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING, "ABCDEF"),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex3() throws Exception {
        System.out.println("lex - delimitedtext");
        lextest("   {ABCDEF}  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING, "ABCDEF"),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex4() throws Exception {
        System.out.println("lex - delimitedtext2");
        lextest("   {A B C D.E.F..}  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING, "A B C D.E.F.."),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex5() throws Exception {
        System.out.println("lex - boolean true");
        lextest("   TRUE  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.BOOLEAN, true),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex6() throws Exception {
        System.out.println("lex - boolean false");
        lextest("   {FALSE}  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.BOOLEAN, false),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex7() throws Exception {
        System.out.println("lex - operators");
        lextest("{boolean} {string} ¬ + && || == =~ != !~ ( ) $ , ;",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING2BOOLEAN),
            new S_Token(Tokentype.BOOLEAN2STRING),
            new S_Token(Tokentype.NOT),
            new S_Token(Tokentype.CONCATONATE),
            new S_Token(Tokentype.AND),
            new S_Token(Tokentype.OR),
            new S_Token(Tokentype.EQUALS),
            new S_Token(Tokentype.EQUALSIGNORECASE),
            new S_Token(Tokentype.NOTEQUALS),
            new S_Token(Tokentype.NOTEQUALSIGNORECASE),
            new S_Token(Tokentype.BRA),
            new S_Token(Tokentype.KET),
            new S_Token(Tokentype.FIELDOP),
            new S_Token(Tokentype.LISTSEPARATOR),
            new S_Token(Tokentype.LISTTERMINATOR),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex8() throws Exception {
        System.out.println("lex - operators packed");
        lextest("{boolean}{string}¬+&&||===~!=!~()$,;",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING2BOOLEAN),
            new S_Token(Tokentype.BOOLEAN2STRING),
            new S_Token(Tokentype.NOT),
            new S_Token(Tokentype.CONCATONATE),
            new S_Token(Tokentype.AND),
            new S_Token(Tokentype.OR),
            new S_Token(Tokentype.EQUALS),
            new S_Token(Tokentype.EQUALSIGNORECASE),
            new S_Token(Tokentype.NOTEQUALS),
            new S_Token(Tokentype.NOTEQUALSIGNORECASE),
            new S_Token(Tokentype.BRA),
            new S_Token(Tokentype.KET),
            new S_Token(Tokentype.FIELDOP),
            new S_Token(Tokentype.LISTSEPARATOR),
            new S_Token(Tokentype.LISTTERMINATOR),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex9() throws Exception {
        System.out.println("lex - operators and literals");
        lextest("{boolean} s {string} b ¬ b s + s b && b b || b s == s s =~ s s != s s !~ s (s) $a x , x ; ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING2BOOLEAN),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.BOOLEAN2STRING),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.NOT),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.CONCATONATE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.AND),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.OR),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.EQUALS),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.EQUALSIGNORECASE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.NOTEQUALS),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.NOTEQUALSIGNORECASE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.BRA),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.KET),
            new S_Token(Tokentype.FIELDOP),
            new S_Token(Tokentype.STRING, "a"),
            new S_Token(Tokentype.STRING, "x"),
            new S_Token(Tokentype.LISTSEPARATOR),
            new S_Token(Tokentype.STRING, "x"),
            new S_Token(Tokentype.LISTTERMINATOR),
            new S_Token(Tokentype.END)
        );
    }
    
     @Test
    public void testLex10() throws Exception {
        System.out.println("lex - operators and literals packed");
        lextest("{boolean}s {string}b¬b s+s b&&b b||b s==s s=~s s!=s s!~s(s)$a x,x;",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.STRING2BOOLEAN),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.BOOLEAN2STRING),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.NOT),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.CONCATONATE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.AND),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.OR),
            new S_Token(Tokentype.STRING, "b"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.EQUALS),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.EQUALSIGNORECASE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.NOTEQUALS),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.NOTEQUALSIGNORECASE),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.BRA),
            new S_Token(Tokentype.STRING, "s"),
            new S_Token(Tokentype.KET),
            new S_Token(Tokentype.FIELDOP),
            new S_Token(Tokentype.STRING, "a"),
            new S_Token(Tokentype.STRING, "x"),
            new S_Token(Tokentype.LISTSEPARATOR),
            new S_Token(Tokentype.STRING, "x"),
            new S_Token(Tokentype.LISTTERMINATOR),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex11() throws Exception {
        System.out.println("lex - data cmd");
        lextest("   DATA  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.DATACMD),
            new S_Token(Tokentype.END)
        );
    }
    
    @Test
    public void testLex12() throws Exception {
        System.out.println("lex - filter cmd");
        lextest("   FILTER  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.FILTERCMD),
            new S_Token(Tokentype.END)
        );
    }
    
     @Test
    public void testLex13() throws Exception {
        System.out.println("lex - fields cmd");
        lextest("   FIELDS  ",
            new S_Token(Tokentype.START),
            new S_Token(Tokentype.FIELDSCMD),
            new S_Token(Tokentype.END)
        );
    }
    
    private void lextest(String input, S_Token... results) throws LexerException {
        Lexer instance = new Lexer();
        List<S_Token> lexresult = instance.lex(input.lines());
        assertEquals(results.length,lexresult.size());
        for (int i = 0; i< results.length; i++) {
            assertEquals(results[i],lexresult.get(i));
        }
    }
}
