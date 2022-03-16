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
package uk.theretiredprogrammer.scmreportwriter.oldstuff;

import uk.theretiredprogrammer.scmreportwriter.DataSourceCSV;
import java.io.IOException;

public class EntryReport {
    
    private DataSourceCSV entryexport;

    public EntryReport() throws IOException {
        //entryexport = new DataSourceCSV(new File("/home/pi/Downloads/club-dinghy-racing-2022-bookings20220222 (1).csv"));
    }
    
    public void createAdultEntryReport() {
        String label3 = entryexport.get(0).get("Extra label (3)");
        String label4 = entryexport.get(0).get("Extra label (4)");
        String label5 = entryexport.get(0).get("Extra label (5)");
        System.out.println("Made for, "+label3+", "+label4+", "+label5+ ", Emergency name, Emergency number, Emergency / medical details");

        entryexport.stream().filter((map)-> map.get("Event").equals("Club Dinghy Racing 2022")
                && map.get("Type").equals("Entry - Adult")
            ).forEach((map)-> {
            System.out.print(map.get("Made for"));
            System.out.print(", " +map.get("Extra info (3)"));
            System.out.print(", " +map.get("Extra info (4)"));
            System.out.print(", " +map.get("Extra info (5)"));
            System.out.print(", " +map.get("Emergency name"));
            System.out.print(", " +map.get("Emergency number"));
            System.out.print(", " +map.get("Emergency / medical details"));
            System.out.println();
        });
        
    }
    
    public void checkAdultEntryReport() {

        entryexport.stream().filter((map)-> map.get("Event").equals("Club Dinghy Racing 2022")
               && map.get("Type").equals("Entry - Adult")
        ).forEach((map)-> {
           boolean madeforEqualsMadeby = map.get("Made for").equals(map.get("Made by"));
           boolean imageconsentcheck = map.get("Extra info (3)").equals("Yes") || map.get("Extra info (3)").equals("No");
           boolean dataconsentcheck = map.get("Extra info (4)").equals("Yes");
           boolean declarationcheck = map.get("Extra info (5)").equals("Yes");
           boolean hasEmergencyName = !map.get("Emergency name").isBlank();
           boolean hasEmergencyNumber = !map.get("Emergency number").isBlank();
           //
           //   ***** need to check age over 18  ******
           //
           if (!(madeforEqualsMadeby &&imageconsentcheck && dataconsentcheck && declarationcheck && hasEmergencyName && hasEmergencyNumber)) {
               System.out.println("Validation Failure: Ticket ID="+map.get("Ticket ID")+"; Made by="+map.get("Made by")+";");
               if (!madeforEqualsMadeby) System.out.println("        Made for does not equal Made by");
               if (!imageconsentcheck) System.out.println("        Image consent not defined (not Yes or No)");
               if (!dataconsentcheck) System.out.println("        Data consent not Yes");
               if (!declarationcheck) System.out.println("        Declaration not Yes");
               if (!hasEmergencyName) System.out.println("        Emergency Contact Name is empty");
               if (!hasEmergencyNumber) System.out.println("        Emergency Contact Number is empty");
           }
        });
        
    }
    
     public void createU18EntryReport() {
        String label1 = entryexport.get(0).get("Extra label (1)");
        String label2 = entryexport.get(0).get("Extra label (2)");
        String label3 = entryexport.get(0).get("Extra label (3)");
        String label4 = entryexport.get(0).get("Extra label (4)");
        String label5 = entryexport.get(0).get("Extra label (5)");
        System.out.println("Competitor, Consent by, Date of Birth, "+label1+", "+label2+", "+label3+", "+label4+", "+label5+ ", Emergency name, Emergency number, Emergency / medical details");

        entryexport.stream().filter((map)-> map.get("Event").equals("Club Dinghy Racing 2022")
                && map.get("Type").equals("Entry - Under 18/Vulnerable Adult")
            ).forEach((map)-> {
            System.out.print(map.get("Made for"));
            System.out.print(", " +map.get("Made by"));
            System.out.print(", " +map.get("Date of birth"));
            System.out.print(", " +map.get("Extra info (1)"));
            System.out.print(", " +map.get("Extra info (2)"));
            System.out.print(", " +map.get("Extra info (3)"));
            System.out.print(", " +map.get("Extra info (4)"));
            System.out.print(", " +map.get("Extra info (5)"));
            System.out.print(", " +map.get("Emergency name"));
            System.out.print(", " +map.get("Emergency number"));
            System.out.print(", " +map.get("Emergency / medical details"));
            System.out.println();
        });
        
    }
    
    public void checkU18EntryReport() {

        entryexport.stream().filter((map)-> map.get("Event").equals("Club Dinghy Racing 2022")
               && map.get("Type").equals("Entry - Under 18/Vulnerable Adult")
        ).forEach((map)-> {
           boolean madeforEqualsMadeby = map.get("Made for").equals(map.get("Made by"));
           boolean imageconsentcheck = map.get("Extra info (3)").equals("Yes") || map.get("Extra info (3)").equals("No");
           boolean dataconsentcheck = map.get("Extra info (4)").equals("Yes");
           boolean declarationcheck = map.get("Extra info (5)").equals("Yes");
           boolean hasEmergencyName = !map.get("Emergency name").isBlank();
           boolean hasEmergencyNumber = !map.get("Emergency number").isBlank();
           if (!(madeforEqualsMadeby &&imageconsentcheck && dataconsentcheck && declarationcheck && hasEmergencyName && hasEmergencyNumber)) {
               System.out.println("Validation Failure: Ticket ID="+map.get("Ticket ID")+"; Made by="+map.get("Made by")+";");
               if (!madeforEqualsMadeby) System.out.println("        Made for does not equal Made by");
               if (!imageconsentcheck) System.out.println("        Image consent not defined (not Yes or No)");
               if (!dataconsentcheck) System.out.println("        Data consent not Yes");
               if (!declarationcheck) System.out.println("        Declaration not Yes");
               if (!hasEmergencyName) System.out.println("        Emergency Contact Name is empty");
               if (!hasEmergencyNumber) System.out.println("        Emergency Contact Number is empty");
           }
        });
        
    }
}
