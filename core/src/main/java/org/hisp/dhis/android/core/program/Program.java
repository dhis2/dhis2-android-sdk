/*
 * Copyright (c) 2017, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class Program extends BaseNameableObject {
    private static final String VERSION = "version";
    private static final String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
    private static final String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
    private static final String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
    private static final String INCIDENT_DATE_LABEL = "incidentDateLabel";
    private static final String REGISTRATION = "registration";
    private static final String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
    private static final String DATA_ENTRY_METHOD = "dataEntryMethod";
    private static final String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
    private static final String RELATIONSHIP_FROM_A = "relationshipFromA";
    private static final String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
    private static final String CAPTURE_COORDINATES = "captureCoordinates";
    private static final String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
    private static final String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
    private static final String PROGRAM_TYPE = "programType";
    private static final String RELATIONSHIP_TYPE = "relationshipType";
    private static final String RELATIONSHIP_TEXT = "relationshipText";
    private static final String PROGRAM_TRACKED_ENTITY_ATTRIBUTES = "programTrackedEntityAttributes";
    private static final String RELATED_PROGRAM = "relatedProgram";
    private static final String TRACKED_ENTITY = "trackedEntity";
    private static final String CATEGORY_COMBO = "categoryCombo";
    private static final String PROGRAM_INDICATORS = "programIndicators";
    private static final String PROGRAM_STAGES = "programStages";
    private static final String PROGRAM_RULES = "programRules";
    private static final String PROGRAM_RULE_VARIABLES = "programRuleVariables";

    public static final Field<Program, String> uid
            = Field.create(UID);
    public static final Field<Program, String> code
            = Field.create(CODE);
    public static final Field<Program, String> name
            = Field.create(NAME);
    public static final Field<Program, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<Program, String> created
            = Field.create(CREATED);
    public static final Field<Program, String> lastUpdated
            = Field.create(LAST_UPDATED);
    public static final Field<Program, Boolean> deleted
            = Field.create(DELETED);
    public static final Field<Program, String> shortName
            = Field.create(SHORT_NAME);
    public static final Field<Program, String> displayShortName
            = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<Program, String> description
            = Field.create(DESCRIPTION);
    public static final Field<Program, String> displayDescription
            = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<Program, Integer> version
            = Field.create(VERSION);
    public static final Field<Program, Boolean> onlyEnrollOnce
            = Field.create(ONLY_ENROLL_ONCE);
    public static final Field<Program, String> enrollmentDateLabel
            = Field.create(ENROLLMENT_DATE_LABEL);
    public static final Field<Program, Boolean> displayIncidentDate
            = Field.create(DISPLAY_INCIDENT_DATE);
    public static final Field<Program, String> incidentDateLabel
            = Field.create(INCIDENT_DATE_LABEL);
    public static final Field<Program, Boolean> registration
            = Field.create(REGISTRATION);
    public static final Field<Program, Boolean> selectEnrollmentDatesInFuture
            = Field.create(SELECT_ENROLLMENT_DATES_IN_FUTURE);
    public static final Field<Program, Boolean> dataEntryMethod
            = Field.create(DATA_ENTRY_METHOD);
    public static final Field<Program, Boolean> ignoreOverdueEvents
            = Field.create(IGNORE_OVERDUE_EVENTS);
    public static final Field<Program, Boolean> relationshipFromA
            = Field.create(RELATIONSHIP_FROM_A);
    public static final Field<Program, Boolean> selectIncidentDatesInFuture
            = Field.create(SELECT_INCIDENT_DATES_IN_FUTURE);
    public static final Field<Program, Boolean> captureCoordinates
            = Field.create(CAPTURE_COORDINATES);
    public static final Field<Program, Boolean> useFirstStageDuringRegistration
            = Field.create(USE_FIRST_STAGE_DURING_REGISTRATION);
    public static final Field<Program, Boolean> displayFrontPageList
            = Field.create(DISPLAY_FRONT_PAGE_LIST);
    public static final Field<Program, ProgramType> programType
            = Field.create(PROGRAM_TYPE);
    public static final Field<Program, String> relationshipText
            = Field.create(RELATIONSHIP_TEXT);
    public static final NestedField<Program, RelationshipType> relationshipType
            = NestedField.create(RELATIONSHIP_TYPE);
    public static final NestedField<Program, ProgramTrackedEntityAttribute> programTrackedEntityAttributes
            = NestedField.create(PROGRAM_TRACKED_ENTITY_ATTRIBUTES);
    public static final NestedField<Program, Program> relatedProgram
            = NestedField.create(RELATED_PROGRAM);
    public static final NestedField<Program, TrackedEntity> trackedEntity
            = NestedField.create(TRACKED_ENTITY);
    public static final NestedField<Program, CategoryCombo> categoryCombo
            = NestedField.create(CATEGORY_COMBO);
    public static final NestedField<Program, ProgramIndicator> programIndicators
            = NestedField.create(PROGRAM_INDICATORS);
    public static final NestedField<Program, ProgramStage> programStages
            = NestedField.create(PROGRAM_STAGES);
    public static final NestedField<Program, ProgramRule> programRules
            = NestedField.create(PROGRAM_RULES);
    public static final NestedField<Program, ProgramRuleVariable> programRuleVariables
            = NestedField.create(PROGRAM_RULE_VARIABLES);

    @Nullable
    @JsonProperty(VERSION)
    public abstract Integer version();

    @Nullable
    @JsonProperty(ONLY_ENROLL_ONCE)
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    @JsonProperty(ENROLLMENT_DATE_LABEL)
    public abstract String enrollmentDateLabel();

    @Nullable
    @JsonProperty(DISPLAY_INCIDENT_DATE)
    public abstract Boolean displayIncidentDate();

    @Nullable
    @JsonProperty(INCIDENT_DATE_LABEL)
    public abstract String incidentDateLabel();

    @Nullable
    @JsonProperty(REGISTRATION)
    public abstract Boolean registration();

    @Nullable
    @JsonProperty(SELECT_ENROLLMENT_DATES_IN_FUTURE)
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    @JsonProperty(DATA_ENTRY_METHOD)
    public abstract Boolean dataEntryMethod();

    @Nullable
    @JsonProperty(IGNORE_OVERDUE_EVENTS)
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    @JsonProperty(RELATIONSHIP_FROM_A)
    public abstract Boolean relationshipFromA();

    @Nullable
    @JsonProperty(SELECT_INCIDENT_DATES_IN_FUTURE)
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    @JsonProperty(CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty(USE_FIRST_STAGE_DURING_REGISTRATION)
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    @JsonProperty(DISPLAY_FRONT_PAGE_LIST)
    public abstract Boolean displayFrontPageList();

    @Nullable
    @JsonProperty(PROGRAM_TYPE)
    public abstract ProgramType programType();

    @Nullable
    @JsonProperty(RELATIONSHIP_TYPE)
    public abstract RelationshipType relationshipType();

    @Nullable
    @JsonProperty(RELATIONSHIP_TEXT)
    public abstract String relationshipText();

    @Nullable
    @JsonProperty(PROGRAM_TRACKED_ENTITY_ATTRIBUTES)
    public abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    @JsonProperty(RELATED_PROGRAM)
    public abstract Program relatedProgram();

    @Nullable
    @JsonProperty(TRACKED_ENTITY)
    public abstract TrackedEntity trackedEntity();

    @Nullable
    @JsonProperty(CATEGORY_COMBO)
    public abstract CategoryCombo categoryCombo();

    @Nullable
    @JsonProperty(PROGRAM_INDICATORS)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty(PROGRAM_STAGES)
    public abstract List<ProgramStage> programStages();

    @Nullable
    @JsonProperty(PROGRAM_RULES)
    public abstract List<ProgramRule> programRules();

    @Nullable
    @JsonProperty(PROGRAM_RULE_VARIABLES)
    public abstract List<ProgramRuleVariable> programRuleVariables();

    @JsonCreator
    public static Program create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DISPLAY_DESCRIPTION) String displayDesciption,
            @JsonProperty(VERSION) Integer version,
            @JsonProperty(ONLY_ENROLL_ONCE) Boolean onlyEnrollOnce,
            @JsonProperty(ENROLLMENT_DATE_LABEL) String enrollmentDateLabel,
            @JsonProperty(DISPLAY_INCIDENT_DATE) Boolean displayIncidentDate,
            @JsonProperty(INCIDENT_DATE_LABEL) String incidentDateLabel,
            @JsonProperty(REGISTRATION) Boolean registration,
            @JsonProperty(SELECT_ENROLLMENT_DATES_IN_FUTURE) Boolean selectEnrollmentDatesInFuture,
            @JsonProperty(DATA_ENTRY_METHOD) Boolean dataEntryMethod,
            @JsonProperty(IGNORE_OVERDUE_EVENTS) Boolean ignoreOverdueEvents,
            @JsonProperty(RELATIONSHIP_FROM_A) Boolean relationshipFromA,
            @JsonProperty(SELECT_INCIDENT_DATES_IN_FUTURE) Boolean selectIncidentDatesInFuture,
            @JsonProperty(CAPTURE_COORDINATES) Boolean captureCoordinates,
            @JsonProperty(USE_FIRST_STAGE_DURING_REGISTRATION) Boolean useFirstStageDuringRegistration,
            @JsonProperty(DISPLAY_FRONT_PAGE_LIST) Boolean displayFrontPageList,
            @JsonProperty(PROGRAM_TYPE) ProgramType programType,
            @JsonProperty(RELATIONSHIP_TYPE) RelationshipType relationshipType,
            @JsonProperty(RELATIONSHIP_TEXT) String relationshipText,
            @JsonProperty(PROGRAM_TRACKED_ENTITY_ATTRIBUTES) List<ProgramTrackedEntityAttribute> attributes,
            @JsonProperty(RELATED_PROGRAM) Program relatedProgram,
            @JsonProperty(TRACKED_ENTITY) TrackedEntity trackedEntity,
            @JsonProperty(CATEGORY_COMBO) CategoryCombo categoryCombo,
            @JsonProperty(PROGRAM_INDICATORS) List<ProgramIndicator> programIndicators,
            @JsonProperty(PROGRAM_STAGES) List<ProgramStage> programStages,
            @JsonProperty(PROGRAM_RULES) List<ProgramRule> programRules,
            @JsonProperty(PROGRAM_RULE_VARIABLES) List<ProgramRuleVariable> programRuleVariables,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_Program(
                uid,
                code,
                name,
                displayName,
                created,
                lastUpdated,
                deleted,
                shortName,
                displayShortName,
                description,
                displayDesciption,
                version,
                onlyEnrollOnce,
                enrollmentDateLabel,
                displayIncidentDate,
                incidentDateLabel,
                registration,
                selectEnrollmentDatesInFuture,
                dataEntryMethod,
                ignoreOverdueEvents,
                relationshipFromA,
                selectIncidentDatesInFuture,
                captureCoordinates,
                useFirstStageDuringRegistration,
                displayFrontPageList,
                programType,
                relationshipType,
                relationshipText,
                safeUnmodifiableList(attributes),
                relatedProgram,
                trackedEntity,
                categoryCombo,
                safeUnmodifiableList(programIndicators),
                safeUnmodifiableList(programStages),
                safeUnmodifiableList(programRules),
                safeUnmodifiableList(programRuleVariables));
    }
}
