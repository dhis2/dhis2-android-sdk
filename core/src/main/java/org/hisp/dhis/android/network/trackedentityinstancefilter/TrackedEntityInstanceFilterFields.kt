/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.network.trackedentityinstancefilter

import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterTableInfo.Columns
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields

internal object TrackedEntityInstanceFilterFields : BaseFields<TrackedEntityInstanceFilter>() {
    private const val ENROLLMENT_CREATED_PERIOD = "enrollmentCreatedPeriod"
    const val FOLLOW_UP = "followup"
    const val EVENT_FILTERS = "eventFilters"
    private const val ENTITY_QUERY_CRITERIA = "entityQueryCriteria"

    val programUid = fh.field(Columns.PROGRAM + "." + BaseIdentifiableObject.UID)

    val allFields = Fields.from(
        commonFields(),
        fh.nestedField<EntityQueryCriteria>(ENTITY_QUERY_CRITERIA).with(EntityQueryCriteriaFields.allFields),
    )

    val allFields37 = Fields.from(
        commonFields(),
        fh.field(Columns.ENROLLMENT_STATUS),
        fh.field(FOLLOW_UP),
        fh.field(ENROLLMENT_CREATED_PERIOD),
    )

    private fun commonFields() =
        fh.getIdentifiableFields() + listOf(
            fh.field(Columns.DESCRIPTION),
            fh.field(Columns.SORT_ORDER),
            fh.nestedFieldWithUid(Columns.PROGRAM),
            fh.nestedField<TrackedEntityInstanceEventFilter>(EVENT_FILTERS)
                .with(TrackedEntityInstanceEventFilterFields.allFields),
        )
}
