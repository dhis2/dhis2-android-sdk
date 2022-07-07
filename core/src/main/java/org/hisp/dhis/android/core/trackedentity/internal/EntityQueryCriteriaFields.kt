/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.DateFilterPeriodFields
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterTableInfo.Columns

internal object EntityQueryCriteriaFields {
    const val ATTRIBUTE_VALUE_FILTER = "attributeValueFilters"
    private val fh = FieldsHelper<EntityQueryCriteria>()
    val allFields: Fields<EntityQueryCriteria> = Fields.builder<EntityQueryCriteria>()
        .fields(
            fh.field<Boolean>(Columns.FOLLOW_UP),
            fh.field<String>(Columns.ORGANISATION_UNIT),
            fh.field<OrganisationUnitMode>(Columns.OU_MODE),
            fh.field<AssignedUserMode>(Columns.ASSIGNED_USER_MODE),
            fh.field<String>(Columns.ORDER),
            fh.field<String>(Columns.DISPLAY_COLUMN_ORDER),
            fh.field<EventStatus>(Columns.EVENT_STATUS),
            fh.field<String>(Columns.PROGRAM_STAGE),
            fh.field<String>(Columns.TRACKED_ENTITY_INSTANCES),
            fh.field<String>(Columns.TRACKED_ENTITY_TYPE),
            fh.field<EnrollmentStatus>(Columns.ENROLLMENT_STATUS),
            fh.nestedField<DateFilterPeriod>(Columns.EVENT_DATE)
                .with(DateFilterPeriodFields.allFields),
            fh.nestedField<DateFilterPeriod>(Columns.LAST_UPDATED_DATE)
                .with(DateFilterPeriodFields.allFields),
            fh.nestedField<DateFilterPeriod>(Columns.ENROLLMENT_INCIDENT_DATE)
                .with(DateFilterPeriodFields.allFields),
            fh.nestedField<DateFilterPeriod>(Columns.ENROLLMENT_CREATED_DATE)
                .with(DateFilterPeriodFields.allFields),
            fh.nestedField<AttributeValueFilter>(ATTRIBUTE_VALUE_FILTER).with(AttributeValueFilterFields.allFields)
        ).build()
}
