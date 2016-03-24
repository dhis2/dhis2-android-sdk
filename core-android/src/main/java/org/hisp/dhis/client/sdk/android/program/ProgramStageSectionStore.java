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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageSectionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageSectionFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.IMapper;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionStore;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;

public final class ProgramStageSectionStore extends AbsIdentifiableObjectStore<ProgramStageSection,
        ProgramStageSectionFlow> implements IProgramStageSectionStore {
    private final IProgramIndicatorStore mProgramIndicatorStore;
    private final IProgramStageDataElementStore mProgramStageDataElementStore;

    public ProgramStageSectionStore(IMapper<ProgramStageSection,
            ProgramStageSectionFlow> mapper, IProgramIndicatorStore mProgramIndicatorStore,
                                    IProgramStageDataElementStore mProgramStageDataElementStore) {
        super(mapper);

        this.mProgramIndicatorStore = mProgramIndicatorStore;
        this.mProgramStageDataElementStore = mProgramStageDataElementStore;
    }

    @Override
    public boolean insert(ProgramStageSection object) {
        ProgramStageSectionFlow databaseEntity = getMapper().mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.insert();

            /* setting id which DbFlows' BaseModel generated after insertion */
            object.setId(databaseEntity.getId());

            List<ProgramIndicator> programIndicators = object.getProgramIndicators();
            if (programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.insert(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStageDataElement> programStageDataElements = object
                    .getProgramStageDataElements();
            if (programStageDataElements != null) {
                for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
                    if (!mProgramStageDataElementStore.insert(programStageDataElement)) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean save(ProgramStageSection object) {
        ProgramStageSectionFlow databaseEntity = getMapper().mapToDatabaseEntity(object);
        if (databaseEntity != null) {
            databaseEntity.save();

            /* setting id which DbFlows' BaseModel generated after insertion */
            object.setId(databaseEntity.getId());

            List<ProgramIndicator> programIndicators = object.getProgramIndicators();
            if (programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.save(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStageDataElement> programStageDataElements = object
                    .getProgramStageDataElements();
            if (programStageDataElements != null) {
                for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
                    if (!mProgramStageDataElementStore.save(programStageDataElement)) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<ProgramStageSection> query(ProgramStage programStage) {
        List<ProgramStageSectionFlow> programStageSectionFlows = new Select()
                .from(ProgramStageSectionFlow.class).where(ProgramStageSectionFlow_Table
                        .programstage.is(programStage.getUId()))
                .queryList();
        return getMapper().mapToModels(programStageSectionFlows);
    }
}
