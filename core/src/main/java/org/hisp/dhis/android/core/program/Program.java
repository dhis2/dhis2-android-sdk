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
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Program.Builder.class)
public abstract class Program extends BaseNameableObject {

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

        public abstract Builder programTrackedEntityAttributes(List<ProgramTrackedEntityAttribute>
                                                                       programTrackedEntityAttributes);

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
