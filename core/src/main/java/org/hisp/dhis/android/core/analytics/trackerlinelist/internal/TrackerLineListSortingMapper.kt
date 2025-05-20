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

import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.composedUidOperandRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.tripleComposedUidOperandRegex
import org.hisp.dhis.android.core.analytics.internal.AnalyticsRegex.uidRegex
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListSortingItem
import org.hisp.dhis.android.core.visualization.TrackerVisualizationSorting

@Suppress("ComplexMethod")
internal object TrackerLineListSortingMapper {
    fun mapSorting(
        sorting: List<TrackerVisualizationSorting>?,
        dimensions: List<TrackerLineListItem>,
    ): List<TrackerLineListSortingItem> {
        return sorting?.mapNotNull { sortingItem ->
            val sortDimension = dimensions.find { lineListItem ->
                when (lineListItem) {
                    is TrackerLineListItem.ProgramAttribute -> lineListItem.id == sortingItem.dimension()
                    is TrackerLineListItem.Category -> lineListItem.id == sortingItem.dimension()
                    is TrackerLineListItem.ProgramIndicator -> lineListItem.id == sortingItem.dimension()

                    is TrackerLineListItem.EventDate -> sortingItem.dimension() == "eventDate"
                    is TrackerLineListItem.ScheduledDate -> sortingItem.dimension() == "scheduledDate"
                    is TrackerLineListItem.EventStatusItem -> sortingItem.dimension() == "eventStatus"
                    is TrackerLineListItem.LastUpdated -> sortingItem.dimension() == "lastUpdated"

                    is TrackerLineListItem.ProgramDataElement -> checkIsProgramDataElement(sortingItem, lineListItem)
                    is TrackerLineListItem.EnrollmentDate -> checkIsEnrollmentDate(sortingItem, lineListItem)
                    is TrackerLineListItem.IncidentDate -> checkIsIncidentDate(sortingItem, lineListItem)
                    is TrackerLineListItem.ProgramStatusItem -> checkIsProgramStatus(sortingItem, lineListItem)
                    is TrackerLineListItem.OrganisationUnitItem -> checkIsOrgunit(sortingItem, lineListItem)

                    TrackerLineListItem.CreatedBy -> sortingItem.dimension() == "createdBy"
                    TrackerLineListItem.LastUpdatedBy -> sortingItem.dimension() == "lastUpdatedBy"
                }
            }

            sortDimension?.let {
                TrackerLineListSortingItem(it, sortingItem.direction())
            }
        } ?: emptyList()
    }

    private fun checkIsEnrollmentDate(
        sortingItem: TrackerVisualizationSorting,
        lineListItem: TrackerLineListItem.EnrollmentDate,
    ): Boolean {
        return sortingItem.dimension() == "enrollmentDate" ||
            matchesRegexProgramUid(
                sortingItem.dimension(),
                "^($uidRegex)\\.enrollmentdate$".toRegex(),
                lineListItem.programUid,
            )
    }

    private fun checkIsIncidentDate(
        sortingItem: TrackerVisualizationSorting,
        lineListItem: TrackerLineListItem.IncidentDate,
    ): Boolean {
        return sortingItem.dimension() == "incidentDate" ||
            matchesRegexProgramUid(
                sortingItem.dimension(),
                "^($uidRegex)\\.incidentdate$".toRegex(),
                lineListItem.programUid,
            )
    }

    private fun checkIsProgramStatus(
        sortingItem: TrackerVisualizationSorting,
        lineListItem: TrackerLineListItem.ProgramStatusItem,
    ): Boolean {
        return sortingItem.dimension() == "programStatus" ||
            matchesRegexProgramUid(
                sortingItem.dimension(),
                "^($uidRegex)\\.programstatus$".toRegex(),
                lineListItem.programUid,
            )
    }

    private fun checkIsOrgunit(
        sortingItem: TrackerVisualizationSorting,
        lineListItem: TrackerLineListItem.OrganisationUnitItem,
    ): Boolean {
        return sortingItem.dimension() == "ou" ||
            matchesRegexProgramUid(
                sortingItem.dimension(),
                "^($uidRegex)\\.ouname$".toRegex(),
                lineListItem.programUid,
            )
    }

    @Suppress("ReturnCount")
    private fun checkIsProgramDataElement(
        sortingItem: TrackerVisualizationSorting,
        lineListItem: TrackerLineListItem.ProgramDataElement,
    ): Boolean {
        val doubleRegex = composedUidOperandRegex.find(sortingItem.dimension())
        if (doubleRegex != null) {
            val (stage, dataElement) = doubleRegex.destructured
            return stage == lineListItem.programStage && dataElement == lineListItem.dataElement
        }

        val tripleRegex = tripleComposedUidOperandRegex.find(sortingItem.dimension())
        if (tripleRegex != null) {
            val (_, stage, dataElement) = tripleRegex.destructured
            return stage == lineListItem.programStage && dataElement == lineListItem.dataElement
        }

        return false
    }

    private fun matchesRegexProgramUid(dimension: String, regex: Regex, programUid: String?): Boolean {
        val matchResult = regex.find(dimension)
        return matchResult != null && matchResult.destructured.component1() == programUid
    }
}
