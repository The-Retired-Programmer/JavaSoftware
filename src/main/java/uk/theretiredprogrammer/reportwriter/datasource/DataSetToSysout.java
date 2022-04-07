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

import java.util.stream.Collectors;

public class DataSetToSysout {

    public static void display(String title, DataSetStream lines) {
        new DataSetToSysout().sysout(title, lines);
    }

    private void sysout(String title, DataSetStream lines) {
        if (title != null) {
            System.out.println();
            System.out.println(title);
            System.out.println();
        }
        lines.forEachOrdered(record -> {
            System.out.println(
                    record.getAll().stream()
                            .map(value -> value.replace("\"", "\"\""))
                            .collect(Collectors.joining("\",\"", "\"", "\""))
            );
        });
        System.out.println();
    }
}
