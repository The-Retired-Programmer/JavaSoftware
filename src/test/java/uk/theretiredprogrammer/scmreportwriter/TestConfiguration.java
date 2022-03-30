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

import uk.theretiredprogrammer.scmreportwriter.configuration.Configuration;
import java.io.IOException;

public class TestConfiguration {

    public static void create(String reportdefinition) throws IOException, RPTWTRException {
        create(reportdefinition, "<undefined>");
    }

    public static void create(String reportdefinition, String commandparameter) throws IOException, RPTWTRException {
        Configuration.create(
                new String[]{"-wd", "TESTRPTWTR", "-od", "output", "-dd", "TESTRPTWTR/Downloads", reportdefinition, commandparameter}
        );
    }
}
