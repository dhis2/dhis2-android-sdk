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

package org.hisp.dhis.android.sdk.core.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.program.ProgramStage;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class ProgramStage$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String program;

    @Column
    String dataEntryType;

    @Column
    boolean blockEntryForm;

    @Column
    String reportDateDescription;

    @Column
    boolean displayGenerateEventBox;

    @Column
    String description;

    @Column
    boolean externalAccess;

    @Column
    boolean openAfterEnrollment;

    @Column
    boolean captureCoordinates;

    @Column
    String defaultTemplateMessage;

    @Column
    boolean remindCompleted;

    @Column
    boolean validCompleteOnly;

    @Column
    int sortOrder;

    @Column
    boolean generatedByEnrollmentDate;

    @Column
    boolean preGenerateUID;

    @Column
    boolean autoGenerateEvent;

    @Column
    boolean allowGenerateNextVisit;

    @Column
    boolean repeatable;

    @Column
    int minDaysFromStart;

    List<ProgramStageDataElement$Flow> programStageDataElements;

    List<ProgramStageSection$Flow> programStageSections;

    List<ProgramIndicator$Flow> programIndicators;

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDataEntryType() {
        return dataEntryType;
    }

    public void setDataEntryType(String dataEntryType) {
        this.dataEntryType = dataEntryType;
    }

    public boolean isBlockEntryForm() {
        return blockEntryForm;
    }

    public void setBlockEntryForm(boolean blockEntryForm) {
        this.blockEntryForm = blockEntryForm;
    }

    public String getReportDateDescription() {
        return reportDateDescription;
    }

    public void setReportDateDescription(String reportDateDescription) {
        this.reportDateDescription = reportDateDescription;
    }

    public boolean isDisplayGenerateEventBox() {
        return displayGenerateEventBox;
    }

    public void setDisplayGenerateEventBox(boolean displayGenerateEventBox) {
        this.displayGenerateEventBox = displayGenerateEventBox;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public boolean isOpenAfterEnrollment() {
        return openAfterEnrollment;
    }

    public void setOpenAfterEnrollment(boolean openAfterEnrollment) {
        this.openAfterEnrollment = openAfterEnrollment;
    }

    public boolean isCaptureCoordinates() {
        return captureCoordinates;
    }

    public void setCaptureCoordinates(boolean captureCoordinates) {
        this.captureCoordinates = captureCoordinates;
    }

    public String getDefaultTemplateMessage() {
        return defaultTemplateMessage;
    }

    public void setDefaultTemplateMessage(String defaultTemplateMessage) {
        this.defaultTemplateMessage = defaultTemplateMessage;
    }

    public boolean isRemindCompleted() {
        return remindCompleted;
    }

    public void setRemindCompleted(boolean remindCompleted) {
        this.remindCompleted = remindCompleted;
    }

    public boolean isValidCompleteOnly() {
        return validCompleteOnly;
    }

    public void setValidCompleteOnly(boolean validCompleteOnly) {
        this.validCompleteOnly = validCompleteOnly;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isGeneratedByEnrollmentDate() {
        return generatedByEnrollmentDate;
    }

    public void setGeneratedByEnrollmentDate(boolean generatedByEnrollmentDate) {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    public boolean isPreGenerateUID() {
        return preGenerateUID;
    }

    public void setPreGenerateUID(boolean preGenerateUID) {
        this.preGenerateUID = preGenerateUID;
    }

    public boolean isAutoGenerateEvent() {
        return autoGenerateEvent;
    }

    public void setAutoGenerateEvent(boolean autoGenerateEvent) {
        this.autoGenerateEvent = autoGenerateEvent;
    }

    public boolean isAllowGenerateNextVisit() {
        return allowGenerateNextVisit;
    }

    public void setAllowGenerateNextVisit(boolean allowGenerateNextVisit) {
        this.allowGenerateNextVisit = allowGenerateNextVisit;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public int getMinDaysFromStart() {
        return minDaysFromStart;
    }

    public void setMinDaysFromStart(int minDaysFromStart) {
        this.minDaysFromStart = minDaysFromStart;
    }

    public List<ProgramStageDataElement$Flow> getProgramStageDataElements() {
        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement$Flow> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramStageSection$Flow> getProgramStageSections() {
        return programStageSections;
    }

    public void setProgramStageSections(List<ProgramStageSection$Flow> programStageSections) {
        this.programStageSections = programStageSections;
    }

    public List<ProgramIndicator$Flow> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator$Flow> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public ProgramStage$Flow() {
        // empty constructor
    }

    public static ProgramStage toModel(ProgramStage$Flow programStageFlow) {
        if (programStageFlow == null) {
            return null;
        }

        ProgramStage programStage = new ProgramStage();
        programStage.setId(programStageFlow.getId());
        programStage.setUId(programStageFlow.getUId());
        programStage.setCreated(programStageFlow.getCreated());
        programStage.setLastUpdated(programStageFlow.getLastUpdated());
        programStage.setName(programStageFlow.getName());
        programStage.setDisplayName(programStageFlow.getDisplayName());
        programStage.setAccess(programStageFlow.getAccess());
        programStage.setProgram(programStageFlow.getProgram());
        programStage.setDataEntryType(programStageFlow.getDataEntryType());
        programStage.setBlockEntryForm(programStageFlow.isBlockEntryForm());
        programStage.setReportDateDescription(programStageFlow.getReportDateDescription());
        programStage.setDisplayGenerateEventBox(programStageFlow.isDisplayGenerateEventBox());
        programStage.setDescription(programStageFlow.getDescription());
        programStage.setExternalAccess(programStageFlow.isExternalAccess());
        programStage.setOpenAfterEnrollment(programStageFlow.isOpenAfterEnrollment());
        programStage.setCaptureCoordinates(programStageFlow.isCaptureCoordinates());
        programStage.setDefaultTemplateMessage(programStageFlow.getDefaultTemplateMessage());
        programStage.setRemindCompleted(programStageFlow.isRemindCompleted());
        programStage.setValidCompleteOnly(programStageFlow.isValidCompleteOnly());
        programStage.setSortOrder(programStageFlow.getSortOrder());
        programStage.setGeneratedByEnrollmentDate(programStageFlow.isGeneratedByEnrollmentDate());
        programStage.setPreGenerateUID(programStageFlow.isPreGenerateUID());
        programStage.setAutoGenerateEvent(programStageFlow.isAutoGenerateEvent());
        programStage.setAllowGenerateNextVisit(programStageFlow.isAllowGenerateNextVisit());
        programStage.setRepeatable(programStageFlow.isRepeatable());
        programStage.setMinDaysFromStart(programStageFlow.getMinDaysFromStart());
        programStage.setProgramStageDataElements(ProgramStageDataElement$Flow
                .toModels(programStageFlow.getProgramStageDataElements()));
        programStage.setProgramStageSections(ProgramStageSection$Flow
                .toModels(programStageFlow.getProgramStageSections()));
        programStage.setProgramIndicators(ProgramIndicator$Flow
                .toModels(programStageFlow.getProgramIndicators()));
        return programStage;
    }

    public static ProgramStage$Flow fromModel(ProgramStage programStage) {
        if (programStage == null) {
            return null;
        }

        ProgramStage$Flow programStageFlow = new ProgramStage$Flow();
        programStageFlow.setId(programStage.getId());
        programStageFlow.setUId(programStage.getUId());
        programStageFlow.setCreated(programStage.getCreated());
        programStageFlow.setLastUpdated(programStage.getLastUpdated());
        programStageFlow.setName(programStage.getName());
        programStageFlow.setDisplayName(programStage.getDisplayName());
        programStageFlow.setAccess(programStage.getAccess());
        programStageFlow.setProgram(programStage.getProgram());
        programStageFlow.setDataEntryType(programStage.getDataEntryType());
        programStageFlow.setBlockEntryForm(programStage.isBlockEntryForm());
        programStageFlow.setReportDateDescription(programStage.getReportDateDescription());
        programStageFlow.setDisplayGenerateEventBox(programStage.isDisplayGenerateEventBox());
        programStageFlow.setDescription(programStage.getDescription());
        programStageFlow.setExternalAccess(programStage.isExternalAccess());
        programStageFlow.setOpenAfterEnrollment(programStage.isOpenAfterEnrollment());
        programStageFlow.setCaptureCoordinates(programStage.isCaptureCoordinates());
        programStageFlow.setDefaultTemplateMessage(programStage.getDefaultTemplateMessage());
        programStageFlow.setRemindCompleted(programStage.isRemindCompleted());
        programStageFlow.setValidCompleteOnly(programStage.isValidCompleteOnly());
        programStageFlow.setSortOrder(programStage.getSortOrder());
        programStageFlow.setGeneratedByEnrollmentDate(programStage.isGeneratedByEnrollmentDate());
        programStageFlow.setPreGenerateUID(programStage.isPreGenerateUID());
        programStageFlow.setAutoGenerateEvent(programStage.isAutoGenerateEvent());
        programStageFlow.setAllowGenerateNextVisit(programStage.isAllowGenerateNextVisit());
        programStageFlow.setRepeatable(programStage.isRepeatable());
        programStageFlow.setMinDaysFromStart(programStage.getMinDaysFromStart());
        programStageFlow.setProgramStageDataElements(ProgramStageDataElement$Flow
                .fromModels(programStage.getProgramStageDataElements()));
        programStageFlow.setProgramStageSections(ProgramStageSection$Flow
                .fromModels(programStage.getProgramStageSections()));
        programStageFlow.setProgramIndicators(ProgramIndicator$Flow
                .fromModels(programStage.getProgramIndicators()));
        return programStageFlow;
    }

    public static List<ProgramStage> toModels(List<ProgramStage$Flow> programStageFlows) {
        List<ProgramStage> programStages = new ArrayList<>();

        if (programStageFlows != null && !programStageFlows.isEmpty()) {
            for (ProgramStage$Flow programStageFlow : programStageFlows) {
                programStages.add(toModel(programStageFlow));
            }
        }

        return programStages;
    }

    public static List<ProgramStage$Flow> fromModels(List<ProgramStage> programStages) {
        List<ProgramStage$Flow> programStageFlows = new ArrayList<>();

        if (programStages != null && !programStages.isEmpty()) {
            for (ProgramStage programStage : programStages) {
                programStageFlows.add(fromModel(programStage));
            }
        }

        return programStageFlows;
    }
}
