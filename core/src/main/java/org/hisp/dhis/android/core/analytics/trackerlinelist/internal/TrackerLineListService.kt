/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListResponse
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListServiceHelper.mapCursorToColumns
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListEvaluatorMapper
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EnrollmentAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EventAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.OrgunitAlias
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationCollectionRepository
import org.koin.core.annotation.Singleton
import java.lang.RuntimeException

@Singleton
internal class TrackerLineListService(
    private val databaseAdapter: DatabaseAdapter,
    private val trackerVisualizationCollectionRepository: TrackerVisualizationCollectionRepository,
    private val metadataHelper: TrackerLineListServiceMetadataHelper,
    private val trackerVisualizationMapper: TrackerVisualizationMapper,
) {
    fun evaluate(params: TrackerLineListParams): Result<TrackerLineListResponse, AnalyticsException> {
        return try {
            val evaluatedParams = evaluateParams(params)

            // TODO Validate params

            val metadata = metadataHelper.getMetadata(evaluatedParams)
            val context = TrackerLineListContext(metadata, databaseAdapter)

            val sqlClause = when (evaluatedParams.outputType!!) {
                TrackerLineListOutputType.EVENT -> getEventSqlClause(evaluatedParams, context)
                TrackerLineListOutputType.ENROLLMENT -> getEnrollmentSqlClause(evaluatedParams, context)
            }

            val cursor = databaseAdapter.rawQuery(sqlClause)
            val values = mapCursorToColumns(evaluatedParams, cursor)

            Result.Success(
                TrackerLineListResponse(
                    metadata = metadata,
                    headers = evaluatedParams.columns,
                    filters = evaluatedParams.filters,
                    rows = values,
                ),
            )
        } catch (e: AnalyticsException) {
            Result.Failure(e)
        } catch (e: RuntimeException) {
            Result.Failure(AnalyticsException.SQLException(e.message ?: ""))
        }
    }

    private fun evaluateParams(params: TrackerLineListParams): TrackerLineListParams {
        return params
            .run {
                if (this.trackerVisualization != null) {
                    val visualization = getTrackerVisualization(this.trackerVisualization)
                        ?: throw AnalyticsException.InvalidVisualization(this.trackerVisualization)

                    trackerVisualizationMapper.toTrackerLineListParams(visualization) + this
                } else {
                    this
                }
            }.run {
                this.flattenRepeatedDataElements()
            }
    }

    private fun getTrackerVisualization(trackerVisualization: String): TrackerVisualization? {
        return trackerVisualizationCollectionRepository
            .withColumnsAndFilters()
            .uid(trackerVisualization)
            .blockingGet()
    }

    private fun getEventSqlClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return "SELECT " +
                "${getEventSelectColumns(params, context)} " +
                "FROM ${EventTableInfo.TABLE_INFO.name()} $EventAlias " +
                "LEFT JOIN ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
                "ON $EventAlias.${EventTableInfo.Columns.ENROLLMENT} = " +
                "$EnrollmentAlias.${EnrollmentTableInfo.Columns.UID} " +
                if (params.hasOrgunit()) {
                    "LEFT JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OrgunitAlias " +
                            "ON $EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT} = " +
                            "$OrgunitAlias.${OrganisationUnitTableInfo.Columns.UID} "
                } else {
                    ""
                } +
                "WHERE " +
                "$EventAlias.${EventTableInfo.Columns.PROGRAM} = '${params.programId!!}' AND " +
                "$EventAlias.${EventTableInfo.Columns.PROGRAM_STAGE} = '${params.programStageId!!}' AND " +
                "${getEventWhereClause(params, context)} "
    }

    private fun getEnrollmentSqlClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return "SELECT " +
                "${getEnrollmentSelectColumns(params, context)} " +
                "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
                if (params.hasOrgunit()) {
                    "LEFT JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OrgunitAlias " +
                            "ON $EnrollmentAlias.${EnrollmentTableInfo.Columns.ORGANISATION_UNIT} = " +
                            "$OrgunitAlias.${OrganisationUnitTableInfo.Columns.UID} "
                } else {
                    ""
                } +
                "WHERE " +
                "$EnrollmentAlias.${EnrollmentTableInfo.Columns.PROGRAM} = '${params.programId!!}' AND " +
                "${getEnrollmentWhereClause(params, context)} "
    }

    private fun getEventSelectColumns(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return params.allItems.joinToString(", ") {
            "(${TrackerLineListEvaluatorMapper.getEvaluator(it, context).getSelectSQLForEvent()}) '${it.id}'"
        }
    }

    private fun getEventWhereClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return params.allItems.joinToString(" AND ") {
            TrackerLineListEvaluatorMapper.getEvaluator(it, context).getWhereSQLForEvent()
        }
    }

    private fun getEnrollmentSelectColumns(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return params.allItems.joinToString(", ") {
            "(${TrackerLineListEvaluatorMapper.getEvaluator(it, context).getSelectSQLForEnrollment()}) '${it.id}'"
        }
    }

    private fun getEnrollmentWhereClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        val unflattenedRepeatedDataElements = params.allItems.groupBy { item ->
            when (item) {
                is TrackerLineListItem.ProgramDataElement -> item.stageDataElementIdx
                else -> item.id
            }
        }

        return unflattenedRepeatedDataElements.values.joinToString(" AND ") { items ->
            val orClause = items.joinToString(" OR ") {
                TrackerLineListEvaluatorMapper.getEvaluator(it, context).getWhereSQLForEnrollment()
            }
            "($orClause)"
        }
    }
}
