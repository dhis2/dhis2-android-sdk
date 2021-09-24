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

import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkTableInfo as cToCcInfo
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo as cocToCoInfo
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo as cocInfo
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodTableInfo

/**
 * This class includes some SQL helpers to build the where clause. Dimensions might include several items, like for
 * example a Period dimension might include January, February and March. It is important to join the inner clauses
 * using an OR operator in order to include all the element that match any element in the dimension. For example, this
 * query should result in something like:
 * - ... AND (period = January OR period = February OR period = March)...
 *
 * This logic applies for all the dimensions.
 */
internal object AnalyticsEvaluatorHelper {

    fun getItemsByDimension(evaluationItem: AnalyticsServiceEvaluationItem): Map<Dimension, List<DimensionItem>> {
        return (evaluationItem.dimensionItems + evaluationItem.filters)
            .map { it as DimensionItem }
            .groupBy { it.dimension }
    }

    fun appendOrgunitWhereClause(
        columnName: String,
        items: List<DimensionItem>,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        val innerClause = WhereClauseBuilder().apply {
            items.map { i ->
                when (val item = i as DimensionItem.OrganisationUnitItem) {
                    is DimensionItem.OrganisationUnitItem.Absolute -> {
                        appendOrInSubQuery(columnName, getOrgunitClause(item.uid))
                    }
                    is DimensionItem.OrganisationUnitItem.Level -> {
                        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitLevelItem
                        appendOrInSubQuery(columnName, getOrgunitListClause(metadataItem.organisationUnitUids))
                    }
                    is DimensionItem.OrganisationUnitItem.Relative -> {
                        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitRelativeItem
                        appendOrInSubQuery(columnName, getOrgunitListClause(metadataItem.organisationUnitUids))
                    }
                    is DimensionItem.OrganisationUnitItem.Group -> {
                        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitGroupItem
                        appendOrInSubQuery(columnName, getOrgunitListClause(metadataItem.organisationUnitUids))
                    }
                }
            }
        }.build()

        return builder.appendComplexQuery(innerClause)
    }

    fun getReportingPeriods(
        items: List<DimensionItem>,
        metadata: Map<String, MetadataItem>
    ): List<Period> {
        return mutableListOf<Period>().apply {
            items.forEach { i ->
                when (val item = i as DimensionItem.PeriodItem) {
                    is DimensionItem.PeriodItem.Absolute -> {
                        val periodItem = metadata[item.periodId] as MetadataItem.PeriodItem
                        add(periodItem.item)
                    }
                    is DimensionItem.PeriodItem.Relative -> {
                        val relativeItem = metadata[item.id] as MetadataItem.RelativePeriodItem
                        addAll(relativeItem.periods)
                    }
                }
            }
        }
    }

    fun getInPeriodsClause(periods: List<Period>): String {
        return "SELECT ${PeriodTableInfo.Columns.PERIOD_ID} " +
            "FROM ${PeriodTableInfo.TABLE_INFO.name()} " +
            "WHERE ${periods.joinToString(" OR ") {
                "(${getPeriodWhereClause(PeriodTableInfo.Columns.START_DATE, PeriodTableInfo.Columns.END_DATE, it)})"
            }}"
    }

    fun getPeriodWhereClause(columnStart: String, columnEnd: String, period: Period): String {
        return "$columnStart >= '${DateUtils.DATE_FORMAT.format(period.startDate()!!)}' " +
            "AND " +
            "$columnEnd <= '${DateUtils.DATE_FORMAT.format(period.endDate()!!)}'"
    }

    private fun getOrgunitClause(orgunitUid: String): String {
        return "SELECT ${OrganisationUnitTableInfo.Columns.UID} " +
            "FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} " +
            "WHERE " +
            "${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$orgunitUid%'"
    }

    private fun getOrgunitListClause(orgunitUids: List<String>): String {
        return "SELECT ${OrganisationUnitTableInfo.Columns.UID} " +
            "FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} " +
            "WHERE " +
            orgunitUids.joinToString(" OR ") { "${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$it%'" }
    }

    fun appendCategoryWhereClause(
        columnName: String,
        items: List<DimensionItem>,
        builder: WhereClauseBuilder
    ): WhereClauseBuilder {
        val innerClause = WhereClauseBuilder().apply {
            items.map { it as DimensionItem.CategoryItem }.map { item ->
                appendOrInSubQuery(columnName, getCategoryOptionClause(item.uid, item.categoryOption))
            }
        }.build()

        return builder.appendComplexQuery(innerClause)
    }

    private fun getCategoryOptionClause(categoryUid: String, categoryOptionUid: String): String {
        return "SELECT ${cocInfo.Columns.UID} " +
            "FROM ${cocInfo.TABLE_INFO.name()} " +
            "WHERE " +
            "${cocInfo.Columns.UID} IN " +
            "(" +
            "SELECT ${cocToCoInfo.Columns.CATEGORY_OPTION_COMBO} " +
            "FROM ${cocToCoInfo.TABLE_INFO.name()} " +
            "WHERE ${cocToCoInfo.Columns.CATEGORY_OPTION} = '$categoryOptionUid'" +
            ") " +
            "AND " +
            "${cocInfo.Columns.CATEGORY_COMBO} IN " +
            "(" +
            "SELECT ${cToCcInfo.Columns.CATEGORY_COMBO} " +
            "FROM ${cToCcInfo.TABLE_INFO.name()} " +
            "WHERE ${cToCcInfo.Columns.CATEGORY} = '$categoryUid'" +
            ") "
    }
}
