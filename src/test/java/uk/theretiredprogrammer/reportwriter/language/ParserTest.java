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

import uk.theretiredprogrammer.reportwriter.language.StringExpression;
import uk.theretiredprogrammer.reportwriter.language.Lexer;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;
import uk.theretiredprogrammer.reportwriter.language.DefinitionSource;
import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.language.Operand;
import uk.theretiredprogrammer.reportwriter.language.Language;
import uk.theretiredprogrammer.reportwriter.language.Property;
import uk.theretiredprogrammer.reportwriter.language.Parser;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;
import uk.theretiredprogrammer.reportwriter.SCM_ExpressionLanguage;
import uk.theretiredprogrammer.reportwriter.datasource.DataSourceRecord;
import uk.theretiredprogrammer.reportwriter.TestConfiguration;

public class ParserTest {

    public ParserTest() {

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    private void commonTestString(String input, String expected) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof StringExpression stringexp) {
            assertEquals(expected, stringexp.evaluate(datarecord));
            return;
        }
        fail("result expression is not stringexpression");
    }

    private void commonTestProperty(String input, String expected) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof Property propertyexp) {
            assertEquals(expected, propertyexp.evaluate(datarecord));
            return;
        }
        fail("result expression is not property");
    }

    private void commonTestStringMultiple(String input, String... expected) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof ExpressionList expressions) {
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
        fail("result expression is not expressionlist");
    }

    private void commonTestCommandMultipleStringMultiple(String input, String[] commands, String[]... expected) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof ExpressionMap map) {
            assertEquals(expected.length, map.size());
            assertEquals(expected.length, commands.length);
            for (int j = 0; j < commands.length; j++) {
                Operand operand = map.get(commands[j]);
                if (operand instanceof ExpressionList expressions) {
                    assertEquals(expected[j].length, expressions.size());
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
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord(datakey, datavalue);
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof StringExpression stringexp) {
            assertEquals(expected, stringexp.evaluate(datarecord));
            return;
        }
        fail("result expression is not stringexpression");
    }

    private void commonTestBoolean(String input, boolean expected) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof BooleanExpression boolexp) {
            assertEquals(expected, boolexp.evaluate(datarecord));
            return;
        }
        fail("result expression is not booleanexpression");
    }

    private void commonTestMap(String input, String expectedkey, String expectedvalue) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord datarecord = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof ExpressionMap map) {
            assertEquals(true, map.containsKey(expectedkey));
            if (map.get(expectedkey) instanceof StringExpression string) {
                assertEquals(expectedvalue, string.evaluate(datarecord));
                return;
            } else {
                fail("result expression is not expressionmap>stringexpression");
            }
        }
        fail("result expression is not an expressionmap");
    }

    private void commonTestCommandMultipleBooleanMap2List(String input, Boolean filterres, String[] datares, String[] fieldsres) throws Exception {
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        Language language = new SCM_ExpressionLanguage();
        DataSourceRecord dr = new DataSourceRecord();
        DefinitionSource source = new DefinitionSource(input);
        Lexer lexer = new Lexer(source, language);
        lexer.lex();
        Parser parser = new Parser(source, language);
        Operand presult = parser.parse();
        if (presult instanceof ExpressionMap map) {
            assertEquals(3, map.size());
            //
            checkFilter(map, filterres, dr);
            checkData(map, datares, dr);
            checkFields(map, fieldsres, dr);
        }
    }

    private void checkFilter(ExpressionMap map, Boolean res, DataSourceRecord datarecord) throws RPTWTRException {
        if (map.get("filter") instanceof BooleanExpression bexp) {
            assertEquals(res, bexp.evaluate(datarecord));
        } else {
            fail("filter is not a booleanexpression");
        }
    }

    private void checkData(ExpressionMap map, String[] res, DataSourceRecord datarecord) throws RPTWTRException {
        if (map.get("data") instanceof ExpressionMap p) {
            if (p.get(res[0]) instanceof ExpressionMap p2) {
                if (p2.get(res[1]) instanceof StringExpression sexp) {
                    assertEquals(res[2], sexp.evaluate(datarecord));
                } else {
                    fail("data third level is not a string expression");
                }
            } else {
                fail("data second level is not an ExpressionMap");
            }
        } else {
            fail("data first level is not an ExpressionMap");
        }

    }

    private void checkFields(ExpressionMap map, String[] fieldsres, DataSourceRecord datarecord) throws RPTWTRException {
        if (map.get("fields") instanceof ExpressionList expl) {
            assertEquals(3, expl.size());
            for (int i = 0; i < 3; i++) {
                if (expl.get(i) instanceof StringExpression sexp) {
                    assertEquals(fieldsres[i], sexp.evaluate(datarecord));
                } else {
                    fail("an element of fields is not a string expression");
                }
            }
        } else {
            fail("fields is not an expression list");
        }
    }

    @Test
    public void testParse1() throws Exception {
        System.out.println("TEST1 - parse - concatonate");
        commonTestString("abc+xyz", "abcxyz");
    }

    @Test
    public void testParse2() throws Exception {
        System.out.println("TEST2 - parse - equals - true");
        commonTestBoolean(" abc==abc", true);
    }

    @Test
    public void testParse3() throws Exception {
        System.out.println("TEST3 - parse - equals - false");
        commonTestBoolean("abc==xyz", false);
    }

    @Test
    public void testParse4() throws Exception {
        System.out.println("TEST4 - parse - equals - true with rhs concatonate");
        commonTestBoolean("abc==a+bc", true);
    }

    @Test
    public void testParse5() throws Exception {
        System.out.println("TEST5 - parse - equals - true with lhs concatonate");
        commonTestBoolean("a+bc==abc", true);
    }

    @Test
    public void testParse6() throws Exception {
        System.out.println("TEST6 - parse - equals - true with both side concatonates");
        commonTestBoolean("a+bc==ab+c", true);
    }

    @Test
    public void testParse7() throws Exception {
        System.out.println("TEST7 - parse - true And false");
        commonTestBoolean("TRUE&&FALSE", false);
    }

    @Test
    public void testParse8() throws Exception {
        System.out.println("TEST8 - parse - true And true");
        commonTestBoolean("TRUE&&TRUE", true);
    }

    @Test
    public void testParse9() throws Exception {
        System.out.println("TEST9 - parse - false And true");
        commonTestBoolean("FALSE&&TRUE", false);
    }

    @Test
    public void testParse10() throws Exception {
        System.out.println("TEST10 - parse - false And false");
        commonTestBoolean("FALSE&&FALSE", false);
    }

    @Test
    public void testParse11() throws Exception {
        System.out.println("TEST11 - parse - true Or false");
        commonTestBoolean("TRUE||FALSE", true);
    }

    @Test
    public void testParse12() throws Exception {
        System.out.println("TEST12 - parse - true Or true");
        commonTestBoolean("TRUE||TRUE", true);
    }

    @Test
    public void testParse13() throws Exception {
        System.out.println("TEST13 - parse - false Or true");
        commonTestBoolean("FALSE||TRUE", true);
    }

    @Test
    public void testParse14() throws Exception {
        System.out.println("TEST14 - parse - false Or false");
        commonTestBoolean("FALSE||FALSE", false);
    }

    @Test
    public void testParse15() throws Exception {
        System.out.println("TEST15 - parse - Not true");
        commonTestBoolean("!TRUE", false);
    }

    @Test
    public void testParse16() throws Exception {
        System.out.println("TEST16 - parse - Not false");
        commonTestBoolean("!FALSE", true);
    }

    @Test
    public void testParse17() throws Exception {
        System.out.println("TEST17 - parse - brackets");
        commonTestString("(abc)", "abc");
    }

    @Test
    public void testParse18() throws Exception {
        System.out.println("TEST18 - parse - brackets with concatonate");
        commonTestString("(abc+xyz)", "abcxyz");
    }

    @Test
    public void testParse19() throws Exception {
        System.out.println("TEST19 - parse - notequals false");
        commonTestBoolean("abc!=abc", false);
    }

    @Test
    public void testParse20() throws Exception {
        System.out.println("TEST20 - parse - notequals true");
        commonTestBoolean("abc!=xyz", true);
    }

    @Test
    public void testParse21() throws Exception {
        System.out.println("TEST21 - parse - equalsignorecase true ");
        commonTestBoolean("abc=~abc", true);
    }

    @Test
    public void testParse22() throws Exception {
        System.out.println("TEST22 - parse - equalsignorecase true ");
        commonTestBoolean("abc=~ABC", true);
    }

    @Test
    public void testParse23() throws Exception {
        System.out.println("TEST23 - parse - equalsignorecase false ");
        commonTestBoolean("abc=~XYZ", false);
    }

    @Test
    public void testParse24() throws Exception {
        System.out.println("TEST24 - parse - notequalsignorecase false ");
        commonTestBoolean("abc!=~abc", false);
    }

    @Test
    public void testParse25() throws Exception {
        System.out.println("TEST25 - parse - notequalsignorecase false ");
        commonTestBoolean("abc!=~ABC", false);
    }

    @Test
    public void testParse26() throws Exception {
        System.out.println("TEST26 - parse - notequalsignorecase true ");
        commonTestBoolean("abc!=~XYZ", true);
    }

    @Test
    public void testParse27() throws Exception {
        System.out.println("TEST27 - parse - field op ");
        commonTestStringWithData("$\"abc\"", "xyz", "abc", "xyz");
    }

    @Test
    public void testParse28() throws Exception {
        System.out.println("TEST28 - parse - boolean cast true");
        commonTestBoolean("boolean Yes", true);
    }

    @Test
    public void testParse29() throws Exception {
        System.out.println("TEST29 - parse - boolean cast false");
        commonTestBoolean("boolean No", false);
    }

    @Test
    public void testParse30() throws Exception {
        System.out.println("TEST30 - parse - boolean cast true");
        commonTestBoolean("boolean true", true);
    }

    @Test
    public void testParse31() throws Exception {
        System.out.println("TEST31 - parse - string cast true");
        commonTestString("string TRUE", "Yes");
    }

    @Test
    public void testParse32() throws Exception {
        System.out.println("TEST32 - parse - string cast false");
        commonTestString("string FALSE", "No");
    }

    @Test
    public void testParse33() throws Exception {
        System.out.println("TEST33 - parse - list separator, list terminator");
        commonTestStringMultiple("[abc,xyz]", "abc", "xyz");
    }

    @Test
    public void testParse34MINUS1() throws Exception {
        System.out.println("TEST34MINUS - parse - simplified complex expression 1");
        commonTestString("string!TRUE", "No");
    }

    @Test
    public void testParse34MINUS2() throws Exception {
        System.out.println("TEST34MINUS - parse - simplified complex expression 1");
        commonTestString("string!(TRUE)", "No");
    }

    @Test
    public void testParse34MINUS3() throws Exception {
        System.out.println("TEST34MINUS - parse - simplified complex expression 1");
        commonTestString("string!(TRUE)+\"?\"", "No?");
    }

    @Test
    public void testParse34() throws Exception {
        System.out.println("TEST34 - parse - complex expression 1");
        commonTestBoolean("string!(ab+cd == abcd)+NE=~NONE", true);
    }

    @Test
    public void testParse35() throws Exception {
        System.out.println("TEST 35 - parse - complex expression 2");
        commonTestBoolean("TRUE && FALSE||FALSE||ab==cd||boolean NO|| !FALSE", true);
    }

    @Test
    public void testParse36() throws Exception {
        System.out.println("TEST36 - parse - list separator * n, list terminator");
        commonTestStringMultiple("[a,b,c,x,y,z]", "a", "b", "c", "x", "y", "z");
    }

    @Test
    public void testParse37() throws Exception {
        System.out.println("TEST37 - parse - list separator * n, list terminator with expressions");
        commonTestStringMultiple("[string (a==b),string(!(FALSE||FALSE||TRUE&&FALSE||FALSE)),c+d]", "No", "Yes", "cd");
    }

    @Test
    public void testParse38MINUS() throws Exception {
        System.out.println("TEST38MINUS - parse - expressionmap builer");
        commonTestMap("{filter:abc}", "filter", "abc");
    }

    @Test
    public void testParse38() throws Exception {
        System.out.println("TEST38 - parse - multiple commands; list separator , list terminator");
        commonTestCommandMultipleStringMultiple("{filter:[a,b,c], data:[x,y,z]}", new String[]{"filter", "data"},
                new String[]{"a", "b", "c"}, new String[]{"x", "y", "z"});
    }

    @Test
    public void testParse39() throws Exception {
        System.out.println("TEST39 - parse - simple property");
        commonTestProperty("abc:xyz", "xyz");
    }

    @Test
    public void testParse40() throws Exception {
        System.out.println("TEST40 - parse - single Property");
        commonTestProperty("abc:x+y+z", "xyz");
    }

    @Test
    public void testParse41() throws Exception {
        System.out.println("TEST41 - parse - three commands");
        commonTestCommandMultipleBooleanMap2List("{filter:TRUE,data:{bookings:{path:\"file.csv\"}},fields:[Hello,World,AGAIN]}",
                true, new String[]{"bookings", "path", "file.csv"}, new String[]{"Hello", "World", "AGAIN"});
    }

    @Test
    public void testParse42() throws Exception {
        System.out.println("TEST42 - parse - system parameter");
        commonTestString("sys \"file.encoding\"", "UTF-8");
    }

    @Test
    public void testParse43() throws Exception {
        System.out.println("TEST43 - parse - env value");
        commonTestString("env SHELL", "/bin/bash");
    }

    @Test
    public void testParse44() throws Exception {
        System.out.println("TEST44 - parse - command parameter");
        commonTestString("parameter 1", "<undefined>");
    }
}
