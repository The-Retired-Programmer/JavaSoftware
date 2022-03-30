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
package uk.theretiredprogrammer.scmreportwriter.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.scmreportwriter.RPTWTRException;

public class DefinitionSource {

    private final List<String> sourcelines;
    private final int linecount;
    private int charoffset;
    private int markoffset;
    private int lineoffset;
    private String line;
    private int linelength;
    private final List<S_Token> s_tokens;
    private boolean lastwasoperand;

    public DefinitionSource(String input) {
        this(Arrays.asList(input.split("\\r?\\n")));
    }

    public DefinitionSource(Stream<String> lines) {
        this(lines.collect(Collectors.toList()));
    }

    private DefinitionSource(List<String> sourcelines) {
        this.sourcelines = sourcelines;
        this.linecount = sourcelines.size();
        markoffset = 0;
        lineoffset = -1;
        move2nextline();
        s_tokens = new ArrayList<>();
        lastwasoperand = false;
    }

    private boolean move2nextline() {
        if (lineoffset < linecount - 1) {
            line = sourcelines.get(++lineoffset);
            linelength = line.length();
            charoffset = 0;
            return linelength == 0 ? move2nextline() : true;
        } else {
            return false;
        }
    }

    public void mark() {
        markoffset = charoffset - 1;
    }

    public char getchar() {
        if (charoffset < linelength) {
            char c = line.charAt(charoffset++);
            return c;
        } else {
            if (charoffset == linelength) {
                charoffset++;
                return '\n';
            } else {
                return move2nextline() ? line.charAt(charoffset++) : '\0';
            }
        }
    }

    public void add(S_Token stoken) throws RPTWTRException {
        stoken.setLocator(getTokenLocator());
        if (stoken instanceof Operand && lastwasoperand) {
            throw new RPTWTRException("Consecutive Terminal symbols seen", stoken);
        }
        lastwasoperand = stoken instanceof Operand;
        markoffset = charoffset;
        s_tokens.add(stoken);
    }

    private TokenSourceLocator getTokenLocator() {
        return new TokenSourceLocator(line, lineoffset, markoffset, charoffset - markoffset - 1);
    }

    public List<S_Token> getS_Tokens() {
        return s_tokens;
    }
}
