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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo.Columns as dvColumns
import org.hisp.dhis.android.core.period.PeriodTableInfo
import org.hisp.dhis.android.core.period.PeriodTableInfo.Columns as peColumns

internal class DataElementSQLEvaluator @Inject constructor(
    private val databaseAdapter: DatabaseAdapter
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {
        val sqlQuery = getSql(evaluationItem, metadata)

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            c.getString(0)
        }
    }

    override fun getSql(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        val items = AnalyticsDimensionHelper.getItemsByDimension(evaluationItem)

        val aggregator = getAggregator(evaluationItem, metadata)

        val whereClause = WhereClauseBuilder().apply {
            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Data -> appendDataWhereClause(entry.value, this)
                    is Dimension.Period -> appendPeriodWhereClause(entry.value, this, metadata, aggregator)
                    is Dimension.OrganisationUnit -> appendOrgunitWhereClause(entry.value, this, metadata)
                    is Dimension.Category -> appendCategoryWhereClause(entry.value, this, metadata)
                }
            }
            appendKeyNumberValue(DataValueTableInfo.Columns.DELETED, 0)
        }.build()

        return when (aggregator) {
            AggregationType.AVERAGE,
            AggregationType.SUM,
            AggregationType.COUNT,
            AggregationType.MIN,
            AggregationType.MAX -> {
                "SELECT ${aggregator.sql}(${dvColumns.VALUE}) " +
                    "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
                    "WHERE $whereClause"
            }
            AggregationType.AVERAGE_SUM_ORG_UNIT -> {
                "SELECT SUM(${dvColumns.VALUE}) " +
                    "FROM (" +
                    "SELECT AVG(${dvColumns.VALUE}) AS ${dvColumns.VALUE} " +
                    "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
                    "WHERE $whereClause " +
                    "GROUP BY ${dvColumns.ORGANISATION_UNIT}" +
                    ")"
            }
            AggregationType.FIRST -> {
                "SELECT SUM(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueClauseByOrgunit(whereClause, "MIN")})"
            }
            AggregationType.FIRST_AVERAGE_ORG_UNIT -> {
                "SELECT AVG(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueClauseByOrgunit(whereClause, "MIN")})"
            }
            AggregationType.LAST,
            AggregationType.LAST_IN_PERIOD -> {
                "SELECT SUM(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueClauseByOrgunit(whereClause, "MAX")})"
            }
            AggregationType.LAST_AVERAGE_ORG_UNIT,
            AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT -> {
                "SELECT AVG(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueClauseByOrgunit(whereClause, "MAX")})"
            }
            AggregationType.CUSTOM,
            AggregationType.STDDEV,
            AggregationType.VARIANCE,
            AggregationType.DEFAULT,
            AggregationType.NONE -> throw AnalyticsException.UnsupportedAggregationType(aggregator)
        }
    }

    private fun firstOrLastValueClauseByOrgunit(whereClause: String, minOrMax: String): String {
        val orderColumn = "SELECT ${peColumns.START_DATE} || ${peColumns.END_DATE} " +
            "FROM ${PeriodTableInfo.TABLE_INFO.name()} pe " +
            "WHERE pe.${peColumns.PERIOD_ID} = ${dvColumns.PERIOD}"

        val firstOrLastValueClause = "SELECT " +
            "${dvColumns.VALUE}, " +
            "${dvColumns.ORGANISATION_UNIT}, " +
            "$minOrMax(($orderColumn)) " +
            "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
            "WHERE $whereClause " +
            "GROUP BY ${dvColumns.ORGANISATION_UNIT}, " +
            "${dvColumns.DATA_ELEMENT}, " +
            "${dvColumns.CATEGORY_OPTION_COMBO}, " +
            "${dvColumns.ATTRIBUTE_OPTION_COMBO} "

        return "SELECT SUM(${dvColumns.VALUE}) as ${dvColumns.VALUE} " +
            "FROM ($firstOrLastValueClause)" +
            "GROUP BY ${dvColumns.ORGANISATION_UNIT}"
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
        metadata: Map<String, MetadataItem>,
        aggregationType: AggregationType
    ): WhereClauseBuilder {
        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(items, metadata)
        val periods = AnalyticsEvaluatorHelper.getReportingPeriodsForAggregationType(reportingPeriods, aggregationType)

        return builder.appendInSubQuery(
            DataValueTableInfo.Columns.PERIOD,
            AnalyticsEvaluatorHelper.getInPeriodsClause(periods)
        )
    }

    private fun appendOrgunitWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        return AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
            columnName = DataValueTableInfo.Columns.ORGANISATION_UNIT,
            items = items,
            builder = builder,
            metadata = metadata
        )
    }

    private fun appendCategoryWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        return AnalyticsEvaluatorHelper.appendCategoryWhereClause(
            attributeColumnName = DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
            disaggregationColumnName = DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO,
            items = items,
            builder = builder,
            metadata = metadata
        )
    }

    private fun getAggregator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): AggregationType {
        val itemList: List<DimensionItem.DataItem> = AnalyticsDimensionHelper.getSingleItemByDimension(evaluationItem)

        return if (evaluationItem.aggregationType != AggregationType.DEFAULT) {
            evaluationItem.aggregationType
        } else if (itemList.size > 1) {
            AggregationType.SUM
        } else {
            val item = itemList[0]
            val metadataItem = metadata[item.id]
                ?: throw AnalyticsException.InvalidArguments("Invalid arguments: ${item.id} not found in metadata.")

            val aggregationType = when (metadataItem) {
                is MetadataItem.DataElementItem -> metadataItem.item.aggregationType()
                is MetadataItem.DataElementOperandItem ->
                    metadata[metadataItem.item.dataElement()?.uid()]?.let {
                        (it as MetadataItem.DataElementItem).item.aggregationType()
                    }
                else ->
                    throw AnalyticsException.InvalidArguments("Invalid arguments: invalid dataElement item ${item.id}.")
            }

            AnalyticsEvaluatorHelper.getElementAggregator(aggregationType)
        }
    }
}
