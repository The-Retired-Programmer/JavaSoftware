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
import uk.theretiredprogrammer.scmreportwriter.language.LexerException;
import uk.theretiredprogrammer.scmreportwriter.language.ParserException;

public class App {

    public static void main(String args[]) {

        Configuration configuration = new Configuration();
        try {
            configuration.loadconfiguration(args);
        } catch (ConfigurationException | IOException ex) {
            System.err.println("Configuration Failure: " + ex.getLocalizedMessage());
            System.exit(1);
        }

        @SuppressWarnings("UnusedAssignment")
        ReportWriter reportwriter=null;
        try {
            reportwriter = new ReportWriter(configuration);
        } catch (IOException | LexerException | ParserException | ReportWriterException ex) {
            System.err.println("Report Definition Failure: " + ex.getLocalizedMessage());
            System.exit(2);
        }
        try {
            reportwriter.loadDataFiles();
            reportwriter.createAllReports();
        } catch (ConfigurationException | ReportWriterException | IOException ex) {
            System.err.println("Report Execution Failure: " + ex.getLocalizedMessage());
            System.exit(3);
        }
    }
}
