/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DBCaptureCoordinatesFromFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbProgramTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AccessLevelColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.FeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.TrackedEntityTypeWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAttributeValuesListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramRuleVariableListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramSectionListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramTrackedEntityAttributeListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_Program.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public abstract class Program extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<Program, Program.Builder> {

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
    public abstract Boolean selectIncidentDatesInFuture();

    /**
     * @deprecated since 2.29, replaced by {@link #featureType()}
     */
    @Deprecated
    @Nullable
    @JsonProperty()
    @ColumnAdapter(DBCaptureCoordinatesFromFeatureTypeColumnAdapter.class)
    abstract Boolean captureCoordinates();

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
    @ColumnAdapter(IgnoreProgramTrackedEntityAttributeListColumnAdapter.class)
    abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid relatedProgram();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(TrackedEntityTypeWithUidColumnAdapter.class)
    public abstract TrackedEntityType trackedEntityType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid categoryCombo();

    public String categoryComboUid() {
        ObjectWithUid combo = categoryCombo();
        return combo == null ? CategoryCombo.DEFAULT_UID : combo.uid();
    }

    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramRuleVariableListColumnAdapter.class)
    abstract List<ProgramRuleVariable> programRuleVariables();

    @Nullable
    @JsonProperty()
    public abstract Integer expiryDays();

    @Nullable
    @JsonProperty()
    public abstract Integer completeEventsExpiryDays();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
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

    @Nullable
    @JsonProperty()
    @ColumnAdapter(FeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AccessLevelColumnAdapter.class)
    public abstract AccessLevel accessLevel();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreAttributeValuesListAdapter.class)
    public abstract List<AttributeValue> attributeValues();

    public static Builder builder() {
        return new $$AutoValue_Program.Builder();
    }

    public static Program create(Cursor cursor) {
        return $AutoValue_Program.createFromCursor(cursor);
    }

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

        public abstract Builder selectIncidentDatesInFuture(Boolean selectIncidentDatesInFuture);

        /**
         * @deprecated since 2.29, replaced by {@link #featureType()}
         */
        @Deprecated
        abstract Builder captureCoordinates(Boolean captureCoordinates);

        public abstract Builder useFirstStageDuringRegistration(Boolean useFirstStageDuringRegistration);

        public abstract Builder displayFrontPageList(Boolean displayFrontPageList);

        public abstract Builder programType(ProgramType programType);

        abstract Builder programTrackedEntityAttributes(List<ProgramTrackedEntityAttribute>
                                                                       programTrackedEntityAttributes);

        public abstract Builder relatedProgram(ObjectWithUid relatedProgram);

        public abstract Builder trackedEntityType(TrackedEntityType trackedEntityType);

        public abstract Builder categoryCombo(ObjectWithUid categoryCombo);

        public abstract Builder access(Access access);

        abstract Builder programRuleVariables(List<ProgramRuleVariable> programRuleVariables);

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder completeEventsExpiryDays(Integer completeEventsExpiryDays);

        public abstract Builder expiryPeriodType(PeriodType expiryPeriodType);

        public abstract Builder minAttributesRequiredToSearch(Integer minAttributesRequiredToSearch);

        public abstract Builder maxTeiCountToReturn(Integer maxTeiCountToReturn);

        abstract Builder programSections(List<ProgramSection> programSections);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder accessLevel(AccessLevel accessLevel);

        public abstract Builder attributeValues(List<AttributeValue> attributeValues);

        abstract Program autoBuild();

        // Auxiliary fields
        abstract Boolean captureCoordinates();
        abstract FeatureType featureType();
        abstract Access access();
        abstract AccessLevel accessLevel();
        abstract ObjectStyle style();

        public Program build() {
            if (featureType() == null) {
                if (captureCoordinates() != null) {
                    featureType(captureCoordinates() ? FeatureType.POINT : FeatureType.NONE);
                }
            } else {
                captureCoordinates(featureType() != FeatureType.NONE);
            }

            try {
                access();
            } catch (IllegalStateException e) {
                access(AccessHelper.defaultAccess());
            }

            if (accessLevel() == null) {
                accessLevel(AccessLevel.OPEN);      // Since 2.30
            }

            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            return autoBuild();
        }
    }
}