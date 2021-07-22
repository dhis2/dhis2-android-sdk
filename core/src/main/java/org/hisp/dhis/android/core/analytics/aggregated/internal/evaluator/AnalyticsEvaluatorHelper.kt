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

import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkTableInfo as cToCcInfo
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo as cocToCoInfo
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo as cocInfo
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodTableInfo

internal object AnalyticsEvaluatorHelper {

    const val Sum = "SUM"
    const val Avg = "AVG"
    const val Count = "COUNT"
    const val Max = "MAX"
    const val Min = "MIN"

    fun getInPeriodClause(period: Period): String {
        return "SELECT ${PeriodTableInfo.Columns.PERIOD_ID} " +
            "FROM ${PeriodTableInfo.TABLE_INFO.name()} " +
            "WHERE ${getPeriodWhereClause(period)}"
    }

    fun getInPeriodsClause(periods: List<Period>): String {
        return "SELECT ${PeriodTableInfo.Columns.PERIOD_ID} " +
            "FROM ${PeriodTableInfo.TABLE_INFO.name()} " +
            "WHERE ${periods.joinToString(" OR ") { "(${getPeriodWhereClause(it)})" }}"
    }

    private fun getPeriodWhereClause(period: Period): String {
        return "${PeriodTableInfo.Columns.START_DATE} >= '${DateUtils.DATE_FORMAT.format(period.startDate()!!)}' " +
            "AND " +
            "${PeriodTableInfo.Columns.END_DATE} <= '${DateUtils.DATE_FORMAT.format(period.endDate()!!)}'"
    }

    fun getOrgunitClause(orgunitUid: String): String {
        return "SELECT ${OrganisationUnitTableInfo.Columns.UID} " +
            "FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} " +
            "WHERE " +
            "${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$orgunitUid%'"
    }

    fun getLevelOrgunitClause(level: Int): String {
        return "SELECT ${OrganisationUnitTableInfo.Columns.UID} " +
            "FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} " +
            "WHERE " +
            "${OrganisationUnitTableInfo.Columns.LEVEL} = $level"
    }

    fun getOrgunitListClause(orgunitUids: List<String>): String {
        return "SELECT ${OrganisationUnitTableInfo.Columns.UID} " +
            "FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} " +
            "WHERE " +
            orgunitUids.joinToString(" OR ") { "${OrganisationUnitTableInfo.Columns.PATH} LIKE '%$it%'" }
    }

    fun getCategoryOptionClause(categoryUid: String, categoryOptionUid: String): String {
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

    fun getDataElementAggregator(aggregationType: String?): String {
        return when (aggregationType?.let { AggregationType.valueOf(it) } ?: AggregationType.SUM) {
            AggregationType.SUM -> Sum
            AggregationType.AVERAGE -> Avg
            AggregationType.COUNT -> Count
            AggregationType.MAX -> Max
            AggregationType.MIN -> Min
            else -> Sum
        }
    }
}
