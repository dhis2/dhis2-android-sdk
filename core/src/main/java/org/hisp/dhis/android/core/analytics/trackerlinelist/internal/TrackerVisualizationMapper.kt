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
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.dateRangeRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.orgunitGroupRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.orgunitLevelRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.singleUidRegex
import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.EnumFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.OrganisationUnitFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListSortingMapper.mapSorting
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimension
import org.hisp.dhis.android.core.visualization.TrackerVisualizationOutputType
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class TrackerVisualizationMapper(
    private val organisationUnitLevelStore: OrganisationUnitLevelStore,
) {
    fun toTrackerLineListParams(trackerVisualization: TrackerVisualization): TrackerLineListParams {
        val columns = mapDimensions(trackerVisualization.columns(), trackerVisualization)
        val filters = mapDimensions(trackerVisualization.filters(), trackerVisualization)
        val sorting = mapSorting(trackerVisualization.sorting(), columns + filters)

        return TrackerLineListParams(
            trackerVisualization = trackerVisualization.uid(),
            programId = trackerVisualization.program()?.uid(),
            programStageId = trackerVisualization.programStage()?.uid(),
            trackedEntityTypeId = trackerVisualization.trackedEntityType()?.uid(),
            outputType = mapOutputType(trackerVisualization.outputType()),
            columns = columns,
            filters = filters,
            sorting = sorting,
        )
    }

    private fun mapOutputType(type: TrackerVisualizationOutputType?): TrackerLineListOutputType? {
        return when (type) {
            TrackerVisualizationOutputType.ENROLLMENT -> TrackerLineListOutputType.ENROLLMENT
            TrackerVisualizationOutputType.EVENT -> TrackerLineListOutputType.EVENT
            TrackerVisualizationOutputType.TRACKED_ENTITY_INSTANCE -> TrackerLineListOutputType.TRACKED_ENTITY_INSTANCE
            else -> null
        }
    }

    private fun mapDimensions(
        dimensions: List<TrackerVisualizationDimension>?,
        trackerVisualization: TrackerVisualization,
    ): List<TrackerLineListItem> {
        return dimensions?.mapNotNull { item ->
            when (item.dimensionType()) {
                "ORGANISATION_UNIT" -> mapOrganisationUnit(item)
                "PERIOD" -> mapPeriod(item)
                "PROGRAM_INDICATOR" -> mapProgramIndicator(item)
                "PROGRAM_ATTRIBUTE" -> mapProgramAttribute(item)
                "PROGRAM_DATA_ELEMENT" -> mapProgramDataElement(item, trackerVisualization)
                "DATA_X" -> mapDataX(item)
                "CATEGORY" -> mapCategory(item)
                "ORGANISATION_UNIT_GROUP_SET" ->
                    throw AnalyticsException.InvalidArguments("Dimension ORGANISATION_UNIT_GROUP_SET IS not supported")

                else -> null
            }
        } ?: emptyList()
    }

    private fun mapOrganisationUnit(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return TrackerLineListItem.OrganisationUnitItem(
            programUid = item.program()?.uid(),
            filters = item.items()?.mapNotNull { it.uid() }?.mapNotNull { uid ->
                val relativeOrgunit = RelativeOrganisationUnit.entries.find { it.name == uid }

                when {
                    relativeOrgunit != null -> {
                        OrganisationUnitFilter.Relative(relativeOrgunit)
                    }

                    orgunitLevelRegex.matches(uid) -> {
                        val (levelNumber) = orgunitLevelRegex.find(uid)!!.destructured
                        val level = organisationUnitLevelStore.selectOneWhere(
                            WhereClauseBuilder()
                                .appendKeyNumberValue(OrganisationUnitLevelTableInfo.Columns.LEVEL, levelNumber.toInt())
                                .build(),
                        ) ?: throw AnalyticsException.InvalidOrganisationUnitLevel(levelNumber)
                        OrganisationUnitFilter.Level(level.uid())
                    }

                    orgunitGroupRegex.matches(uid) -> {
                        val (groupUid) = orgunitGroupRegex.find(uid)!!.destructured
                        OrganisationUnitFilter.Group(groupUid)
                    }

                    singleUidRegex.matches(uid) -> {
                        OrganisationUnitFilter.Absolute(uid)
                    }

                    else -> null
                }
            } ?: emptyList(),
        )
    }

    private fun mapPeriod(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return when (item.dimension()) {
            "lastUpdated" -> TrackerLineListItem.LastUpdated(mapDateFilters(item))
            "incidentDate" -> TrackerLineListItem.IncidentDate(mapRelatedProgram(item), mapDateFilters(item))
            "enrollmentDate" -> TrackerLineListItem.EnrollmentDate(mapRelatedProgram(item), mapDateFilters(item))
            "scheduledDate" -> TrackerLineListItem.ScheduledDate(mapDateFilters(item))
            "eventDate" -> TrackerLineListItem.EventDate(mapDateFilters(item))
            else -> null
        }
    }

    private fun mapProgramIndicator(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return item.dimension()?.let { uid ->
            TrackerLineListItem.ProgramIndicator(uid, mapDataFilters(item))
        }
    }

    private fun mapProgramAttribute(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return item.dimension()?.let { uid ->
            TrackerLineListItem.ProgramAttribute(uid, mapDataFilters(item))
        }
    }

    private fun mapProgramDataElement(
        item: TrackerVisualizationDimension,
        trackerVisualization: TrackerVisualization,
    ): TrackerLineListItem? {
        return item.dimension()?.let { uid ->
            TrackerLineListItem.ProgramDataElement(
                uid,
                item.programStage()?.uid() ?: trackerVisualization.programStage()!!.uid(),
                mapDataFilters(item),
                item.repetition()?.indexes(),
            )
        }
    }

    internal fun mapDataX(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return when (item.dimension()) {
            "createdBy" -> TrackerLineListItem.CreatedBy
            "lastUpdatedBy" -> TrackerLineListItem.LastUpdatedBy
            "programStatus" -> TrackerLineListItem.ProgramStatusItem(
                programUid = item.program()?.uid(),
                filters = item.items()?.mapNotNull { e -> EnrollmentStatus.entries.find { it.name == e.uid() } }
                    .takeIf { !it.isNullOrEmpty() }
                    ?.let { statuses -> listOf(EnumFilter.In(statuses)) }
                    ?: emptyList(),
            )

            "eventStatus" -> TrackerLineListItem.EventStatusItem(
                filters = item.items()?.mapNotNull { e -> EventStatus.entries.find { it.name == e.uid() } }
                    .takeIf { !it.isNullOrEmpty() }
                    ?.let { statuses -> listOf(EnumFilter.In(statuses)) }
                    ?: emptyList(),
            )

            else -> null
        }
    }

    internal fun mapCategory(item: TrackerVisualizationDimension): TrackerLineListItem? {
        val filters = item.items()
            .takeIf { !it.isNullOrEmpty() }
            ?.let { listOf(DataFilter.In(it.map { it.uid() })) }
            ?: emptyList()
        return item.dimension()?.let { uid ->
            TrackerLineListItem.Category(uid, filters)
        }
    }

    @Suppress("ComplexMethod")
    internal fun mapDataFilters(item: TrackerVisualizationDimension): List<DataFilter> {
        return if (item.filter().isNullOrEmpty()) {
            emptyList()
        } else {
            val filterPairs = item.filter()!!.split(":").chunked(2)

            filterPairs.mapNotNull { filterPair ->
                val operator = filterPair.getOrNull(0)
                val value = filterPair.getOrNull(1)
                if (operator != null && value != null) {
                    when (operator) {
                        "EQ" -> DataFilter.EqualTo(value, ignoreCase = false)
                        "!EQ" -> DataFilter.NotEqualTo(value, ignoreCase = false)
                        "IEQ" -> DataFilter.EqualTo(value, ignoreCase = true)
                        "!IEQ" -> DataFilter.NotEqualTo(value, ignoreCase = true)
                        "GT" -> DataFilter.GreaterThan(value)
                        "GE" -> DataFilter.GreaterThanOrEqualTo(value)
                        "LT" -> DataFilter.LowerThan(value)
                        "LE" -> DataFilter.LowerThanOrEqualTo(value)
                        "NE" -> DataFilter.NotEqualTo(value)
                        "LIKE" -> DataFilter.Like(value, ignoreCase = false)
                        "!LIKE" -> DataFilter.NotLike(value, ignoreCase = false)
                        "ILIKE" -> DataFilter.Like(value, ignoreCase = true)
                        "!ILIKE" -> DataFilter.NotLike(value, ignoreCase = true)
                        "IN" -> DataFilter.In(value.split(";"))
                        else -> null
                    }
                } else {
                    null
                }
            }
        }
    }

    internal fun mapDateFilters(item: TrackerVisualizationDimension): List<DateFilter> {
        return item.items()?.mapNotNull { it.uid() }?.map { uid ->
            val relativePeriod = RelativePeriod.entries.find { it.name == uid }

            when {
                relativePeriod != null -> {
                    DateFilter.Relative(relativePeriod)
                }

                dateRangeRegex.matches(uid) -> {
                    val (start, end) = dateRangeRegex.find(uid)!!.destructured
                    DateFilter.Range(start, end)
                }

                else -> {
                    DateFilter.Absolute(uid)
                }
            }
        } ?: emptyList()
    }

    internal fun mapRelatedProgram(item: TrackerVisualizationDimension): String? {
        return item.program()?.uid()
    }
}
