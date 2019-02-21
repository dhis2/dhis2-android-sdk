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

package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.FormType;

public class CreateProgramStageUtils {

    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String EXECUTION_DATE_LABEL = "test_executionDateLabel";
    private static final Integer ALLOW_GENERATE_NEXT_VISIT = 0;
    private static final Integer VALID_COMPLETE_ONLY = 0;
    private static final String REPORT_DATE_TO_USE = "test_reportDateToUse";
    private static final Integer OPEN_AFTER_ENROLLMENT = 0;

    private static final Integer REPEATABLE = 0;
    private static final Integer CAPTURE_COORDINATES = 1;
    private static final FormType FORM_TYPE = FormType.DEFAULT;
    private static final Integer DISPLAY_GENERATE_EVENT_BOX = 1;
    private static final Integer GENERATED_BY_ENROLMENT_DATE = 1;
    private static final Integer AUTO_GENERATE_EVENT = 0;
    private static final Integer SORT_ORDER = 0;
    private static final Integer HIDE_DUE_DATE = 1;
    private static final Integer BLOCK_ENTRY_FORM = 0;
    private static final Integer MIN_DAYS_FROM_START = 5;
    private static final Integer STANDARD_INTERVAL = 7;

    // used for timestamps
    private static final String DATE = "2017-01-05T15:39:00.000";

    public static ContentValues create(long id, String uid, String programId) {
        ContentValues programStage = new ContentValues();
        programStage.put(BaseIdentifiableObjectModel.Columns.ID, id);
        programStage.put(BaseIdentifiableObjectModel.Columns.UID, uid);
        programStage.put(BaseIdentifiableObjectModel.Columns.CODE, CODE);
        programStage.put(BaseIdentifiableObjectModel.Columns.NAME, NAME);
        programStage.put(BaseIdentifiableObjectModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStage.put(BaseIdentifiableObjectModel.Columns.CREATED, DATE);
        programStage.put(BaseIdentifiableObjectModel.Columns.LAST_UPDATED, DATE);
        programStage.put(ProgramStageFields.EXECUTION_DATE_LABEL, EXECUTION_DATE_LABEL);
        programStage.put(ProgramStageFields.ALLOW_GENERATE_NEXT_VISIT, ALLOW_GENERATE_NEXT_VISIT);
        programStage.put(ProgramStageFields.VALID_COMPLETE_ONLY, VALID_COMPLETE_ONLY);
        programStage.put(ProgramStageFields.REPORT_DATE_TO_USE, REPORT_DATE_TO_USE);
        programStage.put(ProgramStageFields.OPEN_AFTER_ENROLLMENT, OPEN_AFTER_ENROLLMENT);
        programStage.put(ProgramStageFields.REPEATABLE, REPEATABLE);
        programStage.put(ProgramStageFields.CAPTURE_COORDINATES, CAPTURE_COORDINATES);
        programStage.put(ProgramStageFields.FORM_TYPE, FORM_TYPE.name());
        programStage.put(ProgramStageFields.DISPLAY_GENERATE_EVENT_BOX, DISPLAY_GENERATE_EVENT_BOX);
        programStage.put(ProgramStageFields.GENERATED_BY_ENROLMENT_DATE, GENERATED_BY_ENROLMENT_DATE);
        programStage.put(ProgramStageFields.AUTO_GENERATE_EVENT, AUTO_GENERATE_EVENT);
        programStage.put(ProgramStageFields.SORT_ORDER, SORT_ORDER);
        programStage.put(ProgramStageFields.HIDE_DUE_DATE, HIDE_DUE_DATE);
        programStage.put(ProgramStageFields.BLOCK_ENTRY_FORM, BLOCK_ENTRY_FORM);
        programStage.put(ProgramStageFields.MIN_DAYS_FROM_START, MIN_DAYS_FROM_START);
        programStage.put(ProgramStageFields.STANDARD_INTERVAL, STANDARD_INTERVAL);
        programStage.put(ProgramStageFields.PROGRAM, programId);

        return programStage;
    }
}