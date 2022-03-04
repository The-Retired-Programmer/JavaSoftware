/*
 * Copyright 2022 pi.
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
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.expression.Expression;
import uk.theretiredprogrammer.scmreportwriter.expression.FilterCommand;
import uk.theretiredprogrammer.scmreportwriter.expression.ListSeparator;

public class ParserTest {

    public ParserTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testParse1() throws Exception {
        System.out.println("parse - concatonate");
        commonTestString("FILTER abc+xyz;","abcxyz");
    }
    
    private void commonTestString(String input, String expected) throws Exception {
        var instance = new Parser();
        Expression presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof FilterCommand cmd) {
            ListSeparator lresult = cmd.getExpressionlist();
            DataSourceRecord datarecord = new DataSourceRecord();
            assertEquals(expected, lresult.evaluate(datarecord));
            assertEquals(null, lresult.getNextExpression());
        } else {
            fail("result expression is not FilterCommand");
        }
    }
    
    private void commonTestStringMultiple(String input, String... expected) throws Exception {
        var instance = new Parser();
        Expression presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof FilterCommand cmd) {
            ListSeparator lresult = cmd.getExpressionlist();
            DataSourceRecord datarecord = new DataSourceRecord();
            for (String nextexp: expected) {
                assertEquals(nextexp, lresult.evaluate(datarecord));
                lresult = (ListSeparator) lresult.getNextExpression();
            }
            assertEquals(null, lresult);
        } else {
            fail("result expression is not FilterCommand");
        }
    }
    
    private void commonTestCommandMultipleStringMultiple(String input, String[]... expected) throws Exception {
        var instance = new Parser();
        Expression presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof FilterCommand cmd) {
            FilterCommand fc = cmd;
            int filtercommandindex = 0;
            while (fc != null) {
                ListSeparator lresult = fc.getExpressionlist();
                DataSourceRecord datarecord = new DataSourceRecord();
                for (String nextexp : expected[filtercommandindex]) {
                    assertEquals(nextexp, lresult.evaluate(datarecord));
                    lresult = (ListSeparator) lresult.getNextExpression();
                }
                assertEquals(null, lresult);
                filtercommandindex++;
                fc = fc.getNext();
            }
        } else {
            fail("result expression is not FilterCommand");
        }
    }
    
    private void commonTestStringWithData(String input, String expected, String datakey, String datavalue) throws Exception {
        var instance = new Parser();
        Expression presult = instance.parse(buildS_Tokens(input));
        if (presult instanceof FilterCommand cmd) {
            ListSeparator lresult = cmd.getExpressionlist();
            DataSourceRecord datarecord = new DataSourceRecord();
            datarecord.put(datakey, datavalue);
            assertEquals(expected, lresult.evaluate(datarecord));
            assertEquals(null, lresult.getNextExpression());
        } else {
            fail("result expression is not FilterCommand");
        }
    }
    
     private void commonTestBoolean(String input, boolean expected) throws Exception {
        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens(input)
        );
        if (presult instanceof FilterCommand cmd) {
            ListSeparator lresult = cmd.getExpressionlist();
            DataSourceRecord datarecord = new DataSourceRecord();
            assertEquals(expected, lresult.evaluate(datarecord));
            assertEquals(null, lresult.getNextExpression());
        } else {
            fail("result expression is not FilterCommand");
        }
    }

    @Test
    public void testParse2() throws Exception {
        System.out.println("parse - equals - true");
        commonTestBoolean("FILTER abc==abc;", true);
    }

    @Test
    public void testParse3() throws Exception {
        System.out.println("parse - equals - false");
        commonTestBoolean("FILTER abc==xyz;", false);
    }

    @Test
    public void testParse4() throws Exception {
        System.out.println("parse - equals - true with rhs concatonate");
        commonTestBoolean("FILTER abc==a+bc;", true);
    }

    @Test
    public void testParse5() throws Exception {
        System.out.println("parse - equals - true with lhs concatonate");
        commonTestBoolean("FILTER a+bc==abc;", true);
    }

    @Test
    public void testParse6() throws Exception {
        System.out.println("parse - equals - true with both side concatonates");
        commonTestBoolean("FILTER a+bc==ab+c;", true);
    }

    @Test
    public void testParse7() throws Exception {
        System.out.println("parse - true And false");
        commonTestBoolean("FILTER TRUE&&FALSE;", false);
    }

    @Test
    public void testParse8() throws Exception {
        System.out.println("parse - true And true");
        commonTestBoolean("FILTER TRUE&&TRUE;", true);
    }

    @Test
    public void testParse9() throws Exception {
        System.out.println("parse - false And true");
        commonTestBoolean("FILTER FALSE&&TRUE;", false);
    }

    @Test
    public void testParse10() throws Exception {
        System.out.println("parse - false And false");
        commonTestBoolean("FILTER FALSE&&FALSE;", false);
    }

    @Test
    public void testParse11() throws Exception {
        System.out.println("parse - true Or false");
        commonTestBoolean("FILTER TRUE||FALSE;", true);
    }

    @Test
    public void testParse12() throws Exception {
        System.out.println("parse - true Or true");
        commonTestBoolean("FILTER TRUE||TRUE;", true);
    }

    @Test
    public void testParse13() throws Exception {
        System.out.println("parse - false Or true");
        commonTestBoolean("FILTER FALSE||TRUE;", true);
    }

    @Test
    public void testParse14() throws Exception {
        System.out.println("parse - false Or false");
        commonTestBoolean("FILTER FALSE||FALSE;", false);
    }

    @Test
    public void testParse15() throws Exception {
        System.out.println("parse - Not true");
        commonTestBoolean("FILTER ¬TRUE;", false);
    }

    @Test
    public void testParse16() throws Exception {
        System.out.println("parse - Not false");
        commonTestBoolean("FILTER ¬FALSE;", true);
    }

    @Test
    public void testParse17() throws Exception {
        System.out.println("parse - brackets");
        commonTestString("FILTER (abc);", "abc");
    }

    @Test
    public void testParse18() throws Exception {
        System.out.println("parse - brackets with concatonate");
        commonTestString("FILTER (abc+xyz);", "abcxyz");
    }
    
    @Test
    public void testParse19() throws Exception {
        System.out.println("parse - notequals false");
        commonTestBoolean("FILTER abc!=abc;", false);
    }
    
    @Test
    public void testParse20() throws Exception {
        System.out.println("parse - notequals true");
        commonTestBoolean("FILTER abc!=xyz;", true);
    }
    
    @Test
    public void testParse21() throws Exception {
        System.out.println("parse - equalsignorecase true ");
        commonTestBoolean("FILTER abc=~abc;", true);
    }
    
    @Test
    public void testParse22() throws Exception {
        System.out.println("parse - equalsignorecase true ");
        commonTestBoolean("FILTER abc=~ABC;", true);
    }
    
    @Test
    public void testParse23() throws Exception {
        System.out.println("parse - equalsignorecase false ");
        commonTestBoolean("FILTER abc=~XYZ;", false);
    }
    
    @Test
    public void testParse24() throws Exception {
        System.out.println("parse - notequalsignorecase false ");
        commonTestBoolean("FILTER abc!~abc;", false);
    }
    
    @Test
    public void testParse25() throws Exception {
        System.out.println("parse - notequalsignorecase false ");
        commonTestBoolean("FILTER abc!~ABC;", false);
    }
    
    @Test
    public void testParse26() throws Exception {
        System.out.println("parse - notequalsignorecase true ");
        commonTestBoolean("FILTER abc!~XYZ;", true);
    }
    
    @Test
    public void testParse27() throws Exception {
        System.out.println("parse - field op ");
        commonTestStringWithData("FILTER ${abc};", "xyz", "abc","xyz");
    }
    
    @Test
    public void testParse28() throws Exception {
        System.out.println("parse - boolean cast true");
        commonTestBoolean("FILTER boolean Yes;", true);
    }
    
    @Test
    public void testParse29() throws Exception {
        System.out.println("parse - boolean cast false");
         commonTestBoolean("FILTER boolean No;", false);
    }
    
    @Test
    public void testParse30() throws Exception {
        System.out.println("parse - boolean cast true");
        commonTestBoolean("FILTER boolean true;", true);
    }
    
    @Test
    public void testParse31() throws Exception {
        System.out.println("parse - string cast true");
        commonTestString("FILTER string TRUE;", "Yes");
    }
    
    @Test
    public void testParse32() throws Exception {
        System.out.println("parse - string cast false");
        commonTestString("FILTER string FALSE;", "No");
    }
    
    @Test
    public void testParse33() throws Exception {
        System.out.println("parse - list separator, list terminator");
        commonTestStringMultiple("FILTER abc,xyz;", "abc","xyz");
    }
    
    @Test
    public void testParse34() throws Exception {
        System.out.println("parse - complex expression 1");
        commonTestBoolean("FILTER string¬(ab+cd == abcd)+NE=~NONE;", true);
    }
    
    @Test
    public void testParse35() throws Exception {
        System.out.println("parse - complex expression 2");
        commonTestBoolean("FILTER TRUE && FALSE||FALSE||ab==cd||boolean NO|| ¬FALSE;", true);
    }
    
    @Test
    public void testParse36() throws Exception {
        System.out.println("parse - list separator * n, list terminator");
        commonTestStringMultiple("FILTER a,b,c,x,y,z;", "a","b","c","x","y","z");
    }
    
    @Test
    public void testParse37() throws Exception {
        System.out.println("parse - list separator * n, list terminator with expressions");
        commonTestStringMultiple("FILTER string (a==b),string(¬(FALSE||FALSE||TRUE&&FALSE||FALSE)),c+d;", "No","Yes","cd");
    }
    
    @Test
    public void testParse38() throws Exception {
        System.out.println("parse - single commands; list separator , list terminator");
        commonTestCommandMultipleStringMultiple("FILTER a,b,c;", new String[]{"a","b","c"});
    }
    
    @Test
    public void testParse39() throws Exception {
        System.out.println("parse - multiple commands; list separator , list terminator");
        commonTestCommandMultipleStringMultiple("FILTER a,b,c;FILTER x,y,z;", new String[]{"x","y","z"}, new String[]{"a","b","c"});
    }

    private List<S_Token> buildS_Tokens(String input) throws LexerException {
        Lexer instance = new Lexer();
        return instance.lex(input.lines());
    }
}
