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
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus

sealed class TrackerLineListItem(val id: String) {

    data class OrganisationUnitItem(val filters: List<OrganisationUnitFilter> = emptyList()) :
        TrackerLineListItem(Label.OrganisationUnit)

    data class LastUpdated(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.LastUpdated), DateItem

    data class IncidentDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.IncidentDate), DateItem

    data class EnrollmentDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.EnrollmentDate), DateItem

    data class ScheduledDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.ScheduledDate), DateItem

    data class EventDate(override val filters: List<DateFilter> = emptyList()) :
        TrackerLineListItem(Label.EventDate), DateItem

    data class ProgramIndicator(val uid: String, val filters: List<DataFilter> = emptyList()) :
        TrackerLineListItem(uid)

    data class ProgramAttribute(val uid: String, val filters: List<DataFilter> = emptyList()) :
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

    data class ProgramStatusItem(val filters: List<EnumFilter<EnrollmentStatus>> = emptyList()) :
        TrackerLineListItem(Label.ProgramStatus)

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
    data class EqualTo(val orgunitName: String) : OrganisationUnitFilter()
    data class NotEqualTo(val orgunitName: String) : OrganisationUnitFilter()
    data class EqualToIgnoreCase(val orgunitName: String) : OrganisationUnitFilter()
    data class NotEqualToIgnoreCase(val orgunitName: String) : OrganisationUnitFilter()
    data class Like(val orgunitName: String) : OrganisationUnitFilter()
    data class NotLike(val orgunitName: String) : OrganisationUnitFilter()
    data class LikeIgnoreCase(val orgunitName: String) : OrganisationUnitFilter()
    data class NotLikeIgnoreCase(val orgunitName: String) : OrganisationUnitFilter()
}

sealed class DateFilter {
    data class Relative(val relative: RelativePeriod) : DateFilter()
    data class Absolute(val uid: String) : DateFilter()
    data class Range(val startDate: String, val endDate: String) : DateFilter()
    data class EqualTo(val timestamp: String) : DateFilter()
    data class NotEqualTo(val timestamp: String) : DateFilter()
    data class EqualToIgnoreCase(val timestamp: String) : DateFilter()
    data class NotEqualToIgnoreCase(val timestamp: String) : DateFilter()
    data class Like(val timestamp: String) : DateFilter()
    data class NotLike(val timestamp: String) : DateFilter()
    data class LikeIgnoreCase(val timestamp: String) : DateFilter()
    data class NotLikeIgnoreCase(val timestamp: String) : DateFilter()
}

sealed class EnumFilter<T> {
    data class EqualTo<T>(val value: String) : EnumFilter<T>()
    data class NotEqualTo<T>(val value: String) : EnumFilter<T>()
    data class EqualToIgnoreCase<T>(val value: String) : EnumFilter<T>()
    data class NotEqualToIgnoreCase<T>(val value: String) : EnumFilter<T>()
    data class Like<T>(val value: String) : EnumFilter<T>()
    data class NotLike<T>(val value: String) : EnumFilter<T>()
    data class LikeIgnoreCase<T>(val value: String) : EnumFilter<T>()
    data class NotLikeIgnoreCase<T>(val value: String) : EnumFilter<T>()
    data class In<T>(val values: List<T>) : EnumFilter<T>()
}

sealed class DataFilter {
    data class EqualTo(val value: String) : DataFilter()
    data class NotEqualTo(val value: String) : DataFilter()
    data class EqualToIgnoreCase(val value: String) : DataFilter()
    data class NotEqualToIgnoreCase(val value: String) : DataFilter()
    data class GreaterThan(val value: String) : DataFilter()
    data class GreaterThanOrEqualTo(val value: String) : DataFilter()
    data class LowerThan(val value: String) : DataFilter()
    data class LowerThanOrEqualTo(val value: String) : DataFilter()
    data class Like(val value: String) : DataFilter()
    data class NotLike(val value: String) : DataFilter()
    data class LikeIgnoreCase(val value: String) : DataFilter()
    data class NotLikeIgnoreCase(val value: String) : DataFilter()
    data class In(val values: List<String>) : DataFilter()
}

internal object Label {
    const val OrganisationUnit = "ou"
    const val LastUpdated = "lastUpdated"
    const val IncidentDate = "incidentDate"
    const val EnrollmentDate = "enrollmentDate"
    const val ScheduledDate = "scheduledDate"
    const val EventDate = "eventDate"
    const val CreatedBy = "createdBy"
    const val LastUpdatedBy = "lastUpdatedBy"
    const val ProgramStatus = "programStatus"
    const val EventStatus = "eventStatus"
}
