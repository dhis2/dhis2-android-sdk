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

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.Property;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.internal.NoteFields;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hisp.dhis.android.core.event.EventTableInfo.Columns;

public final class EventFields {

    public static final String UID = "event";
    private static final String COORDINATE = "coordinate";
    public static final String ORGANISATION_UNIT = "orgUnit";
    public static final String TRACKED_ENTITY_DATA_VALUES = "dataValues";
    private final static String GEOMETRY = "geometry";
    public static final String NOTES = "notes";
    public static final String RELATIONSHIPS = "relationships";

    private static FieldsHelper<Event> fh = new FieldsHelper<>();

    public static final Fields<Event> allFields = Fields.<Event>builder()
            .fields(getCommonFields())
            .fields(
                    fh.<Note>nestedField(NOTES).with(NoteFields.all),
                    fh.<Relationship>nestedField(RELATIONSHIPS).with(RelationshipFields.allFields),
                    fh.<TrackedEntityDataValue>nestedField(TRACKED_ENTITY_DATA_VALUES)
                            .with(TrackedEntityDataValueFields.allFields)
            ).build();

    public static final Fields<Event> asRelationshipFields = Fields.<Event>builder()
            .fields(getCommonFields()).build();

    private static List<Property<Event, ?>> getCommonFields() {
        return new ArrayList<>(Arrays.asList(
                fh.<String>field(UID),
                fh.<String>field(Columns.ENROLLMENT),
                fh.<String>field(Columns.CREATED),
                fh.<String>field(Columns.LAST_UPDATED),
                fh.<EventStatus>field(Columns.STATUS),
                fh.<Coordinates>field(COORDINATE),
                fh.<Geometry>field(GEOMETRY),
                fh.<String>field(Columns.PROGRAM),
                fh.<String>field(Columns.PROGRAM_STAGE),
                fh.<String>field(ORGANISATION_UNIT),
                fh.<String>field(Columns.EVENT_DATE),
                fh.<String>field(Columns.COMPLETE_DATE),
                fh.<Boolean>field(Columns.DELETED),
                fh.<String>field(Columns.DUE_DATE),
                fh.<String>field(Columns.ATTRIBUTE_OPTION_COMBO),
                fh.<String>field(Columns.ASSIGNED_USER)
        ));
    }

    private EventFields() {}

}