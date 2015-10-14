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

package org.hisp.dhis.android.sdk.core.models.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.models.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.ProgramTrackedEntityAttribute$Flow$Table;
import org.hisp.dhis.android.sdk.models.program.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

public final class ProgramTrackedEntityAttributeStore implements IProgramTrackedEntityAttributeStore {

    public ProgramTrackedEntityAttributeStore() {
        //empty constructor
    }

    @Override
    public boolean insert(ProgramTrackedEntityAttribute object) {
        ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow = ProgramTrackedEntityAttribute$Flow.fromModel(object);
        programTrackedEntityAttributeFlow.insert();
        return true;
    }

    @Override
    public boolean update(ProgramTrackedEntityAttribute object) {
        ProgramTrackedEntityAttribute$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(ProgramTrackedEntityAttribute object) {
        ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow =
                ProgramTrackedEntityAttribute$Flow.fromModel(object);
        programTrackedEntityAttributeFlow.save();
        return true;
    }

    @Override
    public boolean delete(ProgramTrackedEntityAttribute object) {
        ProgramTrackedEntityAttribute$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public ProgramTrackedEntityAttribute queryById(long id) {
        return null;
    }

    @Override
    public List<ProgramTrackedEntityAttribute> queryAll() {
        List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeFlows = new Select()
                .from(ProgramTrackedEntityAttribute$Flow.class)
                .queryList();
        return ProgramTrackedEntityAttribute$Flow.toModels(programTrackedEntityAttributeFlows);
    }


    @Override
    public List<ProgramTrackedEntityAttribute> query(Program program) {
        if(program == null) {
            return null;
        }
        List<ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeFlows = new Select()
                .from(ProgramTrackedEntityAttribute$Flow.class).where(Condition.column(ProgramTrackedEntityAttribute$Flow$Table.PROGRAM).is(program.getUId()))
                .queryList();
        return ProgramTrackedEntityAttribute$Flow.toModels(programTrackedEntityAttributeFlows);
    }

    @Override
    public ProgramTrackedEntityAttribute query(Program program, TrackedEntityAttribute trackedEntityAttribute) {
        if(program == null || trackedEntityAttribute == null) {
            return null;
        }
        ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow = new Select()
                .from(ProgramTrackedEntityAttribute$Flow.class).where(Condition
                        .column(ProgramTrackedEntityAttribute$Flow$Table.PROGRAM)
                        .is(program.getUId())).and(Condition
                        .column(ProgramTrackedEntityAttribute$Flow$Table.TRACKEDENTITYATTRIBUTE)
                        .is(trackedEntityAttribute.getUId())).querySingle();
        return ProgramTrackedEntityAttribute$Flow.toModel(programTrackedEntityAttributeFlow);
    }
}
