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
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageSectionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageSectionFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionStore;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramStageSectionStore2 extends AbsIdentifiableObjectStore<ProgramStageSection,
        ProgramStageSectionFlow> implements IProgramStageSectionStore {
    private static final String PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS =
            "programStageSectionToProgramStageDataElements";
    private final ITransactionManager transactionManager;

    public ProgramStageSectionStore2(ITransactionManager transactionManager) {
        super(ProgramStageSectionFlow.MAPPER);
        this.transactionManager = transactionManager;
    }

    @Override
    public List<ProgramStageSection> query(ProgramStage programStage) {
        List<ProgramStageSectionFlow> programStageSectionFlows = new Select()
                .from(ProgramStageSectionFlow.class)
                .where(ProgramStageSectionFlow_Table
                        .programstage.is(programStage.getUId()))
                .queryList();
        return getMapper().mapToModels(programStageSectionFlows);
    }

    @Override
    public List<ProgramStageSection> queryAll() {
        return queryProgramStageRelationships(super.queryAll());
    }

    @Override
    public ProgramStageSection queryById(long id) {
        return queryProgramStageRelationships(super.queryById(id));
    }

    @Override
    public ProgramStageSection queryByUid(String uid) {
        return queryProgramStageRelationships(super.queryByUid(uid));
    }

    @Override
    public List<ProgramStageSection> queryByUids(Set<String> uids) {
        return queryProgramStageRelationships(super.queryByUids(uids));
    }

    @Override
    public boolean insert(ProgramStageSection object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(ProgramStageSection object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean save(ProgramStageSection object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateProgramStageRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean delete(ProgramStageSection object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            ModelLinkFlow.deleteRelatedModels(object,
                    PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS);
        }

        return isSuccess;
    }

    @Override
    public boolean deleteAll() {
        boolean isSuccess = super.deleteAll();

        if (isSuccess) {
            ModelLinkFlow.deleteModels(PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS);
        }

        return isSuccess;
    }

    private void updateProgramStageRelationships(ProgramStageSection programStageSection) {
        List<IDbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(ModelLinkFlow.updateLinksToModel(programStageSection,
                programStageSection.getProgramStageDataElements(),
                PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS));
        transactionManager.transact(dbOperations);
    }

    private List<ProgramStageSection> queryProgramStageRelationships(List<ProgramStageSection>
                                                                             programStageSections) {
        if (programStageSections != null) {
            Map<String, List<ProgramStageDataElement>> sectionsToElements = ModelLinkFlow
                    .queryLinksForModel(
                            ProgramStageDataElement.class,
                            PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS);
            for (ProgramStageSection programStageSection : programStageSections) {
                programStageSection.setProgramStageDataElements(sectionsToElements.get
                        (programStageSection.getUId()));
            }
        }

        return programStageSections;
    }

    private ProgramStageSection queryProgramStageRelationships(ProgramStageSection stageSection) {
        if (stageSection != null) {
            List<ProgramStageDataElement> programStageDataElements = ModelLinkFlow
                    .queryLinksForModel(ProgramStageDataElement.class,
                            PROGRAMSTAGESECTION_TO_PROGRAMSTAGEDATAELEMENTS, stageSection.getUId());
            stageSection.setProgramStageDataElements(programStageDataElements);
        }

        return stageSection;
    }

}
