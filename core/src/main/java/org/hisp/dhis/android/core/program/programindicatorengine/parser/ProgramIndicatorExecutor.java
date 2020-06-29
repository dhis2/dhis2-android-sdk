/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program.programindicatorengine.parser;


import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.parser.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.expression.ExpressionItemMethod;
import org.hisp.dhis.android.core.parser.expression.ParserUtils;
import org.hisp.dhis.antlr.AntlrParserUtils;
import org.hisp.dhis.antlr.Parser;

import java.util.Map;

public class ProgramIndicatorExecutor {

    private Map<String, Constant> constantMap;
    private ProgramIndicatorContext programIndicatorContext;

    ProgramIndicatorExecutor(Map<String, Constant> constantMap,
                             ProgramIndicatorContext programIndicatorContext) {
        this.constantMap = constantMap;
        this.programIndicatorContext = programIndicatorContext;
    }

    public String getProgramIndicatorValue(String expression) {
        CommonExpressionVisitor visitor = newVisitor(ParserUtils.ITEM_EVALUATE);

        Object result = Parser.visit(expression, visitor);

        return AntlrParserUtils.castString(result);
    }

    public int getValueCount(String expression) {
        return getCountVisitor(expression).getItemValuesFound();
    }

    public int getZeroPosValueCount(String expression) {
        return getCountVisitor(expression).getItemZeroPosValuesFound();
    }

    private CommonExpressionVisitor getCountVisitor(String expression) {
        CommonExpressionVisitor visitor = newVisitor(ParserUtils.ITEM_VALUE_COUNT);

        Parser.visit(expression, visitor);

        return visitor;
    }

    private CommonExpressionVisitor newVisitor(ExpressionItemMethod itemMethod) {
        return CommonExpressionVisitor.newBuilder()
                .withItemMap(ProgramIndicatorParserUtils.PROGRAM_INDICATOR_EXPRESSION_ITEMS)
                .withItemMethod(itemMethod)
                .withConstantMap(constantMap)
                .withProgramIndicatorContext(programIndicatorContext)
                .withProgramIndicatorExecutor(this)
                .buildForProgramIndicator();
    }
}