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

import org.hisp.dhis.android.core.arch.api.fields.internal.Field
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.FilterPeriod
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterTableInfo

object TrackedEntityInstanceFilterFields {
    private const val ENROLLMENT_CREATED_PERIOD = "enrollmentCreatedPeriod"
    const val FOLLOW_UP = "followup"
    const val EVENT_FILTERS = "eventFilters"
    const val ENTITY_QUERY_CRITERIA = "entityQueryCriteria"

    private val fh = FieldsHelper<TrackedEntityInstanceFilter>()

    val programUid: Field<TrackedEntityInstanceFilter, String> =
        Field.create(TrackedEntityInstanceFilterTableInfo.Columns.PROGRAM + "." + BaseIdentifiableObject.UID)

    private val commonFields = Fields.builder<TrackedEntityInstanceFilter>()
        .fields(fh.getIdentifiableFields())
        .fields(
            fh.nestedFieldWithUid(TrackedEntityInstanceFilterTableInfo.Columns.PROGRAM),
            fh.field<String>(TrackedEntityInstanceFilterTableInfo.Columns.DESCRIPTION),
            fh.field<Int>(TrackedEntityInstanceFilterTableInfo.Columns.SORT_ORDER),
            fh.nestedField<TrackedEntityInstanceEventFilter>(EVENT_FILTERS)
                .with(TrackedEntityInstanceEventFilterFields.allFields)
        )

    val allFields: Fields<TrackedEntityInstanceFilter> = commonFields
        .fields(
            fh.nestedField<EntityQueryCriteria>(ENTITY_QUERY_CRITERIA)
                .with(EntityQueryCriteriaFields.allFields)
        ).build()

    val allFieldsAPI37: Fields<TrackedEntityInstanceFilter> = commonFields
        .fields(
            fh.field<EnrollmentStatus>(TrackedEntityInstanceFilterTableInfo.Columns.ENROLLMENT_STATUS),
            fh.field<Boolean>(FOLLOW_UP),
            fh.field<FilterPeriod>(ENROLLMENT_CREATED_PERIOD)
        ).build()
}
