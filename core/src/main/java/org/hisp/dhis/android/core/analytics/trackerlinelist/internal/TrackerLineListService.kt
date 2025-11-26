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
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListSortingItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListServiceHelper.fetchDataWithD2Dao
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListEvaluatorMapper
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EnrollmentAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EventAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.OrgUnitAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.TrackedEntityInstanceAlias
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.repositories.paging.PageConfig
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationCollectionRepository
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class TrackerLineListService(
    private val databaseAdapter: DatabaseAdapter,
    private val trackerVisualizationCollectionRepository: TrackerVisualizationCollectionRepository,
    private val metadataHelper: TrackerLineListServiceMetadataHelper,
    private val trackerVisualizationMapper: TrackerVisualizationMapper,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend fun evaluate(params: TrackerLineListParams): Result<TrackerLineListResponse, AnalyticsException> {
        return try {
            val evaluatedParams = evaluateParams(params)
            if (evaluatedParams.outputType == null) {
                throw AnalyticsException.InvalidArguments("Output type cannot be empty.")
            }

            val metadata = metadataHelper.getMetadata(evaluatedParams)
            val context = TrackerLineListContext(metadata, databaseAdapter)

            val sqlClause = when (evaluatedParams.outputType) {
                TrackerLineListOutputType.EVENT ->
                    getEventSqlClause(evaluatedParams, context)

                TrackerLineListOutputType.ENROLLMENT ->
                    getEnrollmentSqlClause(evaluatedParams, context)

                TrackerLineListOutputType.TRACKED_ENTITY_INSTANCE ->
                    getTrackedEntityInstanceSqlClause(evaluatedParams, context)
            }

            val values = fetchDataWithD2Dao(sqlClause, evaluatedParams, databaseAdapter)

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

    private suspend fun evaluateParams(params: TrackerLineListParams): TrackerLineListParams {
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

    private suspend fun getTrackerVisualization(trackerVisualization: String): TrackerVisualization? {
        return trackerVisualizationCollectionRepository
            .withColumnsAndFilters()
            .uid(trackerVisualization)
            .getInternal()
    }

    private suspend fun getEventSqlClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return "SELECT " +
            "${getEventSelectColumns(params, context)} " +
            "FROM ${EventTableInfo.TABLE_INFO.name()} $EventAlias " +
            "LEFT JOIN ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
            "ON $EventAlias.${EventTableInfo.Columns.ENROLLMENT} = " +
            "$EnrollmentAlias.${EnrollmentTableInfo.Columns.UID} " +
            if (params.hasOrgunit()) {
                "LEFT JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OrgUnitAlias " +
                    "ON $EventAlias.${EventTableInfo.Columns.ORGANISATION_UNIT} = " +
                    "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.UID} "
            } else {
                ""
            } +
            "WHERE " +
            "$EventAlias.${EventTableInfo.Columns.PROGRAM_STAGE} = '${params.programStageId!!}' AND " +
            "${getEventWhereClause(params, context)} " +
            appendOrderBy(params.sorting) +
            appendPaging(params.pageConfig)
    }

    private suspend fun getEnrollmentSqlClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return "SELECT " +
            "${getEnrollmentSelectColumns(params, context)} " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
            if (params.hasOrgunit()) {
                "LEFT JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OrgUnitAlias " +
                    "ON $EnrollmentAlias.${EnrollmentTableInfo.Columns.ORGANISATION_UNIT} = " +
                    "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.UID} "
            } else {
                ""
            } +
            "WHERE " +
            "$EnrollmentAlias.${EnrollmentTableInfo.Columns.PROGRAM} = '${params.programId!!}' " +
            "AND ${getEnrollmentWhereClause(params, context)} " +
            appendOrderBy(params.sorting) +
            appendPaging(params.pageConfig)
    }

    private suspend fun getTrackedEntityInstanceSqlClause(
        params: TrackerLineListParams,
        context: TrackerLineListContext,
    ): String {
        return "SELECT " +
            "${getTrackedEntityInstanceSelectColumns(params, context)} " +
            "FROM ${TrackedEntityInstanceTableInfo.TABLE_INFO.name()} $TrackedEntityInstanceAlias " +
            if (params.hasOrgunit()) {
                "LEFT JOIN ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OrgUnitAlias " +
                    "ON $TrackedEntityInstanceAlias.${TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT} = " +
                    "$OrgUnitAlias.${OrganisationUnitTableInfo.Columns.UID} "
            } else {
                ""
            } +
            "WHERE " +
            "$TrackedEntityInstanceAlias.${TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE} = " +
            "'${params.trackedEntityTypeId!!}' AND " +
            "${getTrackedEntityInstanceWhereClause(params, context)} " +
            appendOrderBy(params.sorting) +
            appendPaging(params.pageConfig)
    }

    private suspend fun getEventSelectColumns(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return params.allItems.map {
            "(${TrackerLineListEvaluatorMapper.getEvaluator(it, context).getSelectSQLForEvent()}) '${it.id}'"
        }.joinToString(", ")
    }

    private suspend fun getEventWhereClause(params: TrackerLineListParams, context: TrackerLineListContext): String {
        return params.allItems.map {
            TrackerLineListEvaluatorMapper.getEvaluator(it, context).getWhereSQLForEvent()
        }.joinToString(" AND ")
    }

    private suspend fun getEnrollmentSelectColumns(
        params: TrackerLineListParams,
        context: TrackerLineListContext,
    ): String {
        return params.allItems.map {
            "(${TrackerLineListEvaluatorMapper.getEvaluator(it, context).getSelectSQLForEnrollment()}) '${it.id}'"
        }.joinToString(", ")
    }

    private suspend fun getEnrollmentWhereClause(
        params: TrackerLineListParams,
        context: TrackerLineListContext,
    ): String {
        val unflattenedRepeatedDataElements = params.allItems.groupBy { item ->
            when (item) {
                is TrackerLineListItem.ProgramDataElement -> item.stageDataElementIdx
                else -> item.id
            }
        }

        return unflattenedRepeatedDataElements.values.map { items ->
            val orClause = items.map {
                TrackerLineListEvaluatorMapper.getEvaluator(it, context).getWhereSQLForEnrollment()
            }.joinToString(" OR ")
            "($orClause)"
        }.joinToString(" AND ")
    }

    private suspend fun getTrackedEntityInstanceSelectColumns(
        params: TrackerLineListParams,
        context: TrackerLineListContext,
    ): String {
        return params.allItems.map {
            "(${
                TrackerLineListEvaluatorMapper.getEvaluator(
                    it,
                    context,
                ).getSelectSQLForTrackedEntityInstance()
            }) '${it.id}'"
        }.joinToString(", ")
    }

    private suspend fun getTrackedEntityInstanceWhereClause(
        params: TrackerLineListParams,
        context: TrackerLineListContext,
    ): String {
        val unflattenedRepeatedDataElements = params.allItems.groupBy { item ->
            when (item) {
                is TrackerLineListItem.ProgramDataElement -> item.stageDataElementIdx
                else -> item.id
            }
        }

        return unflattenedRepeatedDataElements.values.map { items ->
            val orClause = items.map {
                TrackerLineListEvaluatorMapper.getEvaluator(it, context).getWhereSQLForTrackedEntityInstance()
            }.joinToString(" OR ")
            "($orClause)"
        }.joinToString(" AND ")
    }

    private fun appendPaging(pageConfig: PageConfig): String {
        return when (pageConfig) {
            is PageConfig.NoPaging -> ""
            is PageConfig.Paging -> "LIMIT ${pageConfig.pageSize} OFFSET ${pageConfig.pageSize * (pageConfig.page - 1)}"
        }
    }

    private fun appendOrderBy(sorting: List<TrackerLineListSortingItem>): String {
        return if (sorting.isNotEmpty()) {
            " ORDER BY ${sorting.joinToString(", ") { item -> "\"${item.dimension.id}\" ${item.direction.name}" }} "
        } else {
            ""
        }
    }
}
