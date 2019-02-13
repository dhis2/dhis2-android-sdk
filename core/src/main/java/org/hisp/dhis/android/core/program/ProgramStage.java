/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.AccessColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbFormTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramStageDataElementListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramStageSectionListColumnAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStage.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.GodClass"})
public abstract class ProgramStage extends BaseIdentifiableObject
        implements ObjectWithStyle<ProgramStage, ProgramStage.Builder>, Model {

    @Nullable
    @JsonProperty()
    public abstract String description();

    @Nullable
    @JsonProperty()
    public abstract String displayDescription();

    @Nullable
    @JsonProperty()
    public abstract String executionDateLabel();

    @Nullable
    @JsonProperty()
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    @JsonProperty()
    public abstract Boolean validCompleteOnly();

    @Nullable
    @JsonProperty()
    public abstract String reportDateToUse();

    @Nullable
    @JsonProperty()
    public abstract Boolean openAfterEnrollment();

    @Nullable
    @JsonProperty()
    public abstract Boolean repeatable();

    /**
     * @deprecated since 2.29, replaced by {@link #featureType()}
     */
    @Deprecated
    @Nullable
    @JsonProperty()
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbFeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbFormTypeColumnAdapter.class)
    public abstract FormType formType();

    @Nullable
    @JsonProperty()
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    @JsonProperty()
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    @JsonProperty()
    public abstract Boolean autoGenerateEvent();

    @Nullable
    @JsonProperty()
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty()
    public abstract Boolean hideDueDate();

    @Nullable
    @JsonProperty()
    public abstract Boolean blockEntryForm();

    @Nullable
    @JsonProperty()
    public abstract Integer minDaysFromStart();

    @Nullable
    @JsonProperty()
    public abstract Integer standardInterval();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramStageSectionListColumnAdapter.class)
    public abstract List<ProgramStageSection> programStageSections();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramStageDataElementListColumnAdapter.class)
    public abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbPeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    @Nullable
    @JsonProperty()
    public abstract Boolean remindCompleted();

    public static Builder builder() {
        return new AutoValue_ProgramStage.Builder();
    }

    public static ProgramStage create(Cursor cursor) {
        return AutoValue_ProgramStage.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<ProgramStage, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder description(String description);

        public abstract Builder displayDescription(String displayDescription);

        public abstract Builder executionDateLabel(String executionDateLabel);

        public abstract Builder allowGenerateNextVisit(Boolean allowGenerateNextVisit);

        public abstract Builder validCompleteOnly(Boolean validCompleteOnly);

        public abstract Builder reportDateToUse(String reportDateToUse);

        public abstract Builder openAfterEnrollment(Boolean openAfterEnrollment);

        public abstract Builder repeatable(Boolean repeatable);

        public abstract Builder captureCoordinates(Boolean captureCoordinates);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder formType(FormType formType);

        public abstract Builder displayGenerateEventBox(Boolean displayGenerateEventBox);

        public abstract Builder generatedByEnrollmentDate(Boolean generatedByEnrollmentDate);

        public abstract Builder autoGenerateEvent(Boolean autoGenerateEvent);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder hideDueDate(Boolean hideDueDate);

        public abstract Builder blockEntryForm(Boolean blockEntryForm);

        public abstract Builder minDaysFromStart(Integer minDaysFromStart);

        public abstract Builder standardInterval(Integer standardInterval);

        public abstract Builder programStageSections(List<ProgramStageSection> programStageSections);

        public abstract Builder programStageDataElements(List<ProgramStageDataElement> programStageDataElements);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder access(Access access);

        public abstract Builder remindCompleted(Boolean remindCompleted);

        public abstract ProgramStage build();
    }
}