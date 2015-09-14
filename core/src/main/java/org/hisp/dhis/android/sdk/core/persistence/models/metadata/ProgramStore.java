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

package org.hisp.dhis.android.sdk.core.persistence.models.metadata;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.api.Models;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Program$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Program$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStage$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.metadata.Program;

import java.util.List;

public final class ProgramStore implements IIdentifiableObjectStore<Program> {

    public ProgramStore() {
        //empty constructor
    }

    @Override
    public void insert(Program object) {
        Program$Flow programFlow = Program$Flow.fromModel(object);
        programFlow.insert();

        object.setId(programFlow.getId());
    }

    @Override
    public void update(Program object) {
        Program$Flow.fromModel(object).update();
    }

    @Override
    public void save(Program object) {
        Program$Flow programFlow =
                Program$Flow.fromModel(object);
        programFlow.save();

        object.setId(programFlow.getId());
    }

    @Override
    public void delete(Program object) {
        Program$Flow.fromModel(object).delete();
    }

    @Override
    public List<Program> query() {
        List<Program$Flow> programFlows = new Select()
                .from(Program$Flow.class)
                .queryList();
        for(Program$Flow programFlow : programFlows) {
            setProgramStages(programFlow);
            setProgramTrackedEntityAttributes(programFlow);
        }
        return Program$Flow.toModels(programFlows);
    }

    @Override
    public Program query(long id) {
        Program$Flow programFlow = new Select()
                .from(Program$Flow.class)
                .where(Condition.column(Program$Flow$Table.ID).is(id))
                .querySingle();
        setProgramStages(programFlow);
        setProgramTrackedEntityAttributes(programFlow);
        return Program$Flow.toModel(programFlow);
    }

    @Override
    public Program query(String uid) {
        Program$Flow programFlow = new Select()
                .from(Program$Flow.class)
                .where(Condition.column(Program$Flow$Table.UID).is(uid))
                .querySingle();
        setProgramStages(programFlow);
        setProgramTrackedEntityAttributes(programFlow);
        return Program$Flow.toModel(programFlow);
    }

    private void setProgramStages(Program$Flow programFlow) {
        if(programFlow == null) {
            return;
        }
        programFlow.setProgramStages(ProgramStage$Flow
                .fromModels(Models.programStages()
                        .query(Program$Flow.toModel(programFlow))));
    }

    private void setProgramTrackedEntityAttributes(Program$Flow programFlow) {
        if(programFlow == null) {
            return;
        }
        programFlow.setProgramTrackedEntityAttributes(ProgramTrackedEntityAttribute$Flow
                .fromModels(Models.programTrackedEntityAttributes()
                        .query(Program$Flow.toModel(programFlow))));
    }
}
