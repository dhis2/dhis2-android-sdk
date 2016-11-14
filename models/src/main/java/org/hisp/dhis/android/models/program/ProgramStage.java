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

package org.hisp.dhis.android.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.models.common.BaseIdentifiableObject;
import org.hisp.dhis.android.models.common.FormType;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStage.Builder.class)
public abstract class ProgramStage extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_EXECUTION_DATE_LABEL = "executionDateLabel";
    private static final String JSON_PROPERTY_ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
    private static final String JSON_PROPERTY_VALID_COMPLETE_ONLY = "validCompleteOnly";
    private static final String JSON_PROPERTY_REPORT_DATE_TO_USE = "reportDateToUse";
    private static final String JSON_PROPERTY_OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
    private static final String JSON_PROPERTY_PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    private static final String JSON_PROPERTY_REPEATABLE = "repeatable";
    private static final String JSON_PROPERTY_CAPTURE_COORDINATES = "captureCoordinates";
    private static final String JSON_PROPERTY_FORM_TYPE = "formType";
    private static final String JSON_PROPERTY_DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
    private static final String JSON_PROPERTY_GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
    private static final String JSON_PROPERTY_AUTO_GENERATE_EVENT = "autoGenerateEvent";
    private static final String JSON_PROPERTY_SORT_ORDER = "sortOrder";
    private static final String JSON_PROPERTY_HIDE_DUE_DATE = "hideDueDate";
    private static final String JSON_PROPERTY_BLOCK_ENTRY_FORM = "blockEntryForm";
    private static final String JSON_PROPERTY_MIN_DAYS_FROM_START = "minDaysFromStart";
    private static final String JSON_PROPERTY_STANDARD_INTERVAL = "standardInterval";
    private static final String JSON_PROPERTY_PROGRAM_STAGE_SECTIONS = "programStageSections";

    @Nullable
    @JsonProperty(JSON_PROPERTY_EXECUTION_DATE_LABEL)
    public abstract String executionDateLabel();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ALLOW_GENERATE_NEXT_VISIT)
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALID_COMPLETE_ONLY)
    public abstract Boolean validCompleteOnly();

    @Nullable
    @JsonProperty(JSON_PROPERTY_REPORT_DATE_TO_USE)
    public abstract String reportDateToUse();

    @Nullable
    @JsonProperty(JSON_PROPERTY_OPEN_AFTER_ENROLLMENT)
    public abstract Boolean openAfterEnrollment();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_DATA_ELEMENTS)
    public abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    @JsonProperty(JSON_PROPERTY_REPEATABLE)
    public abstract Boolean repeatable();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FORM_TYPE)
    public abstract FormType formType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_GENERATE_EVENT_BOX)
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    @JsonProperty(JSON_PROPERTY_GENERATED_BY_ENROLMENT_DATE)
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_AUTO_GENERATE_EVENT)
    public abstract Boolean autoGenerateEvent();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(JSON_PROPERTY_HIDE_DUE_DATE)
    public abstract Boolean hideDueDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_BLOCK_ENTRY_FORM)
    public abstract Boolean blockEntryForm();

    @Nullable
    @JsonProperty(JSON_PROPERTY_MIN_DAYS_FROM_START)
    public abstract Integer minDaysFromStart();

    @Nullable
    @JsonProperty(JSON_PROPERTY_STANDARD_INTERVAL)
    public abstract Integer standardInterval();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_SECTIONS)
    public abstract List<ProgramStageSection> programStageSections();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_EXECUTION_DATE_LABEL)
        public abstract Builder executionDateLabel(@Nullable String executionDateLabel);

        @JsonProperty(JSON_PROPERTY_ALLOW_GENERATE_NEXT_VISIT)
        public abstract Builder allowGenerateNextVisit(@Nullable Boolean allowGenerateNextVisit);

        @JsonProperty(JSON_PROPERTY_VALID_COMPLETE_ONLY)
        public abstract Builder validCompleteOnly(@Nullable Boolean validCompleteOnly);

        @JsonProperty(JSON_PROPERTY_REPORT_DATE_TO_USE)
        public abstract Builder reportDateToUse(@Nullable String reportDateToUse);

        @JsonProperty(JSON_PROPERTY_OPEN_AFTER_ENROLLMENT)
        public abstract Builder openAfterEnrollment(@Nullable Boolean openAfterEnrollment);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_DATA_ELEMENTS)
        public abstract Builder programStageDataElements(
                @Nullable List<ProgramStageDataElement> programStageDataElements);

        @JsonProperty(JSON_PROPERTY_REPEATABLE)
        public abstract Builder repeatable(@Nullable Boolean repeatable);

        @JsonProperty(JSON_PROPERTY_CAPTURE_COORDINATES)
        public abstract Builder captureCoordinates(@Nullable Boolean captureCoordinates);

        @JsonProperty(JSON_PROPERTY_FORM_TYPE)
        public abstract Builder formType(@Nullable FormType formType);

        @JsonProperty(JSON_PROPERTY_DISPLAY_GENERATE_EVENT_BOX)
        public abstract Builder displayGenerateEventBox(@Nullable Boolean displayGenerateEventBox);

        @JsonProperty(JSON_PROPERTY_GENERATED_BY_ENROLMENT_DATE)
        public abstract Builder generatedByEnrollmentDate(@Nullable Boolean generatedByEnrollmentDate);

        @JsonProperty(JSON_PROPERTY_AUTO_GENERATE_EVENT)
        public abstract Builder autoGenerateEvent(@Nullable Boolean autoGenerateEvent);

        @JsonProperty(JSON_PROPERTY_SORT_ORDER)
        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        @JsonProperty(JSON_PROPERTY_HIDE_DUE_DATE)
        public abstract Builder hideDueDate(@Nullable Boolean hideDueDate);

        @JsonProperty(JSON_PROPERTY_BLOCK_ENTRY_FORM)
        public abstract Builder blockEntryForm(@Nullable Boolean blockEntryForm);

        @JsonProperty(JSON_PROPERTY_MIN_DAYS_FROM_START)
        public abstract Builder minDaysFromStart(@Nullable Integer minDaysFromStart);

        @JsonProperty(JSON_PROPERTY_STANDARD_INTERVAL)
        public abstract Builder standardInterval(@Nullable Integer standardInterval);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_SECTIONS)
        public abstract Builder programStageSections(
                @Nullable List<ProgramStageSection> programStageSections);

        abstract List<ProgramStageSection> programStageSections();

        abstract ProgramStage autoBuild();

        public ProgramStage build() {
            if (programStageSections() != null) {
                programStageSections(Collections.unmodifiableList(programStageSections()));
            }

            return autoBuild();
        }
    }
}