/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.support.annotation.Nullable;

public class CreateProgramUtils {
    /**
     * BaseIdentifiable properties
     */
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATE = "2011-12-24T12:24:25.203";

    /**
     * BaseNameableProperties
     */
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";

    /**
     * Properties bound to Program
     */

    private static final Integer VERSION = 1;
    private static final Boolean ONLY_ENROLL_ONCE = true;
    private static final String ENROLLMENT_DATE_LABEL = "enrollment date";
    private static final Boolean DISPLAY_INCIDENT_DATE = true;
    private static final String INCIDENT_DATE_LABEL = "incident date label";
    private static final Boolean REGISTRATION = true;
    private static final Boolean SELECT_ENROLLMENT_DATES_IN_FUTURE = true;
    private static final Boolean DATA_ENTRY_METHOD = true;
    private static final Boolean IGNORE_OVERDUE_EVENTS = false;
    private static final Boolean RELATIONSHIP_FROM_A = true;
    private static final Boolean SELECT_INCIDENT_DATES_IN_FUTURE = true;
    private static final Boolean CAPTURE_COORDINATES = true;
    private static final Boolean USE_FIRST_STAGE_DURING_REGISTRATION = true;
    private static final Boolean DISPLAY_FRONT_PAGE_LIST = true;
    private static final ProgramType PROGRAM_TYPE = ProgramType.WITH_REGISTRATION;
    private static final String RELATIONSHIP_TEXT = "test relationship";
    private static final String RELATED_PROGRAM = "RelatedProgramUid";

    /**
     * A method to createTrackedEntityAttribute ContentValues for a Program.
     * To be used by other tests.
     *  @param id
     * @param uid
     * @param relationshipTypeUid
     * @param relatedProgram
     * @param trackedEntityUid @return
     */
    public static ContentValues create(long id, String uid,
                                       @Nullable String relationshipTypeUid,
                                       String relatedProgram, @Nullable String trackedEntityUid) {

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.ID, id);
        program.put(ProgramModel.Columns.UID, uid);
        program.put(ProgramModel.Columns.CODE, CODE);
        program.put(ProgramModel.Columns.NAME, NAME);
        program.put(ProgramModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        program.put(ProgramModel.Columns.CREATED, DATE);
        program.put(ProgramModel.Columns.LAST_UPDATED, DATE);
        program.put(ProgramModel.Columns.SHORT_NAME, SHORT_NAME);
        program.put(ProgramModel.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        program.put(ProgramModel.Columns.DESCRIPTION, DESCRIPTION);
        program.put(ProgramModel.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        program.put(ProgramModel.Columns.VERSION, VERSION);
        program.put(ProgramModel.Columns.ONLY_ENROLL_ONCE, ONLY_ENROLL_ONCE);
        program.put(ProgramModel.Columns.ENROLLMENT_DATE_LABEL, ENROLLMENT_DATE_LABEL);
        program.put(ProgramModel.Columns.DISPLAY_INCIDENT_DATE, DISPLAY_INCIDENT_DATE);
        program.put(ProgramModel.Columns.INCIDENT_DATE_LABEL, INCIDENT_DATE_LABEL);
        program.put(ProgramModel.Columns.REGISTRATION, REGISTRATION);
        program.put(ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE, SELECT_ENROLLMENT_DATES_IN_FUTURE);
        program.put(ProgramModel.Columns.DATA_ENTRY_METHOD, DATA_ENTRY_METHOD);
        program.put(ProgramModel.Columns.IGNORE_OVERDUE_EVENTS, IGNORE_OVERDUE_EVENTS);
        program.put(ProgramModel.Columns.RELATIONSHIP_FROM_A, RELATIONSHIP_FROM_A);
        program.put(ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE, SELECT_INCIDENT_DATES_IN_FUTURE);
        program.put(ProgramModel.Columns.CAPTURE_COORDINATES, CAPTURE_COORDINATES);
        program.put(ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION, USE_FIRST_STAGE_DURING_REGISTRATION);
        program.put(ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST, DISPLAY_FRONT_PAGE_LIST);
        program.put(ProgramModel.Columns.PROGRAM_TYPE, PROGRAM_TYPE.name());
        if(relationshipTypeUid == null) {
            program.putNull(ProgramModel.Columns.RELATIONSHIP_TYPE);
        } else {
            program.put(ProgramModel.Columns.RELATIONSHIP_TYPE, relationshipTypeUid);
        }
        program.put(ProgramModel.Columns.RELATIONSHIP_TEXT, RELATIONSHIP_TEXT);
        if (relatedProgram == null) {
            program.putNull(ProgramModel.Columns.RELATED_PROGRAM);
        } else {
            program.put(ProgramModel.Columns.RELATED_PROGRAM, RELATED_PROGRAM);
        }
        if(trackedEntityUid == null) {
            program.putNull(ProgramModel.Columns.TRACKED_ENTITY);
        } else {
            program.put(ProgramModel.Columns.TRACKED_ENTITY, trackedEntityUid);
        }
        return program;
    }
}
