/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteFields;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventFields;

public final class EnrollmentFields {

    public static final String UID = "enrollment";
    public static final String CREATED = "created";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String ORGANISATION_UNIT = "orgUnit";
    public static final String PROGRAM = "program";
    public static final String ENROLLMENT_DATE = "enrollmentDate";
    public static final String INCIDENT_DATE = "incidentDate";
    public static final String FOLLOW_UP = "followup";
    public static final String STATUS = "status";
    public static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    public static final String COORDINATE = "coordinate";
    public static final String DELETED = "deleted";
    public static final String EVENTS = "events";
    public static final String NOTES = "notes";

    private static FieldsHelper<Enrollment> fh = new FieldsHelper<>();

    public static final Fields<Enrollment> allFields = Fields.<Enrollment>builder()
            .fields(fh.<String>field(UID),
                    fh.<String>field(CREATED),
                    fh.<String>field(LAST_UPDATED),
                    fh.<String>field(ORGANISATION_UNIT),
                    fh.<String>field(PROGRAM),
                    fh.<String>field(ENROLLMENT_DATE),
                    fh.<String>field(INCIDENT_DATE),
                    fh.<String>field(FOLLOW_UP),
                    fh.<EnrollmentStatus>field(STATUS),
                    fh.<Boolean>field(DELETED),
                    fh.<String>field(TRACKED_ENTITY_INSTANCE),
                    fh.<Coordinates>field(COORDINATE),
                    fh.<Event>nestedField(EVENTS).with(EventFields.allFields),
                    fh.<Note>nestedField(NOTES).with(NoteFields.all)
    ).build();

    private EnrollmentFields() {}
}