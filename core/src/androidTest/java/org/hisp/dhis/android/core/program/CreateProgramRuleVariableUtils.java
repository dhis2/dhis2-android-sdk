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

public class CreateProgramRuleVariableUtils {

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

    private static final String PROGRAM_STAGE = "test_programStage";
    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.CALCULATED_VALUE;

    private static final Integer USE_CODE_FOR_OPTION_SET = 1; // true
    private static final String PROGRAM = "test_program";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";


    public static ContentValues create(long id, String uid) {
        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(ProgramRuleVariableModel.Columns.ID, id);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.UID, uid);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.CODE, CODE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.NAME, NAME);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.CREATED, DATE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.LAST_UPDATED, DATE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET, USE_CODE_FOR_OPTION_SET);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        programRuleVariable.put(ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());
        return programRuleVariable;
    }
}
