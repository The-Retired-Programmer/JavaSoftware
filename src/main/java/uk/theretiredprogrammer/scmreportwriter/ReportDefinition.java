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

import uk.theretiredprogrammer.scmreportwriter.configuration.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionList;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.DefinitionSource;
import uk.theretiredprogrammer.scmreportwriter.language.Lexer;
import uk.theretiredprogrammer.scmreportwriter.language.Operand;
import uk.theretiredprogrammer.scmreportwriter.language.Parser;

public class ReportDefinition {

    private ExpressionMap definition;

    private DefinitionSource source;

    public boolean buildReportDefinition() throws FileNotFoundException, IOException, RPTWTRException {
        File deffile = Configuration.getDefault().getDefinitionFile();
        if (deffile == null) {
            return false;
        }
        try ( BufferedReader brdr = new BufferedReader(new FileReader(deffile))) {
            Language scmlanguage = new SCM_ExpressionLanguage();
            source = new DefinitionSource(brdr.lines());
            Lexer lexer = new Lexer(source, scmlanguage);
            Parser parser = new Parser(source, scmlanguage);
            lexer.lex();
            definition = DataTypes.isExpressionMap(parser.parse());
        }
        return true;
    }

    protected DefinitionSource getDefinitionSource() {
        return source;
    }

    protected Operand getPropertyExpression(String propertyname) {
        return definition.get(propertyname);
    }

    public ExpressionMap getDatadefinitions() throws RPTWTRException {
        return DataTypes.isExpressionMap(definition, "data");
    }

    public ExpressionMap getGeneratedFilesdefinitions() throws RPTWTRException {
        return DataTypes.isExpressionMap(definition, "generated_data");
    }

    public ExpressionList getReportdefinitions() throws RPTWTRException {
        return DataTypes.isExpressionList(definition, "reports");
    }
}
