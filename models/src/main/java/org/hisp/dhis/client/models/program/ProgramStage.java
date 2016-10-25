package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.common.FormType;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStage.Builder.class)
public abstract class ProgramStage extends BaseIdentifiableObject {
    private static final String EXECUTION_DATE_LABEL = "executionDateLabel";
    private static final String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
    private static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
    private static final String REPORT_DATE_TO_USE = "reportDateToUse";
    private static final String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
    private static final String PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    private static final String REPEATABLE = "repeatable";
    private static final String CAPTURE_COORDINATES = "captureCoordinates";
    private static final String FORM_TYPE = "formType";
    private static final String DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
    private static final String GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
    private static final String AUTO_GENERATE_EVENT = "autoGenerateEvent";
    private static final String SORT_ORDER = "sortOrder";
    private static final String HIDE_DUE_DATE = "hideDueDate";
    private static final String BLOCK_ENTRY_FORM = "blockEntryForm";
    private static final String MIN_DAYS_FROM_START = "minDaysFromStart";
    private static final String STANDARD_INTERVAL = "standardInterval";
    private static final String PROGRAM_STAGE_SECTIONS = "programStageSections";

    @Nullable
    @JsonProperty(EXECUTION_DATE_LABEL)
    public abstract String executionDateLabel();

    @Nullable
    @JsonProperty(ALLOW_GENERATE_NEXT_VISIT)
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    @JsonProperty(VALID_COMPLETE_ONLY)
    public abstract Boolean validCompleteOnly();

    @Nullable
    @JsonProperty(REPORT_DATE_TO_USE)
    public abstract String reportDateToUse();

    @Nullable
    @JsonProperty(OPEN_AFTER_ENROLLMENT)
    public abstract Boolean openAfterEnrollment();

    @Nullable
    @JsonProperty(PROGRAM_STAGE_DATA_ELEMENTS)
    public abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    @JsonProperty(REPEATABLE)
    public abstract Boolean repeatable();

    @Nullable
    @JsonProperty(CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @JsonProperty(FORM_TYPE)
    public abstract FormType formType();

    @Nullable
    @JsonProperty(DISPLAY_GENERATE_EVENT_BOX)
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    @JsonProperty(GENERATED_BY_ENROLMENT_DATE)
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    @JsonProperty(AUTO_GENERATE_EVENT)
    public abstract Boolean autoGenerateEvent();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(HIDE_DUE_DATE)
    public abstract Boolean hideDueDate();

    @Nullable
    @JsonProperty(BLOCK_ENTRY_FORM)
    public abstract Boolean blockEntryForm();

    @Nullable
    @JsonProperty(MIN_DAYS_FROM_START)
    public abstract Integer minDaysFromStart();

    @Nullable
    @JsonProperty(STANDARD_INTERVAL)
    public abstract Integer standardInterval();

    @Nullable
    @JsonProperty(PROGRAM_STAGE_SECTIONS)
    public abstract List<ProgramStageSection> programStageSections();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {
        @Nullable
        @JsonProperty(EXECUTION_DATE_LABEL)
        public abstract Builder executionDateLabel(@Nullable String executionDateLabel);

        @Nullable
        @JsonProperty(ALLOW_GENERATE_NEXT_VISIT)
        public abstract Builder allowGenerateNextVisit(@Nullable Boolean allowGenerateNextVisit);

        @Nullable
        @JsonProperty(VALID_COMPLETE_ONLY)
        public abstract Builder validCompleteOnly(@Nullable Boolean validCompleteOnly);

        @Nullable
        @JsonProperty(REPORT_DATE_TO_USE)
        public abstract Builder reportDateToUse(@Nullable String reportDateToUse);

        @Nullable
        @JsonProperty(OPEN_AFTER_ENROLLMENT)
        public abstract Builder openAfterEnrollment(@Nullable Boolean openAfterEnrollment);

        @Nullable
        @JsonProperty(PROGRAM_STAGE_DATA_ELEMENTS)
        public abstract Builder programStageDataElements(@Nullable List<ProgramStageDataElement> programStageDataElements);

        @Nullable
        @JsonProperty(REPEATABLE)
        public abstract Builder repeatable(@Nullable Boolean repeatable);

        @Nullable
        @JsonProperty(CAPTURE_COORDINATES)
        public abstract Builder captureCoordinates(@Nullable Boolean captureCoordinates);

        @Nullable
        @JsonProperty(FORM_TYPE)
        public abstract Builder formType(@Nullable FormType formType);

        @Nullable
        @JsonProperty(DISPLAY_GENERATE_EVENT_BOX)
        public abstract Builder displayGenerateEventBox(@Nullable Boolean displayGenerateEventBox);

        @Nullable
        @JsonProperty(GENERATED_BY_ENROLMENT_DATE)
        public abstract Builder generatedByEnrollmentDate(@Nullable Boolean generatedByEnrollmentDate);

        @Nullable
        @JsonProperty(AUTO_GENERATE_EVENT)
        public abstract Builder autoGenerateEvent(@Nullable Boolean autoGenerateEvent);

        @Nullable
        @JsonProperty(SORT_ORDER)
        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        @Nullable
        @JsonProperty(HIDE_DUE_DATE)
        public abstract Builder hideDueDate(@Nullable Boolean hideDueDate);

        @Nullable
        @JsonProperty(BLOCK_ENTRY_FORM)
        public abstract Builder blockEntryForm(@Nullable Boolean blockEntryForm);

        @Nullable
        @JsonProperty(MIN_DAYS_FROM_START)
        public abstract Builder minDaysFromStart(@Nullable Integer minDaysFromStart);

        @Nullable
        @JsonProperty(STANDARD_INTERVAL)
        public abstract Builder standardInterval(@Nullable Integer standardInterval);

        @Nullable
        @JsonProperty(PROGRAM_STAGE_SECTIONS)
        public abstract Builder programStageSections(@Nullable List<ProgramStageSection> programStageSections);

        abstract ProgramStage autoBuild();

        abstract List<ProgramStageSection> programStageSections();

        public ProgramStage build() {
            if (programStageSections() != null) {
                programStageSections(Collections.unmodifiableList(programStageSections()));
            }
            return autoBuild();
        }
    }
}