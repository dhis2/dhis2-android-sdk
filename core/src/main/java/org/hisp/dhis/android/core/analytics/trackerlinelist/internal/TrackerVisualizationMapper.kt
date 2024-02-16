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
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.uidRegex
import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.DateFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.OrganisationUnitFilter
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
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
internal class TrackerVisualizationMapper(
    private val organisationUnitLevelStore: OrganisationUnitLevelStore
) {
    fun toTrackerLineListParams(trackerVisualization: TrackerVisualization): TrackerLineListParams {
        return TrackerLineListParams(
            trackerVisualization = trackerVisualization.uid(),
            programId = trackerVisualization.program()?.uid(),
            programStageId = trackerVisualization.programStage()?.uid(),
            outputType = mapOutputType(trackerVisualization.outputType()),
            columns = mapDimensions(trackerVisualization.columns()),
            filters = mapDimensions(trackerVisualization.filters()),
        )
    }

    private fun mapOutputType(type: TrackerVisualizationOutputType?): TrackerLineListOutputType? {
        return when (type) {
            TrackerVisualizationOutputType.ENROLLMENT -> TrackerLineListOutputType.ENROLLMENT
            TrackerVisualizationOutputType.EVENT -> TrackerLineListOutputType.EVENT
            else -> null
        }
    }

    private fun mapDimensions(dimensions: List<TrackerVisualizationDimension>?): List<TrackerLineListItem> {
        return dimensions?.mapNotNull { item ->
            val mapper = when (item.dimensionType()) {
                "ORGANISATION_UNIT" -> ::mapOrganisationUnit
                "ORGANISATION_UNIT_GROUP_SET" -> ::mapOrganisationUnitGroup
                "PERIOD" -> ::mapPeriod
                "PROGRAM_INDICATOR" -> ::mapProgramIndicator
                "PROGRAM_ATTRIBUTE" -> ::mapProgramAttribute
                "PROGRAM_DATA_ELEMENT" -> ::mapProgramDataElement
                "DATA_X" -> ::mapDataX
                else -> { _ -> null }
            }
            mapper(item)
        } ?: emptyList()
    }

    private fun mapOrganisationUnit(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return TrackerLineListItem.OrganisationUnitItem(
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

                    uidRegex.matches(uid) -> {
                        OrganisationUnitFilter.Absolute(uid)
                    }

                    else -> null
                }
            } ?: emptyList()
        )
    }

    private fun mapOrganisationUnitGroup(item: TrackerVisualizationDimension): TrackerLineListItem? {
        // TODO
        return null
    }

    private fun mapPeriod(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return when (item.dimension()) {
            "lastUpdated" -> TrackerLineListItem.DateItem.LastUpdated(mapDateFilters(item))
            "incidentDate" -> TrackerLineListItem.DateItem.IncidentDate(mapDateFilters(item))
            "enrollmentDate" -> TrackerLineListItem.DateItem.EnrollmentDate(mapDateFilters(item))
            "scheduledDate" -> TrackerLineListItem.DateItem.ScheduledDate(mapDateFilters(item))
            "eventDate" -> TrackerLineListItem.DateItem.EventDate(mapDateFilters(item))
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

    private fun mapProgramDataElement(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return item.dimension()?.let { uid ->
            TrackerLineListItem
                .ProgramDataElement(uid, item.program()?.uid(), item.programStage()?.uid(), mapDataFilters(item))
        }
    }

    private fun mapDataX(item: TrackerVisualizationDimension): TrackerLineListItem? {
        return when (item.dimension()) {
            "createdBy" -> TrackerLineListItem.CreatedBy
            "lastUpdatedBy" -> TrackerLineListItem.LastUpdatedBy
            "programStatus" -> TrackerLineListItem.ProgramStatusItem(
                filters = item.items()?.mapNotNull { e -> EnrollmentStatus.entries.find { it.name == e.uid() } }
                    ?: emptyList()
            )

            "eventStatus" -> TrackerLineListItem.EventStatusItem(
                filters = item.items()?.mapNotNull { e -> EventStatus.entries.find { it.name == e.uid() } }
                    ?: emptyList()
            )

            else -> null
        }
    }

    private fun mapDataFilters(item: TrackerVisualizationDimension): List<DataFilter> {
        return if (item.filter().isNullOrEmpty()) {
            emptyList()
        } else {
            val filterPairs = item.filter()!!.split(":").chunked(2)

            filterPairs.mapNotNull { filterPair ->
                val operator = filterPair.getOrNull(0)
                val value = filterPair.getOrNull(1)
                if (operator != null && value != null) {
                    when (operator) {
                        "EQ" -> DataFilter.EqualTo(value)
                        "!EQ" -> DataFilter.NotEqualTo(value)
                        "IEQ" -> DataFilter.EqualToIgnoreCase(value)
                        "!IEQ" -> DataFilter.NotEqualToIgnoreCase(value)
                        "GT" -> DataFilter.GreaterThan(value)
                        "GE" -> DataFilter.GreaterThanOrEqualTo(value)
                        "LT" -> DataFilter.LowerThan(value)
                        "LE" -> DataFilter.LowerThanOrEqualTo(value)
                        "NE" -> DataFilter.NotEqualTo(value)
                        "LIKE" -> DataFilter.Like(value)
                        "!LIKE" -> DataFilter.NotLike(value)
                        "ILIKE" -> DataFilter.LikeIgnoreCase(value)
                        "!ILIKE" -> DataFilter.NotLikeIgnoreCase(value)
                        "IN" -> DataFilter.In(value.split(";"))
                        else -> null
                    }
                } else {
                    null
                }
            }
        }
    }

    private fun mapDateFilters(item: TrackerVisualizationDimension): List<DateFilter> {
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
}
