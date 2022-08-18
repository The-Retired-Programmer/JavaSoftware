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

import java.util.Iterator;
import uk.theretiredprogrammer.reportwriter.datasource.DataSetFromCSV;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.reportwriter.datasource.DataRecord;
import uk.theretiredprogrammer.reportwriter.datasource.StoredDataSet;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.functions.StringLiteral;

public class DataSourceCSVTest {

    @Test
    @SuppressWarnings("null")
    public void testLoad() {
        System.out.println("load");
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ExpressionMap parameters = new ExpressionMap();
        parameters.put("path", new StringLiteral("club-dinghy-racing-2022-bookings20220222 (1).csv"));
        parameters.put("match", new StringLiteral("full"));
        StoredDataSet dataset = null;
        try {
            dataset = DataSetFromCSV.create("bookings", parameters);
        } catch (Throwable t) {
            fail("loading CSV failure: " + t.getMessage());
        }

        Iterator<DataRecord> i = dataset.getStream().iterator();
        assertEquals("Janie Linsdale", i.next().get("Made for"));
        assertEquals("Richard Linsdale", i.next().get("Made for"));
    }

    @Test
    @SuppressWarnings("null")
    public void testLoad2() {
        System.out.println("load2");
        try {
            TestConfiguration.create("reportdefinition");
        } catch (RPTWTRException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ExpressionMap parameters = new ExpressionMap();
        parameters.put("path", new StringLiteral("enquiries20220317.csv"));
        parameters.put("match", new StringLiteral("full"));
        StoredDataSet dataset = null;
        try {
            dataset = DataSetFromCSV.create("enquiries", parameters);
        } catch (Throwable t) {
            fail("loading CSV failure: " + t.getLocalizedMessage());
        }
        Iterator<DataRecord> i = dataset.getStream().iterator();
        i.next();
        i.next();
        i.next();
        assertEquals("Lucas Hartley", i.next().get("Subject"));
        i.next();
        assertEquals("richard@rlinsdale.uk", i.next().get("Email"));
    }
}
