/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.enrollment.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.BaseFields
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.internal.NewEventFields
import org.hisp.dhis.android.core.note.NewTrackerImporterNote
import org.hisp.dhis.android.core.note.internal.NewNoteFields
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipFields

internal object NewEnrollmentFields : BaseFields<NewTrackerImporterEnrollment>() {
    private const val CREATED_AT = "createdAt"
    private const val UPDATED_AT = "updatedAt"
    private const val CREATED_AT_CLIENT = "createdAtClient"
    private const val UPDATED_AT_CLIENT = "updatedAtClient"
    private const val PROGRAM = "program"
    private const val ENROLLED_AT = "enrolledAt"
    private const val OCCURRED_AT = "occurredAt"
    private const val COMPLETED_AT = "completedAt"
    private const val FOLLOW_UP = "followUp"
    private const val STATUS = "status"
    const val TRACKED_ENTITY = "trackedEntity"
    const val UID = "enrollment"
    const val ORGANISATION_UNIT = "orgUnit"
    const val DELETED = "deleted"
    private const val EVENTS = "events"
    private const val NOTES = "notes"
    private const val GEOMETRY = "geometry"
    private const val RELATIONSHIPS = "relationships"

    val allFields = Fields.from(
        commonFields(),
        fh.nestedField<NewTrackerImporterEvent>(EVENTS).with(NewEventFields.allFields),
        fh.nestedField<NewTrackerImporterNote>(NOTES).with(NewNoteFields.allFields),
        fh.nestedField<NewTrackerImporterRelationship>(RELATIONSHIPS).with(NewRelationshipFields.allFields),
    )

    val asRelationshipFields = Fields.from(commonFields())

    private fun commonFields() = listOf(
        fh.field(UID),
        fh.field(CREATED_AT),
        fh.field(UPDATED_AT),
        fh.field(CREATED_AT_CLIENT),
        fh.field(UPDATED_AT_CLIENT),
        fh.field(ORGANISATION_UNIT),
        fh.field(PROGRAM),
        fh.field(ENROLLED_AT),
        fh.field(OCCURRED_AT),
        fh.field(COMPLETED_AT),
        fh.field(FOLLOW_UP),
        fh.field(STATUS),
        fh.field(DELETED),
        fh.field(TRACKED_ENTITY),
        fh.field(GEOMETRY),
    )
}
