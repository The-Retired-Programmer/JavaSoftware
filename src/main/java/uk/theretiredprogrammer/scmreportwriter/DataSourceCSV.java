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

//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalParserException;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;

public class DataSourceCSV extends DataSource {

    public static DataSource read(Configuration configuration, ExpressionMap parameters) throws IOException, InternalReportWriterException {
        return new DataSourceCSV().load(configuration, parameters);
    }

    public static void write(Configuration configuration, String path, List<List<String>> lines) throws IOException, InternalReportWriterException, InternalParserException, ConfigurationException {
        new DataSourceCSV().store(configuration, path, lines);
    }

    public static void sysout(String title, List<List<String>> lines) {
        new DataSourceCSV().sysoutlist(title, lines);
    }

    public DataSource load(Configuration configuration, ExpressionMap parameters) throws IOException, InternalReportWriterException {
        File f = getInputFile(configuration, parameters);
        try ( Reader rdr = new FileReader(f);  BufferedReader brdr = new BufferedReader(rdr)) {
            charsource = new CharacterSource(brdr.lines().toList());
            createDataSourceRecords(charsource);
        }
        return this;
    }

    public void sysoutlist(String title, List<List<String>> lines) {
        if (title != null) {
            System.out.println(title);
        }
        lines.stream().forEach((List<String> record) -> {
            System.out.println(
                    record.stream()
                            .map(field -> mapfield(field))
                            .collect(Collectors.joining("\",\"", "\"", "\""))
            );
        });
    }

    public void store(Configuration configuration, String path, List<List<String>> lines) throws IOException, ConfigurationException {
        File f = getOutputFile(configuration, path);
        try ( Writer wtr = new FileWriter(f);  PrintWriter pwtr = new PrintWriter(wtr)) {
            lines.stream().forEach((List<String> record) -> {
                pwtr.println(
                        record.stream()
                                .map(field -> mapfield(field))
                                .collect(Collectors.joining("\",\"", "\"", "\""))
                );
            });
        }
    }

    private String mapfield(String field) {
        return field.replace("\"","\"\"");
    }

    // the READ section
    private CharacterSource charsource;
    private List<String> columnKeys;

    private enum State {
        STARTOFFIELD, INQUOTEDFIELD, INUNQUOTEDFIELD, AFTERQUOTEDFIELD
    }
    private State state;
    private List<String> tokenlist;
    private StringBuilder token;
    private boolean inData = false;

    private void createDataSourceRecords(CharacterSource charsource) throws IOException {
        state = State.STARTOFFIELD;
        tokenlist = new ArrayList<>();
        token = new StringBuilder();
        try {
            do {
                processNextChar(charsource);
            } while (!charsource.isEOF());

        } catch (IOException ex) {
            throw new IOException(ex.getMessage() + charsource.getCurrentLine());
        }
    }

    private void processlineoftokens() throws IOException {
        if (inData) {
            if (columnKeys.size() != tokenlist.size()) {
                throw new IOException("Badly formatted CSV (columns count inconsistent): " + charsource.getCurrentLine());
            }
            DataSourceRecord record = new DataSourceRecord();
            for (String key : columnKeys) {
                record.put(key, tokenlist.remove(0));
            }
            add(record);
        } else {
            columnKeys = tokenlist;
            inData = true;
        }
        tokenlist = new ArrayList<>();
        state = State.STARTOFFIELD;
        token = new StringBuilder();
    }

    private void processNextChar(CharacterSource charsource) throws IOException {
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
                        throw new IOException("Badly formatted CSV (extra text after closing quote): ");
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
