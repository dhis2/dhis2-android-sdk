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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo.Columns;
import org.hisp.dhis.android.core.program.ProgramRuleActionType;

public final class ProgramRuleActionFields {

    private static FieldsHelper<ProgramRuleAction> fh = new FieldsHelper<>();

    static final Fields<ProgramRuleAction> allFields = Fields.<ProgramRuleAction>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(Columns.DATA),
                    fh.<String>field(Columns.CONTENT),
                    fh.<String>field(Columns.LOCATION),
                    fh.nestedFieldWithUid(Columns.TRACKED_ENTITY_ATTRIBUTE),
                    fh.nestedFieldWithUid(Columns.PROGRAM_INDICATOR),
                    fh.nestedFieldWithUid(Columns.PROGRAM_STAGE_SECTION),
                    fh.<ProgramRuleActionType>field(Columns.PROGRAM_RULE_ACTION_TYPE),
                    fh.nestedFieldWithUid(Columns.PROGRAM_STAGE),
                    fh.nestedFieldWithUid(Columns.DATA_ELEMENT),
                    fh.nestedFieldWithUid(Columns.OPTION),
                    fh.nestedFieldWithUid(Columns.OPTION_GROUP)
                    ).build();

    private ProgramRuleActionFields() {
    }
}
