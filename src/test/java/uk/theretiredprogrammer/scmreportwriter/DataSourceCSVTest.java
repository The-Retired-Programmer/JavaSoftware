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

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;
import uk.theretiredprogrammer.scmreportwriter.language.functions.StringLiteral;

public class DataSourceCSVTest {

    public DataSourceCSVTest() {
    }

    /**
     * Test of load method, of class DataSourceCSVExtended.
     */
    @Test
    @SuppressWarnings("null")
    public void testLoad() {
        System.out.println("load");
        Configuration configuration= null;
        try {
            configuration = new TestConfiguration("reportdefinition");
        } catch (ConfigurationException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ExpressionMap parameters = new ExpressionMap();
        parameters.put("path", new StringLiteral("club-dinghy-racing-2022-bookings20220222 (1).csv"));
        DataSource datasource = null;
        try {
            try {
                datasource = DataSourceCSV.read(configuration, parameters);
            } catch (IOException ex) {
                fail("loading CSV failure: " + ex.getMessage());
            }
        } catch (InternalReportWriterException ex) {
            fail("Datatype failure: " + ex.getMessage());
        }
        assertEquals(2, datasource.size());
        assertEquals("Richard Linsdale", datasource.get(1).get("Made for"));
        assertEquals("Janie Linsdale", datasource.get(0).get("Made for"));
    }
    
     /**
     * Test of load method, of class DataSourceCSVExtended.
     */
    @Test
    @SuppressWarnings("null")
    public void testLoad2() throws ConfigurationException {
        System.out.println("load2");
        Configuration configuration= null;
        try {
            configuration = new TestConfiguration("reportdefinition");
        } catch (ConfigurationException | IOException ex) {
            fail("Configuration Failure: " + ex.getLocalizedMessage());
        }
        ExpressionMap parameters = new ExpressionMap();
        parameters.put("path", new StringLiteral("enquiries20220317.csv"));
        DataSource datasource = null;
        try {
            try {
                datasource = DataSourceCSV.read(configuration, parameters);
            } catch (IOException ex) {
                fail("loading CSV failure: " + ex.getMessage());
            }
        } catch (InternalReportWriterException ex) {
            fail("Datatype failure: " + ex.getMessage());
        }
        assertEquals(40, datasource.size());
        assertEquals("Lucas Hartley", datasource.get(3).get("Subject"));
        assertEquals("richard@rlinsdale.uk", datasource.get(5).get("Email"));
    }

}
