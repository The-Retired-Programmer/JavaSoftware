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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;

public abstract class DataSource extends ArrayList<DataSourceRecord> {

    protected File getInputFile(Configuration configuration, ExpressionMap parameters) throws InternalReportWriterException, IOException {
        File f;
        switch (getRequiredString(configuration, parameters, "match")) {
            case "full" -> {
                f = new File(getRequiredString(configuration, parameters, "path"));
                if (f.isAbsolute()) {
                    return f;
                }
                return new File(configuration.getDownloadDir(), f.getPath());
            }
            case "latest_startswith" -> {
                String startswith = getRequiredString(configuration, parameters, "path");
                String[] files = configuration.getDownloadDir().list((file, filename) -> filename.startsWith(startswith));
                f = getResolvedLatestFile(configuration, files);
            }
            default ->
                throw new InternalReportWriterException(parameters, "illegal parameter value for \"match\" parameter in data statement");
        }
        return f.isAbsolute() ? f : new File(configuration.getDownloadDir(), f.getPath());
    }

    private File getResolvedLatestFile(Configuration configuration, String[] files) throws IOException {
        long mostrecenttime = 0;
        File mostrecentfile = null;
        for (String file : files) {
            File f = new File(file);
            if (!f.isAbsolute()){
                f =  new File(configuration.getDownloadDir(), f.getPath());
            }
            long time = Files.getLastModifiedTime(f.toPath()).to(TimeUnit.SECONDS);
            if (time > mostrecenttime) {
                mostrecenttime = time;
                mostrecentfile = f;
            }
        }
        return mostrecentfile;
    }

    private String getRequiredString(Configuration configuration, ExpressionMap parameters, String key) throws InternalReportWriterException {
        StringExpression keyparameter = DataTypes.isStringExpression(parameters, key);
        if (keyparameter != null) {
            return keyparameter.evaluate(configuration, new DataSourceRecord());
        }
        throw new InternalReportWriterException(parameters, key + " parameter missing in data statement");
    }

    protected File getOutputFile(Configuration configuration, String path) throws ConfigurationException {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(configuration.findOutputDir(), path);
    }
}
