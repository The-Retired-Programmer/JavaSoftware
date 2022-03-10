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
import uk.theretiredprogrammer.scmreportwriter.language.Parser;
import uk.theretiredprogrammer.scmreportwriter.language.Lexer;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.Property;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;

public class ParserTest {

    private final Language language;
    
    public ParserTest() {
        language = new SCM_Language();
    }
    
    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    private List<S_Token> buildS_Tokens(String input) throws LexerException {
        Lexer instance = new Lexer(language);
        return instance.lex(input.lines());
    }

    private void commonTestString(String input, String expected) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            Operand operand = map.get("FILTER");
            DataSourceRecord datarecord = new DataSourceRecord();
            if (operand instanceof StringExpression stringexp) {
                assertEquals(expected, stringexp.evaluate(datarecord));
                return;
            }
        }
        fail("result expression is not FilterCommand>stringexpression");
    }

    private void commonTestProperty(String input, String expected) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            Operand operand = map.get("FILTER");
            DataSourceRecord datarecord = new DataSourceRecord();
            if (operand instanceof Property propertyexp) {
                assertEquals(expected, propertyexp.evaluate(datarecord));
                return;
            }
        }
        fail("result expression is not FilterCommand>property");
    }

    private void commonTestStringMultiple(String input, String... expected) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            Operand operand = map.get("FILTER");
            if (operand instanceof ExpressionList expressions) {
                DataSourceRecord datarecord = new DataSourceRecord();
                assertEquals(expected.length, expressions.size());
                for (int i = 0; i < expected.length; i++) {
                    if (expressions.get(i) instanceof StringExpression stringexp) {
                        assertEquals(expected[i], stringexp.evaluate(datarecord));
                    } else {
                        fail("result expression " + i + " is not stringexpression");
                    }
                }
                return;
            }
        }
        fail("result expression is not FilterCommand>expressionlist");
    }

    private void commonTestCommandMultipleStringMultiple(String input, String[] commands, String[]... expected) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            assertEquals(expected.length, map.size());
            assertEquals(expected.length, commands.length);
            for (int j = 0; j < commands.length; j++) {
                Operand operand = map.get(commands[j]);
                if (operand instanceof ExpressionList expressions) {
                    assertEquals(expected[j].length, expressions.size());
                    DataSourceRecord datarecord = new DataSourceRecord();
                    for (int i = 0; i < expected.length; i++) {
                        if (expressions.get(i) instanceof StringExpression stringexp) {
                            assertEquals(expected[j][i], stringexp.evaluate(datarecord));
                        } else {
                            fail("result command " + commands[j] + " expression " + i + " is not stringexpression");
                        }
                    }
                }
            }
            return;
        }
        fail("result expression is not Command>expressionlist ,,,");
    }

    private void commonTestStringWithData(String input, String expected, String datakey, String datavalue) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            Operand operand = map.get("FILTER");
            DataSourceRecord datarecord = new DataSourceRecord();
            datarecord.put(datakey, datavalue);
            if (operand instanceof StringExpression stringexp) {
                assertEquals(expected, stringexp.evaluate(datarecord));
                return;
            }
        }
        fail("result expression is not FilterCommand>stringexpression");
    }

    private void commonTestBoolean(String input, boolean expected) throws Exception {
        var instance = new Parser(language);
        Operand presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof ExpressionMap map) {
            Operand operand = map.get("FILTER");
            DataSourceRecord datarecord = new DataSourceRecord();
            if (operand instanceof BooleanExpression boolexp) {
                assertEquals(expected, boolexp.evaluate(datarecord));
                return;
            }
        }
        fail("result expression is not FilterCommand>booleanexpression");
    }

    @Test
    public void testParse1() throws Exception {
        System.out.println("TEST1 - parse - concatonate");
        commonTestString("FILTER abc+xyz", "abcxyz");
    }

    @Test
    public void testParse2() throws Exception {
        System.out.println("TEST2 - parse - equals - true");
        commonTestBoolean("FILTER abc==abc", true);
    }

    @Test
    public void testParse3() throws Exception {
        System.out.println("TEST3 - parse - equals - false");
        commonTestBoolean("FILTER abc==xyz", false);
    }

    @Test
    public void testParse4() throws Exception {
        System.out.println("TEST4 - parse - equals - true with rhs concatonate");
        commonTestBoolean("FILTER abc==a+bc", true);
    }

    @Test
    public void testParse5() throws Exception {
        System.out.println("TEST5 - parse - equals - true with lhs concatonate");
        commonTestBoolean("FILTER a+bc==abc", true);
    }

    @Test
    public void testParse6() throws Exception {
        System.out.println("TEST6 - parse - equals - true with both side concatonates");
        commonTestBoolean("FILTER a+bc==ab+c", true);
    }

    @Test
    public void testParse7() throws Exception {
        System.out.println("TEST7 - parse - true And false");
        commonTestBoolean("FILTER TRUE&&FALSE", false);
    }

    @Test
    public void testParse8() throws Exception {
        System.out.println("TEST8 - parse - true And true");
        commonTestBoolean("FILTER TRUE&&TRUE", true);
    }

    @Test
    public void testParse9() throws Exception {
        System.out.println("TEST9 - parse - false And true");
        commonTestBoolean("FILTER FALSE&&TRUE", false);
    }

    @Test
    public void testParse10() throws Exception {
        System.out.println("TEST10 - parse - false And false");
        commonTestBoolean("FILTER FALSE&&FALSE", false);
    }

    @Test
    public void testParse11() throws Exception {
        System.out.println("TEST11 - parse - true Or false");
        commonTestBoolean("FILTER TRUE||FALSE", true);
    }

    @Test
    public void testParse12() throws Exception {
        System.out.println("TEST12 - parse - true Or true");
        commonTestBoolean("FILTER TRUE||TRUE", true);
    }

    @Test
    public void testParse13() throws Exception {
        System.out.println("TEST13 - parse - false Or true");
        commonTestBoolean("FILTER FALSE||TRUE", true);
    }

    @Test
    public void testParse14() throws Exception {
        System.out.println("TEST14 - parse - false Or false");
        commonTestBoolean("FILTER FALSE||FALSE", false);
    }

    @Test
    public void testParse15() throws Exception {
        System.out.println("TEST15 - parse - Not true");
        commonTestBoolean("FILTER !TRUE", false);
    }

    @Test
    public void testParse16() throws Exception {
        System.out.println("TEST16 - parse - Not false");
        commonTestBoolean("FILTER !FALSE", true);
    }

    @Test
    public void testParse17() throws Exception {
        System.out.println("TEST17 - parse - brackets");
        commonTestString("FILTER (abc)", "abc");
    }

    @Test
    public void testParse18() throws Exception {
        System.out.println("TEST18 - parse - brackets with concatonate");
        commonTestString("FILTER (abc+xyz)", "abcxyz");
    }

    @Test
    public void testParse19() throws Exception {
        System.out.println("TEST19 - parse - notequals false");
        commonTestBoolean("FILTER abc!=abc", false);
    }

    @Test
    public void testParse20() throws Exception {
        System.out.println("TEST20 - parse - notequals true");
        commonTestBoolean("FILTER abc!=xyz", true);
    }

    @Test
    public void testParse21() throws Exception {
        System.out.println("TEST21 - parse - equalsignorecase true ");
        commonTestBoolean("FILTER abc=~abc", true);
    }

    @Test
    public void testParse22() throws Exception {
        System.out.println("TEST22 - parse - equalsignorecase true ");
        commonTestBoolean("FILTER abc=~ABC", true);
    }

    @Test
    public void testParse23() throws Exception {
        System.out.println("TEST23 - parse - equalsignorecase false ");
        commonTestBoolean("FILTER abc=~XYZ", false);
    }

    @Test
    public void testParse24() throws Exception {
        System.out.println("TEST24 - parse - notequalsignorecase false ");
        commonTestBoolean("FILTER abc!=~abc", false);
    }

    @Test
    public void testParse25() throws Exception {
        System.out.println("TEST25 - parse - notequalsignorecase false ");
        commonTestBoolean("FILTER abc!=~ABC", false);
    }

    @Test
    public void testParse26() throws Exception {
        System.out.println("TEST26 - parse - notequalsignorecase true ");
        commonTestBoolean("FILTER abc!=~XYZ", true);
    }

    @Test
    public void testParse27() throws Exception {
        System.out.println("TEST27 - parse - field op ");
        commonTestStringWithData("FILTER ${abc}", "xyz", "abc", "xyz");
    }

    @Test
    public void testParse28() throws Exception {
        System.out.println("TEST28 - parse - boolean cast true");
        commonTestBoolean("FILTER boolean Yes", true);
    }

    @Test
    public void testParse29() throws Exception {
        System.out.println("TEST29 - parse - boolean cast false");
        commonTestBoolean("FILTER boolean No", false);
    }

    @Test
    public void testParse30() throws Exception {
        System.out.println("TEST30 - parse - boolean cast true");
        commonTestBoolean("FILTER boolean true", true);
    }

    @Test
    public void testParse31() throws Exception {
        System.out.println("TEST31 - parse - string cast true");
        commonTestString("FILTER string TRUE", "Yes");
    }

    @Test
    public void testParse32() throws Exception {
        System.out.println("TEST32 - parse - string cast false");
        commonTestString("FILTER string FALSE", "No");
    }

    @Test
    public void testParse33() throws Exception {
        System.out.println("TEST33 - parse - list separator, list terminator");
        commonTestStringMultiple("FILTER (abc,xyz)", "abc", "xyz");
    }

    @Test
    public void testParse34() throws Exception {
        System.out.println("TEST34 - parse - complex expression 1");
        commonTestBoolean("FILTER string!(ab+cd == abcd)+NE=~NONE", true);
    }

    @Test
    public void testParse35() throws Exception {
        System.out.println("TEST 35 - parse - complex expression 2");
        commonTestBoolean("FILTER TRUE && FALSE||FALSE||ab==cd||boolean NO|| !FALSE", true);
    }

    @Test
    public void testParse36() throws Exception {
        System.out.println("TEST36 - parse - list separator * n, list terminator");
        commonTestStringMultiple("FILTER (a,b,c,x,y,z)", "a", "b", "c", "x", "y", "z");
    }

    @Test
    public void testParse37() throws Exception {
        System.out.println("TEST37 - parse - list separator * n, list terminator with expressions");
        commonTestStringMultiple("FILTER (string (a==b),string(!(FALSE||FALSE||TRUE&&FALSE||FALSE)),c+d)", "No", "Yes", "cd");
    }

    @Test
    public void testParse38() throws Exception {
        System.out.println("TEST38 - parse - multiple commands; list separator , list terminator");
        commonTestCommandMultipleStringMultiple("FILTER (a,b,c) DATA (x,y,z)", new String[]{"FILTER", "DATA"},
                new String[]{"a", "b", "c"}, new String[]{"x", "y", "z"});
    }

    @Test
    public void testParse39() throws Exception {
        System.out.println("TEST39 - parse - simple property");
        commonTestProperty("FILTER abc:xyz", "xyz");
    }

    @Test
    public void testParse40() throws Exception {
        System.out.println("TEST40 - parse - single Property");
        commonTestProperty("FILTER abc:x+y+z", "xyz");
    }
}
