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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class Program extends BaseMetaDataObject {

    @JsonProperty("trackedEntity")
    @Column
    @ForeignKey(references = {
            @ForeignKeyReference(columnName = "trackedEntity",
                    columnType = String.class, foreignColumnName = "id")
    })
    TrackedEntity trackedEntity;

    @JsonProperty("programType")
    @Column(name = "programType")
    ProgramType programType;

    @JsonProperty("version")
    @Column(name = "version")
    int version;

    @JsonProperty("enrollmentDateLabel")
    @Column(name = "enrollmentDateLabel")
    String enrollmentDateLabel;

    @JsonProperty("description")
    @Column(name = "description")
    String description;

    @JsonProperty("onlyEnrollOnce")
    @Column(name = "onlyEnrollOnce")
    boolean onlyEnrollOnce;

    @JsonProperty("externalAccess")
    @Column(name = "externalAccess")
    boolean extenalAccess;

    @JsonProperty("displayIncidentDate")
    @Column(name = "displayIncidentDate")
    boolean displayIncidentDate;

    @JsonProperty("incidentDateLabel")
    @Column(name = "incidentDateLabel")
    String incidentDateLabel;

    @JsonProperty("registration")
    @Column(name = "registration")
    boolean registration;

    @JsonProperty("selectEnrollmentDatesInFuture")
    @Column(name = "selectEnrollmentDatesInFuture")
    boolean selectEnrollmentDatesInFuture;

    @JsonProperty("dataEntryMethod")
    @Column(name = "dataEntryMethod")
    boolean dataEntryMethod;

    @JsonProperty("singleEvent")
    @Column(name = "singleEvent")
    boolean singleEvent;

    @JsonProperty("ignoreOverdueEvents")
    @Column(name = "ignoreOverdueEvents")
    boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    @Column(name = "relationshipFromA")
    boolean relationshipFromA;

    @JsonProperty("selectIncidentDatesInFuture")
    @Column(name = "selectIncidentDatesInFuture")
    boolean selectIncidentDatesInFuture;

    @JsonProperty("displayFrontPageList")
    @Column(name = "displayFrontPageList")
    boolean displayFrontPageList;

    @JsonProperty("programIndicators")
    List<ProgramIndicator> programIndicators;

    @JsonProperty("programStages")
    List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    public Program() {
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    @JsonProperty("trackedEntityType")
    public void setTrackedEntityType(TrackedEntity trackedEntityType) {
        trackedEntity = trackedEntityType;
    }

    /**
     * Using lazy loading for one to many relationships
     *
     * @return
     */
    public List<ProgramIndicator> getProgramIndicators() {
        if (programIndicators == null) {
            programIndicators = MetaDataController.getProgramIndicators(id);
        }
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }

    /**
     * Using lazy loading for one to many relationships
     *
     * @return
     */
    public List<ProgramStage> getProgramStages() {
        if (programStages == null) {
            programStages = MetaDataController.getProgramStages(id);
        }
        return programStages;
    }

    public void setProgramStages(List<ProgramStage> programStages) {
        this.programStages = programStages;
    }

    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes() {
        if (programTrackedEntityAttributes == null) {
            programTrackedEntityAttributes = MetaDataController.getProgramTrackedEntityAttributes(id);
        }
        return programTrackedEntityAttributes;
    }

    public void setProgramTrackedEntityAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
    }

    public List<ProgramRule> getProgramRules() {
        return new Select().from(ProgramRule.class).where(Condition.column(ProgramRule$Table.PROGRAM).is(id)).queryList();
    }

    public boolean getRelationshipFromA() {
        return relationshipFromA;
    }

    public void setRelationshipFromA(boolean relationshipFromA) {
        this.relationshipFromA = relationshipFromA;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEnrollmentDateLabel() {
        return enrollmentDateLabel;
    }

    public void setEnrollmentDateLabel(String enrollmentDateLabel) {
        this.enrollmentDateLabel = enrollmentDateLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    public boolean getExtenalAccess() {
        return extenalAccess;
    }

    public void setExtenalAccess(boolean extenalAccess) {
        this.extenalAccess = extenalAccess;
    }

    public boolean getDisplayIncidentDate() {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate(boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    public String getIncidentDateLabel() {
        return incidentDateLabel;
    }

    public void setIncidentDateLabel(String incidentDateLabel) {
        this.incidentDateLabel = incidentDateLabel;
    }

    public boolean getRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public boolean getSelectEnrollmentDatesInFuture() {
        return selectEnrollmentDatesInFuture;
    }

    public void setSelectEnrollmentDatesInFuture(boolean selectEnrollmentDatesInFuture) {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    public boolean getDataEntryMethod() {
        return dataEntryMethod;
    }

    public void setDataEntryMethod(boolean dataEntryMethod) {
        this.dataEntryMethod = dataEntryMethod;
    }

    public boolean getSingleEvent() {
        return singleEvent;
    }

    public void setSingleEvent(boolean singleEvent) {
        this.singleEvent = singleEvent;
    }

    public boolean getIgnoreOverdueEvents() {
        return ignoreOverdueEvents;
    }

    public void setIgnoreOverdueEvents(boolean ignoreOverdueEvents) {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    public boolean getSelectIncidentDatesInFuture() {
        return selectIncidentDatesInFuture;
    }

    public void setSelectIncidentDatesInFuture(boolean selectIncidentDatesInFuture) {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    public boolean isDisplayFrontPageList() {
        return displayFrontPageList;
    }

    public void setDisplayFrontPageList(boolean displayFrontPageList) {
        this.displayFrontPageList = displayFrontPageList;
    }
}
