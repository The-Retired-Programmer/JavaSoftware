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
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;

// provide a data model which contains an individual csv file exported from SCM
//
public class DataSourceCSV extends DataSource {

    public DataSourceCSV(ExpressionMap parameters) throws IOException, InternalReportWriterException {
        String path = DataTypes.isStringLiteral(parameters,"path");
        File f = new File(path);
        try ( Reader rdr = new FileReader(f);  BufferedReader brdr = new BufferedReader(rdr)) {
            List<String> columnKeys = createTokenList(brdr.lines().findFirst().orElseThrow());
            for (String line : brdr.lines().toList()) {
                add(createRecord(columnKeys, createTokenList(line), line));
            }
        }
    }

    private DataSourceRecord createRecord(List<String> keys, List<String> values, String line) throws IOException {
        if (keys.size() != values.size()) {
            throw new IOException("Badly formatted CSV (columns count inconsistent): " + line);
        }
        DataSourceRecord record = new DataSourceRecord();
        for (String key : keys) {
            record.put(key, values.remove(0));
        }
        return record;
    }

    private enum State {
        STARTOFFIELD, INQUOTEDFIELD, INUNQUOTEDFIELD, AFTERQUOTEDFIELD
    }

    private static final char EOLCHAR = (char) 0;

    private State state;

    private List<String> tokenlist;

    private StringBuilder token;

    private List<String> createTokenList(String line) throws IOException {
        state = State.STARTOFFIELD;
        tokenlist = new ArrayList<>();
        token = new StringBuilder();
        try {
            for (char c : line.toCharArray()) {
                processNextChar(c);
            }
            processNextChar(EOLCHAR);
        } catch (IOException ex) {
            throw new IOException(ex.getMessage() + line);
        }
        return tokenlist;
    }

    private void processNextChar(char c) throws IOException {
        switch (state) {
            case STARTOFFIELD -> {
                switch (c) {
                    case EOLCHAR -> {
                        tokenlist.add(token.toString());
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
                    case EOLCHAR ->
                        throw new IOException("Badly formatted CSV (unterminated quoted value): ");
                    case '"' -> {
                        tokenlist.add(token.toString());
                        token = new StringBuilder();
                        state = State.AFTERQUOTEDFIELD;
                    }
                    default ->
                        token.append(c);
                }
            }

            case INUNQUOTEDFIELD -> {
                switch (c) {
                    case EOLCHAR ->
                        tokenlist.add(token.toString());
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
                    case EOLCHAR -> {
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
}
