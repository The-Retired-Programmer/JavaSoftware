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
package uk.theretiredprogrammer.reportwriter.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import uk.theretiredprogrammer.reportwriter.RPTWTRRuntimeException;

public class ArgConfiguration {

    private String downloaddir = null;
    private String workingdir = null;
    private String outputdir = null;
    private String definitionfile = null;
    private final List<String> commandparameters = new ArrayList<>();
    private boolean save = false;
    private boolean clear = false;
    private boolean list = false;
    private boolean debuglist = false;

    public Properties parseArgs(String[] args) {
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
        return p;
    }

    public String getDefinitionFile() {
        return definitionfile;
    }

    public String getCommandParameter(int index) {
        if (index < 1 || index > commandparameters.size()) {
            return null;
        }
        return commandparameters.get(index - 1);
    }

    public boolean isClearCmd() {
        return clear;
    }

    public boolean isSaveCmd() {
        return save;
    }

    public boolean isListCmd() {
        return list;
    }

    public boolean isDebugListCmd() {
        return debuglist;
    }

    private void extractArgCommands(String[] args) {
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
                case "--save" ->
                    save = true;
                case "--clear" ->
                    clear = true;
                case "--listuserconfig" ->
                    list = true;
                case "--debuglist" ->
                    debuglist = true;
                case "-dd" ->
                    downloaddir = argrdr.next();
                case "-wd" ->
                    workingdir = argrdr.next();
                case "-od" ->
                    outputdir = argrdr.next();
                case "-s" ->
                    save = true;
                case "-c" ->
                    clear = true;
                case "-l" ->
                    list = true;
                case "-dl" ->
                    debuglist = true;
                default -> {
                    definitionfile = p1;
                    commandparameters.clear();
                    while (argrdr.more()) {
                        commandparameters.add(argrdr.next());
                    }
                }
            }
        }
    }

    private class ArgReader {

        private final String[] args;
        private int index;

        public ArgReader(String[] args) {
            this.args = args;
            index = 0;
        }

        public String next() {
            if (index >= args.length) {
                throw new RPTWTRRuntimeException("Command Line: bad structure - missing arguements");
            }
            return args[index++];
        }

        public boolean more() {
            return index < args.length;
        }
    }
}
