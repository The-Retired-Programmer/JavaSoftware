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

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;
import uk.theretiredprogrammer.scmreportwriter.SCM_ExpressionLanguage;
import uk.theretiredprogrammer.scmreportwriter.datasource.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.TestConfiguration;

public class MapParserTest {

    public MapParserTest() {

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
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

    private void commonTestMap2deep(String input, String[] datares) throws Exception {
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
            assertEquals(1, map.size());
            //
            if (map.get(datares[0]) instanceof ExpressionMap p) {
                assertEquals(1, p.size());
                if (p.get(datares[1]) instanceof StringExpression stringexp) {
                    assertEquals(datares[2], stringexp.evaluate(dr));
                } else {
                    fail("data second level is not a string expression");
                }
            } else {
                fail("data first level is not an ExpressionMap");
            }
        }
    }

    private void commonTestMap3deep(String input, String[] datares) throws Exception {
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
            assertEquals(1, map.size());
            //
            if (map.get(datares[0]) instanceof ExpressionMap p) {
                assertEquals(1, p.size());
                if (p.get(datares[1]) instanceof ExpressionMap p2) {
                    assertEquals(1, p2.size());
                    if (p2.get(datares[2]) instanceof StringExpression stringexp) {
                        assertEquals(datares[3], stringexp.evaluate(dr));
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
    }

    private void commonTestMap2wide(String input, String[] datares) throws Exception {
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
            assertEquals(2, map.size());
            //
            if (map.get(datares[0]) instanceof ExpressionMap p) {
                assertEquals(1, p.size());
                if (p.get(datares[1]) instanceof StringExpression stringexp) {
                    assertEquals(datares[2], stringexp.evaluate(dr));
                } else {
                    fail("data1 second level is not a string expression");
                }
            } else {
                fail("data1 first level is not an ExpressionMap");
            }
            if (map.get(datares[3]) instanceof ExpressionMap p) {
                assertEquals(1, p.size());
                if (p.get(datares[4]) instanceof StringExpression stringexp) {
                    assertEquals(datares[5], stringexp.evaluate(dr));
                } else {
                    fail("data2 second level is not a string expression");
                }
            } else {
                fail("data2 first level is not an ExpressionMap");
            }
        }
    }

    private void commonTestMap3wide(String input, String[] datares) throws Exception {
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
            assertEquals(2, map.size());
            //
            if (map.get(datares[0]) instanceof ExpressionMap p) {
                assertEquals(1, p.size());
                if (p.get(datares[1]) instanceof ExpressionMap p2) {
                    assertEquals(1, p2.size());
                    if (p2.get(datares[2]) instanceof StringExpression stringexp) {
                        assertEquals(datares[3], stringexp.evaluate(dr));
                    } else {
                        fail("data1 third level is not a string expression");
                    }
                } else {
                    fail("data1 second level is not an ExpressionMap");
                }
            } else {
                fail("data1 first level is not an ExpressionMap");
            }
            if (map.get(datares[4]) instanceof ExpressionMap q) {
                assertEquals(1, q.size());
                if (q.get(datares[5]) instanceof ExpressionMap q2) {
                    assertEquals(1, q2.size());
                    if (q2.get(datares[6]) instanceof StringExpression stringexp) {
                        assertEquals(datares[7], stringexp.evaluate(dr));
                    } else {
                        fail("data2 third level is not a string expression");
                    }
                } else {
                    fail("data2 second level is not an ExpressionMap");
                }
            } else {
                fail("data2 first level is not an ExpressionMap");
            }
        }
    }
    
    private void commonTestMap(String input, int elements) throws Exception {
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
            assertEquals(elements, map.size());
        } else {
            fail("top level is not an ExpressionMap");
        }
    }

    @Test
    public void test1MapParse() throws Exception {
        System.out.println("TEST1 - parse - map");
        commonTestMap("{ a:b, x:y }", "x", "y");
    }

    @Test
    public void test2MapParse() throws Exception {
        System.out.println("TEST2 - parse - map>map");
        commonTestMap2deep("{ a:{ b:c } }", new String[]{"a", "b", "c"});
    }

    @Test
    public void test3MapParse() throws Exception {
        System.out.println("TEST3 - parse - map>{map,map}");
        commonTestMap2wide("{ a:{ b:c } , d:{e:f } }", new String[]{"a", "b", "c", "d", "e", "f"});
    }

    @Test
    public void test4MapParse() throws Exception {
        System.out.println("TEST4 - parse - map>map>map");
        commonTestMap3deep("{ a:{ b:{c:d }}}", new String[]{"a", "b", "c", "d"});
    }
    
    @Test
    public void test5MapParse() throws Exception {
        System.out.println("TEST5 - parse - map>{map>map,map>map}");
        commonTestMap3wide("{ a:{ b:{c:d }} , e:{f:{g:h }}}", new String[]{"a", "b", "c", "d", "e", "f", "g", "h"});
    }
    
    @Test
    public void test6MapParse() throws Exception {
        System.out.println("TEST6 - parse - map");
        commonTestMap("{ a:{ b:{c:d }} , e_1:{f:{g:h }}}", 2);
    }
    
    @Test
    public void test7MapParse() throws Exception {
        System.out.println("TEST7 - parse - map");
        commonTestMap("""
                    {   data:{
                            bookings:{match:latest_startswith, path:"club-dinghy-racing-2022"},
                            contacts:{match:latest_startswith, path:"enquiries"}
                        },
                        generated_data:{
                            adult_entry:{
                                using: bookings,
                                filter: $Event == "Club Dinghy Racing 2022" && $Type == "Entry - Adult",
                                sort_by: ["Last name", "First name"]
                            }
                        }
                    }""", 2);
    }
}
