/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.antlr.AntlrParserUtils
import org.hisp.dhis.antlr.ParserException

internal class ProgramIndicatorExecutor constructor(
    private val constantMap: Map<String, Constant>,
    private val programIndicatorContext: ProgramIndicatorContext,
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
    private val programStageStore: IdentifiableObjectStore<ProgramStage>
) {
    fun getProgramIndicatorValue(programIndicator: ProgramIndicator): String? {
        val visitor = newVisitor(ParserUtils.ITEM_EVALUATE)
        return if (getFilterValue(programIndicator, visitor)) {
            getProgramIndicatorExpressionValue(programIndicator.expression(), visitor)
        } else {
            null
        }
    }

    fun getProgramIndicatorExpressionValue(expression: String?): String? {
        val visitor = newVisitor(ParserUtils.ITEM_EVALUATE)
        return getProgramIndicatorExpressionValue(expression, visitor)
    }

    private fun getProgramIndicatorExpressionValue(expression: String?, visitor: CommonExpressionVisitor): String? {
        return try {
            val result = CommonParser.visit(expression, visitor)
            val resultStr = AntlrParserUtils.castString(result)
            if (ParserUtils.isNumeric(resultStr)) {
                ParserUtils.fromDouble(java.lang.Double.valueOf(resultStr))
            } else {
                resultStr
            }
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: ParserException) {
            null
        }
    }

    private fun getFilterValue(programIndicator: ProgramIndicator, visitor: CommonExpressionVisitor): Boolean {
        val filter = programIndicator.filter()

        return filter.isNullOrBlank() ||
            try {
                val result = CommonParser.visit(filter, visitor)
                result?.let {
                    AntlrParserUtils.castBoolean(result)
                } ?: false
            } catch (e: IllegalArgumentException) {
                false
            } catch (e: ParserException) {
                false
            }
    }

    fun getValueCount(expression: String): Int {
        return getCountVisitor(expression).state.itemValuesFound
    }

    fun getZeroPosValueCount(expression: String): Int {
        return getCountVisitor(expression).state.itemZeroPosValuesFound
    }

    private fun getCountVisitor(expression: String): CommonExpressionVisitor {
        val visitor = newVisitor(ParserUtils.ITEM_VALUE_COUNT)
        CommonParser.visit(expression, visitor)
        return visitor
    }

    private fun newVisitor(itemMethod: ExpressionItemMethod): CommonExpressionVisitor {
        return CommonExpressionVisitor.newBuilder()
            .withItemMap(ProgramIndicatorParserUtils.PROGRAM_INDICATOR_EXPRESSION_ITEMS)
            .withItemMethod(itemMethod)
            .withConstantMap(constantMap)
            .withProgramIndicatorContext(programIndicatorContext)
            .withProgramIndicatorExecutor(this)
            .withDataElementStore(dataElementStore)
            .withTrackedEntityAttributeStore(trackedEntityAttributeStore)
            .withProgramStageStore(programStageStore)
            .buildForProgramIndicator()
    }
}
