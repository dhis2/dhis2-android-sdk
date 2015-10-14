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

package org.hisp.dhis.java.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.java.sdk.models.common.base.BaseIdentifiableObject;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Program extends BaseIdentifiableObject {

    public enum ProgramType {

        /* pre DHIS 2.20 */
        SINGLE_EVENT_WITH_REGISTRATION("SINGLE_EVENT_WITH_REGISTRATION"),
        SINGLE_EVENT_WITHOUT_REGISTRATION("SINGLE_EVENT_WITHOUT_REGISTRATION"),
        MULTIPLE_EVENTS_WITH_REGISTRATION ("MULTIPLE_EVENTS_WITH_REGISTRATION"),

        /* DHIS 2.20 and up */
        WITH_REGISTRATION("WITH_REGISTRATION"),
        WITHOUT_REGISTRATION("WITHOUT_REGISTRATION");

        private final String value;

        private ProgramType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return ProgramType.this.value;
        }

    }

    private String trackedEntity;

    @JsonProperty("type")
    private int type;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("version")
    private int version;

    @JsonProperty("dateOfEnrollmentDescription")
    private String dateOfEnrollmentDescription;

    @JsonProperty("description")
    private String description;

    @JsonProperty("onlyEnrollOnce")
    private boolean onlyEnrollOnce;

    @JsonProperty("externalAccess")
    private boolean extenalAccess;

    @JsonProperty("displayIncidentDate")
    private boolean displayIncidentDate;

    @JsonProperty("dateOfIncidentDescription")
    private String dateOfIncidentDescription;

    @JsonProperty("registration")
    private boolean registration;

    @JsonProperty("selectEnrollmentDatesInFuture")
    private boolean selectEnrollmentDatesInFuture;

    @JsonProperty("dataEntryMethod")
    private boolean dataEntryMethod;

    @JsonProperty("singleEvent")
    private boolean singleEvent;

    @JsonProperty("ignoreOverdueEvents")
    private boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    private boolean relationshipFromA;

    @JsonProperty("selectIncidentDatesInFuture")
    private boolean selectIncidentDatesInFuture;

    @JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @JsonProperty("trackedEntity")
    public void setTrackedEntityFromJSON(Map<String, Object> trackedEntity) {
        this.trackedEntity = (String) trackedEntity.get("id");
    }

    public String getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(String trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDateOfEnrollmentDescription() {
        return dateOfEnrollmentDescription;
    }

    public void setDateOfEnrollmentDescription(String dateOfEnrollmentDescription) {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    public boolean isExtenalAccess() {
        return extenalAccess;
    }

    public void setExtenalAccess(boolean extenalAccess) {
        this.extenalAccess = extenalAccess;
    }

    public boolean isDisplayIncidentDate() {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate(boolean displayIncidentDate) {
        this.displayIncidentDate = displayIncidentDate;
    }

    public String getDateOfIncidentDescription() {
        return dateOfIncidentDescription;
    }

    public void setDateOfIncidentDescription(String dateOfIncidentDescription) {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public boolean isSelectEnrollmentDatesInFuture() {
        return selectEnrollmentDatesInFuture;
    }

    public void setSelectEnrollmentDatesInFuture(boolean selectEnrollmentDatesInFuture) {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    public boolean isDataEntryMethod() {
        return dataEntryMethod;
    }

    public void setDataEntryMethod(boolean dataEntryMethod) {
        this.dataEntryMethod = dataEntryMethod;
    }

    public boolean isSingleEvent() {
        return singleEvent;
    }

    public void setSingleEvent(boolean singleEvent) {
        this.singleEvent = singleEvent;
    }

    public boolean isIgnoreOverdueEvents() {
        return ignoreOverdueEvents;
    }

    public void setIgnoreOverdueEvents(boolean ignoreOverdueEvents) {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    public boolean isRelationshipFromA() {
        return relationshipFromA;
    }

    public void setRelationshipFromA(boolean relationshipFromA) {
        this.relationshipFromA = relationshipFromA;
    }

    public boolean isSelectIncidentDatesInFuture() {
        return selectIncidentDatesInFuture;
    }

    public void setSelectIncidentDatesInFuture(boolean selectIncidentDatesInFuture) {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    public List<ProgramStage> getProgramStages() {
        return programStages;
    }

    public void setProgramStages(List<ProgramStage> programStages) {
        this.programStages = programStages;
    }

    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes() {
        return programTrackedEntityAttributes;
    }

    public void setProgramTrackedEntityAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
    }
}
