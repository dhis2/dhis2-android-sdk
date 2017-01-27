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

public class CreateProgramRuleUtils {
    /**
     * BaseIdentifiable propertites
     */
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATE = "2011-12-24T12:24:25.203";

    /**
     * Properties bound to ProgramRuleVariable
     */


    private static final String CONDITION = "test_condition";
    private static final Integer PRIORITY = 1; // true
    private static final String PROGRAM_STAGE = "test_programStage";

    public static ContentValues createWithProgramStage(long id,
                                                       String uid,
                                                       String programUid,
                                                       String programStageUid) {
        ContentValues programRule = new ContentValues();
        programRule.put(ProgramRuleModel.Columns.ID, id);
        programRule.put(ProgramRuleModel.Columns.UID, uid);
        programRule.put(ProgramRuleModel.Columns.CODE, CODE);
        programRule.put(ProgramRuleModel.Columns.NAME, NAME);
        programRule.put(ProgramRuleModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRule.put(ProgramRuleModel.Columns.CREATED, DATE);
        programRule.put(ProgramRuleModel.Columns.LAST_UPDATED, DATE);
        programRule.put(ProgramRuleModel.Columns.CONDITION, CONDITION);
        programRule.put(ProgramRuleModel.Columns.PRIORITY, PRIORITY);
        programRule.put(ProgramRuleModel.Columns.PROGRAM, programUid);
        programRule.put(ProgramRuleModel.Columns.PROGRAM_STAGE, programStageUid);
        return programRule;
    }

    public static ContentValues createWithoutProgramStage(long id, String uid, String programUid) {
        ContentValues programRule = new ContentValues();
        programRule.put(ProgramRuleModel.Columns.ID, id);
        programRule.put(ProgramRuleModel.Columns.UID, uid);
        programRule.put(ProgramRuleModel.Columns.CODE, CODE);
        programRule.put(ProgramRuleModel.Columns.NAME, NAME);
        programRule.put(ProgramRuleModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRule.put(ProgramRuleModel.Columns.CREATED, DATE);
        programRule.put(ProgramRuleModel.Columns.LAST_UPDATED, DATE);
        programRule.put(ProgramRuleModel.Columns.CONDITION, CONDITION);
        programRule.put(ProgramRuleModel.Columns.PRIORITY, PRIORITY);
        programRule.put(ProgramRuleModel.Columns.PROGRAM, programUid);
        programRule.putNull(ProgramRuleModel.Columns.PROGRAM_STAGE);
        return programRule;
    }
}
