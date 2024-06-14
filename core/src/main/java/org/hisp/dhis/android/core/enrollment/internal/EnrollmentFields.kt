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

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteFields
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipFields

internal object EnrollmentFields {
    const val UID = "enrollment"
    const val ORGANISATION_UNIT = "orgUnit"
    private const val COORDINATE = "coordinate"
    const val DELETED = "deleted"
    private const val EVENTS = "events"
    const val NOTES = "notes"
    private const val GEOMETRY = "geometry"
    private const val RELATIONSHIPS = "relationships"
    private val fh = FieldsHelper<Enrollment>()

    val allFields: Fields<Enrollment> = commonFields()
        .fields(
            fh.nestedField<Event>(EVENTS).with(EventFields.allFields),
            fh.nestedField<Note>(NOTES).with(NoteFields.all),
            fh.nestedField<Relationship>(RELATIONSHIPS).with(RelationshipFields.allFields),
        ).build()

    val asRelationshipFields: Fields<Enrollment> = commonFields().build()

    private fun commonFields(): Fields.Builder<Enrollment> {
        return Fields.builder<Enrollment>().fields(
            fh.field(UID),
            fh.field(EnrollmentTableInfo.Columns.CREATED),
            fh.field(EnrollmentTableInfo.Columns.LAST_UPDATED),
            fh.field(EnrollmentTableInfo.Columns.CREATED_AT_CLIENT),
            fh.field(EnrollmentTableInfo.Columns.LAST_UPDATED_AT_CLIENT),
            fh.field(ORGANISATION_UNIT),
            fh.field(EnrollmentTableInfo.Columns.PROGRAM),
            fh.field(EnrollmentTableInfo.Columns.ENROLLMENT_DATE),
            fh.field(EnrollmentTableInfo.Columns.INCIDENT_DATE),
            fh.field(EnrollmentTableInfo.Columns.COMPLETED_DATE),
            fh.field(EnrollmentTableInfo.Columns.FOLLOW_UP),
            fh.field(EnrollmentTableInfo.Columns.STATUS),
            fh.field(DELETED),
            fh.field(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE),
            fh.field(COORDINATE),
            fh.field(GEOMETRY),
        )
    }
}
