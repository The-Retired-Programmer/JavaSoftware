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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private Properties systemproperties;
    private Properties envproperties;
    private Properties userproperties;
    private Properties argproperties;

    private File downloaddir;
    private File workingdir;
    private File outputdir;
    private File definitionfile;

    public void loadconfiguration(String args[]) throws IOException {
        getSystemConfig();
        getUserConfig();
        parseArgs(args);
        getEnvConfig();
        //
        downloaddir = findDir("downloaddir", "Downloads");
        workingdir = findDir("workingdir", systemproperties.getProperty("user.dir"));
        outputdir = findOutputDir();
        definitionfile = findDefinitionFile();
    }

    public File findDir(String propertyname, String defaultvalue) throws IOException {
        String dir = mergeProperty(propertyname);
        if (dir == null) {
            dir = defaultvalue;
        }
        File f = new File(dir);
        if (!f.isAbsolute()) {
            f = new File(systemproperties.getProperty("user.home"), dir);
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("Failed to create a new file - " + f.getCanonicalPath());
            }
        }
        if (!f.isDirectory()) {
            throw new IOException(propertyname + " does not evauate to a file system directory");
        }
        return f;
    }

    public File findOutputDir() throws IOException {
        String dir = mergeProperty("outputdir");
        if (dir == null) {
            return getWorkingDir();
        }
        File f = new File(dir);
        if (!f.isAbsolute()) {
            f = new File(getWorkingDir(), dir);
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("Failed to create a new file - " + f.getCanonicalPath());
            }
        }
        if (!f.isDirectory()) {
            throw new IOException("Output directory does not evauate to a file system directory");
        }
        return f;
    }

    public File findDefinitionFile() throws IOException {
        String file = argproperties.getProperty("definitionfile");
        if (file == null) {
            throw new IOException("definitionfile parameter missing");
        }
        File f = new File(getWorkingDir(), file);
        if (!f.exists()) {
            throw new IOException("Definition file does not evauate to a file system file");
        }
        if (!f.canRead()) {
            throw new IOException("Definition file is not readable");
        }
        return f;
    }

    private String mergeProperty(String key) {
        return argproperties.getProperty(key,
                userproperties.getProperty(key,
                        envproperties.getProperty(key)));
    }

    public File getDownloadDir() {
        return downloaddir;
    }

    public File getWorkingDir() {
        return workingdir;
    }

    public File getOutputDir() {
        return outputdir;
    }

    public File getDefinitionFile() {
        return definitionfile;
    }

    private void dumpargs() {
        System.out.println("SYSTEM PROPERTIES");
        systemproperties.list(System.out);
        System.out.println("ENVIRONMENT PROPERTIES");
        envproperties.list(System.out);
        System.out.println("USER PROPERTIES");
        userproperties.list(System.out);
        System.out.println("COMMAND LINE PROPERTIES");
        argproperties.list(System.out);
    }

    private void getSystemConfig() {
        systemproperties = System.getProperties();
    }

    private void getEnvConfig() {
        envproperties = new Properties();
        //System.getenv();
        //RPTWTR_dd,wd,od
    }

    private void getUserConfig() throws IOException {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new IOException("Cannot identify user home directory");
        }
        userproperties = new Properties();
        File file_config = new File(userhome + "/.scmreportwriter.config");
        if (!file_config.canRead()) {
            return;
        }
        try ( FileReader in = new FileReader(file_config)) {
            userproperties.load(in);
        }
    }

    private void parseArgs(String args[]) throws IOException {
        ArgConfiguration argconfig = new ArgConfiguration();
        argproperties = argconfig.parseArgs(args);
    }

    private void saveUserConfig() throws IOException {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new IOException("Cannot identify user home directory");
        }
        File file_config = new File(userhome + "/.scmreportwriter.config");
        if (file_config.exists()) {
            file_config.delete();
        }
        try ( FileWriter out = new FileWriter(file_config)) {
            userproperties.store(out, "-- ReportWriter User Configuration --");
        }
    }
}
