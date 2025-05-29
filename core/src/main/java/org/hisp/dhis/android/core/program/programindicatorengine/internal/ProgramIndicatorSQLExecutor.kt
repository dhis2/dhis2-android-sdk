/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.ProgramIndicatorEvaluatorHelper
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitorScope
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.parser.internal.expression.QueryMods
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.EnrollmentAlias
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.EventAlias
import org.hisp.dhis.android.core.program.programindicatorengine.internal.literal.ProgramIndicatorSQLLiteral
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.antlr.Parser
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramIndicatorSQLExecutor(
    private val constantStore: ConstantStore,
    private val dataElementStore: DataElementStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val databaseAdapter: DatabaseAdapter,
) {

    suspend fun getProgramIndicatorValue(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>,
        queryMods: QueryMods?,
    ): String? {
        val sqlQuery = getProgramIndicatorSQL(evaluationItem, metadata, queryMods)

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            c.getString(0)
        }
    }

    suspend fun getProgramIndicatorSQL(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>,
        queryMods: QueryMods?,
    ): String {
        val programIndicator = ProgramIndicatorEvaluatorHelper.getProgramIndicator(evaluationItem, metadata)
        val periodItems = evaluationItem.allDimensionItems.filterIsInstance<DimensionItem.PeriodItem>()
        val periods = AnalyticsEvaluatorHelper.getReportingPeriods(periodItems, metadata, queryMods)

        if (programIndicator.expression() == null) {
            throw IllegalArgumentException("Program Indicator ${programIndicator.uid()} has empty expression.")
        }

        val targetTable = when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                "${EventTableInfo.TABLE_INFO.name()} as $EventAlias"
            AnalyticsType.ENROLLMENT, null ->
                "${EnrollmentTableInfo.TABLE_INFO.name()} as $EnrollmentAlias"
        }

        val contextWhereClause = when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                ProgramIndicatorEvaluatorHelper.getEventWhereClause(
                    programIndicator,
                    evaluationItem,
                    metadata,
                    queryMods,
                )
            AnalyticsType.ENROLLMENT, null ->
                ProgramIndicatorEvaluatorHelper.getEnrollmentWhereClause(
                    programIndicator,
                    evaluationItem,
                    metadata,
                    queryMods,
                )
        }

        val context = ProgramIndicatorSQLContext(
            programIndicator = programIndicator,
            periods = periods,
        )

        val collector = ProgramIndicatorItemIdsCollector()
        Parser.listen(programIndicator.expression(), collector)

        val sqlVisitor = newVisitor(ParserUtils.ITEM_GET_SQL, context)
        sqlVisitor.itemIds = collector.itemIds.toMutableSet()
        sqlVisitor.setExpressionLiteral(ProgramIndicatorSQLLiteral())

        val aggregator = ProgramIndicatorEvaluatorHelper.getAggregator(evaluationItem, programIndicator, queryMods)
        val selectExpression = CommonParser.visit(programIndicator.expression(), sqlVisitor)

        // TODO Include more cases that are expected to be evaluated as "1"
        val filterExpression = when (programIndicator.filter()?.trim()) {
            "true", "", null -> "1"
            else -> CommonParser.visit(programIndicator.filter(), sqlVisitor)
        }

        return "SELECT ${aggregator.sql}($selectExpression) " +
            "FROM $targetTable " +
            "WHERE ($filterExpression) " +
            "AND $contextWhereClause"
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
}
