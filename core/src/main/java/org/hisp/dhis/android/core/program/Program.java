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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Program.Builder.class)
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
    private static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
    private static final String CATEGORY_COMBO = "categoryCombo";
    private static final String PROGRAM_INDICATORS = "programIndicators";
    private static final String PROGRAM_STAGES = "programStages";
    private static final String PROGRAM_RULES = "programRules";
    private static final String PROGRAM_RULE_VARIABLES = "programRuleVariables";
    private final static String ACCESS = "access";
    private final static String STYLE = "style";
    private final static String EXPIRY_DAYS = "expiryDays";
    private final static String COMPLETE_EVENTS_EXPIRY_DAYS = "completeEventsExpiryDays";
    private final static String EXPIRY_PERIOD_TYPE = "expiryPeriodType";
    private final static String MIN_ATTRIBUTES_REQUIRED_TO_SEARCH = "minAttributesRequiredToSearch";
    private final static String MAX_TEI_COUNT_TO_RETURN = "maxTeiCountToReturn";
    private final static String PROGRAM_SECTIONS = "programSections";

    static final Field<Program, String> uid = Field.create(UID);
    private static final Field<Program, String> code = Field.create(CODE);
    private static final Field<Program, String> name = Field.create(NAME);
    private static final Field<Program, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<Program, String> created = Field.create(CREATED);
    static final Field<Program, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<Program, Boolean> deleted = Field.create(DELETED);
    private static final Field<Program, String> shortName = Field.create(SHORT_NAME);
    private static final Field<Program, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    private static final Field<Program, String> description = Field.create(DESCRIPTION);
    private static final Field<Program, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    private static final Field<Program, Integer> version = Field.create(VERSION);
    private static final Field<Program, Boolean> onlyEnrollOnce = Field.create(ONLY_ENROLL_ONCE);
    private static final Field<Program, String> enrollmentDateLabel = Field.create(ENROLLMENT_DATE_LABEL);
    private static final Field<Program, Boolean> displayIncidentDate = Field.create(DISPLAY_INCIDENT_DATE);
    private static final Field<Program, String> incidentDateLabel = Field.create(INCIDENT_DATE_LABEL);
    private static final Field<Program, Boolean> registration = Field.create(REGISTRATION);
    private static final Field<Program, Boolean> selectEnrollmentDatesInFuture
            = Field.create(SELECT_ENROLLMENT_DATES_IN_FUTURE);
    private static final Field<Program, Boolean> dataEntryMethod = Field.create(DATA_ENTRY_METHOD);
    private static final Field<Program, Boolean> ignoreOverdueEvents = Field.create(IGNORE_OVERDUE_EVENTS);
    private static final Field<Program, Boolean> relationshipFromA = Field.create(RELATIONSHIP_FROM_A);
    private static final Field<Program, Boolean> selectIncidentDatesInFuture
            = Field.create(SELECT_INCIDENT_DATES_IN_FUTURE);
    private static final Field<Program, Boolean> captureCoordinates = Field.create(CAPTURE_COORDINATES);
    private static final Field<Program, Boolean> useFirstStageDuringRegistration
            = Field.create(USE_FIRST_STAGE_DURING_REGISTRATION);
    private static final Field<Program, Boolean> displayFrontPageList = Field.create(DISPLAY_FRONT_PAGE_LIST);
    private static final Field<Program, ProgramType> programType = Field.create(PROGRAM_TYPE);
    private static final Field<Program, String> relationshipText = Field.create(RELATIONSHIP_TEXT);
    private static final Field<Program, String> expiryDays = Field.create(EXPIRY_DAYS);
    private static final Field<Program, String> completeEventsExpiryDays
            = Field.create(COMPLETE_EVENTS_EXPIRY_DAYS);
    private static final Field<Program, String> expiryPeriodType = Field.create(EXPIRY_PERIOD_TYPE);
    private static final Field<Program, Integer> minAttributesRequiredToSearch =
            Field.create(MIN_ATTRIBUTES_REQUIRED_TO_SEARCH);
    private static final Field<Program, Integer> maxTeiCountToReturn =
            Field.create(MAX_TEI_COUNT_TO_RETURN);

    private static final NestedField<Program, ObjectWithUid> relationshipType
            = NestedField.create(RELATIONSHIP_TYPE);
    private static final NestedField<Program, ProgramTrackedEntityAttribute> programTrackedEntityAttributes
            = NestedField.create(PROGRAM_TRACKED_ENTITY_ATTRIBUTES);
    private static final NestedField<Program, Program> relatedProgram
            = NestedField.create(RELATED_PROGRAM);
    private static final NestedField<Program, ObjectWithUid> trackedEntityType
            = NestedField.create(TRACKED_ENTITY_TYPE);
    private static final NestedField<Program, ObjectWithUid> categoryCombo
            = NestedField.create(CATEGORY_COMBO);
    static final NestedField<Program, Access> access = NestedField.create(ACCESS);
    private static final NestedField<Program, ProgramIndicator> programIndicators
            = NestedField.create(PROGRAM_INDICATORS);
    private static final NestedField<Program, ObjectWithUid> programStages
            = NestedField.create(PROGRAM_STAGES);
    private static final NestedField<Program, ProgramRule> programRules = NestedField.create(PROGRAM_RULES);
    private static final NestedField<Program, ProgramRuleVariable> programRuleVariables
            = NestedField.create(PROGRAM_RULE_VARIABLES);
    private static final NestedField<Program, ObjectStyle> style = NestedField.create(STYLE);
    private static final NestedField<Program, ProgramSection> programSections = NestedField.create(PROGRAM_SECTIONS);

    static final Fields<Program> allFields = Fields.<Program>builder().fields(
            uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description,
            displayDescription, version, captureCoordinates, dataEntryMethod, deleted, displayFrontPageList,
            displayIncidentDate, enrollmentDateLabel, ignoreOverdueEvents, incidentDateLabel, onlyEnrollOnce,
            programType, registration, relationshipFromA, relationshipText, selectEnrollmentDatesInFuture,
            selectIncidentDatesInFuture, useFirstStageDuringRegistration, expiryDays, completeEventsExpiryDays,
            expiryPeriodType, minAttributesRequiredToSearch, maxTeiCountToReturn,
            relatedProgram.with(Program.uid), programStages.with(ObjectWithUid.uid),
            programRules.with(ProgramRuleFields.allFields), programRuleVariables.with(ProgramRuleVariable.allFields),
            programIndicators.with(ProgramIndicator.allFields),
            programTrackedEntityAttributes.with(ProgramTrackedEntityAttribute.allFields),
            trackedEntityType.with(ObjectWithUid.uid), categoryCombo.with(ObjectWithUid.uid),
            relationshipType.with(ObjectWithUid.uid), access.with(Access.data.with(DataAccess.write)),
            style.with(ObjectStyle.allFields), programSections.with(ProgramSection.allFields)).build();

    @Nullable
    public abstract Integer version();

    @Nullable
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    public abstract String enrollmentDateLabel();

    @Nullable
    public abstract Boolean displayIncidentDate();

    @Nullable
    public abstract String incidentDateLabel();

    @Nullable
    public abstract Boolean registration();

    @Nullable
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    public abstract Boolean dataEntryMethod();

    @Nullable
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    public abstract Boolean relationshipFromA();

    @Nullable
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    public abstract Boolean captureCoordinates();

    @Nullable
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    public abstract Boolean displayFrontPageList();

    @Nullable
    public abstract ProgramType programType();

    @Nullable
    public abstract RelationshipType relationshipType();

    String relationshipTypeUid() {
        RelationshipType relationshipType = relationshipType();
        return relationshipType == null ? null : relationshipType.uid();
    }

    @Nullable
    public abstract String relationshipText();

    @Nullable
    public abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    public abstract Program relatedProgram();

    String relatedProgramUid() {
        Program relatedProgram = relatedProgram();
        return relatedProgram == null ? null : relatedProgram.uid();
    }

    @Nullable
    public abstract TrackedEntityType trackedEntityType();

    String trackedEntityTypeUid() {
        TrackedEntityType trackedEntityType = trackedEntityType();
        return trackedEntityType == null ? null : trackedEntityType.uid();
    }

    @Nullable
    public abstract CategoryCombo categoryCombo();

    String categoryComboUid() {
        CategoryCombo combo = categoryCombo();
        return combo == null ? CategoryComboModel.DEFAULT_UID : combo.uid();
    }

    @Nullable
    public abstract Access access();

    @Nullable
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    public abstract List<ObjectWithUid> programStages();

    @Nullable
    public abstract List<ProgramRule> programRules();

    @Nullable
    public abstract List<ProgramRuleVariable> programRuleVariables();

    @Nullable
    public abstract ObjectStyle style();

    @Nullable
    public abstract Integer expiryDays();

    @Nullable
    public abstract Integer completeEventsExpiryDays();

    @Nullable
    public abstract PeriodType expiryPeriodType();

    @Nullable
    public abstract Integer minAttributesRequiredToSearch();

    @Nullable
    public abstract Integer maxTeiCountToReturn();

    @Nullable
    public abstract List<ProgramSection> programSections();

    public static Builder builder() {
        return new AutoValue_Program.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        public abstract Builder version(Integer version);

        public abstract Builder onlyEnrollOnce(Boolean onlyEnrollOnce);

        public abstract Builder enrollmentDateLabel(String enrollmentDateLabel);

        public abstract Builder displayIncidentDate(Boolean displayIncidentDate);

        public abstract Builder incidentDateLabel(String incidentDateLabel);

        public abstract Builder registration(Boolean registration);

        public abstract Builder selectEnrollmentDatesInFuture(Boolean selectEnrollmentDatesInFuture);

        public abstract Builder dataEntryMethod(Boolean dataEntryMethod);

        public abstract Builder ignoreOverdueEvents(Boolean ignoreOverdueEvents);

        public abstract Builder relationshipFromA(Boolean relationshipFromA);

        public abstract Builder selectIncidentDatesInFuture(Boolean selectIncidentDatesInFuture);

        public abstract Builder captureCoordinates(Boolean captureCoordinates);

        public abstract Builder useFirstStageDuringRegistration(Boolean useFirstStageDuringRegistration);

        public abstract Builder displayFrontPageList(Boolean displayFrontPageList);

        public abstract Builder programType(ProgramType programType);

        public abstract Builder relationshipType(RelationshipType relationshipType);

        public abstract Builder relationshipText(String relationshipText);

        public abstract Builder programTrackedEntityAttributes(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes);

        public abstract Builder relatedProgram(Program relatedProgram);

        public abstract Builder trackedEntityType(TrackedEntityType trackedEntityType);

        public abstract Builder categoryCombo(CategoryCombo categoryCombo);

        public abstract Builder access(Access access);

        public abstract Builder programIndicators(List<ProgramIndicator> programIndicators);

        public abstract Builder programStages(List<ObjectWithUid> programStages);

        public abstract Builder programRules(List<ProgramRule> programRules);

        public abstract Builder programRuleVariables(List<ProgramRuleVariable> programRuleVariables);

        public abstract Builder style(ObjectStyle style);

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder completeEventsExpiryDays(Integer completeEventsExpiryDays);

        public abstract Builder expiryPeriodType(PeriodType expiryPeriodType);

        public abstract Builder minAttributesRequiredToSearch(Integer minAttributesRequiredToSearch);

        public abstract Builder maxTeiCountToReturn(Integer maxTeiCountToReturn);

        public abstract Builder programSections(List<ProgramSection> programSections);

        public abstract Program build();
    }
}
