/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.program;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.IMapper;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleActionFlow;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionStore;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleStore;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;

public final class ProgramRuleStore extends AbsIdentifiableObjectStore<ProgramRule,
        ProgramRuleFlow> implements IProgramRuleStore {

    private final IProgramRuleActionStore programRuleActionStore;
    private final IMapper<ProgramRuleAction, ProgramRuleActionFlow> programRuleActionMapper;

    public ProgramRuleStore(IMapper<ProgramRule, ProgramRuleFlow> mapper,
                            IProgramRuleActionStore programRuleActionStore,
                            IMapper<ProgramRuleAction, ProgramRuleActionFlow>
                                    programRuleActionMapper) {
        super(mapper);
        this.programRuleActionStore = programRuleActionStore;
        this.programRuleActionMapper = programRuleActionMapper;
    }

    @Override
    public List<ProgramRule> query(Program program) {
        List<ProgramRuleFlow> programRuleFlows = new Select()
                .from(ProgramRuleFlow.class).where(ProgramRuleFlow_Table
                        .program.is(program.getUId()))
                .queryList();
        for (ProgramRuleFlow programRuleFlow : programRuleFlows) {
            setProgramRuleActions(programRuleFlow);
        }
        return getMapper().mapToModels(programRuleFlows);
    }

    @Override
    public List<ProgramRule> query(ProgramStage programStage) {
        return null;
    }

    private void setProgramRuleActions(ProgramRuleFlow programRuleFlow) {
        if (programRuleFlow == null) {
            return;
        }
        programRuleFlow.setProgramRuleActions(programRuleActionMapper
                .mapToDatabaseEntities(programRuleActionStore
                        .query(getMapper().mapToModel(programRuleFlow))));
    }
}
