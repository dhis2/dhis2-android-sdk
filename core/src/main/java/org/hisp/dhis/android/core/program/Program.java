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

package org.hisp.dhis.android.core.program;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.dataelement.CategoryCombo;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;

import java.util.Collections;
import java.util.List;


@AutoValue
@JsonDeserialize(builder = AutoValue_Program.Builder.class)
public abstract class Program extends BaseNameableObject {
    private static final String JSON_PROPERTY_VERSION = "version";
    private static final String JSON_PROPERTY_ONLY_ENROLL_ONCE = "onlyEnrollOnce";
    private static final String JSON_PROPERTY_ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
    private static final String JSON_PROPERTY_DISPLAY_INCIDENT_DATE = "displayIncidentDate";
    private static final String JSON_PROPERTY_INCIDENT_DATE_LABEL = "incidentDateLabel";
    private static final String JSON_PROPERTY_REGISTRATION = "registration";
    private static final String JSON_PROPERTY_SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
    private static final String JSON_PROPERTY_DATA_ENTRY_METHOD = "dataEntryMethod";
    private static final String JSON_PROPERTY_IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
    private static final String JSON_PROPERTY_RELATIONSHIP_FROM_A = "relationshipFromA";
    private static final String JSON_PROPERTY_SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
    private static final String JSON_PROPERTY_CAPTURE_COORDINATES = "captureCoordinates";
    private static final String JSON_PROPERTY_USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
    private static final String JSON_PROPERTY_DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
    private static final String JSON_PROPERTY_PROGRAM_TYPE = "programType";
    private static final String JSON_PROPERTY_RELATIONSHIP_TYPE = "relationshipType";
    private static final String JSON_PROPERTY_RELATIONSHIP_TEXT = "relationshipText";
    private static final String JSON_PROPERTY_PROGRAM_TRACKEDENTITY_ATTRIBUTES = "programTrackedEntityAttributes";
    private static final String JSON_PROPERTY_RELATED_PROGRAM = "relatedProgram";
    private static final String JSON_PROPERTY_TRACKED_ENTITY = "trackedEntity";
    private static final String JSON_PROPERTY_CATEGORY_COMBO = "categoryCombo";
    private static final String JSON_PROPERTY_PROGRAM_INDICATORS = "programIndicators";
    private static final String JSON_PROPERTY_PROGRAM_STAGES = "programStages";
    private static final String JSON_PROPERTY_PROGRAM_RULES = "programRules";
    private static final String JSON_PROPERTY_PROGRAM_RULE_VARIABLES = "programRuleVariables";

