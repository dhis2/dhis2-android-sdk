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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.BaseNameableObject;
import org.hisp.dhis.client.sdk.models.dataelement.CategoryCombo;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Program extends BaseNameableObject {

    @JsonProperty("version")
    private int version;

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

    @JsonProperty("ignoreOverdueEvents")
    private boolean ignoreOverdueEvents;

    @JsonProperty("relationshipFromA")
    private boolean relationshipFromA;

    @JsonProperty("selectIncidentDatesInFuture")
    private boolean selectIncidentDatesInFuture;

    @JsonProperty("captureCoordinates")
    boolean captureCoordinates;

    @JsonProperty("useFirstStageDuringRegistration")
    boolean useFirstStageDuringRegistration;

    @JsonProperty("displayFrontPageList")
    boolean displayFrontPageList;

    @JsonProperty("programType")
    ProgramType programType;

    @JsonProperty("relationshipType")
    RelationshipType relationshipType;

    @JsonProperty("relationshipText")
    String relationshipText;

    @JsonProperty("programTrackedEntityAttribute")
    List<TrackedEntityAttribute> programTrackedEntityAttribute;

    @JsonProperty("relatedProgram")
    Program relatedProgram;

    @JsonProperty("trackedEntity")
    TrackedEntity trackedEntity;

    @JsonProperty("categoryCombo")
    CategoryCombo categoryCombo;

    @JsonProperty("programIndicators")
    List<ProgramIndicator> programIndicators;

    @JsonProperty("programStages")
    List<ProgramStage> programStages;

    @JsonProperty("programRules")
    List<ProgramRule> programRules;

    @JsonProperty("programRuleVariables")
    List<ProgramRuleVariable> programRuleVariables;

    public static void validate(Program program) {
        BaseIdentifiableObject.validate(program);

        if (program.getProgramType() == null) {
            throw new IllegalArgumentException("Program type must not be null");
        }

        if (program.getProgramStages() != null && !program.getProgramStages().isEmpty()) {
            throw new IllegalArgumentException("Program stages cannot be null or empty");
        }

        if (program.getProgramTrackedEntityAttribute() != null &&
                !program.getProgramTrackedEntityAttribute().isEmpty()) {
            throw new IllegalArgumentException("Program tracked entity attributes " +
                    "cannot be null or empty");
        }
    }

    public Program() {
        // explicit empty constructor
    }

    public String getEnrollmentDateLabel() {
        return enrollmentDateLabel;
    }

    public void setEnrollmentDateLabel(String enrollmentDateLabel) {
        this.enrollmentDateLabel = enrollmentDateLabel;
    }

    public boolean isOnlyEnrollOnce() {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce(boolean onlyEnrollOnce) {
        this.onlyEnrollOnce = onlyEnrollOnce;
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

    public List<ProgramIndicator> getProgramIndicators() {
        return programIndicators;
    }

    public void setProgramIndicators(List<ProgramIndicator> programIndicators) {
        this.programIndicators = programIndicators;
    }

    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
    }

    public void setCategoryCombo(CategoryCombo categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    public boolean isCaptureCoordinates() {
        return captureCoordinates;
    }

    public void setCaptureCoordinates(boolean captureCoordinates) {
        this.captureCoordinates = captureCoordinates;
    }

    public boolean isUseFirstStageDuringRegistration() {
        return useFirstStageDuringRegistration;
    }

    public void setUseFirstStageDuringRegistration(boolean useFirstStageDuringRegistration) {
        this.useFirstStageDuringRegistration = useFirstStageDuringRegistration;
    }

    public TrackedEntity getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(TrackedEntity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public List<TrackedEntityAttribute> getProgramTrackedEntityAttribute() {
        return programTrackedEntityAttribute;
    }

    public void setProgramTrackedEntityAttribute(List<TrackedEntityAttribute> programTrackedEntityAttribute) {
        this.programTrackedEntityAttribute = programTrackedEntityAttribute;
    }

    public Program getRelatedProgram() {
        return relatedProgram;
    }

    public void setRelatedProgram(Program relatedProgram) {
        this.relatedProgram = relatedProgram;
    }

    public boolean isDisplayFrontPageList() {
        return displayFrontPageList;
    }

    public void setDisplayFrontPageList(boolean displayFrontPageList) {
        this.displayFrontPageList = displayFrontPageList;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getRelationshipText() {
        return relationshipText;
    }

    public void setRelationshipText(String relationshipText) {
        this.relationshipText = relationshipText;
    }

    public List<ProgramStage> getProgramStages() {
        return programStages;
    }

    public void setProgramStages(List<ProgramStage> programStages) {
        this.programStages = programStages;
    }

    public List<ProgramRule> getProgramRules() {
        return programRules;
    }

    public void setProgramRules(List<ProgramRule> programRules) {
        this.programRules = programRules;
    }

    public List<ProgramRuleVariable> getProgramRuleVariables() {
        return programRuleVariables;
    }

    public void setProgramRuleVariables(List<ProgramRuleVariable> programRuleVariables) {
        this.programRuleVariables = programRuleVariables;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
