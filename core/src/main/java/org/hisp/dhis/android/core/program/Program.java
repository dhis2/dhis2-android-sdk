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

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.AccessColumnAdapter;
import org.hisp.dhis.android.core.data.database.CategoryComboWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbProgramTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramIndicatorListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramRuleListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramRuleVariableListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramSectionListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramTrackedEntityAttributeListColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.RelationshipTypeWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.TrackedEntityTypeWithUidColumnAdapter;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Program.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public abstract class Program extends BaseNameableObject implements Model, ObjectWithStyle<Program, Program.Builder> {

    // TODO move to base class after whole object refactor
    @Override
    @Nullable
    @ColumnName(BaseModel.Columns.ID)
    @JsonIgnore()
    public abstract Long id();

    @Nullable
    @JsonProperty()
    public abstract Integer version();

    @Nullable
    @JsonProperty()
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    @JsonProperty()
    public abstract String enrollmentDateLabel();

    @Nullable
    @JsonProperty()
    public abstract Boolean displayIncidentDate();

    @Nullable
    @JsonProperty()
    public abstract String incidentDateLabel();

    @Nullable
    @JsonProperty()
    public abstract Boolean registration();

    @Nullable
    @JsonProperty()
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataEntryMethod();

    @Nullable
    @JsonProperty()
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    @JsonProperty()
    public abstract Boolean relationshipFromA();

    @Nullable
    @JsonProperty()
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    @JsonProperty()
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty()
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    @JsonProperty()
    public abstract Boolean displayFrontPageList();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbProgramTypeColumnAdapter.class)
    public abstract ProgramType programType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(RelationshipTypeWithUidColumnAdapter.class)
    public abstract RelationshipType relationshipType();

    @Nullable
    @JsonProperty()
    public abstract String relationshipText();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramTrackedEntityAttributeListColumnAdapter.class)
    public abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ProgramWithUidColumnAdapter.class)
    public abstract Program relatedProgram();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(TrackedEntityTypeWithUidColumnAdapter.class)
    public abstract TrackedEntityType trackedEntityType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(CategoryComboWithUidColumnAdapter.class)
    public abstract CategoryCombo categoryCombo();

    String categoryComboUid() {
        CategoryCombo combo = categoryCombo();
        return combo == null ? CategoryComboModel.DEFAULT_UID : combo.uid();
    }

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    @ColumnName(ProgramTableInfo.Columns.ACCESS_DATA_WRITE)
    public abstract Access access();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramIndicatorListColumnAdapter.class)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> programStages();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(IgnoreProgramRuleListColumnAdapter.class)
    public abstract List<ProgramRule> programRules();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramRuleVariableListColumnAdapter.class)
    public abstract List<ProgramRuleVariable> programRuleVariables();

    @Nullable
    @JsonProperty()
    public abstract Integer expiryDays();

    @Nullable
    @JsonProperty()
    public abstract Integer completeEventsExpiryDays();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbPeriodTypeColumnAdapter.class)
    public abstract PeriodType expiryPeriodType();

    @Nullable
    @JsonProperty()
    public abstract Integer minAttributesRequiredToSearch();

    @Nullable
    @JsonProperty()
    public abstract Integer maxTeiCountToReturn();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramSectionListColumnAdapter.class)
    public abstract List<ProgramSection> programSections();

    public static Builder builder() {
        return new $$AutoValue_Program.Builder();
    }

    static Program create(Cursor cursor) {
        return $AutoValue_Program.createFromCursor(cursor);
    }

    public abstract ContentValues toContentValues();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<Program, Builder> {

        public abstract Builder id(Long id);

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

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder completeEventsExpiryDays(Integer completeEventsExpiryDays);

        public abstract Builder expiryPeriodType(PeriodType expiryPeriodType);

        public abstract Builder minAttributesRequiredToSearch(Integer minAttributesRequiredToSearch);

        public abstract Builder maxTeiCountToReturn(Integer maxTeiCountToReturn);

        public abstract Builder programSections(List<ProgramSection> programSections);

        public abstract Program build();
    }
}