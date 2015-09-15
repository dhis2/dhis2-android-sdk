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

package org.hisp.dhis.android.sdk.core.persistence.models.programstagesection;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageSection$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.ProgramStageSection$Flow$Table;
import org.hisp.dhis.android.sdk.models.programstagedataelement.IProgramStageDataElementStore;
import org.hisp.dhis.android.sdk.models.programstagesection.IProgramStageSectionStore;
import org.hisp.dhis.android.sdk.models.programstage.ProgramStage;
import org.hisp.dhis.android.sdk.models.programstagesection.ProgramStageSection;

import java.util.List;

public final class ProgramStageSectionStore implements IProgramStageSectionStore {

    private final IProgramStageDataElementStore programStageDataElementStore;

    public ProgramStageSectionStore(IProgramStageDataElementStore programStageDataElementStore) {
        this.programStageDataElementStore = programStageDataElementStore;
    }

    @Override
    public void insert(ProgramStageSection object) {
        ProgramStageSection$Flow programStageSectionFlow = ProgramStageSection$Flow.fromModel(object);
        programStageSectionFlow.insert();

        object.setId(programStageSectionFlow.getId());
    }

    @Override
    public void update(ProgramStageSection object) {
        ProgramStageSection$Flow.fromModel(object).update();
    }

    @Override
    public void save(ProgramStageSection object) {
        ProgramStageSection$Flow programStageSectionFlow =
                ProgramStageSection$Flow.fromModel(object);
        programStageSectionFlow.save();

        object.setId(programStageSectionFlow.getId());
    }

    @Override
    public void delete(ProgramStageSection object) {
        ProgramStageSection$Flow.fromModel(object).delete();
    }

    @Override
    public List<ProgramStageSection> query() {
        List<ProgramStageSection$Flow> programStageSectionFlows = new Select()
                .from(ProgramStageSection$Flow.class)
                .queryList();
        for(ProgramStageSection$Flow programStageSectionFlow : programStageSectionFlows) {
            setProgramStageDataElements(programStageSectionFlow);
        }
        return ProgramStageSection$Flow.toModels(programStageSectionFlows);
    }

    @Override
    public ProgramStageSection query(long id) {
        ProgramStageSection$Flow programStageSectionFlow = new Select()
                .from(ProgramStageSection$Flow.class)
                .where(Condition.column(ProgramStageSection$Flow$Table.ID).is(id))
                .querySingle();
        setProgramStageDataElements(programStageSectionFlow);
        return ProgramStageSection$Flow.toModel(programStageSectionFlow);
    }

    @Override
    public ProgramStageSection query(String uid) {
        ProgramStageSection$Flow programStageSectionFlow = new Select()
                .from(ProgramStageSection$Flow.class)
                .where(Condition.column(ProgramStageSection$Flow$Table.UID).is(uid))
                .querySingle();
        setProgramStageDataElements(programStageSectionFlow);
        return ProgramStageSection$Flow.toModel(programStageSectionFlow);
    }

    @Override
    public List<ProgramStageSection> query(ProgramStage programStage) {
        List<ProgramStageSection$Flow> programStageSectionFlows = new Select()
                .from(ProgramStageSection$Flow.class).where(Condition
                        .column(ProgramStageSection$Flow$Table.PROGRAMSTAGE)
                        .is(programStage.getUId()))
                .queryList();
        for(ProgramStageSection$Flow programStageSectionFlow : programStageSectionFlows) {
            setProgramStageDataElements(programStageSectionFlow);
        }
        return ProgramStageSection$Flow.toModels(programStageSectionFlows);
    }

    private void setProgramStageDataElements(ProgramStageSection$Flow programStageSectionFlow) {
        if(programStageSectionFlow == null) {
            return;
        }
        programStageSectionFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(programStageDataElementStore
                        .query(ProgramStageSection$Flow.toModel(programStageSectionFlow))));
    }
}
