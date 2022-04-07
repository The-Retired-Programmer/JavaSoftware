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
package uk.theretiredprogrammer.reportwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.ExpressionList;
import uk.theretiredprogrammer.reportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.reportwriter.language.Language;
import uk.theretiredprogrammer.reportwriter.language.DefinitionSource;
import uk.theretiredprogrammer.reportwriter.language.Lexer;
import uk.theretiredprogrammer.reportwriter.language.Parser;

public class ReportCompiler {

    private ExpressionMap compiledoutput;

    @SuppressWarnings("UseSpecificCatch")
    public ReportCompiler(File deffile) {
        try {
            try ( BufferedReader brdr = new BufferedReader(new FileReader(deffile))) {
                Language scmlanguage = new SCM_ExpressionLanguage();
                DefinitionSource source = new DefinitionSource(brdr.lines());
                Lexer lexer = new Lexer(source, scmlanguage);
                Parser parser = new Parser(source, scmlanguage);
                lexer.lex();
                compiledoutput = DataTypes.isExpressionMap(parser.parse());
            }
        } catch (Throwable t) {
            throw new RPTWTRRuntimeException(t);
        }
    }

    public ExpressionMap getCompiledOutputDataStatements() {
        return DataTypes.isExpressionMap(compiledoutput, "data");
    }

    public ExpressionMap getCompiledOutputGeneratedDataStatements() {
        return DataTypes.isExpressionMap(compiledoutput, "generated_data");
    }

    public ExpressionList getCompiledOutputReportsStatements() {
        return DataTypes.isExpressionList(compiledoutput, "reports");
    }
}
