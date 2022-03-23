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


public class TestConfiguration extends Configuration {
    
    public TestConfiguration(String reportdefinition) throws IOException, ConfigurationException {
        this(reportdefinition,"<undefined>");
    }
    
    public TestConfiguration(String reportdefinition, String commandparameter) throws IOException, ConfigurationException {
        super();
        this.loadconfiguration(new String[] {"-wd", "TESTRPTWTR", "-od", "output", "-dd",  "TESTRPTWTR/Downloads", "-cp",  commandparameter, reportdefinition  });
    }
    
}
