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
public class ProgramStage extends BaseIdentifiableObject {

    @JsonProperty("program")
    public void setProgram(Map<String, Object> program) {
        this.program = (String) program.get("id");
    }

    @Column
    protected String program;

    @JsonProperty("dataEntryType")
    @Column
    private String dataEntryType;

    @JsonProperty("blockEntryForm")
    @Column
    private boolean blockEntryForm;

    @JsonProperty("reportDateDescription")
    @Column
    private String reportDateDescription;

    @JsonProperty("displayGenerateEventBox")
    @Column
    private boolean displayGenerateEventBox;

    @JsonProperty("description")
    @Column
    private String description;

    @JsonProperty("externalAccess")
    @Column
    private boolean externalAccess;

    @JsonProperty("openAfterEnrollment")
    @Column
    private boolean openAfterEnrollment;

    @JsonProperty("captureCoordinates")
    @Column
    private boolean captureCoordinates;

    @JsonProperty("defaultTemplateMessage")
    @Column
    private String defaultTemplateMessage;

    @JsonProperty("remindCompleted")
    @Column
    private boolean remindCompleted;

    @JsonProperty("validCompleteOnly")
    @Column
    private boolean validCompleteOnly;

    @JsonProperty("sortOrder")
    @Column
    private int sortOrder;

    @JsonProperty("generatedByEnrollmentDate")
    @Column
    private boolean generatedByEnrollmentDate;

    @JsonProperty("preGenerateUID")
    @Column
    private boolean preGenerateUID;

    @JsonProperty("autoGenerateEvent")
    @Column
    private boolean autoGenerateEvent;

    @JsonProperty("allowGenerateNextVisit")
    @Column
    private boolean allowGenerateNextVisit;

    @JsonProperty("repeatable")
    @Column
    private boolean repeatable;

    @JsonProperty("minDaysFromStart")
    @Column
    private int minDaysFromStart;

    @JsonProperty("displayName")
    @Column
    private String displayName;

    @JsonProperty("programStageDataElements")
    private List<ProgramStageDataElement> programStageDataElements;

    public List<ProgramStageDataElement> getProgramStageDataElements() {
        if(programStageDataElements == null) {
            programStageDataElements = MetaDataController.getProgramStageDataElements(this);
        }
        return programStageDataElements;
    }

    @JsonProperty("programStageSections")
    private List<ProgramStageSection> programStageSections;

    public List<ProgramStageSection> getProgramStageSections() {
        if(programStageSections == null)
            programStageSections = MetaDataController.getProgramStageSections(id);
        return programStageSections;
    }

    public ProgramStageDataElement getProgramStageDataElement(String dataElementId) {
        if(getProgramStageDataElements()==null) return null;
        for( ProgramStageDataElement programStageDataElement: getProgramStageDataElements() ) {
            if(programStageDataElement.getDataElement().equals(dataElementId)) return programStageDataElement;
        } return null;
    }

    @JsonProperty("programIndicators")
    private List<ProgramIndicator> programIndicators;

    public List<ProgramIndicator> getProgramIndicators() {
        if(programIndicators == null)
            programIndicators = MetaDataController.getProgramIndicatorsByProgramStage(id);
        return programIndicators;
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

    public boolean getBlockEntryForm() {
        return blockEntryForm;
    }

    public String getReportDateDescription() {
        return reportDateDescription;
    }

    public boolean getDisplayGenerateEventBox() {
        return displayGenerateEventBox;
    }

    public String getDescription() {
        return description;
    }

    public boolean getExternalAccess() {
        return externalAccess;
    }

    public boolean getOpenAfterEnrollment() {
        return openAfterEnrollment;
    }

    public String getDefaultTemplateMessage() {
        return defaultTemplateMessage;
    }

    public boolean getCaptureCoordinates() {
        return captureCoordinates;
    }

    public boolean getRemindCompleted() {
        return remindCompleted;
    }

    public boolean getValidCompleteOnly() {
        return validCompleteOnly;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean getGeneratedByEnrollmentDate() {
        return generatedByEnrollmentDate;
    }

    public boolean getPreGenerateUID() {
        return preGenerateUID;
    }

    public boolean getAutoGenerateEvent() {
        return autoGenerateEvent;
    }

    public boolean getAllowGenerateNextVisit() {
        return allowGenerateNextVisit;
    }

    public boolean getRepeatable() {
        return repeatable;
    }

    public int getMinDaysFromStart() {
        return minDaysFromStart;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDataEntryType(String dataEntryType) {
        this.dataEntryType = dataEntryType;
    }

    public void setBlockEntryForm(boolean blockEntryForm) {
        this.blockEntryForm = blockEntryForm;
    }

    public void setReportDateDescription(String reportDateDescription) {
        this.reportDateDescription = reportDateDescription;
    }

    public void setDisplayGenerateEventBox(boolean displayGenerateEventBox) {
        this.displayGenerateEventBox = displayGenerateEventBox;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExternalAccess(boolean externalAccess) {
        this.externalAccess = externalAccess;
    }

    public void setCaptureCoordinates(boolean captureCoordinates) {
        this.captureCoordinates = captureCoordinates;
    }

    public void setOpenAfterEnrollment(boolean openAfterEnrollment) {
        this.openAfterEnrollment = openAfterEnrollment;
    }

    public void setDefaultTemplateMessage(String defaultTemplateMessage) {
        this.defaultTemplateMessage = defaultTemplateMessage;
    }

    public void setRemindCompleted(boolean remindCompleted) {
        this.remindCompleted = remindCompleted;
    }

    public void setValidCompleteOnly(boolean validCompleteOnly) {
        this.validCompleteOnly = validCompleteOnly;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setGeneratedByEnrollmentDate(boolean generatedByEnrollmentDate) {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    public void setPreGenerateUID(boolean preGenerateUID) {
        this.preGenerateUID = preGenerateUID;
    }

    public void setAutoGenerateEvent(boolean autoGenerateEvent) {
        this.autoGenerateEvent = autoGenerateEvent;
    }

    public void setAllowGenerateNextVisit(boolean allowGenerateNextVisit) {
        this.allowGenerateNextVisit = allowGenerateNextVisit;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setMinDaysFromStart(int minDaysFromStart) {
        this.minDaysFromStart = minDaysFromStart;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        this.programStageDataElements = programStageDataElements;
    }

    public void setProgramStageSections(List<ProgramStageSection> programStageSections) {
        this.programStageSections = programStageSections;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }
}
