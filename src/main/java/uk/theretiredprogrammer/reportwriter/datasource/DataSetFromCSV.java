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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import uk.theretiredprogrammer.reportwriter.RPTWTRRuntimeException;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.StringExpression;

public class DataSetFromCSV {

    public static StoredDataSet create(String name, ExpressionMap parameters) {
        return new DataSetFromCSV().load(name, parameters);
    }

    private StoredDataSet dataset;

    private StoredDataSet load(String name, ExpressionMap parameters) {
        try {
            File f = getInputFile(parameters);
            if (Configuration.getDefault().getArgConfiguration().isListCmd()) {
                System.out.println("loading " + name + " from " + f.getCanonicalPath());
            }
            try ( Reader rdr = new FileReader(f);  BufferedReader brdr = new BufferedReader(rdr)) {
                charsource = new CharacterSource(brdr.lines().toList());
                createDataSourceRecords(charsource);
            }
            return dataset;
        } catch (IOException t) {
            throw new RPTWTRRuntimeException(t);
        }
    }

    private File getInputFile(ExpressionMap parameters) throws IOException {
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
                throw new RPTWTRRuntimeException("illegal parameter value for \"match\" parameter in data statement", parameters);
        }
        return f.isAbsolute() ? f : new File(Configuration.getDefault().getDownloadDir(), f.getPath());
    }

    private File getResolvedLatestFile(String[] files) throws IOException {
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

    private String getRequiredString(ExpressionMap parameters, String key) {
        StringExpression keyparameter = DataTypes.isStringExpression(parameters, key);
        if (keyparameter != null) {
            return keyparameter.evaluate(DataRecord.EMPTY);
        }
        throw new RPTWTRRuntimeException(key + " parameter missing in data statement", parameters);
    }

    private CharacterSource charsource;

    private enum State {
        STARTOFFIELD, INQUOTEDFIELD, INUNQUOTEDFIELD, AFTERQUOTEDFIELD
    }
    private State state;
    private List<String> tokenlist;
    private StringBuilder token;
    private boolean inData = false;

    private void createDataSourceRecords(CharacterSource charsource) {
        state = State.STARTOFFIELD;
        tokenlist = new ArrayList<>();
        token = new StringBuilder();
        do {
            processNextChar(charsource);
        } while (!charsource.isEOF());
    }

    private void processlineoftokens() {
        if (inData) {
            dataset.insertDataRecord(tokenlist.stream());
        } else {
            dataset = new StoredDataSet(tokenlist);
            inData = true;
        }
        tokenlist = new ArrayList<>();
        state = State.STARTOFFIELD;
        token = new StringBuilder();
    }

    private void processNextChar(CharacterSource charsource) {
        char c = charsource.getChar();
        switch (state) {
            case STARTOFFIELD -> {
                switch (c) {
                    case '\n' -> {
                        tokenlist.add(token.toString());
                        processlineoftokens();
                    }
                    case ',' -> {
                        tokenlist.add(token.toString());
                        token = new StringBuilder();
                    }
                    case ' ' -> {
                    }
                    case '"' ->
                        state = State.INQUOTEDFIELD;
                    default -> {
                        token.append(c);
                        state = State.INUNQUOTEDFIELD;
                    }
                }
            }
            case INQUOTEDFIELD -> {
                switch (c) {
                    case '\n' ->
                        token.append('\n'); // insert as field content
                    case '"' -> {
                        if (charsource.peekChar() == '"') {
                            token.append(c);
                            charsource.getChar();
                        } else {
                            tokenlist.add(token.toString());
                            token = new StringBuilder();
                            state = State.AFTERQUOTEDFIELD;
                        }
                    }
                    default ->
                        token.append(c);
                }
            }

            case INUNQUOTEDFIELD -> {
                switch (c) {
                    case '\n' -> {
                        tokenlist.add(token.toString());
                        processlineoftokens();
                    }
                    case ',' -> {
                        state = State.STARTOFFIELD;
                        tokenlist.add(token.toString());
                        token = new StringBuilder();
                    }
                    default ->
                        token.append(c);
                }
            }

            case AFTERQUOTEDFIELD -> {
                switch (c) {
                    case '\n' -> {
                        processlineoftokens();
                    }
                    case ',' ->
                        state = State.STARTOFFIELD;
                    case ' ' -> {
                    }
                    default ->
                        throw new RPTWTRRuntimeException("Badly formatted CSV (extra text after closing quote): ");
                }
            }
        }
    }

    private class CharacterSource {

        private final List<String> lines;
        private int linesindex;

        private char[] currentline;
        private int characteroffset;

        private boolean atEOF = false;

        public CharacterSource(List<String> lines) {
            this.lines = lines;
            this.linesindex = 0;
            getnextline();
        }

        private void getnextline() {
            if (linesindex < lines.size()) {
                currentline = lines.get(linesindex++).toCharArray();
                characteroffset = 0;
            } else {
                atEOF = true;
            }
        }

        public char getChar() {
            if (characteroffset < currentline.length) {
                return currentline[characteroffset++];
            }
            getnextline();
            return '\n';
        }

        public char peekChar() {
            if (characteroffset < currentline.length) {
                return currentline[characteroffset];
            }
            return '\n';
        }

        public String getCurrentLine() {
            return lines.get(linesindex - 1);
        }

        public boolean isEOF() {
            return atEOF;
        }
    }
}
