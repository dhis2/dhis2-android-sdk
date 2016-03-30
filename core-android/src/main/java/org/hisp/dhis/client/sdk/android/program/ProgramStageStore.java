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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ModelLinkFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramStageStore;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramStageStore extends AbsIdentifiableObjectStore<ProgramStage, ProgramStageFlow>
        implements IProgramStageStore {

    private static final String PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS =
            "programStageToProgramStageSections";
    private final ITransactionManager transactionManager;

    public ProgramStageStore(ITransactionManager transactionManager) {
        super(ProgramStageFlow.MAPPER);

        this.transactionManager = transactionManager;
    }

    @Override
    public List<ProgramStage> queryAll() {
        return queryProgramStageRelationships(super.queryAll());
    }

    @Override
    public ProgramStage queryById(long id) {
        return queryProgramStageRelationships(super.queryById(id));
    }

    @Override
    public ProgramStage queryByUid(String uid) {
        return queryProgramStageRelationships(super.queryByUid(uid));
    }

    @Override
    public List<ProgramStage> queryByUids(Set<String> uids) {
        return queryProgramStageRelationships(super.queryByUids(uids));
    }

    @Override
    public boolean insert(ProgramStage object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(ProgramStage object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean save(ProgramStage object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean delete(ProgramStage object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            ModelLinkFlow.deleteRelatedModels(object, PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS);
        }

        return isSuccess;
    }

    @Override
    public boolean deleteAll() {
        boolean isSuccess = super.deleteAll();

        if (isSuccess) {
            ModelLinkFlow.deleteModels(PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS);
        }

        return isSuccess;
    }

    @Override
    public List<ProgramStage> query(Program program) {
        List<ProgramStageFlow> programStageFlows = new Select()
                .from(ProgramStageFlow.class)
                .where(ProgramStageFlow_Table.program.is(program.getUId()))
                .queryList();

        return getMapper().mapToModels(programStageFlows);
    }

    private void updateProgramStageRelationships(ProgramStage programStage) {
        List<IDbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(ModelLinkFlow.updateLinksToModel(programStage,
                programStage.getProgramStageSections(), PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS));
        transactionManager.transact(dbOperations);
    }

    private List<ProgramStage> queryProgramStageRelationships(List<ProgramStage> programStages) {
        if (programStages != null) {
            Map<String, List<ProgramStageSection>> stagesToSections = ModelLinkFlow
                    .queryLinksForModel(
                            ProgramStageSection.class, PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS);
            for (ProgramStage programStage : programStages) {
                programStage.setProgramStageSections(stagesToSections.get(programStage.getUId()));
            }
        }

        return programStages;
    }

    private ProgramStage queryProgramStageRelationships(ProgramStage stage) {
        if (stage != null) {
            List<ProgramStageSection> programStageSections = ModelLinkFlow
                    .queryLinksForModel(ProgramStageSection.class,
                            PROGRAMSTAGE_TO_PROGRAMSTAGESECTIONS, stage.getUId());
            stage.setProgramStageSections(programStageSections);
        }

        return stage;
    }
}
