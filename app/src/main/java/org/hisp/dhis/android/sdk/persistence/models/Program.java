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

import java.util.List;

/**
 * @author Simen Skogly Russnes on 17.02.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class Program extends BaseIdentifiableObject {

    public static final String SINGLE_EVENT_WITH_REGISTRATION = "SINGLE_EVENT_WITH_REGISTRATION";

    public static final String SINGLE_EVENT_WITHOUT_REGISTRATION = "SINGLE_EVENT_WITHOUT_REGISTRATION";

    public static final String MULTIPLE_EVENTS_WITH_REGISTRATION = "MULTIPLE_EVENTS_WITH_REGISTRATION";

    @JsonProperty("type")
    @Column
    private int type;

    @JsonProperty("kind")
    @Column
    private String kind;

    @JsonProperty("version")
    @Column
    private int version;

    @JsonProperty("dateOfEnrollmentDescription")
    @Column
    private String dateOfEnrollmentDescription;

    @JsonProperty("description")
    @Column
    private String description;

    @JsonProperty("onlyEnrollOnce")
    @Column
    private boolean onlyEnrollOnce;

    @JsonProperty("externalAccess")
    @Column
    private boolean extenalAccess;

    @JsonProperty("displayIncidentDate")
    @Column
    private boolean displayIncidentDate;

    @JsonProperty("dateOfIncidentDescription")
    @Column
    private String dateOfIncidentDescription;

    @JsonProperty("registration")
    @Column
    private boolean registration;

    @JsonProperty("selectEnrollmentDatesInFuture")
    @Column
    private boolean selectEnrollmentDatesInFuture;

    @JsonProperty("dataEntryMethod")
    @Column
    private boolean dataEntryMethod;

    @JsonProperty("singleEvent")
    @Column
    private boolean singleEvent;

    @JsonProperty("ignoreOverdueEvents")
    @Column
    private boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    @Column
    private boolean relationshipFromA;

    @JsonProperty("displayName")
    @Column
    private String displayName;

    @JsonProperty("selectIncidentDatesInFuture")
    @Column
    private boolean selectIncidentDatesInFuture;

    @JsonProperty("trackedEntity")
    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "trackedEntity",
            columnType = String.class, foreignColumnName = "id")})
    protected TrackedEntity trackedEntity;

    @JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    public Program() {}

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    /**
     * Using lazy loading for one to many relationships
     * @return
     */
    public List<ProgramStage> getProgramStages() {
        if(programStages == null) {
            programStages = MetaDataController.getProgramStages(id);
        }
        return programStages;
    }

    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes() {
        if(programTrackedEntityAttributes == null) {
            programTrackedEntityAttributes = MetaDataController.getProgramTrackedEntityAttributes(id);
        }
        return programTrackedEntityAttributes;
    }

    public List<ProgramIndicator> getProgramIndicators() {
        return MetaDataController.getProgramIndicatorsByProgram(id);
    }

    public List<ProgramRule> getProgramRules() {
        return new Select().from(ProgramRule.class).where(Condition.column(ProgramRule$Table.PROGRAM).is(id)).queryList();
    }

    public boolean getRelationshipFromA() {
        return relationshipFromA;
    }

    public int getType() {
        return type;
    }

    public String getKind() {
        return kind;
    }

    public int getVersion() {
        return version;
    }

    public String getDateOfEnrollmentDescription() {
        return dateOfEnrollmentDescription;
    }

    public String getDescription() {
        return description;
    }

    public boolean getOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public boolean getExtenalAccess() {
        return extenalAccess;
    }

    public boolean getDisplayIncidentDate() {
        return displayIncidentDate;
    }

    public String getDateOfIncidentDescription() {
        return dateOfIncidentDescription;
    }

    public boolean getRegistration() {
        return registration;
    }

    public boolean getSelectEnrollmentDatesInFuture() {
        return selectEnrollmentDatesInFuture;
    }

    public boolean getDataEntryMethod() {
        return dataEntryMethod;
    }

    public boolean getSingleEvent() {
        return singleEvent;
    }

    public boolean getIgnoreOverdueEvents() {
        return ignoreOverdueEvents;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getSelectIncidentDatesInFuture() {
        return selectIncidentDatesInFuture;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public void setDataEntryMethod(boolean dataEntryMethod) {
        this.dataEntryMethod = dataEntryMethod;
    }

    public void setSelectEnrollmentDatesInFuture(boolean selectEnrollmentDatesInFuture) {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    public void setDateOfIncidentDescription(String dateOfIncidentDescription) {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    public void setDisplayIncidentDate(boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    public void setExtenalAccess(boolean extenalAccess) {
        this.extenalAccess = extenalAccess;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateOfEnrollmentDescription(String dateOfEnrollmentDescription) {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSelectIncidentDatesInFuture(boolean selectIncidentDatesInFuture) {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    public void setProgramStages(List<ProgramStage> programStages) {
        this.programStages = programStages;
    }

    public void setProgramTrackedEntityAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
    }

    public void setRelationshipFromA(boolean relationshipFromA) {
        this.relationshipFromA = relationshipFromA;
    }

    public void setIgnoreOverdueEvents(boolean ignoreOverdueEvents) {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    public void setSingleEvent(boolean singleEvent) {
        this.singleEvent = singleEvent;
    }
}
