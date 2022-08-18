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

import java.io.File;
import uk.theretiredprogrammer.reportwriter.configuration.Configuration;

public class App {

    public static void main(String args[]) {
        try {
            try {
                Configuration.create(args);
            } catch (RPTWTRException ex) {
                System.err.println("Configuration Failure: " + ex.getLocalizedMessage());
                System.exit(1);
            }
            File f = Configuration.getDefault().getReportFile();
            if (f == null) {
                System.exit(0);
            }
            @SuppressWarnings("UnusedAssignment")
            ReportWriter reportwriter = null;
            try {
                reportwriter = new ReportWriter(f);
            } catch (RPTWTRException ex) {
                System.err.println("Report Definition Failure: " + ex.getLocalizedMessage());
                System.exit(2);
            }
            try {
                reportwriter.loadDataFiles();
                reportwriter.createAllGeneratedFiles();
                reportwriter.createAllReports();
            } catch (RPTWTRException ex) {
                System.err.println("Report Execution Failure: " + ex.getLocalizedMessage());
                System.exit(3);
            }
        } catch (Throwable t) {
            System.err.println("Program Exception Caught: " + t.getLocalizedMessage());
            System.exit(8);
        }
    }
}
