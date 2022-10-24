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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementSQLEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.EventDataItemSQLEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.ProgramIndicatorSQLEvaluator
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

@Suppress("LongParameterList")
internal class IndicatorSQLEngine @Inject constructor(
    private val indicatorTypeStore: IdentifiableObjectStore<IndicatorType>,
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
    private val categoryOptionComboStore: CategoryOptionComboStore,
    private val programStore: ProgramStoreInterface,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val dataElementEvaluator: DataElementSQLEvaluator,
    private val programIndicatorEvaluator: ProgramIndicatorSQLEvaluator,
    private val eventDataItemEvaluator: EventDataItemSQLEvaluator,
    private val constantStore: IdentifiableObjectStore<Constant>,
    private val databaseAdapter: DatabaseAdapter
) {

    fun evaluateIndicator(
        indicator: Indicator,
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): String? {
        val sqlQuery = getSql(indicator, contextEvaluationItem, contextMetadata)

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            val valueStr = c.getString(0)
            IndicatorParserUtils.roundValue(valueStr, indicator.decimals())
        }
    }

    fun getSql(
        indicator: Indicator,
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): String {
        val indicatorContext = IndicatorContext(
            dataElementStore = dataElementStore,
            trackedEntityAttributeStore = trackedEntityAttributeStore,
            categoryOptionComboStore = categoryOptionComboStore,
            programStore = programStore,
            programIndicatorRepository = programIndicatorRepository,
            dataElementEvaluator = dataElementEvaluator,
            programIndicatorEvaluator = programIndicatorEvaluator,
            eventDataItemEvaluator = eventDataItemEvaluator,
            evaluationItem = contextEvaluationItem,
            contextMetadata = contextMetadata
        )

        val indicatorType = indicator.indicatorType()?.let {
            indicatorTypeStore.selectByUid(it.uid())
        }

        val visitor = newVisitor(indicatorContext)

        IndicatorParserUtils.getDays(contextEvaluationItem, contextMetadata)?.let {
            visitor.days = it.toDouble()
        }

        val numerator = evaluate(indicator.numerator()!!, visitor)
        val denominator = evaluate(indicator.denominator()!!, visitor)
        val factor = indicatorType?.factor() ?: 1

        return "SELECT $factor * ($numerator) / ($denominator)"
    }

    private fun evaluate(expression: String, visitor: CommonExpressionVisitor): Any? {
        return CommonParser.visit(expression, visitor)
    }

    private val constantMap: Map<String, Constant>
        get() {
            val constants = constantStore.selectAll()
            return mapByUid(constants)
        }

    private fun newVisitor(indicatorContext: IndicatorContext): CommonExpressionVisitor {
        return CommonExpressionVisitor.newBuilder()
            .withItemMap(IndicatorParserUtils.INDICATOR_EXPRESSION_ITEMS)
            .withItemMethod(ParserUtils.ITEM_GET_SQL)
            .withConstantMap(constantMap)
            .withIndicatorContext(indicatorContext)
            .buildForAnalyticsIndicator()
    }
}
