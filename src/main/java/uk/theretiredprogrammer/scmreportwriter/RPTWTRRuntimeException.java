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

import uk.theretiredprogrammer.scmreportwriter.language.S_Token;
import uk.theretiredprogrammer.scmreportwriter.language.TokenSourceLocator;

public class RPTWTRRuntimeException extends RuntimeException {

    public RPTWTRRuntimeException(String message) {
        super(message);
    }
    
    public RPTWTRRuntimeException(String message, S_Token token) {
        this(message,token.getLocator());
    }
    
    public RPTWTRRuntimeException(String message, TokenSourceLocator locator) {
        super(createSourceExtract(message, locator));
    }
    
    private static String createSourceExtract(String message, TokenSourceLocator locator) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append('\n');
        sb.append("at ");
        sb.append(locator.lineoffset+1);
        sb.append(':');
        sb.append(locator.charoffset + 1);
        sb.append('\n');
        sb.append(locator.source);
        sb.append('\n');
        int charoffset = locator.charoffset;
        while (charoffset-- > 0) {
            sb.append(' ');
        }
        int length = locator.length;
        while (length-- > 0) {
            sb.append('^');
        }
        sb.append('\n');
        return sb.toString();
    }
}
