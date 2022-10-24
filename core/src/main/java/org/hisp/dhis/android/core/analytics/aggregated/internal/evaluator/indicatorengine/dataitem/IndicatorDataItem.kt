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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.AbsoluteDimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluator
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal interface IndicatorDataItem : ExpressionItem {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        return getEvaluationItem(ctx, visitor)?.let { evaluationItem ->
            getMetadataEntry(evaluationItem, visitor)?.let { metadataEntry ->
                getEvaluator(visitor).evaluate(
                    evaluationItem = evaluationItem,
                    metadata = visitor.indicatorContext.contextMetadata + metadataEntry
                )
            }
        } ?: ParserUtils.DOUBLE_VALUE_IF_NULL
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        return getEvaluationItem(ctx, visitor)?.let { evaluationItem ->
            getMetadataEntry(evaluationItem, visitor)?.let { metadataEntry ->
                getEvaluator(visitor).getSql(
                    evaluationItem = evaluationItem,
                    metadata = visitor.indicatorContext.contextMetadata + metadataEntry
                )?.let { "($it)" }
            }
        }
    }

    fun getEvaluator(visitor: CommonExpressionVisitor): AnalyticsEvaluator

    fun getDataItem(ctx: ExprContext, visitor: CommonExpressionVisitor): AbsoluteDimensionItem?

    private fun getEvaluationItem(
        ctx: ExprContext,
        visitor: CommonExpressionVisitor
    ): AnalyticsServiceEvaluationItem? {
        return getDataItem(ctx, visitor)?.let { dataItem ->
            AnalyticsServiceEvaluationItem(
                dimensionItems = listOf(dataItem),
                filters = visitor.indicatorContext.evaluationItem.allDimensionItems,
                aggregationType = visitor.indicatorContext.evaluationItem.aggregationType
            )
        }
    }

    @Suppress("ThrowsCount")
    private fun getMetadataEntry(
        evaluationItem: AnalyticsServiceEvaluationItem,
        visitor: CommonExpressionVisitor
    ): Pair<String, MetadataItem>? {
        return when (val dataItem = evaluationItem.dimensionItems.first()) {
            is DimensionItem.DataItem.DataElementOperandItem -> {
                val dataElement = visitor.indicatorContext.dataElementStore.selectByUid(dataItem.dataElement)
                val coc = visitor.indicatorContext.categoryOptionComboStore.selectByUid(dataItem.categoryOptionCombo)

                if (dataElement == null || coc == null) {
                    throw AnalyticsException.InvalidDataElementOperand(dataItem.id)
                }

                val dataElementOperandId = dataItem.id
                val dataElementOperand = DataElementOperand.builder()
                    .uid(dataItem.id)
                    .dataElement(ObjectWithUid.create(dataItem.dataElement))
                    .categoryOptionCombo(ObjectWithUid.create(dataItem.categoryOptionCombo))
                    .build()

                dataElementOperandId to MetadataItem.DataElementOperandItem(
                    dataElementOperand,
                    dataElement.displayName()!!,
                    coc.displayName()
                )
            }
            is DimensionItem.DataItem.DataElementItem -> {
                val dataElement =
                    visitor.indicatorContext.dataElementStore.selectByUid(dataItem.uid)
                        ?: throw AnalyticsException.InvalidDataElement(dataItem.uid)

                dataItem.uid to MetadataItem.DataElementItem(dataElement)
            }
            is DimensionItem.DataItem.ProgramIndicatorItem -> {
                val programIndicator = visitor.indicatorContext.programIndicatorRepository
                    .withAnalyticsPeriodBoundaries()
                    .uid(dataItem.uid)
                    .blockingGet()
                    ?: throw AnalyticsException.InvalidProgramIndicator(dataItem.uid)

                dataItem.uid to MetadataItem.ProgramIndicatorItem(programIndicator)
            }
            is DimensionItem.DataItem.EventDataItem.DataElement -> {
                val program = visitor.indicatorContext.programStore.selectByUid(dataItem.program)
                    ?: throw AnalyticsException.InvalidProgram(dataItem.program)

                val dataElement = visitor.indicatorContext.dataElementStore.selectByUid(dataItem.dataElement)
                    ?: throw AnalyticsException.InvalidDataElement(dataItem.dataElement)

                val metadataItem = MetadataItem.EventDataElementItem(dataElement, program)
                metadataItem.id to metadataItem
            }
            is DimensionItem.DataItem.EventDataItem.Attribute -> {
                val program = visitor.indicatorContext.programStore.selectByUid(dataItem.program)
                    ?: throw AnalyticsException.InvalidProgram(dataItem.program)

                val attribute = visitor.indicatorContext.trackedEntityAttributeStore.selectByUid(dataItem.attribute)
                    ?: throw AnalyticsException.InvalidTrackedEntityAttribute(dataItem.attribute)

                val metadataItem = MetadataItem.EventAttributeItem(attribute, program)
                metadataItem.id to metadataItem
            }
            else ->
                null
        }
    }
}
