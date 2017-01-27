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

public class CreateProgramStageSectionUtils {
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer SORT_ORDER = 7;

    private static final String DATE = "2017-01-05T10:26:00.000";

    public static ContentValues create(long id, String uid, String programStageUid) {
        ContentValues programStageSection = new ContentValues();
        programStageSection.put(ProgramStageSectionModel.Columns.ID, id);
        programStageSection.put(ProgramStageSectionModel.Columns.UID, uid);
        programStageSection.put(ProgramStageSectionModel.Columns.CODE, CODE);
        programStageSection.put(ProgramStageSectionModel.Columns.NAME, NAME);
        programStageSection.put(ProgramStageSectionModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageSection.put(ProgramStageSectionModel.Columns.CREATED, DATE);
        programStageSection.put(ProgramStageSectionModel.Columns.LAST_UPDATED, DATE);
        programStageSection.put(ProgramStageSectionModel.Columns.SORT_ORDER, SORT_ORDER);

        if (programStageUid == null) {
            programStageSection.putNull(ProgramStageSectionModel.Columns.PROGRAM_STAGE);
        } else {
            programStageSection.put(ProgramStageSectionModel.Columns.PROGRAM_STAGE, programStageUid);
        }

        return programStageSection;
    }
}
