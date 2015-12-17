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

package org.hisp.dhis.client.sdk.android.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.client.sdk.android.common.meta.DbDhis;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = ProgramStageDataElement$Flow.UNIQUE_PROGRAM_DATA_ELEMENT_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public final class ProgramStageDataElement$Flow extends BaseModel$Flow {
    static final int UNIQUE_PROGRAM_DATA_ELEMENT_GROUP = 1;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_PROGRAM_DATA_ELEMENT_GROUP})
    String programStage;

    @Column
    @Unique(unique = false, uniqueGroups = {UNIQUE_PROGRAM_DATA_ELEMENT_GROUP})
    String dataElement;

    @Column
    boolean allowFutureDate;

    @Column
    int sortOrder;

    @Column
    boolean displayInReports;

    @Column
    boolean allowProvidedElsewhere;

    @Column
    boolean compulsory;

    @Column
    String programStageSection;

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isDisplayInReports() {
        return displayInReports;
    }

    public void setDisplayInReports(boolean displayInReports) {
        this.displayInReports = displayInReports;
    }

    public boolean isAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setAllowProvidedElsewhere(boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public String getProgramStageSection() {
        return programStageSection;
    }

    public void setProgramStageSection(String programStageSection) {
        this.programStageSection = programStageSection;
    }

    public ProgramStageDataElement$Flow() {
        // empty constructor
    }

    /*public static ProgramStageDataElement toModel(ProgramStageDataElement$Flow programStageDataElementFlow) {
        if (programStageDataElementFlow == null) {
            return null;
        }

        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setProgramStage(programStageDataElementFlow.getProgramStage());
        programStageDataElement.setDataElement(programStageDataElementFlow.getDataElement());
        programStageDataElement.setAllowFutureDate(programStageDataElementFlow.isAllowFutureDate());
        programStageDataElement.setSortOrder(programStageDataElementFlow.getSortOrder());
        programStageDataElement.setDisplayInReports(programStageDataElementFlow.isDisplayInReports());
        programStageDataElement.setAllowProvidedElsewhere(programStageDataElementFlow.isAllowProvidedElsewhere());
        programStageDataElement.setCompulsory(programStageDataElementFlow.isCompulsory());
        programStageDataElement.setProgramStageSection(programStageDataElementFlow.getProgramStageSection());
        return programStageDataElement;
    }

    public static ProgramStageDataElement$Flow fromModel(ProgramStageDataElement programStageDataElement) {
        if (programStageDataElement == null) {
            return null;
        }

        ProgramStageDataElement$Flow programStageDataElementFlow = new ProgramStageDataElement$Flow();
        programStageDataElementFlow.setProgramStage(programStageDataElement.getProgramStage());
        programStageDataElementFlow.setDataElement(programStageDataElement.getDataElement());
        programStageDataElementFlow.setAllowFutureDate(programStageDataElement.isAllowFutureDate());
        programStageDataElementFlow.setSortOrder(programStageDataElement.getSortOrder());
        programStageDataElementFlow.setDisplayInReports(programStageDataElement.isDisplayInReports());
        programStageDataElementFlow.setAllowProvidedElsewhere(programStageDataElement.isAllowProvidedElsewhere());
        programStageDataElementFlow.setCompulsory(programStageDataElement.isCompulsory());
        programStageDataElementFlow.setProgramStageSection(programStageDataElement.getProgramStageSection());
        return programStageDataElementFlow;
    }

    public static List<ProgramStageDataElement> toModels(List<ProgramStageDataElement$Flow> programStageDataElementFlows) {
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<>();

        if (programStageDataElementFlows != null && !programStageDataElementFlows.isEmpty()) {
            for (ProgramStageDataElement$Flow programStageDataElementFlow : programStageDataElementFlows) {
                programStageDataElements.add(toModel(programStageDataElementFlow));
            }
        }

        return programStageDataElements;
    }

    public static List<ProgramStageDataElement$Flow> fromModels(List<ProgramStageDataElement> programStageDataElements) {
        List<ProgramStageDataElement$Flow> programStageDataElementFlows = new ArrayList<>();

        if (programStageDataElements != null && !programStageDataElements.isEmpty()) {
            for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
                programStageDataElementFlows.add(fromModel(programStageDataElement));
            }
        }

        return programStageDataElementFlows;
    }*/
}
