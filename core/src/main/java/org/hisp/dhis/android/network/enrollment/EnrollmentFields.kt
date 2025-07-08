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
package org.hisp.dhis.android.network.enrollment

import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo.Columns
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.event.EventFields
import org.hisp.dhis.android.network.note.NoteFields
import org.hisp.dhis.android.network.relationship.RelationshipFields

internal object EnrollmentFields : BaseFields<Enrollment>() {
    const val UID = "enrollment"
    const val ORGANISATION_UNIT = "orgUnit"
    private const val COORDINATE = "coordinate"
    const val DELETED = "deleted"
    private const val EVENTS = "events"
    const val NOTES = "notes"
    private const val GEOMETRY = "geometry"
    private const val RELATIONSHIPS = "relationships"

    val allFields = Fields.from(
        commonFields(),
        fh.nestedField<Event>(EVENTS).with(EventFields.allFields),
        fh.nestedField<Note>(NOTES).with(NoteFields.allFields),
        fh.nestedField<Relationship>(RELATIONSHIPS).with(RelationshipFields.allFields),
    )

    val asRelationshipFields: Fields<Enrollment> = Fields.from(commonFields())

    private fun commonFields() = listOf(
        fh.field(UID),
        fh.field(Columns.CREATED),
        fh.field(Columns.LAST_UPDATED),
        fh.field(Columns.CREATED_AT_CLIENT),
        fh.field(Columns.LAST_UPDATED_AT_CLIENT),
        fh.field(ORGANISATION_UNIT),
        fh.field(Columns.PROGRAM),
        fh.field(Columns.ENROLLMENT_DATE),
        fh.field(Columns.INCIDENT_DATE),
        fh.field(Columns.COMPLETED_DATE),
        fh.field(Columns.FOLLOW_UP),
        fh.field(Columns.STATUS),
        fh.field(DELETED),
        fh.field(Columns.TRACKED_ENTITY_INSTANCE),
        fh.field(COORDINATE),
        fh.field(GEOMETRY),
    )
}
