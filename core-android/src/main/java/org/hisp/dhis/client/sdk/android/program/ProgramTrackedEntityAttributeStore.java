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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramTrackedEntityAttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow
        .ProgramTrackedEntityAttributeFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsStore;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.core.program.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

public final class ProgramTrackedEntityAttributeStore extends
        AbsStore<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow> implements
        IProgramTrackedEntityAttributeStore {

    public ProgramTrackedEntityAttributeStore(Mapper<ProgramTrackedEntityAttribute,
                ProgramTrackedEntityAttributeFlow> mapper) {
        super(mapper);
    }

    @Override
    public List<ProgramTrackedEntityAttribute> query(Program program) {
        if (program == null) {
            return null;
        }
        List<ProgramTrackedEntityAttributeFlow> programTrackedEntityAttributeFlows = new Select()
                .from(ProgramTrackedEntityAttributeFlow.class)
                .where(ProgramTrackedEntityAttributeFlow_Table
                        .program.is(program.getUId()))
                .queryList();
        return getMapper().mapToModels(programTrackedEntityAttributeFlows);
    }

    @Override
    public ProgramTrackedEntityAttribute query(Program program, TrackedEntityAttribute
            trackedEntityAttribute) {
        if (program == null || trackedEntityAttribute == null) {
            return null;
        }
        ProgramTrackedEntityAttributeFlow programTrackedEntityAttributeFlow = new Select()
                .from(ProgramTrackedEntityAttributeFlow.class)
                .where(ProgramTrackedEntityAttributeFlow_Table
                        .program.is(program.getUId()))
                .and(ProgramTrackedEntityAttributeFlow_Table
                        .trackedEntityAttribute.is(trackedEntityAttribute.getUId()))
                .querySingle();
        return getMapper().mapToModel(programTrackedEntityAttributeFlow);
    }
}
