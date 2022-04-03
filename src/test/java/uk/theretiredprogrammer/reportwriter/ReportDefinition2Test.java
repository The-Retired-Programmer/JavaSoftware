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
package uk.theretiredprogrammer.reportwriter;

import uk.theretiredprogrammer.reportwriter.ReportDefinition;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;
import uk.theretiredprogrammer.reportwriter.datasource.DataSourceRecord;
import java.io.FileNotFoundException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.Operand;

public class ReportDefinition2Test {

    public ReportDefinition2Test() {
    }

    @Test
    public void testCreate() throws IOException, RPTWTRException, FileNotFoundException {
        System.out.println("definitions 2");

        DataSourceRecord datarecord = new DataSourceRecord("Event", "Type", "dummy", "dummy");
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ReportDefinition reportdefinition = new ReportDefinition();
        reportdefinition.buildReportDefinition();
        //
        ExpressionMap map = reportdefinition.getDatadefinitions();
        assertEquals(2, map.size());
        if (map.get("contacts") instanceof ExpressionMap contacts) {
            assertEquals(2, contacts.size());
            assertEquals("latest_startswith", contacts.get("match").evaluate(datarecord));
        } else {
            fail("Data - contacts is not a map");
        }
        //
        Operand reports = reportdefinition.getReportdefinitions();
        if (reports instanceof ExpressionList rptlist) {
            Operand report1 = rptlist.get(0);
            if (report1 instanceof ExpressionMap rptmap) {
                if (rptmap.get("headers") instanceof ExpressionList list) {
                    assertEquals(7, list.size());
                    assertEquals("Made for", list.get(0).evaluate(datarecord));
                }
                if (rptmap.get("filter") instanceof BooleanExpression bexp) {
                    assertEquals(false, bexp.evaluate(datarecord));
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
