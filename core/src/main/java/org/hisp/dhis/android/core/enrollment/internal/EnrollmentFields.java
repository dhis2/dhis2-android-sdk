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

package org.hisp.dhis.android.core.enrollment.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.Property;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo.Columns;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventFields;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.internal.NoteFields;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EnrollmentFields {

    public static final String UID = "enrollment";
    public static final String ORGANISATION_UNIT = "orgUnit";
    private static final String COORDINATE = "coordinate";
    public static final String DELETED = "deleted";
    private static final String EVENTS = "events";
    public static final String NOTES = "notes";
    private final static String GEOMETRY = "geometry";
    public static final String RELATIONSHIPS = "relationships";

    private static FieldsHelper<Enrollment> fh = new FieldsHelper<>();

    public static final Fields<Enrollment> allFields = Fields.<Enrollment>builder()
            .fields(getCommonFields())
            .fields(
                    fh.<Event>nestedField(EVENTS).with(EventFields.allFields),
                    fh.<Note>nestedField(NOTES).with(NoteFields.all),
                    fh.<Relationship>nestedField(RELATIONSHIPS).with(RelationshipFields.allFields)
            ).build();

    public static final Fields<Enrollment> asRelationshipFields = Fields.<Enrollment>builder()
            .fields(getCommonFields()).build();

    private static List<Property<Enrollment, ?>> getCommonFields() {
        return new ArrayList<>(Arrays.asList(
                fh.<String>field(UID),
                fh.<String>field(Columns.CREATED),
                fh.<String>field(Columns.LAST_UPDATED),
                fh.<String>field(ORGANISATION_UNIT),
                fh.<String>field(Columns.PROGRAM),
                fh.<String>field(Columns.ENROLLMENT_DATE),
                fh.<String>field(Columns.INCIDENT_DATE),
                fh.<String>field(Columns.COMPLETED_DATE),
                fh.<String>field(Columns.FOLLOW_UP),
                fh.<EnrollmentStatus>field(Columns.STATUS),
                fh.<Boolean>field(DELETED),
                fh.<String>field(Columns.TRACKED_ENTITY_INSTANCE),
                fh.<Coordinates>field(COORDINATE),
                fh.<Geometry>field(GEOMETRY)
        ));
    }

    private EnrollmentFields() {}
}