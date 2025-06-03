/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListContext
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.internal.ConstantStoreImpl
import org.hisp.dhis.android.core.dataelement.internal.DataElementStoreImpl
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitorScope
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorItemIdsCollector
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorParserUtils
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLContext
import org.hisp.dhis.android.core.program.programindicatorengine.internal.literal.ProgramIndicatorSQLLiteral
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStoreImpl
import org.hisp.dhis.antlr.Parser

internal class ProgramIndicatorEvaluator(
    private val item: TrackerLineListItem.ProgramIndicator,
    private val context: TrackerLineListContext,
) : TrackerLineListEvaluator() {

    private val constantStore = ConstantStoreImpl(context.databaseAdapter)
    private val dataElementStore = DataElementStoreImpl(context.databaseAdapter)
    private val trackedEntityAttributeStore = TrackedEntityAttributeStoreImpl(context.databaseAdapter)

    override suspend fun getCommonSelectSQL(): String {
        val programIndicator = getProgramIndicator()

        val context = ProgramIndicatorSQLContext(
            programIndicator = programIndicator,
            periods = emptyList(),
        )

        val collector = ProgramIndicatorItemIdsCollector()
        Parser.listen(programIndicator.expression(), collector)

        val sqlVisitor = newVisitor(ParserUtils.ITEM_GET_SQL, context)
        sqlVisitor.itemIds = collector.itemIds.toMutableSet()
        sqlVisitor.setExpressionLiteral(ProgramIndicatorSQLLiteral())

        val selectExpression = CommonParser.visit(programIndicator.expression(), sqlVisitor)

        val filterExpression = when (programIndicator.filter()?.trim()) {
            "true", "", null -> "1"
            else -> CommonParser.visit(programIndicator.filter(), sqlVisitor)
        }

        return "SELECT CASE ($filterExpression) " +
            "WHEN 1 THEN ($selectExpression) " +
            "ELSE '' " +
            "END"
    }

    override suspend fun getCommonWhereSQL(): String {
        return DataFilterHelper.getWhereClause(item.id, item.filters)
    }

    private fun getProgramIndicator(): ProgramIndicator {
        val programIndicatorMetadata = context.metadata[item.id]
            ?: throw AnalyticsException.InvalidProgramIndicator(item.id)

        return ((programIndicatorMetadata) as MetadataItem.ProgramIndicatorItem).item
    }

    private suspend fun constantMap(): Map<String, Constant> {
        val constants = constantStore.selectAll()
        return UidsHelper.mapByUid(constants)
    }

    private suspend fun newVisitor(
        itemMethod: ExpressionItemMethod,
        context: ProgramIndicatorSQLContext,
    ): CommonExpressionVisitor {
        return CommonExpressionVisitor(
            CommonExpressionVisitorScope.ProgramSQLIndicator(
                itemMap = ProgramIndicatorParserUtils.PROGRAM_INDICATOR_SQL_EXPRESSION_ITEMS,
                itemMethod = itemMethod,
                constantMap = constantMap(),
                programIndicatorSQLContext = context,
                dataElementStore = dataElementStore,
                trackedEntityAttributeStore = trackedEntityAttributeStore,
            ),
        )
    }

    override suspend fun getSelectSQLForTrackedEntityInstance(): String {
        throw AnalyticsException.InvalidArguments(
            "ProgramIndicator is not supported in TRACKED_ENTITY_INSTANCE output type",
        )
    }

    override suspend fun getWhereSQLForTrackedEntityInstance(): String {
        throw AnalyticsException.InvalidArguments(
            "ProgramIndicator is not supported in TRACKED_ENTITY_INSTANCE output type",
        )
    }
}
