/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.upgraders;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import uk.theretiredprogrammer.sketch.core.control.ParseFailure;

public class ConfigFileController {

    private final static int CURRENTVERSION = 2;
    private JsonObject parsedjson;
    int fromversion = 0;

    public ConfigFileController(Path path) throws IOException {
        try ( JsonReader rdr = Json.createReader(Files.newInputStream(path))) {
            parsedjson = rdr.readObject();
        }
    }

    public ConfigFileController(InputStream is) {
        try ( JsonReader rdr = Json.createReader(is)) {
            parsedjson = rdr.readObject();
        }
    }

    public JsonObject getParsedConfigFile() {
        return parsedjson;
    }

    public boolean needsUpgrade() {
        String type = parsedjson.getString("type", "NOTPRESENT");
        if (type.startsWith("sketch-v")) {
            fromversion = Integer.parseInt(type.substring(8));
        } else {
            if (parsedjson.getJsonObject("SAILING AREA") == null) {
                throw new ParseFailure("Probably not a Sketch file");
            }
        }
        return fromversion < CURRENTVERSION;
    }

    public void upgrade() {
        int fromv = fromversion;
        while (fromv < CURRENTVERSION) {
            Upgrader upgrader = UpgraderFactory.createUpgrader(fromv);
            parsedjson = upgrader.upgrade(parsedjson);
            fromv++;
        }
    }

    public void rewriteFile(Path path) throws IOException {
        if (path != null) {
            Files.move(path, path.resolveSibling(path.getFileName() + ".v" + fromversion));
            try ( JsonWriter jsonWriter = Json.createWriter(Files.newOutputStream(path))) {
                jsonWriter.write(parsedjson);
            }
        }
    }
}
