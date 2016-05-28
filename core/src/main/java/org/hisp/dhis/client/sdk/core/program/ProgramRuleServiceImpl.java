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

package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;

import java.util.List;
import java.util.Set;

public final class ProgramRuleServiceImpl implements ProgramRuleService {
    private final ProgramRuleStore programRuleStore;

    public ProgramRuleServiceImpl(ProgramRuleStore programRuleStore) {
        this.programRuleStore = programRuleStore;
    }

    @Override
    public List<ProgramRule> list(ProgramStage programStage) {
        return programRuleStore.query(programStage);
    }

    @Override
    public List<ProgramRule> list(Program program) {
        return programRuleStore.query(program);
    }

    @Override
    public List<ProgramRule> list(List<Program> programs) {
        return programRuleStore.query(programs);
    }

    @Override
    public ProgramRule get(long id) {
        return programRuleStore.queryById(id);
    }

    @Override
    public ProgramRule get(String uid) {
        return programRuleStore.queryByUid(uid);
    }

    @Override
    public List<ProgramRule> list() {
        return programRuleStore.queryAll();
    }

    @Override
    public boolean save(ProgramRule programRule) {
        return programRuleStore.save(programRule);
    }

    @Override
    public boolean remove(ProgramRule object) {
        return programRuleStore.delete(object);
    }

    @Override
    public List<ProgramRule> list(Set<String> uids) {
        return programRuleStore.queryByUids(uids);
    }
}
