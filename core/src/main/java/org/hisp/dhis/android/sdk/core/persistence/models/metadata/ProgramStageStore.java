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
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramIndicator$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStage$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStage$Flow$Table;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageSection$Flow;
import org.hisp.dhis.android.sdk.models.metadata.IProgramStageStore;
import org.hisp.dhis.android.sdk.models.metadata.Program;
import org.hisp.dhis.android.sdk.models.metadata.ProgramStage;

import java.util.List;

public final class ProgramStageStore implements IProgramStageStore {

    public ProgramStageStore() {
        //empty constructor
    }

    @Override
    public void insert(ProgramStage object) {
        ProgramStage$Flow programStageFlow = ProgramStage$Flow.fromModel(object);
        programStageFlow.insert();

        object.setId(programStageFlow.getId());
    }

    @Override
    public void update(ProgramStage object) {
        ProgramStage$Flow.fromModel(object).update();
    }

    @Override
    public void save(ProgramStage object) {
        ProgramStage$Flow programStageFlow =
                ProgramStage$Flow.fromModel(object);
        programStageFlow.save();

        object.setId(programStageFlow.getId());
    }

    @Override
    public void delete(ProgramStage object) {
        ProgramStage$Flow.fromModel(object).delete();
    }

    @Override
    public List<ProgramStage> query() {
        List<ProgramStage$Flow> programStageFlows = new Select()
                .from(ProgramStage$Flow.class)
                .queryList();
        for(ProgramStage$Flow programStageFlow : programStageFlows) {
            setProgramStageDataElements(programStageFlow);
            setProgramStageSections(programStageFlow);
        }
        return ProgramStage$Flow.toModels(programStageFlows);
    }

    @Override
    public ProgramStage query(long id) {
        ProgramStage$Flow programStageFlow = new Select()
                .from(ProgramStage$Flow.class)
                .where(Condition.column(ProgramStage$Flow$Table.ID).is(id))
                .querySingle();
        programStageFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(Models.programStageDataElements()
                        .query(ProgramStage$Flow.toModel(programStageFlow))));
        setProgramStageDataElements(programStageFlow);
        setProgramStageSections(programStageFlow);
        return ProgramStage$Flow.toModel(programStageFlow);
    }

    @Override
    public ProgramStage query(String uid) {
        ProgramStage$Flow programStageFlow = new Select()
                .from(ProgramStage$Flow.class)
                .where(Condition.column(ProgramStage$Flow$Table.UID).is(uid))
                .querySingle();
        programStageFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(Models.programStageDataElements()
                        .query(ProgramStage$Flow.toModel(programStageFlow))));
        setProgramStageDataElements(programStageFlow);
        setProgramStageSections(programStageFlow);
        return ProgramStage$Flow.toModel(programStageFlow);
    }

    @Override
    public List<ProgramStage> query(Program program) {
        List<ProgramStage$Flow> programStageFlows = new Select()
                .from(ProgramStage$Flow.class).where(Condition
                        .column(ProgramStage$Flow$Table.PROGRAM)
                        .is(program.getUId()))
                .queryList();
        for(ProgramStage$Flow programStageFlow : programStageFlows) {
            setProgramStageDataElements(programStageFlow);
            setProgramStageSections(programStageFlow);
        }
        return ProgramStage$Flow.toModels(programStageFlows);
    }

    private void setProgramStageDataElements(ProgramStage$Flow programStageFlow) {
        if(programStageFlow == null) {
            return;
        }
        programStageFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(Models.programStageDataElements()
                        .query(ProgramStage$Flow.toModel(programStageFlow))));
    }

    private void setProgramStageSections(ProgramStage$Flow programStageFlow) {
        if(programStageFlow == null) {
            return;
        }
        programStageFlow.setProgramStageSections(ProgramStageSection$Flow
                .fromModels(Models.programStageSections()
                        .query(ProgramStage$Flow.toModel(programStageFlow))));
    }

}
