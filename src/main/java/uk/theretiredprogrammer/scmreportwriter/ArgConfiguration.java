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

import java.util.Properties;

public class ArgConfiguration {
    
    // command arguments:
    // --downloaddir -dd <path> ; define download directory <relative to user home or absolute> defaults to Downloads
    // --workingdir -wd <path> ; define working directory  <relative to user home or absolute> defaults to current working directory
    // --outputdir -od <path> ; define output directory for reports <relative to working directory or absolute> defaults to working directory
    //
    //x --noreport  -nr ; will read reportdefinition, but not run the reportwriter phase
    // --save -s; save the preceding directory parameters in user config file;
    // --clear -c; clear the user config file
    // --showuserconfig -su ; list the user config;
    //x --useenviroment -ue ; allow parameters to be set from user environment - using format RPTWTR_<shortform of command>
    //  
    //
    //  followed by filepaths
    //  reportdefinitionfile ; defines the rules for report creation <relative to working directory or absolute> if no file defined, then no report will be generated/compiled
    //
    
    private String downloaddir = null;
    private String workingdir = "RPTWTR";
    private String outputdir = "reports";
    private String definitionfile = "definition2.scm";
    
    private enum Command {
        DD, WD, OD, S, C, SU
    };
    private final String[] dirLong = new String[] {"downloaddir", "workingdir", "outputdir"};
    private final String[] dirShort = new String[] {"dd", "wd", "od"};
    private final Command[] dirCommand = new Command[] {Command.DD,Command.WD,Command.OD};
    private final String[] ctlLong = new String[] {"save", "clear", "showuserconfig"};
    private final String[] ctlShort = new String[] {"s", "c", "su"};
    private final Command[] ctlCommand = new Command[] {Command.S,Command.C,Command.SU};
    
    
    public Properties parseArgs(String[] args) {
        definitionfile = args[0];
        Properties p = new Properties();
        if (downloaddir != null) p.setProperty("downloaddir", downloaddir);
        if (workingdir != null) p.setProperty("workingdir", workingdir);
        if (outputdir != null) p.setProperty("outputdir", outputdir);
        if (definitionfile != null) p.setProperty("definitionfile", definitionfile); 
        return p;
    }
}
