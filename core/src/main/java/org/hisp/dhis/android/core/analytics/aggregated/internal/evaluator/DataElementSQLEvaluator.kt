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
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.getItemsByDimension
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo

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
        val items = getItemsByDimension(evaluationItem)

        val whereClause = WhereClauseBuilder().apply {
            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Data -> appendDataWhereClause(entry.value, this)
                    is Dimension.Period -> appendPeriodWhereClause(entry.value, this, metadata)
                    is Dimension.OrganisationUnit -> appendOrgunitWhereClause(entry.value, this, metadata)
                    is Dimension.Category -> appendCategoryWhereClause(entry.value, this)
                }
            }
            appendKeyNumberValue(DataValueTableInfo.Columns.DELETED, 0)
        }.build()

        val aggregator = getAggregator(evaluationItem, metadata)

        return "SELECT $aggregator(${DataValueTableInfo.Columns.VALUE}) " +
            "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
            "WHERE $whereClause"
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
        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(items, metadata)

        return builder.appendInSubQuery(
            DataValueTableInfo.Columns.PERIOD,
            AnalyticsEvaluatorHelper.getInPeriodsClause(reportingPeriods)
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
        builder: WhereClauseBuilder
    ): WhereClauseBuilder {
        return AnalyticsEvaluatorHelper.appendCategoryWhereClause(
            columnName = DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO,
            items = items,
            builder = builder
        )
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
                getDataElementAggregator(aggregationType)
            }
            else -> getDataElementAggregator(AggregationType.SUM.name)
        }
    }

    private fun getDataElementAggregator(aggregationType: String?): String {
        return aggregationType?.let { AggregationType.valueOf(it).sql }
            ?: AggregationType.SUM.sql!!
    }
}
