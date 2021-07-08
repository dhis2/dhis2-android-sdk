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

package org.hisp.dhis.android.core.analytics.aggregated.service.evaluator

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.service.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import javax.inject.Inject

internal class DataElementEvaluator @Inject constructor(
    private val databaseAdapter: DatabaseAdapter
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {
        val items = (evaluationItem.dimensionItems + evaluationItem.filters).map { it as DimensionItem }

        val whereClause =
            items.foldRight(WhereClauseBuilder()) { item, builder ->
                appendWhereClause(item, builder, metadata)
            }
                .appendKeyNumberValue(DataValueTableInfo.Columns.DELETED, 0)
                .build()

        val sqlQuery =
            "SELECT SUM(${DataValueTableInfo.Columns.VALUE}) " +
                    "FROM ${DataValueTableInfo.TABLE_INFO.name()} " +
                    "WHERE $whereClause"

        return databaseAdapter.rawQuery(sqlQuery)?.use { c ->
            c.moveToFirst()
            c.getString(0)
        }
    }

    private fun appendWhereClause(
        item: DimensionItem,
        builder: WhereClauseBuilder,
        metadata: Map<String, MetadataItem>
    ): WhereClauseBuilder {
        return when (item) {
            is DimensionItem.DataItem ->
                when (item) {
                    is DimensionItem.DataItem.DataElementItem ->
                        builder.appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, item.uid)
                    else -> TODO()

                }

            is DimensionItem.PeriodItem ->
                when (item) {
                    is DimensionItem.PeriodItem.Absolute -> {
                        val periodItem = metadata[item.periodId] as MetadataItem.PeriodItem
                        builder.appendInSubQuery(
                            DataValueTableInfo.Columns.PERIOD,
                            AnalyticsEvaluatorHelper.getPeriodsClause(periodItem.item)
                        )
                    }
                    is DimensionItem.PeriodItem.Relative -> TODO()
                }

            is DimensionItem.OrganisationUnitItem ->
                when (item) {
                    is DimensionItem.OrganisationUnitItem.Absolute ->
                        builder.appendInSubQuery(
                            DataValueTableInfo.Columns.ORGANISATION_UNIT,
                            AnalyticsEvaluatorHelper.getOrgunitClause(item.uid)
                        )
                    is DimensionItem.OrganisationUnitItem.Level ->
                        builder.appendInSubQuery(
                            DataValueTableInfo.Columns.ORGANISATION_UNIT,
                            AnalyticsEvaluatorHelper.getLevelOrgunitClause(item.level)
                        )
                    is DimensionItem.OrganisationUnitItem.Relative -> TODO()
                    is DimensionItem.OrganisationUnitItem.Group -> TODO()
                }

            is DimensionItem.CategoryItem -> TODO()

            is DimensionItem.CategoryOptionGroupSetItem -> TODO()
        }
    }
}