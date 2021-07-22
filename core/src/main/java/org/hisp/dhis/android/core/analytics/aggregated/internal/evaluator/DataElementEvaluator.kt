/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo

internal class DataElementEvaluator @Inject constructor(
    private val databaseAdapter: DatabaseAdapter
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {
        val items = (evaluationItem.dimensionItems + evaluationItem.filters)
            .map { it as DimensionItem }
            .groupBy { it.dimension }

        val whereClause =
            items.entries.fold(WhereClauseBuilder()) { builder, entry ->
                when (entry.key) {
                    is Dimension.Data -> appendDataWhereClause(entry.value, builder)
                    is Dimension.Period -> appendPeriodWhereClause(entry.value, builder, metadata)
                    is Dimension.OrganisationUnit -> appendOrgunitWhereClause(entry.value, builder, metadata)
                    is Dimension.Category -> appendCategoryWhereClause(entry.value, builder)
                }
            }
                .appendKeyNumberValue(DataValueTableInfo.Columns.DELETED, 0)
                .build()

        val aggregator = getAggregator(evaluationItem, metadata)

        val sqlQuery =
            "SELECT $aggregator(${DataValueTableInfo.Columns.VALUE}) " +
                "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
                "WHERE $whereClause"

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            c.getString(0)
        }
    }

    private fun appendDataWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder
    ): WhereClauseBuilder {
        val innerClause = items.map { it as DimensionItem.DataItem }
            .foldRight(WhereClauseBuilder()) { item, innerBuilder ->
                when (item) {
                    is DimensionItem.DataItem.DataElementItem ->
                        innerBuilder.appendOrKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, item.uid)
                    is DimensionItem.DataItem.DataElementOperandItem -> {
                        val operandClause = WhereClauseBuilder()
                            .appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, item.dataElement)
                            .appendKeyStringValue(
                                DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO,
                                item.categoryOptionCombo
                            )
                            .build()
                        innerBuilder.appendOrComplexQuery(operandClause)
                    }
                    else ->
                        throw AnalyticsException.InvalidArguments(
                            "Invalid arguments: unexpected " +
                                "dataItem ${item.javaClass.name} in DataElement Evaluator."
                        )
                }
            }.build()

        return builder.appendComplexQuery(innerClause)
    }

    private fun appendPeriodWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        val innerClause = items.map { it as DimensionItem.PeriodItem }
            .foldRight(WhereClauseBuilder()) { item, innerBuilder ->
                when (item) {
                    is DimensionItem.PeriodItem.Absolute -> {
                        val periodItem = metadata[item.periodId] as MetadataItem.PeriodItem
                        innerBuilder.appendOrInSubQuery(
                            DataValueTableInfo.Columns.PERIOD,
                            AnalyticsEvaluatorHelper.getInPeriodClause(periodItem.item)
                        )
                    }
                    is DimensionItem.PeriodItem.Relative -> {
                        val relativeItem = metadata[item.id] as MetadataItem.RelativePeriodItem
                        innerBuilder.appendOrInSubQuery(
                            DataValueTableInfo.Columns.PERIOD,
                            AnalyticsEvaluatorHelper.getInPeriodsClause(relativeItem.periods)
                        )
                    }
                }
            }.build()

        return builder.appendComplexQuery(innerClause)
    }

    private fun appendOrgunitWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        val innerClause = items.map { it as DimensionItem.OrganisationUnitItem }
            .foldRight(WhereClauseBuilder()) { item, innerBuilder ->
                when (item) {
                    is DimensionItem.OrganisationUnitItem.Absolute ->
                        innerBuilder.appendOrInSubQuery(
                            DataValueTableInfo.Columns.ORGANISATION_UNIT,
                            AnalyticsEvaluatorHelper.getOrgunitClause(item.uid)
                        )
                    is DimensionItem.OrganisationUnitItem.Level ->
                        innerBuilder.appendOrInSubQuery(
                            DataValueTableInfo.Columns.ORGANISATION_UNIT,
                            AnalyticsEvaluatorHelper.getLevelOrgunitClause(item.level)
                        )
                    is DimensionItem.OrganisationUnitItem.Relative -> {
                        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitRelativeItem
                        val orgunits = metadataItem.organisationUnits.map { it.uid() }

                        innerBuilder.appendOrInSubQuery(
                            DataValueTableInfo.Columns.ORGANISATION_UNIT,
                            AnalyticsEvaluatorHelper.getOrgunitListClause(orgunits)
                        )
                    }
                    is DimensionItem.OrganisationUnitItem.Group -> TODO()
                }
            }.build()

        return builder.appendComplexQuery(innerClause)
    }

    private fun appendCategoryWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder
    ): WhereClauseBuilder {
        val innerClause = items.map { it as DimensionItem.CategoryItem }
            .foldRight(WhereClauseBuilder()) { item, innerBuilder ->
                innerBuilder.appendOrInSubQuery(
                    DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO,
                    AnalyticsEvaluatorHelper.getCategoryOptionClause(item.uid, item.categoryOption)
                )
            }.build()

        return builder.appendComplexQuery(innerClause)
    }

    @Suppress("ThrowsCount")
    private fun getAggregator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        val dimensionDataItem = evaluationItem.dimensionItems.filterIsInstance<DimensionItem.DataItem>()

        val dataItemList = when (dimensionDataItem.size) {
            0 -> evaluationItem.filters.filterIsInstance<DimensionItem.DataItem>()
            1 -> dimensionDataItem
            else ->
                throw AnalyticsException.InvalidArguments("Invalid arguments: more than one data item as dimension.")
        }

        return when (dataItemList.size) {
            0 -> throw AnalyticsException.InvalidArguments("Invalid arguments: no data dimension is specified.")
            1 -> {
                val item = metadata[dataItemList.first().id]
                val aggregationType = when (item) {
                    is MetadataItem.DataElementItem -> item.item.aggregationType()
                    is MetadataItem.DataElementOperandItem ->
                        metadata[item.item.dataElement()?.uid()]?.let {
                            (it as MetadataItem.DataElementItem).item.aggregationType()
                        }
                    else -> throw AnalyticsException.InvalidArguments(
                        "Invalid arguments: dimension is not " +
                            "dataelement or operand."
                    )
                }
                AnalyticsEvaluatorHelper.getDataElementAggregator(aggregationType)
            }
            else -> AnalyticsEvaluatorHelper.getDataElementAggregator(AggregationType.SUM.name)
        }
    }
}
