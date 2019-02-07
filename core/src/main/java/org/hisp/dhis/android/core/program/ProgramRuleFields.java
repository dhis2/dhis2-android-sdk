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

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.data.api.Fields;

final class ProgramRuleFields {

    static final String PRIORITY = "priority";
    static final String CONDITION = "condition";
    static final String PROGRAM = "program";
    static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM_RULE_ACTIONS = "programRuleActions";

    private static FieldsHelper<ProgramRule> fh = new FieldsHelper<>();
    static final Fields<ProgramRule> allFields = Fields.<ProgramRule>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<Integer>field(PRIORITY),
                    fh.<String>field(CONDITION),
                    fh.nestedFieldWithUid(PROGRAM),
                    fh.nestedFieldWithUid(PROGRAM_STAGE),
                    fh.<ProgramRuleAction>nestedField(PROGRAM_RULE_ACTIONS).with(ProgramRuleActionFields.allFields)
            ).build();

    private ProgramRuleFields() {
    }
}