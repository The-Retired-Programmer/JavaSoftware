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
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Properties;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;
import uk.theretiredprogrammer.reportwriter.RPTWTRRuntimeException;

public class Configuration {

    private static Configuration configuration;

    public static void create(String args[]) throws RPTWTRException {
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
    private File projectdir;
    private File outputdir;
    private File reportfile;

    private Configuration() {
    }

    @SuppressWarnings("UseSpecificCatch")
    private void loadconfiguration(String args[]) throws RPTWTRException {
        try {
            argconfiguration = new ArgConfiguration();
            argproperties = argconfiguration.parseArgs(args);
            if (argconfiguration.isVersionCmd()) {
                System.out.println("ReportWriter v1.0.1");
            }
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
            projectdir = findDir("projectdir", systemproperties.getProperty("user.dir"));
            outputdir = findOutputDir();
            reportfile = findReportFile();
            if (argconfiguration.isListCmd()) {
                System.out.println("\n Current Directory Parameters and Resulting Paths\n");
                System.out.println("downloadir is " + getPropertyValue("downloaddir") + " expands to " + downloaddir.getCanonicalPath());
                System.out.println("projectdir is " + getPropertyValue("projectdir") + " expands to " + projectdir.getCanonicalPath());
                System.out.println("outputdir is " + getPropertyValue("outputdir") + " expands to " + outputdir.getCanonicalPath());
            }
            if (argconfiguration.isSaveCmd()) {
                String dd = argproperties.getProperty("downloaddir");
                if (dd != null) {
                    userproperties.setProperty("downloaddir", dd);
                }
                String pd = argproperties.getProperty("projectdir");
                if (pd != null) {
                    userproperties.setProperty("projectdir", pd);
                }
                String od = argproperties.getProperty("outputdir");
                if (od != null) {
                    userproperties.setProperty("outputdir", od);
                }
                saveUserConfig();
            }
        } catch (Throwable t) {
            throw new RPTWTRException(t);
        }
    }

    private File findDir(String propertyname, String defaultvalue) {
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
                throw new RPTWTRRuntimeException("Failed to create a new file - " + dir);
            }
        }
        if (!f.isDirectory()) {
            throw new RPTWTRRuntimeException(propertyname + " does not exist");
        }
        return f;
    }

    private File findOutputDir() {
        String dir = getPropertyValue("outputdir");
        if (dir == null) {
            return getProjectDir();
        }
        File f = new File(dir);
        if (!f.isAbsolute()) {
            f = new File(getProjectDir(), dir);
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new RPTWTRRuntimeException("Failed to create a new directory - " + dir);
            }
        }
        if (!f.isDirectory()) {
            throw new RPTWTRRuntimeException("Output directory does not exist");
        }
        return f;
    }

    private File findReportFile() {
        String file = argconfiguration.getDefinitionFile();
        if (file == null) {
            return null;
        }
        File f = new File(getProjectDir(), file);
        if (!f.exists()) {
            throw new RPTWTRRuntimeException("Report file does not exist" + file);
        }
        if (!f.canRead()) {
            throw new RPTWTRRuntimeException("Report file is not readable; " + file);
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

    public File getProjectDir() {
        return projectdir;
    }

    public File getOutputDir() {
        return outputdir;
    }

    public File getReportFile() {
        return reportfile;
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
        String wd = envmap.get("RPTWTR_pd");
        if (wd != null) {
            envproperties.setProperty("projectdir", wd);
        }
        String od = envmap.get("RPTWTR_od");
        if (od != null) {
            envproperties.setProperty("outputdir", od);
        }
    }

    private static final String USERCONFIGFILE = ".reportwriter";

    @SuppressWarnings("UseSpecificCatch")
    private void getUserConfig() {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new RPTWTRRuntimeException("Cannot identify user home directory");
        }
        userproperties = new Properties();
        File file_config = new File(userhome, USERCONFIGFILE);
        if (!file_config.canRead()) {
            return;
        }
        try {
            try ( FileReader in = new FileReader(file_config)) {
                userproperties.load(in);
            }
        } catch (Throwable t) {
            throw new RPTWTRRuntimeException(t);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private void saveUserConfig() {
        try {
            File file_config = deleteUserConfigIfExists();
            try ( FileWriter out = new FileWriter(file_config)) {
                userproperties.store(out, "-- ReportWriter User Configuration --");
            }
        } catch (Throwable t) {
            throw new RPTWTRRuntimeException(t);
        }
    }

    private File deleteUserConfigIfExists() {
        String userhome = systemproperties.getProperty("user.home");
        if (userhome == null) {
            throw new RPTWTRRuntimeException("Cannot identify user home directory");
        }
        File file_config = new File(userhome, USERCONFIGFILE);
        if (file_config.exists()) {
            file_config.delete();
        }
        return file_config;
    }
}
