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

package org.hisp.dhis.android.sdk.models.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.common.BaseIdentifiableObject;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ProgramStage extends BaseIdentifiableObject {

    private String program;

    @JsonProperty("dataEntryType")
    private String dataEntryType;

    @JsonProperty("blockEntryForm")
    private boolean blockEntryForm;

    @JsonProperty("reportDateDescription")
    private String reportDateDescription;

    @JsonProperty("displayGenerateEventBox")
    private boolean displayGenerateEventBox;

    @JsonProperty("description")
    private String description;

    @JsonProperty("externalAccess")
    private boolean externalAccess;

    @JsonProperty("openAfterEnrollment")
    private boolean openAfterEnrollment;

    @JsonProperty("captureCoordinates")
    private boolean captureCoordinates;

    @JsonProperty("defaultTemplateMessage")
    private String defaultTemplateMessage;

    @JsonProperty("remindCompleted")
    private boolean remindCompleted;

    @JsonProperty("validCompleteOnly")
    private boolean validCompleteOnly;

    @JsonProperty("sortOrder")
    private int sortOrder;

    @JsonProperty("generatedByEnrollmentDate")
    private boolean generatedByEnrollmentDate;

    @JsonProperty("preGenerateUID")
    private boolean preGenerateUID;

    @JsonProperty("autoGenerateEvent")
    private boolean autoGenerateEvent;

    @JsonProperty("allowGenerateNextVisit")
    private boolean allowGenerateNextVisit;

    @JsonProperty("repeatable")
    private boolean repeatable;

    @JsonProperty("minDaysFromStart")
    private int minDaysFromStart;

    @JsonProperty("programStageDataElements")
    private List<ProgramStageDataElement> programStageDataElements;

    @JsonProperty("programStageSections")
    private List<ProgramStageSection> programStageSections;

    @JsonProperty("programIndicators")
    private List<ProgramIndicator> programIndicators;

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

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

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        return programStageDataElements;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public List<ProgramStageSection> getProgramStageSections() {
        return programStageSections;
    }

    public void setProgramStageSections(List<ProgramStageSection> programStageSections) {
        this.programStageSections = programStageSections;
    }

    public List<ProgramIndicator> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }
}
