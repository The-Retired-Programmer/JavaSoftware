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
package uk.theretiredprogrammer.scmreportwriter.reportdescriptor;

import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.expression.Expression;
import static uk.theretiredprogrammer.scmreportwriter.reportdescriptor.Lexer.OPCOUNT;

public class Parser {

    public Expression<String> parse(List<S_Token> tokens) {

    }

    private final int[] precedenceleftvalues = new int[]{
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0,
        0, 0, 0
    };

    private final int[] precedencerightvalues = new int[]{
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0,
        0, 0, 0
    };

    private boolean isOperator(S_Token left) {
        return left.operator.ordinal() < OPCOUNT;
    }

    private int precedenceleft(S_Token left) {
        return precedenceleftvalues[left.operator.ordinal()];

    }

    private int precedenceright(S_Token right) {
        return precedenceleftvalues[right.operator.ordinal()];
    }

}
