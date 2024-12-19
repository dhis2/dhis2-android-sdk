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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.QueryMods
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.core.util.SqlAggregator
import org.koin.core.annotation.Singleton
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo.Columns as tavColumns
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo.Columns as dvColumns

@Singleton
@Suppress("TooManyFunctions")
internal class EventDataItemSQLEvaluator(
    private val databaseAdapter: DatabaseAdapter,
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>,
        queryMods: QueryMods?,
    ): String? {
        val sqlQuery = getSql(evaluationItem, metadata, queryMods)

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            c.getString(0)
        }
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun getSql(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>,
        queryMods: QueryMods?,
    ): String {
        val items = AnalyticsDimensionHelper.getItemsByDimension(evaluationItem)

        val eventDataItem = getEventDataItems(evaluationItem)[0]
        val aggregator = getAggregator(evaluationItem, eventDataItem, metadata, queryMods)
        val (valueColumn, fromClause) = getEventDataItemSQLItems(eventDataItem)

        val whereClause = WhereClauseBuilder().apply {
            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Data -> appendDataWhereClause(entry.value, this)
                    is Dimension.Period -> appendPeriodWhereClause(entry.value, this, metadata, aggregator, queryMods)
                    is Dimension.OrganisationUnit -> appendOrgunitWhereClause(entry.value, this, metadata)
                    is Dimension.Category -> appendCategoryWhereClause(entry.value, this, metadata)
                }
            }
            appendKeyNumberValue("$EventAlias.${EventTableInfo.Columns.DELETED}", 0)
        }.build()

        return when (aggregator) {
            AggregationType.AVERAGE,
            AggregationType.SUM,
            AggregationType.COUNT,
            AggregationType.MIN,
            AggregationType.MAX,
            -> {
                "SELECT ${aggregator.sql}($valueColumn) " +
                    "FROM $fromClause " +
                    "WHERE $whereClause"
            }
            AggregationType.AVERAGE_SUM_ORG_UNIT -> {
                aggregateByDataValueAndSumOrgunit(SqlAggregator.AVG, valueColumn, fromClause, whereClause)
            }
            AggregationType.FIRST -> {
                "SELECT SUM($valueColumn) " +
                    "FROM (${firstOrLastValueByOrunit(valueColumn, fromClause, whereClause, ValuePosition.FIRST)})"
            }
            AggregationType.FIRST_AVERAGE_ORG_UNIT -> {
                "SELECT AVG(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueByOrunit(valueColumn, fromClause, whereClause, ValuePosition.FIRST)})"
            }
            AggregationType.FIRST_FIRST_ORG_UNIT -> {
                firstOrLastValueGlobally(valueColumn, fromClause, whereClause, ValuePosition.FIRST)
            }
            AggregationType.LAST,
            AggregationType.LAST_IN_PERIOD,
            -> {
                "SELECT SUM(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueByOrunit(valueColumn, fromClause, whereClause, ValuePosition.LAST)})"
            }
            AggregationType.LAST_AVERAGE_ORG_UNIT,
            AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT,
            -> {
                "SELECT AVG(${dvColumns.VALUE}) " +
                    "FROM (${firstOrLastValueByOrunit(valueColumn, fromClause, whereClause, ValuePosition.LAST)})"
            }
            AggregationType.LAST_LAST_ORG_UNIT -> {
                firstOrLastValueGlobally(valueColumn, fromClause, whereClause, ValuePosition.LAST)
            }
            AggregationType.MAX_SUM_ORG_UNIT -> {
                aggregateByDataValueAndSumOrgunit(SqlAggregator.MAX, valueColumn, fromClause, whereClause)
            }
            AggregationType.MIN_SUM_ORG_UNIT -> {
                aggregateByDataValueAndSumOrgunit(SqlAggregator.MIN, valueColumn, fromClause, whereClause)
            }

            AggregationType.CUSTOM,
            AggregationType.STDDEV,
            AggregationType.VARIANCE,
            AggregationType.DEFAULT,
            AggregationType.NONE,
            -> throw AnalyticsException.UnsupportedAggregationType(aggregator)
        }
    }

    private fun getEventDataItemSQLItems(
        eventDataItem: DimensionItem.DataItem.EventDataItem,
    ): Pair<String, String> {
        return when (eventDataItem) {
            is DimensionItem.DataItem.EventDataItem.DataElement ->
                Pair(dvColumns.VALUE, dataValueFromClauseWithJoins)

            is DimensionItem.DataItem.EventDataItem.Attribute ->
                Pair(tavColumns.VALUE, attributeValueFromClauseWithJoins)
        }
    }

    private val dataValueFromClauseWithJoins =
        "${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} $DataValueAlias " +
            "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} $EventAlias " +
            "ON $DataValueAlias.${dvColumns.EVENT} = " +
            "$EventAlias.${EventTableInfo.Columns.UID} "

    private val attributeValueFromClauseWithJoins =
        "${TrackedEntityAttributeValueTableInfo.TABLE_INFO.name()} $AttAlias " +
            "INNER JOIN ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
            "ON $AttAlias.${tavColumns.TRACKED_ENTITY_INSTANCE} = " +
            "$EnrollmentAlias.${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
            "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} $EventAlias " +
            "ON $EnrollmentAlias.${EnrollmentTableInfo.Columns.UID} = " +
            "$EventAlias.${EventTableInfo.Columns.ENROLLMENT} "

    private fun aggregateByDataValueAndSumOrgunit(
        dataValueAggregator: String,
        valueColumn: String,
        fromClause: String,
        whereClause: String,
    ): String {
        return "SELECT SUM($valueColumn) " +
            "FROM (" +
            "SELECT $dataValueAggregator($valueColumn) as $valueColumn " +
            "FROM $fromClause " +
            "WHERE $whereClause " +
            "GROUP BY $EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT}" +
            ")"
    }

    private fun firstOrLastValueByOrunit(
        valueColumn: String,
        fromClause: String,
        whereClause: String,
        position: ValuePosition,
    ): String {
        val firstOrLastValueClause = "SELECT " +
            "$valueColumn, " +
            "$EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT}, " +
            "${position.aggregator}($EventAlias.${EventTableInfo.Columns.EVENT_DATE}) " +
            "FROM $fromClause " +
            "WHERE $whereClause " +
            "AND $EventAlias.${EventTableInfo.Columns.EVENT_DATE} IS NOT NULL " +
            "GROUP BY $EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT}, " +
            "$EventAlias.${EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO}"

        return "SELECT SUM($valueColumn) AS $valueColumn " +
            "FROM ($firstOrLastValueClause) " +
            "GROUP BY ${EventTableInfo.Columns.ORGANISATION_UNIT}"
    }

    private fun firstOrLastValueGlobally(
        valueColumn: String,
        fromClause: String,
        whereClause: String,
        position: ValuePosition,
    ): String {
        val firstOrLastValueClause = "SELECT " +
            "$valueColumn, " +
            "${position.aggregator}($EventAlias.${EventTableInfo.Columns.EVENT_DATE}) " +
            "FROM $fromClause " +
            "WHERE $whereClause " +
            "AND $EventAlias.${EventTableInfo.Columns.EVENT_DATE} IS NOT NULL "

        return "SELECT $valueColumn " +
            "FROM ($firstOrLastValueClause)"
    }

    private fun appendDataWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
    ): WhereClauseBuilder {
        val innerClause = items.map { it as DimensionItem.DataItem }
            .foldRight(WhereClauseBuilder()) { item, innerBuilder ->
                when (item) {
                    is DimensionItem.DataItem.EventDataItem.DataElement -> {
                        val operandClause = WhereClauseBuilder()
                            .appendKeyStringValue(
                                "$DataValueAlias.${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT}",
                                item.dataElement,
                            )
                            .appendKeyStringValue(
                                "$EventAlias.${EventTableInfo.Columns.PROGRAM}",
                                item.program,
                            )
                            .build()
                        innerBuilder.appendOrComplexQuery(operandClause)
                    }
                    is DimensionItem.DataItem.EventDataItem.Attribute -> {
                        val operandClause = WhereClauseBuilder()
                            .appendKeyStringValue(
                                "$AttAlias.${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE}",
                                item.attribute,
                            )
                            .appendKeyStringValue(
                                "$EventAlias.${EventTableInfo.Columns.PROGRAM}",
                                item.program,
                            )
                            .build()
                        innerBuilder.appendOrComplexQuery(operandClause)
                    }
                    else ->
                        throw AnalyticsException.InvalidArguments(
                            "Invalid arguments: unexpected " +
                                "dataItem ${item.javaClass.name} in EventDataItem Evaluator.",
                        )
                }
            }.build()

        return builder.appendComplexQuery(innerClause)
    }

    private fun appendPeriodWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>,
        aggregation: AggregationType,
        queryMods: QueryMods?,
    ): WhereClauseBuilder {
        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(items, metadata, queryMods)

        return if (reportingPeriods.isEmpty()) {
            builder
        } else {
            val eventDateColumn = "$EventAlias.${EventTableInfo.Columns.EVENT_DATE}"
            val periods = AnalyticsEvaluatorHelper.getReportingPeriodsForAggregationType(reportingPeriods, aggregation)

            val innerClause = periods.joinToString(" OR ") {
                "(${
                    AnalyticsEvaluatorHelper.getPeriodWhereClause(
                        columnStart = eventDateColumn,
                        columnEnd = eventDateColumn,
                        period = it,
                    )
                })"
            }

            builder.appendComplexQuery(innerClause)
        }
    }

    private fun appendOrgunitWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>,
    ): WhereClauseBuilder {
        return AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
            columnName = "$EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT}",
            items = items,
            builder = builder,
            metadata = metadata,
        )
    }

    private fun appendCategoryWhereClause(
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>,
    ): WhereClauseBuilder {
        return AnalyticsEvaluatorHelper.appendCategoryWhereClause(
            attributeColumnName = "$EventAlias.${EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO}",
            disaggregationColumnName = null,
            items = items,
            builder = builder,
            metadata = metadata,
        )
    }

    private fun getEventDataItems(
        evaluationItem: AnalyticsServiceEvaluationItem,
    ): List<DimensionItem.DataItem.EventDataItem> {
        return AnalyticsDimensionHelper.getSingleItemByDimension(evaluationItem)
    }

    private fun getAggregator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        item: DimensionItem.DataItem.EventDataItem,
        metadata: Map<String, MetadataItem>,
        queryMods: QueryMods?,
    ): AggregationType {
        return if (queryMods?.aggregationType?.let { it != AggregationType.DEFAULT } == true) {
            queryMods.aggregationType!!
        } else if (evaluationItem.aggregationType != AggregationType.DEFAULT) {
            evaluationItem.aggregationType
        } else {
            val aggregationType = when (val metadataItem = metadata[item.id]) {
                is MetadataItem.EventDataElementItem ->
                    metadataItem.item.aggregationType()
                is MetadataItem.EventAttributeItem ->
                    metadataItem.item.aggregationType()?.name
                else ->
                    throw AnalyticsException.InvalidArguments("Invalid arguments: invalid event data item ${item.id}.")
            }

            AnalyticsEvaluatorHelper.getElementAggregator(aggregationType)
        }
    }

    companion object {
        private const val EventAlias = "ev"
        private const val DataValueAlias = "tdv"
        private const val AttAlias = "av"
        private const val EnrollmentAlias = "en"
    }
}
