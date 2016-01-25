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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.ProgramIndicator$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramIndicator$Flow$Table;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorStore;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;

public final class ProgramIndicatorStore extends AbsIdentifiableObjectStore<ProgramIndicator,
        ProgramIndicator$Flow> implements IProgramIndicatorStore {


    public ProgramIndicatorStore(IMapper<ProgramIndicator, ProgramIndicator$Flow> mapper) {
        super(mapper);
    }

    @Override
    public List<ProgramIndicator> query(Program program) {
        List<ProgramIndicator$Flow> programIndicatorFlows = new Select()
                .from(ProgramIndicator$Flow.class).where(Condition.
                        column(ProgramIndicator$Flow$Table.PROGRAM_PROGRAM).is(program.getUId()))
                .queryList();
        return getMapper().mapToModels(programIndicatorFlows);
    }

    @Override
    public List<ProgramIndicator> query(ProgramStage programStage) {
        List<ProgramIndicator$Flow> programIndicatorFlows = new Select()
                .from(ProgramIndicator$Flow.class).where(Condition.
                        column(ProgramIndicator$Flow$Table.PROGRAMSTAGE_PROGRAMSTAGE).is(programStage.getUId()))
                .queryList();
        return getMapper().mapToModels(programIndicatorFlows);
    }

    @Override
    public List<ProgramIndicator> query(ProgramStageSection programStageSection) {
        List<ProgramIndicator$Flow> programIndicatorFlows = new Select()
                .from(ProgramIndicator$Flow.class).where(Condition.
                        column(ProgramIndicator$Flow$Table.PROGRAMSTAGESECTION_PROGRAMSTAGESECTION).is(programStageSection.getUId()))
                .queryList();
        return getMapper().mapToModels(programIndicatorFlows);
    }
}
