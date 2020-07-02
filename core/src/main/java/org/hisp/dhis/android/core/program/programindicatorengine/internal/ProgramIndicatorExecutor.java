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

package org.hisp.dhis.android.core.program.programindicatorengine.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser;
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod;
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.antlr.AntlrParserUtils;

import java.util.Map;

public class ProgramIndicatorExecutor {

    private final Map<String, Constant> constantMap;
    private final ProgramIndicatorContext programIndicatorContext;
    private final IdentifiableObjectStore<DataElement> dataElementStore;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;

    ProgramIndicatorExecutor(Map<String, Constant> constantMap,
                             ProgramIndicatorContext programIndicatorContext,
                             IdentifiableObjectStore<DataElement> dataElementStore,
                             IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore) {
        this.constantMap = constantMap;
        this.programIndicatorContext = programIndicatorContext;
        this.dataElementStore = dataElementStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
    }

    public String getProgramIndicatorValue(String expression) {
        CommonExpressionVisitor visitor = newVisitor(ParserUtils.ITEM_EVALUATE);

        try {
            Object result = CommonParser.visit(expression, visitor);

            String resultStr = AntlrParserUtils.castString(result);

            if (ParserUtils.isNumeric(resultStr)) {
                return ParserUtils.fromDouble(Double.valueOf(resultStr));
            } else {
                return resultStr;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public int getValueCount(String expression) {
        return getCountVisitor(expression).getItemValuesFound();
    }

    public int getZeroPosValueCount(String expression) {
        return getCountVisitor(expression).getItemZeroPosValuesFound();
    }

    private CommonExpressionVisitor getCountVisitor(String expression) {
        CommonExpressionVisitor visitor = newVisitor(ParserUtils.ITEM_VALUE_COUNT);

        CommonParser.visit(expression, visitor);

        return visitor;
    }

    private CommonExpressionVisitor newVisitor(ExpressionItemMethod itemMethod) {
        return CommonExpressionVisitor.newBuilder()
                .withItemMap(ProgramIndicatorParserUtils.PROGRAM_INDICATOR_EXPRESSION_ITEMS)
                .withItemMethod(itemMethod)
                .withConstantMap(constantMap)
                .withProgramIndicatorContext(programIndicatorContext)
                .withProgramIndicatorExecutor(this)
                .withDataElementStore(dataElementStore)
                .withTrackedEntityAttributeStore(trackedEntityAttributeStore)
                .buildForProgramIndicator();
    }
}