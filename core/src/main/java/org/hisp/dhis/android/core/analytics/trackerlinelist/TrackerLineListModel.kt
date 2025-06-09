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

package org.hisp.dhis.android.core.analytics.trackerlinelist

import org.hisp.dhis.android.core.analytics.internal.AnalyticsModelHelper.eventDataElementId
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.common.SortingDirection
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus

sealed class TrackerLineListItem(val id: String) {

    data class OrganisationUnitItem(
        val programUid: String? = null,
        val filters: List<OrganisationUnitFilter> = emptyList(),
    ) :
        TrackerLineListItem(Label.OrganisationUnit + (programUid?.let { ".$it" } ?: ""))

    data class LastUpdated(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.LastUpdated), DateItem

    data class IncidentDate(val programUid: String? = null, override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.IncidentDate + (programUid?.let { ".$it" } ?: "")), DateItem

    data class EnrollmentDate(val programUid: String? = null, override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.EnrollmentDate + (programUid?.let { ".$it" } ?: "")), DateItem

    data class ScheduledDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.ScheduledDate), DateItem

    data class EventDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.EventDate), DateItem

    data class ProgramIndicator(val uid: String, val filters: List<DataFilter> = emptyList()) :
        TrackerLineListItem(uid)

    data class ProgramAttribute(val uid: String, val filters: List<DataFilter> = emptyList()) :
        TrackerLineListItem(uid)

    data class Category(val uid: String, val filters: List<DataFilter> = emptyList()) :
        TrackerLineListItem(uid)

    data class ProgramDataElement(
        val dataElement: String,
        val programStage: String,
        val filters: List<DataFilter> = emptyList(),
        val repetitionIndexes: List<Int>? = null,
    ) : TrackerLineListItem(
        eventDataElementId(programStage, dataElement) +
            (repetitionIndexes?.joinToString { it.toString() } ?: ""),
    ) {

        val stageDataElementIdx = eventDataElementId(programStage, dataElement)
    }

    data class ProgramStatusItem(
        val programUid: String? = null,
        val filters: List<EnumFilter<EnrollmentStatus>> = emptyList(),
    ) :
        TrackerLineListItem(Label.ProgramStatus + (programUid?.let { ".$it" } ?: ""))

    data class EventStatusItem(val filters: List<EnumFilter<EventStatus>> = emptyList()) :
        TrackerLineListItem(Label.EventStatus)

    object CreatedBy : TrackerLineListItem(Label.CreatedBy)

    object LastUpdatedBy : TrackerLineListItem(Label.LastUpdatedBy)
}

internal interface DateItem {
    val id: String
    val filters: List<DateFilter>
}

sealed class OrganisationUnitFilter {
    data class Absolute(val uid: String) : OrganisationUnitFilter()
    data class Relative(val relative: RelativeOrganisationUnit) : OrganisationUnitFilter()
    data class Level(val uid: String) : OrganisationUnitFilter()
    data class Group(val uid: String) : OrganisationUnitFilter()
    data class EqualTo(val orgunitName: String, val ignoreCase: Boolean = false) : OrganisationUnitFilter()
    data class NotEqualTo(val orgunitName: String, val ignoreCase: Boolean = false) : OrganisationUnitFilter()
    data class Like(val orgunitName: String, val ignoreCase: Boolean = true) : OrganisationUnitFilter()
    data class NotLike(val orgunitName: String, val ignoreCase: Boolean = true) : OrganisationUnitFilter()
}

sealed class DateFilter {
    data class Relative(val relative: RelativePeriod) : DateFilter()
    data class Absolute(val uid: String) : DateFilter()
    data class Range(val startDate: String, val endDate: String) : DateFilter()
    data class EqualTo(val timestamp: String, val ignoreCase: Boolean = false) : DateFilter()
    data class NotEqualTo(val timestamp: String, val ignoreCase: Boolean = false) : DateFilter()
    data class Like(val timestamp: String, val ignoreCase: Boolean = true) : DateFilter()
    data class NotLike(val timestamp: String, val ignoreCase: Boolean = true) : DateFilter()
}

sealed class EnumFilter<T> {
    data class EqualTo<T>(val value: String, val ignoreCase: Boolean = false) : EnumFilter<T>()
    data class NotEqualTo<T>(val value: String, val ignoreCase: Boolean = false) : EnumFilter<T>()
    data class Like<T>(val value: String, val ignoreCase: Boolean = true) : EnumFilter<T>()
    data class NotLike<T>(val value: String, val ignoreCase: Boolean = true) : EnumFilter<T>()
    data class In<T>(val values: List<T>) : EnumFilter<T>()
}

sealed class DataFilter {
    data class EqualTo(val value: String, val ignoreCase: Boolean = false) : DataFilter()
    data class NotEqualTo(val value: String, val ignoreCase: Boolean = false) : DataFilter()
    data class GreaterThan(val value: String) : DataFilter()
    data class GreaterThanOrEqualTo(val value: String) : DataFilter()
    data class LowerThan(val value: String) : DataFilter()
    data class LowerThanOrEqualTo(val value: String) : DataFilter()
    data class Like(val value: String, val ignoreCase: Boolean = true) : DataFilter()
    data class NotLike(val value: String, val ignoreCase: Boolean = true) : DataFilter()
    data class In(val values: List<String>) : DataFilter()
    data class IsNull(val value: Boolean) : DataFilter()
}

internal object Label {
    const val OrganisationUnit = "ouItem"
    const val LastUpdated = "lastUpdatedItem"
    const val IncidentDate = "incidentDateItem"
    const val EnrollmentDate = "enrollmentDateItem"
    const val ScheduledDate = "scheduledDateItem"
    const val EventDate = "eventDateItem"
    const val CreatedBy = "createdByItem"
    const val LastUpdatedBy = "lastUpdatedByItem"
    const val ProgramStatus = "programStatusItem"
    const val EventStatus = "eventStatusItem"
}

data class TrackerLineListSortingItem(
    val dimension: TrackerLineListItem,
    val direction: SortingDirection,
)