    @Nullable
    @JsonProperty(JSON_PROPERTY_VERSION)
    public abstract Integer version();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ONLY_ENROLL_ONCE)
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ENROLLMENT_DATE_LABEL)
    public abstract String enrollmentDateLabel();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_INCIDENT_DATE)
    public abstract Boolean displayIncidentDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_INCIDENT_DATE_LABEL)
    public abstract String incidentDateLabel();

    @Nullable
    @JsonProperty(JSON_PROPERTY_REGISTRATION)
    public abstract Boolean registration();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SELECT_ENROLLMENT_DATES_IN_FUTURE)
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ENTRY_METHOD)
    public abstract Boolean dataEntryMethod();

    @Nullable
    @JsonProperty(JSON_PROPERTY_IGNORE_OVERDUE_EVENTS)
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    @JsonProperty(JSON_PROPERTY_RELATIONSHIP_FROM_A)
    public abstract Boolean relationshipFromA();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SELECT_INCIDENT_DATES_IN_FUTURE)
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty(JSON_PROPERTY_USE_FIRST_STAGE_DURING_REGISTRATION)
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_FRONT_PAGE_LIST)
    public abstract Boolean displayFrontPageList();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_TYPE)
    public abstract ProgramType programType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TYPE)
    public abstract RelationshipType relationshipType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TEXT)
    public abstract String relationshipText();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_TRACKEDENTITY_ATTRIBUTES)
    public abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    @JsonProperty(JSON_PROPERTY_RELATED_PROGRAM)
    public abstract Program relatedProgram();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY)
    public abstract TrackedEntity trackedEntity();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORY_COMBO)
    public abstract CategoryCombo categoryCombo();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_INDICATORS)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGES)
    public abstract List<ProgramStage> programStages();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_RULES)
    public abstract List<ProgramRule> programRules();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_VARIABLES)
    public abstract List<ProgramRuleVariable> programRuleVariables();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_VERSION)
        public abstract Builder version(@Nullable Integer version);

        @JsonProperty(JSON_PROPERTY_ONLY_ENROLL_ONCE)
        public abstract Builder onlyEnrollOnce(@Nullable Boolean onlyEnrollOnce);

        @JsonProperty(JSON_PROPERTY_ENROLLMENT_DATE_LABEL)
        public abstract Builder enrollmentDateLabel(@Nullable String enrollmentDateLabel);

        @JsonProperty(JSON_PROPERTY_DISPLAY_INCIDENT_DATE)
        public abstract Builder displayIncidentDate(@Nullable Boolean displayIncidentDate);

        @JsonProperty(JSON_PROPERTY_INCIDENT_DATE_LABEL)
        public abstract Builder incidentDateLabel(@Nullable String incidentDateLabel);

        @JsonProperty(JSON_PROPERTY_REGISTRATION)
        public abstract Builder registration(@Nullable Boolean registration);

        @JsonProperty(JSON_PROPERTY_SELECT_ENROLLMENT_DATES_IN_FUTURE)
        public abstract Builder selectEnrollmentDatesInFuture(
                @Nullable Boolean selectEnrollmentDatesInFuture);

        @JsonProperty(JSON_PROPERTY_DATA_ENTRY_METHOD)
        public abstract Builder dataEntryMethod(@Nullable Boolean dataEntryMethod);

        @JsonProperty(JSON_PROPERTY_IGNORE_OVERDUE_EVENTS)
        public abstract Builder ignoreOverdueEvents(@Nullable Boolean ignoreOverdueEvents);

        @JsonProperty(JSON_PROPERTY_RELATIONSHIP_FROM_A)
        public abstract Builder relationshipFromA(@Nullable Boolean relationshipFromA);

        @JsonProperty(JSON_PROPERTY_SELECT_INCIDENT_DATES_IN_FUTURE)
        public abstract Builder selectIncidentDatesInFuture(
                @Nullable Boolean selectIncidentDatesInFuture);

        @JsonProperty(JSON_PROPERTY_CAPTURE_COORDINATES)
        public abstract Builder captureCoordinates(@Nullable Boolean captureCoordinates);

        @JsonProperty(JSON_PROPERTY_USE_FIRST_STAGE_DURING_REGISTRATION)
        public abstract Builder useFirstStageDuringRegistration(
                @Nullable Boolean useFirstStageDuringRegistration);

        @JsonProperty(JSON_PROPERTY_DISPLAY_FRONT_PAGE_LIST)
        public abstract Builder displayFrontPageList(@Nullable Boolean displayInFrontPageList);

        @JsonProperty(JSON_PROPERTY_PROGRAM_TYPE)
        public abstract Builder programType(@Nullable ProgramType programType);

        @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TYPE)
        public abstract Builder relationshipType(@Nullable RelationshipType relationshipType);

        @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TEXT)
        public abstract Builder relationshipText(@Nullable String relationshipText);

        @JsonProperty(JSON_PROPERTY_PROGRAM_TRACKEDENTITY_ATTRIBUTES)
        public abstract Builder programTrackedEntityAttributes(
                @Nullable List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes);

        @JsonProperty(JSON_PROPERTY_RELATED_PROGRAM)
        public abstract Builder relatedProgram(@Nullable Program relatedProgram);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY)
        public abstract Builder trackedEntity(@Nullable TrackedEntity trackedEntity);

        @JsonProperty(JSON_PROPERTY_CATEGORY_COMBO)
        public abstract Builder categoryCombo(@Nullable CategoryCombo categoryCombo);

        @JsonProperty(JSON_PROPERTY_PROGRAM_INDICATORS)
        public abstract Builder programIndicators(
                @Nullable List<ProgramIndicator> programIndicators);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGES)
        public abstract Builder programStages(@Nullable List<ProgramStage> programStages);

        @JsonProperty(JSON_PROPERTY_PROGRAM_RULES)
        public abstract Builder programRules(@Nullable List<ProgramRule> programRules);

        @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_VARIABLES)
        public abstract Builder programRuleVariables(
                @Nullable List<ProgramRuleVariable> programRuleVariables);

        abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

        abstract List<ProgramIndicator> programIndicators();

        abstract List<ProgramStage> programStages();

        abstract List<ProgramRule> programRules();

        abstract List<ProgramRuleVariable> programRuleVariables();

        abstract Program autoBuild();

        public Program build() {
            if (programTrackedEntityAttributes() != null) {
                programTrackedEntityAttributes(Collections.unmodifiableList(
                        programTrackedEntityAttributes()));
            }

            if (programIndicators() != null) {
                programIndicators(Collections.unmodifiableList(programIndicators()));
            }

            if (programStages() != null) {
                programStages(Collections.unmodifiableList(programStages()));
            }

            if (programRules() != null) {
                programRules(Collections.unmodifiableList(programRules()));
            }

            if (programRuleVariables() != null) {
                programRuleVariables(Collections.unmodifiableList(programRuleVariables()));
            }

            return autoBuild();
        }
    }
}
