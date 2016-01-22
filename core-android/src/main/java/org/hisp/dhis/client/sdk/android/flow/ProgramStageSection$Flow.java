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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramStageSection$Flow extends BaseIdentifiableObject$Flow {

    static final String PROGRAM_STAGE_KEY = "programstage";

    @Column
    int sortOrder;

    @Column
    boolean externalAccess;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = PROGRAM_STAGE_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    ProgramStage$Flow programStage;

    List<ProgramStageDataElement$Flow> programStageDataElements;

    List<ProgramIndicator$Flow> programIndicators;

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public ProgramStage$Flow getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage$Flow programStage) {
        this.programStage = programStage;
    }

    public List<ProgramStageDataElement$Flow> getProgramStageDataElements() {
        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement$Flow> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramIndicator$Flow> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator$Flow> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public ProgramStageSection$Flow() {
        // empty constructor
    }

    /*public static ProgramStageSection toModel(ProgramStageSection$Flow programStageSectionFlow) {
        if (programStageSectionFlow == null) {
            return null;
        }

        ProgramStageSection programStageSection = new ProgramStageSection();
        programStageSection.setId(programStageSectionFlow.getId());
        programStageSection.setUId(programStageSectionFlow.getUId());
        programStageSection.setCreated(programStageSectionFlow.getCreated());
        programStageSection.setLastUpdated(programStageSectionFlow.getLastUpdated());
        programStageSection.setName(programStageSectionFlow.getName());
        programStageSection.setDisplayName(programStageSectionFlow.getDisplayName());
        programStageSection.setAccess(programStageSectionFlow.getAccess());
        programStageSection.setSortOrder(programStageSectionFlow.getSortOrder());
        programStageSection.setExternalAccess(programStageSectionFlow.isExternalAccess());
        programStageSection.setProgramStage(programStageSectionFlow.getProgramStage());
        programStageSection.setProgramStageDataElements(ProgramStageDataElement$Flow
                .toModels(programStageSectionFlow.getProgramStageDataElements()));
        programStageSection.setProgramIndicators(ProgramIndicator$Flow
                .toModels(programStageSectionFlow.getProgramIndicators()));
        return programStageSection;
    }

    public static ProgramStageSection$Flow fromModel(ProgramStageSection programStageSection) {
        if (programStageSection == null) {
            return null;
        }

        ProgramStageSection$Flow programStageSectionFlow = new ProgramStageSection$Flow();
        programStageSectionFlow.setId(programStageSection.getId());
        programStageSectionFlow.setUId(programStageSection.getUId());
        programStageSectionFlow.setCreated(programStageSection.getCreated());
        programStageSectionFlow.setLastUpdated(programStageSection.getLastUpdated());
        programStageSectionFlow.setName(programStageSection.getName());
        programStageSectionFlow.setDisplayName(programStageSection.getDisplayName());
        programStageSectionFlow.setAccess(programStageSection.getAccess());
        programStageSectionFlow.setSortOrder(programStageSection.getSortOrder());
        programStageSectionFlow.setExternalAccess(programStageSection.isExternalAccess());
        programStageSectionFlow.setProgramStage(programStageSection.getProgramStage());
        programStageSectionFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(programStageSection.getProgramStageDataElements()));
        programStageSectionFlow.setProgramIndicators(ProgramIndicator$Flow
                .fromModels(programStageSection.getProgramIndicators()));
        return programStageSectionFlow;
    }

    public static List<ProgramStageSection> toModels(List<ProgramStageSection$Flow> programStageSectionFlows) {
        List<ProgramStageSection> programStageSections = new ArrayList<>();

        if (programStageSectionFlows != null && !programStageSectionFlows.isEmpty()) {
            for (ProgramStageSection$Flow programStageSectionFlow : programStageSectionFlows) {
                programStageSections.add(toModel(programStageSectionFlow));
            }
        }

        return programStageSections;
    }

    public static List<ProgramStageSection$Flow> fromModels(List<ProgramStageSection> programStageSections) {
        List<ProgramStageSection$Flow> programStageSectionFlows = new ArrayList<>();

        if (programStageSections != null && !programStageSections.isEmpty()) {
            for (ProgramStageSection programStageSection : programStageSections) {
                programStageSectionFlows.add(fromModel(programStageSection));
            }
        }

        return programStageSectionFlows;
    }*/
}
