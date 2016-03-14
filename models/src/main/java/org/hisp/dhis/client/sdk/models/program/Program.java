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

package org.hisp.dhis.client.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.base.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Program extends BaseIdentifiableObject {

    @JsonProperty("version")
    private int version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("externalAccess")
    private boolean externalAccess;

    @JsonProperty("onlyEnrollOnce")
    private boolean onlyEnrollOnce;

    @JsonProperty("enrollmentDateLabel")
    private String enrollmentDateLabel;

    @JsonProperty("displayIncidentDate")
    private boolean displayIncidentDate;

    @JsonProperty("incidentDateLabel")
    private String incidentDateLabel;

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

    @JsonProperty("trackedEntity")
    private TrackedEntity trackedEntity;

    @JsonProperty("programType")
    private ProgramType programType;

    @JsonProperty("organisationUnits")
    private List<OrganisationUnit> organisationUnits;

    @JsonProperty("programIndicators")
    private List<ProgramIndicator> programIndicators;

    @JsonProperty("programStages")
    private List<ProgramStage> programStages;

    @JsonProperty("programTrackedEntityAttributes")
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @JsonIgnore
    private boolean isAssignedToUser;

    public Program() {
        // explicit empty constructor
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public boolean isOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    public String getEnrollmentDateLabel() {
        return enrollmentDateLabel;
    }

    public void setEnrollmentDateLabel(String enrollmentDateLabel) {
        this.enrollmentDateLabel = enrollmentDateLabel;
    }

    public boolean isDisplayIncidentDate() {
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

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    public List<ProgramIndicator> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
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

    public void setProgramTrackedEntityAttributes(
            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        this.programTrackedEntityAttributes = programTrackedEntityAttributes;
    }

    public boolean isAssignedToUser() {
        return isAssignedToUser;
    }

    public void setIsAssignedToUser(boolean isAssignedToUser) {
        this.isAssignedToUser = isAssignedToUser;
    }
}
