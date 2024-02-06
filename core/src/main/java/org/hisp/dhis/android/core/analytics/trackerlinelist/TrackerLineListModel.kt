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

import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus

sealed class TrackerLineListItem(val id: String) {

    sealed class OrganisationUnitItem(id: String) : TrackerLineListItem(id) {
        data class Absolute(val uid: String) : OrganisationUnitItem(uid)
        data class Relative(val relative: RelativeOrganisationUnit) : OrganisationUnitItem(relative.name)
        data class Level(val uid: String) : OrganisationUnitItem(uid)
        data class Group(val uid: String) : OrganisationUnitItem(uid)
    }

    sealed class DateItem(id: String) : TrackerLineListItem(id) {
        data class LastUpdated(val filters: List<DateFilter>) : DateItem(Label.LastUpdated)
        data class IncidentDate(val filters: List<DateFilter>) : DateItem(Label.IncidentDate)
        data class EnrollmentDate(val filters: List<DateFilter>) : DateItem(Label.EnrollmentDate)
        data class ScheduledDate(val filters: List<DateFilter>) : DateItem(Label.ScheduledDate)
        data class EventDate(val filters: List<DateFilter>) : DateItem(Label.EventDate)
    }

    data class ProgramIndicator(val uid: String, val filters: List<DataFilter>) : TrackerLineListItem(uid)

    data class ProgramAttribute(val uid: String, val filters: List<DataFilter>) : TrackerLineListItem(uid)

    data class ProgramDataElement(
        val uid: String,
        val program: String,
        val programStage: String,
        val filters: List<DataFilter>,
    ) : TrackerLineListItem("$program.$programStage.$uid")

    object CreatedBy : TrackerLineListItem(Label.CreatedBy)

    object LastUpdatedBy : TrackerLineListItem(Label.LastUpdatedBy)

    data class ProgramStatus(val filters: List<EnrollmentStatus>) : TrackerLineListItem(Label.ProgramStatus)

    data class EventStatus(val filters: List<EventStatus>) : TrackerLineListItem(Label.EventStatus)
}

sealed class DateFilter {
    data class Relative(val relative: RelativePeriod) : DateFilter()
    data class Absolute(val uid: String) : DateFilter()
    data class Range(val startDate: String, val endDate: String) : DateFilter()
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
