/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.common.base.IMapper;
import org.hisp.dhis.android.sdk.flow.ProgramRuleVariable$Flow;
import org.hisp.dhis.android.sdk.flow.ProgramRuleVariable$Flow$Table;
import org.hisp.dhis.java.sdk.models.dataelement.DataElement;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.java.sdk.program.IProgramRuleVariableStore;

import java.util.List;

public final class ProgramRuleVariableStore extends AbsIdentifiableObjectStore<ProgramRuleVariable,
        ProgramRuleVariable$Flow> implements IProgramRuleVariableStore {

    public ProgramRuleVariableStore(IMapper<ProgramRuleVariable, ProgramRuleVariable$Flow> mapper) {
        super(mapper);
    }

    @Override
    public ProgramRuleVariable query(Program program, DataElement dataElement) {
        ProgramRuleVariable$Flow programRuleVariableFlow = new Select()
                .from(ProgramRuleVariable$Flow.class)
                .where(Condition.column(ProgramRuleVariable$Flow$Table.PROGRAM)
                        .is(program.getUId())).and(Condition.column(ProgramRuleVariable$Flow$Table
                        .DATAELEMENT).is(dataElement.getUId()))
                .querySingle();
        return ProgramRuleVariable$Flow.toModel(programRuleVariableFlow);
    }

    @Override
    public List<ProgramRuleVariable> query(Program program) {
        List<ProgramRuleVariable$Flow> programRuleVariableFlow = new Select()
                .from(ProgramRuleVariable$Flow.class).where(Condition
                        .column(ProgramRuleVariable$Flow$Table.PROGRAM).is(program.getUId()))
                .queryList();
        return ProgramRuleVariable$Flow.toModels(programRuleVariableFlow);
    }
}
