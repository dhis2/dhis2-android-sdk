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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueFields;

public final class EventFields {

    static final String UID = "event";
    public static final String ENROLLMENT = "enrollment";
    static final String CREATED = "created";
    static final String LAST_UPDATED = "lastUpdated";
    static final String STATUS = "status";
    private static final String COORDINATE = "coordinate";
    static final String PROGRAM = "program";
    static final String PROGRAM_STAGE = "programStage";
    static final String ORGANISATION_UNIT = "orgUnit";
    static final String EVENT_DATE = "eventDate";
    static final String COMPLETE_DATE = "completedDate";
    static final String DUE_DATE = "dueDate";
    private static final String DELETED = "deleted";
    static final String TRACKED_ENTITY_DATA_VALUES = "dataValues";
    static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";

    private static FieldsHelper<Event> fh = new FieldsHelper<>();

    public static final Fields<Event> allFields = Fields.<Event>builder()
            .fields(fh.<String>field(UID),
                    fh.<String>field(ENROLLMENT),
                    fh.<String>field(CREATED),
                    fh.<String>field(LAST_UPDATED),
                    fh.<EventStatus>field(STATUS),
                    fh.<Coordinates>field(COORDINATE),
                    fh.<String>field(PROGRAM),
                    fh.<String>field(PROGRAM_STAGE),
                    fh.<String>field(ORGANISATION_UNIT),
                    fh.<String>field(EVENT_DATE),
                    fh.<String>field(COMPLETE_DATE),
                    fh.<Boolean>field(DELETED),
                    fh.<String>field(DUE_DATE),
                    fh.<String>field(ATTRIBUTE_OPTION_COMBO),
                    fh.<TrackedEntityDataValue>nestedField(TRACKED_ENTITY_DATA_VALUES)
                            .with(TrackedEntityDataValueFields.allFields)
    ).build();

    private EventFields() {}

}