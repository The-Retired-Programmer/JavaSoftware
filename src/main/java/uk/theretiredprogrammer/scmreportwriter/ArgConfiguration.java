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
    // --save -s; save the preceding directory parameters in user config file;
    // --clear -c; clear the user config file
    // --listuserconfig -l ; list the user config;
    //  
    //
    //  followed by filepaths
    //  reportdefinitionfile ; defines the rules for report creation <relative to working directory or absolute> if no file defined, then no report will be generated/compiled
    //
    private String downloaddir = null;
    private String workingdir = null;
    private String outputdir = null;
    private String definitionfile = null;
    private boolean save = false;
    private boolean clear = false;
    private boolean list = false;

    public Properties parseArgs(String[] args) throws ConfigurationException {
        extractArgCommands(args);
        Properties p = new Properties();
        if (downloaddir != null) {
            p.setProperty("downloaddir", downloaddir);
        }
        if (workingdir != null) {
            p.setProperty("workingdir", workingdir);
        }
        if (outputdir != null) {
            p.setProperty("outputdir", outputdir);
        }
        if (definitionfile != null) {
            p.setProperty("definitionfile", definitionfile);
        }
        if (save){
            p.setProperty("save", "save");
        }
        if (clear){
            p.setProperty("clear", "clear");
        }
        if (list){
            p.setProperty("list", "list");
        }
        return p;
    }

    private void extractArgCommands(String[] args) throws ConfigurationException {
        ArgReader argrdr = new ArgReader(args);
        while (argrdr.more()) {
            String p1 = argrdr.next();
            switch (p1) {
                case "--downloaddir" ->
                    downloaddir = argrdr.next();
                case "--workingdir" ->
                    workingdir = argrdr.next();
                case "--outputdir" ->
                    outputdir = argrdr.next();
                case "--save" -> save = true;
                case "--clear" -> clear = true;
                case "--listuserconfig" -> list = true;
                case "-dd" ->
                    downloaddir = argrdr.next();
                case "-wd" ->
                    workingdir = argrdr.next();
                case "-od" ->
                    outputdir = argrdr.next();
                case "-s" -> save = true;
                case "-c" -> clear = true;
                case "-l" -> list = true;
                default -> {
                    if (argrdr.more()) {
                        throw new ConfigurationException("Command Line: Definition file not the last arguement");
                    }
                    definitionfile = p1;
                }
            }
        }
        if (definitionfile == null) {
            throw new ConfigurationException("Command Line: no Definition file");
        }
    }

    private class ArgReader {

        private final String[] args;
        private int index;

        public ArgReader(String[] args) {
            this.args = args;
            index = 0;
        }

        public String next() throws ConfigurationException {
            if (index >= args.length) {
                throw new ConfigurationException("Command Line: bad structure - missing arguements");
            }
            return args[index++];
        }

        public boolean more() {
            return index < args.length;
        }
    }
}
