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
package org.hisp.dhis.android.network.event

import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventTableInfo.Columns
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.note.NoteFields
import org.hisp.dhis.android.network.relationship.RelationshipFields

internal object EventFields : BaseFields<Event>() {
    const val UID = "event"
    private const val COORDINATE = "coordinate"
    const val ORGANISATION_UNIT = "orgUnit"
    const val TRACKED_ENTITY_DATA_VALUES = "dataValues"
    const val TRACKED_ENTITY_INSTANCE = "trackedEntityInstance"
    private const val GEOMETRY = "geometry"
    const val NOTES = "notes"
    private const val RELATIONSHIPS = "relationships"

    val allFields = Fields.from(
        commonFields(),
        fh.nestedField<Note>(NOTES).with(NoteFields.allFields),
        fh.nestedField<Relationship>(RELATIONSHIPS).with(RelationshipFields.allFields),
        fh.nestedField<TrackedEntityDataValue>(TRACKED_ENTITY_DATA_VALUES).with(TrackedEntityDataValueFields.allFields),
    )

    val asRelationshipFields = Fields.from(commonFields())

    val teiQueryFields = Fields.from(
        commonFields(),
        fh.field(TRACKED_ENTITY_INSTANCE),
    )

    private fun commonFields() = listOf(
        fh.field(UID),
        fh.field(Columns.ENROLLMENT),
        fh.field(Columns.CREATED),
        fh.field(Columns.LAST_UPDATED),
        fh.field(Columns.CREATED_AT_CLIENT),
        fh.field(Columns.LAST_UPDATED_AT_CLIENT),
        fh.field(Columns.STATUS),
        fh.field(COORDINATE),
        fh.field(GEOMETRY),
        fh.field(Columns.PROGRAM),
        fh.field(Columns.PROGRAM_STAGE),
        fh.field(ORGANISATION_UNIT),
        fh.field(Columns.EVENT_DATE),
        fh.field(Columns.COMPLETE_DATE),
        fh.field(Columns.DELETED),
        fh.field(Columns.DUE_DATE),
        fh.field(Columns.ATTRIBUTE_OPTION_COMBO),
        fh.field(Columns.ASSIGNED_USER),
        fh.field(Columns.COMPLETED_BY),
    )
}
