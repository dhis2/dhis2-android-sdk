/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.persistence.models.programrule;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramRule$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramRule$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramRuleAction$Flow;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.programrule.IProgramRuleStore;
import org.hisp.dhis.android.sdk.models.programrule.ProgramRule;
import org.hisp.dhis.android.sdk.models.programruleaction.IProgramRuleActionStore;
import org.hisp.dhis.android.sdk.models.programruleaction.ProgramRuleAction;

import java.util.List;

public final class ProgramRuleStore implements IProgramRuleStore {

    private final IProgramRuleActionStore programRuleActionStore;

    public ProgramRuleStore(IProgramRuleActionStore programRuleActionStore) {
        this.programRuleActionStore = programRuleActionStore;
    }

    @Override
    public void insert(ProgramRule object) {
        ProgramRule$Flow programRuleFlow = ProgramRule$Flow.fromModel(object);
        programRuleFlow.insert();

        object.setId(programRuleFlow.getId());
    }

    @Override
    public void update(ProgramRule object) {
        ProgramRule$Flow.fromModel(object).update();
    }

    @Override
    public void save(ProgramRule object) {
        ProgramRule$Flow programRuleFlow =
                ProgramRule$Flow.fromModel(object);
        programRuleFlow.save();

        object.setId(programRuleFlow.getId());
    }

    @Override
    public void delete(ProgramRule object) {
        ProgramRule$Flow.fromModel(object).delete();
    }

    @Override
    public List<ProgramRule> query() {
        List<ProgramRule$Flow> programRuleFlows = new Select()
                .from(ProgramRule$Flow.class)
                .queryList();
        for(ProgramRule$Flow programRuleFlow : programRuleFlows) {
            setProgramRuleActions(programRuleFlow);
        }
        return ProgramRule$Flow.toModels(programRuleFlows);
    }

    @Override
    public ProgramRule query(long id) {
        ProgramRule$Flow programRuleFlow = new Select()
                .from(ProgramRule$Flow.class)
                .where(Condition.column(ProgramRule$Flow$Table.ID).is(id))
                .querySingle();
        setProgramRuleActions(programRuleFlow);
        return ProgramRule$Flow.toModel(programRuleFlow);
    }

    @Override
    public ProgramRule query(String uid) {
        ProgramRule$Flow programRuleFlow = new Select()
                .from(ProgramRule$Flow.class)
                .where(Condition.column(ProgramRule$Flow$Table.UID).is(uid))
                .querySingle();
        setProgramRuleActions(programRuleFlow);
        return ProgramRule$Flow.toModel(programRuleFlow);
    }

    @Override
    public List<ProgramRule> query(Program program) {
        List<ProgramRule$Flow> programRuleFlows = new Select()
                .from(ProgramRule$Flow.class).where(Condition
                        .column(ProgramRule$Flow$Table.PROGRAM).is(program.getUId()))
                .queryList();
        for(ProgramRule$Flow programRuleFlow : programRuleFlows) {
            setProgramRuleActions(programRuleFlow);
        }
        return ProgramRule$Flow.toModels(programRuleFlows);
    }

    private void setProgramRuleActions(ProgramRule$Flow programRuleFlow) {
        if(programRuleFlow == null) {
            return;
        }
        programRuleFlow.setProgramRuleActions(ProgramRuleAction$Flow.
                fromModels(programRuleActionStore.
                        query(ProgramRule$Flow.toModel(programRuleFlow))));
    }
}
