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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;

public class Configuration {

    private static Configuration configuration;

    public static void create(String args[]) throws IOException, RPTWTRException {
        configuration = new Configuration();
        configuration.loadconfiguration(args);
    }

    public static Configuration getDefault() {
        return configuration;
    }

    private ArgConfiguration argconfiguration;

    private Properties systemproperties;
    private Properties envproperties;
    private Properties userproperties;
    private Properties argproperties;

    private Map<String, String> envmap;

    private File downloaddir;
    private File workingdir;
    private File outputdir;
    private File definitionfile;

    private Configuration() {
    }

    private void loadconfiguration(String args[]) throws IOException, RPTWTRException {
        argconfiguration = new ArgConfiguration();
        argproperties = argconfiguration.parseArgs(args);
        getSystemConfig();
        if (argconfiguration.isClearCmd()) {
            deleteUserConfigIfExists();
        }
        getUserConfig();
        getEnvConfig();
        if (argconfiguration.isDebugListCmd()) {
            dumpargs();
        }
        downloaddir = findDir("downloaddir", "Downloads");
        workingdir = findDir("workingdir", systemproperties.getProperty("user.dir"));
        outputdir = findOutputDir();
        definitionfile = findDefinitionFile();
        if (argconfiguration.isListCmd()) {
            System.out.println("\n Current Directory Parameters and Resulting Paths\n");
            System.out.println("downloadir is " + getPropertyValue("downloaddir") + " expands to " + downloaddir.getCanonicalPath());
            System.out.println("workingdir is " + getPropertyValue("workingdir") + " expands to " + workingdir.getCanonicalPath());
            System.out.println("outputdir is " + getPropertyValue("outputdir") + " expands to " + outputdir.getCanonicalPath());
        }
        if (argconfiguration.isSaveCmd()) {
            String dd = argproperties.getProperty("downloaddir");
            if (dd != null) {
                userproperties.setProperty("downloaddir", dd);
            }
            String wd = argproperties.getProperty("workingdir");
            if (wd != null) {
                userproperties.setProperty("workingdir", wd);
            }
            String od = argproperties.getProperty("outputdir");
            if (od != null) {
                userproperties.setProperty("outputdir", od);
            }
            saveUserConfig();
        }
    }

    public File findDir(String propertyname, String defaultvalue) throws RPTWTRException {
        String dir = getPropertyValue(propertyname);
        if (dir == null) {
            dir = defaultvalue;
        }
        File f = new File(dir);
        if (!f.isAbsolute()) {
            f = new File(systemproperties.getProperty("user.home"), dir);
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new RPTWTRException("Failed to create a new file - " + dir);
            }
        }
        if (!f.isDirectory()) {
            throw new RPTWTRException(propertyname + " does not evauate to a file system directory");
        }
        return f;
    }

    public File findOutputDir() throws RPTWTRException {
        String dir = getPropertyValue("outputdir");
        if (dir == null) {
            return getWorkingDir();
        }
        File f = new File(dir);
        if (!f.isAbsolute()) {
            f = new File(getWorkingDir(), dir);
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new RPTWTRException("Failed to create a new directory - " + dir);
            }
        }
        if (!f.isDirectory()) {
            throw new RPTWTRException("Output directory does not evauate to a file system directory");
        }
        return f;
    }

    public File findDefinitionFile() throws RPTWTRException {
        String file = argconfiguration.getDefinitionFile();
        if (file == null) {
            return null;
        }
        File f = new File(getWorkingDir(), file);
        if (!f.exists()) {
            throw new RPTWTRException("Definition file does not evauate to a file system file; " + file);
        }
        if (!f.canRead()) {
            throw new RPTWTRException("Definition file is not readable; " + file);
        }
        return f;
    }

    private String getPropertyValue(String key) {
        String value = argproperties.getProperty(key);
        if (value != null) {
            return value;
        }
        value = userproperties.getProperty(key);
        if (value != null) {
            return value;
        }
        return envproperties.getProperty(key);
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

    public String getSystemProperty(String key) {
        return systemproperties.getProperty(key);
    }

    public String getEnvironmentValue(String key) {
        return envmap.get(key);
    }

    public ArgConfiguration getArgConfiguration() {
        return argconfiguration;
    }

    private void dumpargs() {
        System.out.println("SYSTEM PROPERTIES");
        systemproperties.list(System.out);
        System.out.println("ENVIRONMENT MAP");
        envmap.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
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
        envmap = System.getenv();
        envproperties = new Properties();
        String dd = envmap.get("RPTWTR_dd");
        if (dd != null) {
            envproperties.setProperty("downloaddir", dd);
        }
        String wd = envmap.get("RPTWTR_wd");
        if (wd != null) {
            envproperties.setProperty("workingdir", wd);
        }
        String od = envmap.get("RPTWTR_od");
        if (od != null) {
            envproperties.setProperty("outputdir", od);
        }
    }

    private static final String USERCONFIGFILE = ".reportwriter";

    private void getUserConfig() throws RPTWTRException, FileNotFoundException, IOException {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new RPTWTRException("Cannot identify user home directory");
        }
        userproperties = new Properties();
        File file_config = new File(userhome, USERCONFIGFILE);
        if (!file_config.canRead()) {
            return;
        }
        try ( FileReader in = new FileReader(file_config)) {
            userproperties.load(in);
        }
    }

    private void saveUserConfig() throws RPTWTRException, IOException {
        File file_config = deleteUserConfigIfExists();
        try ( FileWriter out = new FileWriter(file_config)) {
            userproperties.store(out, "-- ReportWriter User Configuration --");
        }
    }

    private File deleteUserConfigIfExists() throws RPTWTRException {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new RPTWTRException("Cannot identify user home directory");
        }
        File file_config = new File(userhome, USERCONFIGFILE);
        if (file_config.exists()) {
            file_config.delete();
        }
        return file_config;
    }
}
