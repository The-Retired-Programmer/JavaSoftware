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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.scmreportwriter.ReportWriterException;

public class LanguageSource {

    private final char[] source;
    private int charoffset;
    private int markoffset;
    private final List<Integer> lineoffsets;
    private final List<S_Token> s_tokens;
    private boolean lastwasoperand;
    
    public LanguageSource(String input) {
        this((input+"\n").toCharArray());
    }

    public LanguageSource(Stream<String> lines) {
        this(lines.collect(Collectors.joining("\n", "", "\n")).toCharArray());
    }
    
    private LanguageSource(char[] source) {
        this.source = source;
        charoffset = 0;
        markoffset = 0;
        lineoffsets = new ArrayList<>();
        lineoffsets.add(charoffset);
        s_tokens = new ArrayList<>();
        lastwasoperand = false;
    }

    public void mark() {
        markoffset = charoffset-1;
    }

    public char getchar() {
        char c = charoffset >= source.length ? Character.MIN_VALUE : source[charoffset++];
        if (c == '\n') {
            lineoffsets.add(charoffset);
        }
        return c;
    }

    public void add(S_Token stoken) throws InternalLexerException {
        stoken.setLocation(markoffset, charoffset-markoffset-1);
        if (stoken instanceof Operand && lastwasoperand) {
            throw new InternalLexerException(markoffset, charoffset-markoffset-1, "Consecutive Terminal symbols seen");
        }
        lastwasoperand = stoken instanceof Operand;
        markoffset = charoffset;
        s_tokens.add(stoken);
    }

    public List<S_Token> getS_Tokens() {
        return s_tokens;
    }

    public LexerException newLexerException(InternalLexerException ex) {
        return new LexerException(buildLocationandMessage(ex.getLocalizedMessage(), ex.getLocation(), ex.getLength()));
    }

    public InternalLexerException newInternalLexerException(String message) {
        return new InternalLexerException(markoffset, charoffset - markoffset -1, message);
    }
    
    public ReportWriterException newReportWriterException(InternalReportWriterException ex) {
        return new ReportWriterException(buildLocationandMessage(ex.getLocalizedMessage(), ex.getToken().getLocation(), ex.getToken().getLength()));
    }
    public ReportWriterException newReportWriterException(InternalParserException ex) {
        return new ReportWriterException(buildLocationandMessage(ex.getLocalizedMessage(), ex.getToken().getLocation(), ex.getToken().getLength()));
    } 
    
     public ParserException newParserException(InternalParserException ex) {
        return new ParserException(buildLocationandMessage(ex.getLocalizedMessage(), ex.getToken().getLocation(), ex.getToken().getLength()));
    }

    private String buildLocationandMessage(String message, int start, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append('\n');
        sb.append("at ");
        int line = getLineNumber(start);
        sb.append(line);
        sb.append(':');
        sb.append(start - lineoffsets.get(line - 1) + 1);
        sb.append('\n');
        int startline = lineoffsets.get(line - 1);
        @SuppressWarnings("null")
        int endline = line == lineoffsets.size() ? source.length : lineoffsets.get(line)-1;
        for (int i = startline; i < endline; i++) {
            sb.append(source[i]);
        }
        sb.append('\n');
        int offsetonline = start - startline;
        while (offsetonline-- > 0) {
            sb.append(' ');
        }
        while (length-- > 0) {
            sb.append('^');
        }
        sb.append('\n');
        return sb.toString();
    }

    private int getLineNumber(int charoffset) {
        int i = 0;
        while (i < lineoffsets.size() - 1) {
            if (charoffset >= lineoffsets.get(i) && charoffset < lineoffsets.get(i + 1)) {
                return i + 1;
            }
            i++;
        }
        return lineoffsets.size();
    }
}
