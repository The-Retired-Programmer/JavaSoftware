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
package uk.theretiredprogrammer.scmreportwriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;
import uk.theretiredprogrammer.scmreportwriter.language.LexerException;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;
import uk.theretiredprogrammer.scmreportwriter.language.ParserException;

public class ReportDefinition2Test {

    public ReportDefinition2Test() {
    }

    @Test
    public void testCreate() throws IOException, LexerException, FileNotFoundException,
            ParserException, InternalReportWriterException {
        System.out.println("definitions 2");
        DataSourceRecord datarecord = new DataSourceRecord();
        datarecord.put("Event","dummy");
        datarecord.put("Type","dummy");
        Configuration configuration= null;
        try {
            configuration = new TestConfiguration("reportdefinition");
        } catch (ConfigurationException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ReportDefinition reportdefinition = new ReportDefinition();
        reportdefinition.buildReportDefinition(configuration);
        //
        ExpressionMap map = reportdefinition.getDatadefinitions();
        assertEquals(2, map.size());
        if (map.get("contacts") instanceof ExpressionMap contacts) {
            assertEquals(2, contacts.size());
            assertEquals("latest_startswith", contacts.get("match").evaluate(configuration, datarecord));
        } else {
            fail("Data - contacts is not a map");
        }
        //
        Operand reports = reportdefinition.getReportdefinitions();
        if (reports instanceof ExpressionList rptlist) {
            Operand report1 = rptlist.get(0);
            if (report1 instanceof ExpressionMap rptmap){
                if (rptmap.get("headers") instanceof ExpressionList list) {
                    assertEquals(7, list.size());
                    assertEquals("Made for", list.get(0).evaluate(configuration, datarecord));
                }
                if (rptmap.get("filter") instanceof BooleanExpression bexp) {
                    assertEquals(false, bexp.evaluate(configuration, datarecord));
                } else {
                    fail("report1>filter is not a boolean expression");
                }
            } else {
                fail("reports>get(0) is not a lmap");
            }
        } else {
            fail("reports is not a list");
        }
    }
}
