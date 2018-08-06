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

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStage.Builder.class)
public abstract class ProgramStage extends BaseIdentifiableObject {
    private final static String DESCRIPTION = "description";
    private final static String DISPLAY_DESCRIPTION = "displayDescription";
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
    private static final String STYLE = "style";
    private static final String PERIOD_TYPE = "periodType";
    private static final String PROGRAM = "program";
    private final static String ACCESS = "access";
    private final static String REMIND_COMPLETED = "remindCompleted";

    static final Field<ProgramStage, String> uid = Field.create(UID);
    private static final Field<ProgramStage, String> code = Field.create(CODE);
    private static final Field<ProgramStage, String> name = Field.create(NAME);
    private static final Field<ProgramStage, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<ProgramStage, String> created = Field.create(CREATED);
    static final Field<ProgramStage, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<ProgramStage, Integer> sortOrder = Field.create(SORT_ORDER);
    private static final Field<ProgramStage, String> description = Field.create(DESCRIPTION);
    private static final Field<ProgramStage, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    private static final Field<ProgramStage, Boolean> deleted = Field.create(DELETED);
    private static final Field<ProgramStage, String> executionDateLabel = Field.create(EXECUTION_DATE_LABEL);
    private static final Field<ProgramStage, Boolean> allowGenerateNextVisit = Field.create(ALLOW_GENERATE_NEXT_VISIT);
    private static final Field<ProgramStage, Boolean> validCompleteOnly = Field.create(VALID_COMPLETE_ONLY);
    private static final Field<ProgramStage, String> reportDateToUse = Field.create(REPORT_DATE_TO_USE);
    private static final Field<ProgramStage, Boolean> openAfterEnrollment = Field.create(OPEN_AFTER_ENROLLMENT);
    private static final Field<ProgramStage, Boolean> repeatable = Field.create(REPEATABLE);
    private static final Field<ProgramStage, Boolean> captureCoordinates = Field.create(CAPTURE_COORDINATES);
    private static final Field<ProgramStage, FormType> formType = Field.create(FORM_TYPE);
    private static final Field<ProgramStage, Boolean> displayGenerateEventBox
            = Field.create(DISPLAY_GENERATE_EVENT_BOX);
    private static final Field<ProgramStage, Boolean> generatedByEnrollmentDate =
            Field.create(GENERATED_BY_ENROLMENT_DATE);
    private static final Field<ProgramStage, Boolean> autoGenerateEvent = Field.create(AUTO_GENERATE_EVENT);
    private static final Field<ProgramStage, Boolean> hideDueDate = Field.create(HIDE_DUE_DATE);
    private static final Field<ProgramStage, Boolean> blockEntryForm = Field.create(BLOCK_ENTRY_FORM);
    private static final Field<ProgramStage, Integer> minDaysFromStart = Field.create(MIN_DAYS_FROM_START);
    private static final Field<ProgramStage, Integer> standardInterval = Field.create(STANDARD_INTERVAL);
    private static final Field<ProgramStage, PeriodType> periodType = Field.create(PERIOD_TYPE);
    private static final Field<ProgramStage, Boolean> remindCompleted = Field.create(REMIND_COMPLETED);

    private static final NestedField<ProgramStage, ProgramStageSection> programStageSections
            = NestedField.create(PROGRAM_STAGE_SECTIONS);
    private static final NestedField<ProgramStage, ProgramStageDataElement> programStageDataElements =
            NestedField.create(PROGRAM_STAGE_DATA_ELEMENTS);
    private static final NestedField<ProgramStage, ObjectStyle> style = NestedField.create(STYLE);
    private static final NestedField<ProgramStage, ObjectWithUid> program = NestedField.create(PROGRAM);
    private static final NestedField<ProgramStage, Access> access = NestedField.create(ACCESS);

    static final Fields<ProgramStage> allFields = Fields.<ProgramStage>builder().fields(
            uid, code, name, displayName, created, lastUpdated, description, displayDescription, allowGenerateNextVisit,
            autoGenerateEvent, blockEntryForm, captureCoordinates, deleted, displayGenerateEventBox, executionDateLabel,
            formType, generatedByEnrollmentDate, hideDueDate, minDaysFromStart, openAfterEnrollment, repeatable,
            reportDateToUse, sortOrder, standardInterval, validCompleteOnly,
            programStageDataElements.with(ProgramStageDataElement.allFields),
            programStageSections.with(ProgramStageSection.allFields),
            style.with(ObjectStyle.allFields), periodType, program.with(ObjectWithUid.uid),
            access.with(Access.data.with(DataAccess.write)), remindCompleted).build();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String displayDescription();

    @Nullable
    public abstract String executionDateLabel();

    @Nullable
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    public abstract Boolean validCompleteOnly();

    @Nullable
    public abstract String reportDateToUse();

    @Nullable
    public abstract Boolean openAfterEnrollment();

    @Nullable
    public abstract Boolean repeatable();

    @Nullable
    public abstract Boolean captureCoordinates();

    @Nullable
    public abstract FormType formType();

    @Nullable
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    public abstract Boolean autoGenerateEvent();

    @Nullable
    public abstract Integer sortOrder();

    @Nullable
    public abstract Boolean hideDueDate();

    @Nullable
    public abstract Boolean blockEntryForm();

    @Nullable
    public abstract Integer minDaysFromStart();

    @Nullable
    public abstract Integer standardInterval();

    @Nullable
    public abstract List<ProgramStageSection> programStageSections();

    @Nullable
    public abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    public abstract ObjectStyle style();

    @Nullable
    public abstract PeriodType periodType();

    @Nullable
    public abstract ObjectWithUid program();

    String programUid() {
        ObjectWithUid program = program();
        return program == null ? null : program.uid();
    }

    @Nullable
    public abstract Access access();

    @Nullable
    public abstract Boolean remindCompleted();

    public static Builder builder() {
        return new AutoValue_ProgramStage.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder description(String description);

        public abstract Builder displayDescription(String displayDescription);

        public abstract Builder executionDateLabel(String executionDateLabel);

        public abstract Builder allowGenerateNextVisit(Boolean allowGenerateNextVisit);

        public abstract Builder validCompleteOnly(Boolean validCompleteOnly);

        public abstract Builder reportDateToUse(String reportDateToUse);

        public abstract Builder openAfterEnrollment(Boolean openAfterEnrollment);

        public abstract Builder repeatable(Boolean repeatable);

        public abstract Builder captureCoordinates(Boolean captureCoordinates);

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

        public abstract Builder style(ObjectStyle style);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder access(Access access);

        public abstract Builder remindCompleted(Boolean remindCompleted);

        public abstract ProgramStage build();
    }
}