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

package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Database;

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
    public String program;

    @JsonProperty("dataEntryType")
    @Column
    public String dataEntryType;

    @JsonProperty("blockEntryForm")
    @Column
    public boolean blockEntryForm;

    @JsonProperty("reportDateDescription")
    @Column
    public String reportDateDescription;

    @JsonProperty("displayGenerateEventBox")
    @Column
    public boolean displayGenerateEventBox;

    @JsonProperty("description")
    @Column
    public String description;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("openAfterEnrollment")
    @Column
    public boolean openAfterEnrollment;

    @JsonProperty("captureCoordinates")
    @Column
    public boolean captureCoordinates;

    @JsonProperty("defaultTemplateMessage")
    @Column
    public String defaultTemplateMessage;

    @JsonProperty("remindCompleted")
    @Column
    public boolean remindCompleted;

    @JsonProperty("validCompleteOnly")
    @Column
    public boolean validCompleteOnly;

    @JsonProperty("sortOrder")
    @Column
    public int sortOrder;

    @JsonProperty("generatedByEnrollmentDate")
    @Column
    public boolean generatedByEnrollmentDate;

    @JsonProperty("preGenerateUID")
    @Column
    public boolean preGenerateUID;

    @JsonProperty("autoGenerateEvent")
    @Column
    public boolean autoGenerateEvent;

    @JsonProperty("allowGenerateNextVisit")
    @Column
    public boolean allowGenerateNextVisit;

    @JsonProperty("repeatable")
    @Column
    public boolean repeatable;

    @JsonProperty("minDaysFromStart")
    @Column
    public int minDaysFromStart;

    @JsonProperty("displayName")
    @Column
    public String displayName;

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
            if(programStageDataElement.dataElement.equals(dataElementId)) return programStageDataElement;
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
}
