/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class ProgramStage extends BaseMetaDataObject {

    @Column(name = "program")
    String program;

    @JsonProperty("dataEntryType")
    @Column(name = "dataEntryType")
    String dataEntryType;

    @JsonProperty("blockEntryForm")
    @Column(name = "blockEntryForm")
    boolean blockEntryForm;

    @JsonProperty("reportDateDescription")
    @Column(name = "reportDateDescription")
    String reportDateDescription;

    @JsonProperty("displayGenerateEventBox")
    @Column(name = "displayGenerateEventBox")
    boolean displayGenerateEventBox;

    @JsonProperty("description")
    @Column(name = "description")
    String description;

    @JsonProperty("externalAccess")
    @Column(name = "externalAccess")
    boolean externalAccess;

    @JsonProperty("openAfterEnrollment")
    @Column(name = "openAfterEnrollment")
    boolean openAfterEnrollment;

    @JsonProperty("captureCoordinates")
    @Column(name = "captureCoordinates")
    boolean captureCoordinates;

    @JsonProperty("defaultTemplateMessage")
    @Column(name = "defaultTemplateMessage")
    String defaultTemplateMessage;

    @JsonProperty("remindCompleted")
    @Column(name = "remindCompleted")
    boolean remindCompleted;

    @JsonProperty("validCompleteOnly")
    @Column(name = "validCompleteOnly")
    boolean validCompleteOnly;

    @JsonProperty("sortOrder")
    @Column(name = "sortOrder")
    int sortOrder;

    @JsonProperty("hideDueDate")
    @Column(name = "hideDueDate")
    boolean hideDueDate;

    @JsonProperty("generatedByEnrollmentDate")
    @Column(name = "generatedByEnrollmentDate")
    boolean generatedByEnrollmentDate;

    @JsonProperty("preGenerateUID")
    @Column(name = "preGenerateUID")
    boolean preGenerateUID;

    @JsonProperty("autoGenerateEvent")
    @Column(name = "autoGenerateEvent")
    boolean autoGenerateEvent;

    @JsonProperty("allowGenerateNextVisit")
    @Column(name = "allowGenerateNextVisit")
    boolean allowGenerateNextVisit;

    @JsonProperty("standardInterval")
    @Column(name = "standardInterval")
    int standardInterval;

    @JsonProperty("repeatable")
    @Column(name = "repeatable")
    boolean repeatable;

    @JsonProperty("minDaysFromStart")
    @Column(name = "minDaysFromStart")
    int minDaysFromStart;

    @JsonProperty("periodType")
    @Column(name = "periodType")
    String periodType;

    @JsonProperty("executionDateLabel")
    @Column(name = "executionDateLabel")
    String executionDateLabel;

    @JsonProperty("programStageDataElements")
    List<ProgramStageDataElement> programStageDataElements;

    @JsonProperty("programStageSections")
    List<ProgramStageSection> programStageSections;

    @JsonProperty("programIndicators")
    List<ProgramIndicator> programIndicators;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        if (programStageDataElements == null) {
            programStageDataElements = MetaDataController.getProgramStageDataElements(this);
        }
        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramStageSection> getProgramStageSections() {
        if (programStageSections == null)
            programStageSections = MetaDataController.getProgramStageSections(id);
        return programStageSections;
    }

    public void setProgramStageSections(List<ProgramStageSection> programStageSections) {
        this.programStageSections = programStageSections;
    }

    public ProgramStageDataElement getProgramStageDataElement(String dataElementId) {
        if (getProgramStageDataElements() == null) return null;
        for (ProgramStageDataElement programStageDataElement : getProgramStageDataElements()) {
            if (programStageDataElement.getDataElement().equals(dataElementId))
                return programStageDataElement;
        }
        return null;
    }

    public List<ProgramIndicator> getProgramIndicators() {
        if (programIndicators == null)
            programIndicators = MetaDataController.getProgramIndicatorsByProgramStage(id);
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public Program getProgram() {
        return MetaDataController.getProgram(program);
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

    public boolean getBlockEntryForm() {
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

    public boolean getDisplayGenerateEventBox() {
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

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public boolean getOpenAfterEnrollment() {
        return openAfterEnrollment;
    }

    public void setOpenAfterEnrollment(boolean openAfterEnrollment) {
        this.openAfterEnrollment = openAfterEnrollment;
    }

    public String getDefaultTemplateMessage() {
        return defaultTemplateMessage;
    }

    public void setDefaultTemplateMessage(String defaultTemplateMessage) {
        this.defaultTemplateMessage = defaultTemplateMessage;
    }

    public boolean getCaptureCoordinates() {
        return captureCoordinates;
    }

    public void setCaptureCoordinates(boolean captureCoordinates) {
        this.captureCoordinates = captureCoordinates;
    }

    public boolean getRemindCompleted() {
        return remindCompleted;
    }

    public void setRemindCompleted(boolean remindCompleted) {
        this.remindCompleted = remindCompleted;
    }

    public boolean getValidCompleteOnly() {
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

    public boolean getGeneratedByEnrollmentDate() {
        return generatedByEnrollmentDate;
    }

    public void setGeneratedByEnrollmentDate(boolean generatedByEnrollmentDate) {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    public boolean getPreGenerateUID() {
        return preGenerateUID;
    }

    public void setPreGenerateUID(boolean preGenerateUID) {
        this.preGenerateUID = preGenerateUID;
    }

    public boolean getAutoGenerateEvent() {
        return autoGenerateEvent;
    }

    public void setAutoGenerateEvent(boolean autoGenerateEvent) {
        this.autoGenerateEvent = autoGenerateEvent;
    }

    public boolean getAllowGenerateNextVisit() {
        return allowGenerateNextVisit;
    }

    public void setAllowGenerateNextVisit(boolean allowGenerateNextVisit) {
        this.allowGenerateNextVisit = allowGenerateNextVisit;
    }

    public boolean getRepeatable() {
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

    public boolean isHideDueDate() {
        return hideDueDate;
    }

    public void setHideDueDate(boolean hideDueDate) {
        this.hideDueDate = hideDueDate;
    }

    public int getStandardInterval() {
        return standardInterval;
    }

    public void setStandardInterval(int standardInterval) {
        this.standardInterval = standardInterval;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public String getExecutionDateLabel() {
        return executionDateLabel;
    }

    public void setExecutionDateLabel(String executionDateLabel) {
        this.executionDateLabel = executionDateLabel;
    }

    public boolean isBlockEntryForm() {
        return blockEntryForm;
    }

    public boolean isDisplayGenerateEventBox() {
        return displayGenerateEventBox;
    }

    public boolean isExternalAccess() {
        return externalAccess;
    }

    public boolean isOpenAfterEnrollment() {
        return openAfterEnrollment;
    }

    public boolean isCaptureCoordinates() {
        return captureCoordinates;
    }

    public boolean isRemindCompleted() {
        return remindCompleted;
    }

    public boolean isValidCompleteOnly() {
        return validCompleteOnly;
    }

    public boolean isGeneratedByEnrollmentDate() {
        return generatedByEnrollmentDate;
    }

    public boolean isAutoGenerateEvent() {
        return autoGenerateEvent;
    }

    public boolean isPreGenerateUID() {
        return preGenerateUID;
    }

    public boolean isAllowGenerateNextVisit() {
        return allowGenerateNextVisit;
    }
}
