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
import uk.theretiredprogrammer.scmreportwriter.expression.And;
import uk.theretiredprogrammer.scmreportwriter.expression.Boolean2String;
import uk.theretiredprogrammer.scmreportwriter.expression.Concatonate;
import uk.theretiredprogrammer.scmreportwriter.expression.DataRecordField;
import uk.theretiredprogrammer.scmreportwriter.expression.Equals;
import uk.theretiredprogrammer.scmreportwriter.expression.EqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Expression;
import uk.theretiredprogrammer.scmreportwriter.expression.ListSeparator;
import uk.theretiredprogrammer.scmreportwriter.expression.Literal;
import uk.theretiredprogrammer.scmreportwriter.expression.Not;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEquals;
import uk.theretiredprogrammer.scmreportwriter.expression.NotEqualsIgnoreCase;
import uk.theretiredprogrammer.scmreportwriter.expression.Or;
import uk.theretiredprogrammer.scmreportwriter.expression.String2Boolean;

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

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc+xyz")
        );
        assert (presult instanceof Concatonate);
        assertEquals("abcxyz", presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse2() throws Exception {
        System.out.println("parse - equals - true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc==xyz")
        );
        assert (presult instanceof Equals);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse3() throws Exception {
        System.out.println("parse - equals - false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc==abc")
        );
        assert (presult instanceof Equals);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse4() throws Exception {
        System.out.println("parse - equals - true with rhs concatonate");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc==a+bc")
        );
        assert (presult instanceof Equals);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse5() throws Exception {
        System.out.println("parse - equals - true with lhs concatonate");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("a+bc==abc")
        );
        assert (presult instanceof Equals);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse6() throws Exception {
        System.out.println("parse - equals - true with both side concatonates");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("a+bc==ab+c")
        );
        assert (presult instanceof Equals);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse7() throws Exception {
        System.out.println("parse - true And false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("TRUE&&FALSE")
        );
        assert (presult instanceof And);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse8() throws Exception {
        System.out.println("parse - true And true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("TRUE&&TRUE")
        );
        assert (presult instanceof And);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse9() throws Exception {
        System.out.println("parse - false And true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("FALSE&&TRUE")
        );
        assert (presult instanceof And);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse10() throws Exception {
        System.out.println("parse - false And false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("FALSE&&FALSE")
        );
        assert (presult instanceof And);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse11() throws Exception {
        System.out.println("parse - true Or false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("TRUE||FALSE")
        );
        assert (presult instanceof Or);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse12() throws Exception {
        System.out.println("parse - true Or true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("TRUE||TRUE")
        );
        assert (presult instanceof Or);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse13() throws Exception {
        System.out.println("parse - false Or true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("FALSE||TRUE")
        );
        assert (presult instanceof Or);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse14() throws Exception {
        System.out.println("parse - false Or false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("FALSE||FALSE")
        );
        assert (presult instanceof Or);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse15() throws Exception {
        System.out.println("parse - Not true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("¬TRUE")
        );
        assert (presult instanceof Not);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse16() throws Exception {
        System.out.println("parse - Not false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("¬FALSE")
        );
        assert (presult instanceof Not);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse17() throws Exception {
        System.out.println("parse - brackets");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("(abc)")
        );
        assert (presult instanceof Literal);
        assertEquals("abc", presult.evaluate(new DataSourceRecord()));
    }

    @Test
    public void testParse18() throws Exception {
        System.out.println("parse - brackets with concatonate");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("(abc+xyz)")
        );
        assert (presult instanceof Concatonate);
        assertEquals("abcxyz", presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse19() throws Exception {
        System.out.println("parse - notequals false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc!=abc")
        );
        assert (presult instanceof NotEquals);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse20() throws Exception {
        System.out.println("parse - notequals true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc!=xyz")
        );
        assert (presult instanceof NotEquals);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse21() throws Exception {
        System.out.println("parse - equalsignorecase true ");
        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc=~abc")
        );
        assert (presult instanceof EqualsIgnoreCase);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse22() throws Exception {
        System.out.println("parse - equalsignorecase true ");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc=~ABC")
        );
        assert (presult instanceof EqualsIgnoreCase);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse23() throws Exception {
        System.out.println("parse - equalsignorecase false ");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc=~XYZ")
        );
        assert (presult instanceof EqualsIgnoreCase);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse24() throws Exception {
        System.out.println("parse - notequalsignorecase false ");
        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc!~abc")
        );
        assert (presult instanceof NotEqualsIgnoreCase);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse25() throws Exception {
        System.out.println("parse - notequalsignorecase false ");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc!~ABC")
        );
        assert (presult instanceof NotEqualsIgnoreCase);
        assertEquals(false, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse26() throws Exception {
        System.out.println("parse - notequalsignorecase true ");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc!~XYZ")
        );
        assert (presult instanceof NotEqualsIgnoreCase);
        assertEquals(true, presult.evaluate(new DataSourceRecord()));
    }
    
    @Test
    public void testParse27() throws Exception {
        System.out.println("parse - field op ");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("${abc}")
        );
        assert (presult instanceof DataRecordField);
        DataSourceRecord datarecord = new DataSourceRecord();
        datarecord.put("abc","xyz");
        assertEquals("xyz", presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse28() throws Exception {
        System.out.println("parse - boolean cast true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("boolean Yes")
        );
        assert (presult instanceof String2Boolean);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals(true, presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse29() throws Exception {
        System.out.println("parse - boolean cast false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("boolean No")
        );
        assert (presult instanceof String2Boolean);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals(false, presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse30() throws Exception {
        System.out.println("parse - boolean cast true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("boolean true")
        );
        assert (presult instanceof String2Boolean);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals(true, presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse31() throws Exception {
        System.out.println("parse - string cast true");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("string TRUE")
        );
        assert (presult instanceof Boolean2String);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals("Yes", presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse32() throws Exception {
        System.out.println("parse - string cast false");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("string FALSE")
        );
        assert (presult instanceof Boolean2String);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals("No", presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse33() throws Exception {
        System.out.println("parse - list terminator");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc;")
        );
        assert (presult instanceof ListSeparator);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals("abc", presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse34() throws Exception {
        System.out.println("parse - list separator, list terminator");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("abc,xyz;")
        );
        if (presult instanceof ListSeparator list) {
            DataSourceRecord datarecord = new DataSourceRecord();
            assertEquals("abc", list.evaluate(datarecord));
            assertEquals("xyz", list.getNextExpression().evaluate(datarecord));
        } else {
            fail("result expression is not ListSeparator");
        }
    }
    
    @Test
    public void testParse35() throws Exception {
        System.out.println("parse - complex expression 1");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("string¬(ab+cd == abcd)+NE=~NONE")
        );
        assert (presult instanceof EqualsIgnoreCase);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals(true, presult.evaluate(datarecord));
    }
    
    @Test
    public void testParse36() throws Exception {
        System.out.println("parse - complex expression 2");

        var instance = new Parser();
        Expression presult = instance.parse(
                buildS_Tokens("TRUE && FALSE||FALSE||ab==cd||boolean NO|| ¬FALSE")
        );
        assert (presult instanceof Or);
        DataSourceRecord datarecord = new DataSourceRecord();
        assertEquals(true, presult.evaluate(datarecord));
    }

    private List<S_Token> buildS_Tokens(String input) throws LexerException {
        Lexer instance = new Lexer();
        return instance.lex(input.lines());
    }
}
