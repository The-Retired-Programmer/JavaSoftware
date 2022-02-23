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
package uk.theretiredprogrammer.scmreportwriter;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class DataSourceCSVTest {
    
    public DataSourceCSVTest() {
    }
    
    /**
     * Test of load method, of class DataSourceCSV.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        File f = new File("/home/pi/Downloads/club-dinghy-racing-2022-bookings20220222 (1).csv");
        DataSourceCSV instance = null;
        try {
            instance = new DataSourceCSV(f);
        } catch (IOException ex) {
            fail("loading CSV failure: "+ex.getMessage());
        }
        assertEquals(2, instance.size());
        assertEquals("Richard Linsdale", instance.get(1).get("Made for"));
        assertEquals("Janie Linsdale", instance.get(0).get("Made for"));
    }
    
}
