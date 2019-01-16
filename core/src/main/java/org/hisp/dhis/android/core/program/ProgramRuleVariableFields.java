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

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.data.api.Fields;

final class ProgramRuleVariableFields {

    private static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
    private static final String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
    private static final String PROGRAM = "program";
    private static final String DATA_ELEMENT = "dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";

    private static FieldsHelper<ProgramRule> fh = new FieldsHelper<>();
    static final Fields<ProgramRule> allFields = Fields.<ProgramRule>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.nestedFieldWithUid(PROGRAM_STAGE),
                    fh.<ProgramRuleVariableSourceType>field(PROGRAM_RULE_VARIABLE_SOURCE_TYPE),
                    fh.<Boolean>field(USE_CODE_FOR_OPTION_SET),
                    fh.nestedFieldWithUid(PROGRAM),
                    fh.nestedFieldWithUid(DATA_ELEMENT),
                    fh.nestedFieldWithUid(TRACKED_ENTITY_ATTRIBUTE)
            ).build();

    private ProgramRuleVariableFields() {
    }
}