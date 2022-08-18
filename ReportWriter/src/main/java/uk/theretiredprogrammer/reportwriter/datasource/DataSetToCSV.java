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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.reportwriter.configuration.Configuration;

public class DataSetToCSV {

    public static void save(String path, DataSetStream lines)  {
        new DataSetToCSV().store(path, lines);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void store(String path, DataSetStream lines) {
        try {
            File f = new File(path);
            f = f.isAbsolute() ? f : new File(Configuration.getDefault().getOutputDir(), path);
            try ( Writer wtr = new FileWriter(f);  PrintWriter pwtr = new PrintWriter(wtr)) {
                lines.getStream().forEachOrdered(record -> {
                    pwtr.println(
                            record.getAll().stream()
                                    .map(value -> value.replace("\"", "\"\""))
                                    .collect(Collectors.joining("\",\"", "\"", "\""))
                    );
                });
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
