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
package org.hisp.dhis.android.core.event.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.Coordinates
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteFields
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipFields
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueFields

internal object EventFields {
    const val UID = "event"
    private const val COORDINATE = "coordinate"
    const val ORGANISATION_UNIT = "orgUnit"
    const val TRACKED_ENTITY_DATA_VALUES = "dataValues"
    private const val GEOMETRY = "geometry"
    const val NOTES = "notes"
    const val RELATIONSHIPS = "relationships"

    private val fh = FieldsHelper<Event>()

    val allFields: Fields<Event> = commonFields()
        .fields(
            fh.nestedField<Note>(NOTES).with(NoteFields.all),
            fh.nestedField<Relationship>(RELATIONSHIPS).with(RelationshipFields.allFields),
            fh.nestedField<TrackedEntityDataValue>(TRACKED_ENTITY_DATA_VALUES)
                .with(TrackedEntityDataValueFields.allFields)
        ).build()

    val asRelationshipFields: Fields<Event> = commonFields().build()

    private fun commonFields(): Fields.Builder<Event> {
        return Fields.builder<Event>().fields(
            fh.field<String>(UID),
            fh.field<String>(EventTableInfo.Columns.ENROLLMENT),
            fh.field<String>(EventTableInfo.Columns.CREATED),
            fh.field<String>(EventTableInfo.Columns.LAST_UPDATED),
            fh.field<String>(EventTableInfo.Columns.CREATED_AT_CLIENT),
            fh.field<String>(EventTableInfo.Columns.LAST_UPDATED_AT_CLIENT),
            fh.field<EventStatus>(EventTableInfo.Columns.STATUS),
            fh.field<Coordinates>(COORDINATE),
            fh.field<Geometry>(GEOMETRY),
            fh.field<String>(EventTableInfo.Columns.PROGRAM),
            fh.field<String>(EventTableInfo.Columns.PROGRAM_STAGE),
            fh.field<String>(ORGANISATION_UNIT),
            fh.field<String>(EventTableInfo.Columns.EVENT_DATE),
            fh.field<String>(EventTableInfo.Columns.COMPLETE_DATE),
            fh.field<Boolean>(EventTableInfo.Columns.DELETED),
            fh.field<String>(EventTableInfo.Columns.DUE_DATE),
            fh.field<String>(EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO),
            fh.field<String>(EventTableInfo.Columns.ASSIGNED_USER)
        )
    }
}
