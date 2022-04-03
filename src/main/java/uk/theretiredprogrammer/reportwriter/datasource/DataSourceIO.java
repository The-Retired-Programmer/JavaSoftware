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
package uk.theretiredprogrammer.reportwriter.datasource;

import uk.theretiredprogrammer.reportwriter.configuration.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import uk.theretiredprogrammer.reportwriter.RPTWTRException;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.StringExpression;

public class DataSourceIO {

    public static File getInputFile(ExpressionMap parameters) throws RPTWTRException, IOException {
        File f;
        switch (getRequiredString(parameters, "match")) {
            case "full" -> {
                f = new File(getRequiredString(parameters, "path"));
                if (f.isAbsolute()) {
                    return f;
                }
                return new File(Configuration.getDefault().getDownloadDir(), f.getPath());
            }
            case "latest_startswith" -> {
                String startswith = getRequiredString(parameters, "path");
                String[] files = Configuration.getDefault().getDownloadDir().list((file, filename) -> filename.startsWith(startswith));
                f = getResolvedLatestFile(files);
            }
            default ->
                throw new RPTWTRException("illegal parameter value for \"match\" parameter in data statement", parameters);
        }
        return f.isAbsolute() ? f : new File(Configuration.getDefault().getDownloadDir(), f.getPath());
    }

    public static File getOutputFile(String path) throws RPTWTRException {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(Configuration.getDefault().findOutputDir(), path);
    }

    private static File getResolvedLatestFile(String[] files) throws IOException {
        long mostrecenttime = 0;
        File mostrecentfile = null;
        for (String file : files) {
            File f = new File(file);
            if (!f.isAbsolute()) {
                f = new File(Configuration.getDefault().getDownloadDir(), f.getPath());
            }
            long time = Files.getLastModifiedTime(f.toPath()).to(TimeUnit.SECONDS);
            if (time > mostrecenttime) {
                mostrecenttime = time;
                mostrecentfile = f;
            }
        }
        return mostrecentfile;
    }

    private static String getRequiredString(ExpressionMap parameters, String key) throws RPTWTRException {
        StringExpression keyparameter = DataTypes.isStringExpression(parameters, key);
        if (keyparameter != null) {
            return keyparameter.evaluate(new DataSourceRecord());
        }
        throw new RPTWTRException(key + " parameter missing in data statement", parameters);
    }
}
