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

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStageModel.Columns;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class ProgramStageModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String EXECUTION_DATE_LABEL = "test_executionDateLabel";
    private static final Integer ALLOW_GENERATE_NEXT_VISIT = 0;
    private static final Integer VALID_COMPLETE_ONLY = 0;
    private static final String REPORT_DATE_TO_USE = "test_reportDateToUse";
    private static final Integer OPEN_AFTER_ENROLLMENT = 0;

    private static final Integer REPEATABLE = 0;
    private static final Integer CAPTURE_COORDINATES = 1;
    private static final FormType FORM_TYPE = FormType.DEFAULT;
    private static final Integer DISPLAY_GENERATE_EVENT_BOX = 1;
    private static final Integer GENERATED_BY_ENROLMENT_DATE = 1;
    private static final Integer AUTO_GENERATE_EVENT = 0;
    private static final Integer SORT_ORDER = 0;
    private static final Integer HIDE_DUE_DATE = 1;
    private static final Integer BLOCK_ENTRY_FORM = 0;
    private static final Integer MIN_DAYS_FROM_START = 5;
    private static final Integer STANDARD_INTERVAL = 7;
    private static final String PROGRAM = "test_program";
    private static final PeriodType PERIOD_TYPE = PeriodType.Weekly;
    private static final Integer ACCESS_DATA_WRITE = 1;
    private static final Integer REMIND_COMPLETED = 1;
    private static final String DESCRIPTION = "description";
    private static final String DISPLAY_DESCRIPTION = "displayDescription";
    private static final FeatureType FEATURE_TYPE = FeatureType.POINT;

    private final Date date;
    private final String dateString;

    public ProgramStageModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    public void create_model_when_created_from_database_cursor() {
        String[] columnsWithId = Utils.appendInNewArray(new ProgramStageModel.Columns().all(), ProgramModel.Columns.ID);
        MatrixCursor cursor = new MatrixCursor(columnsWithId);
        cursor.addRow(new Object[]{
                UID, CODE, NAME, DISPLAY_NAME,
                dateString, dateString,
                DESCRIPTION, DISPLAY_DESCRIPTION,
                EXECUTION_DATE_LABEL,
                ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY,
                REPORT_DATE_TO_USE,
                OPEN_AFTER_ENROLLMENT,
                REPEATABLE,
                CAPTURE_COORDINATES,
                FORM_TYPE,
                DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE,
                AUTO_GENERATE_EVENT,
                SORT_ORDER,
                HIDE_DUE_DATE,
                BLOCK_ENTRY_FORM,
                MIN_DAYS_FROM_START,
                STANDARD_INTERVAL,
                PROGRAM,
                PERIOD_TYPE,
                ACCESS_DATA_WRITE,
                REMIND_COMPLETED,
                FEATURE_TYPE,
                ID
        });
        cursor.moveToFirst();

        ProgramStageModel model = ProgramStageModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.executionDateLabel()).isEqualTo(EXECUTION_DATE_LABEL);
        assertThat(model.allowGenerateNextVisit()).isFalse();
        assertThat(model.validCompleteOnly()).isFalse();
        assertThat(model.reportDateToUse()).isEqualTo(REPORT_DATE_TO_USE);
        assertThat(model.openAfterEnrollment()).isFalse();
        assertThat(model.repeatable()).isFalse();
        assertThat(model.captureCoordinates()).isTrue();
        assertThat(model.formType()).isEqualTo(FORM_TYPE);
        assertThat(model.displayGenerateEventBox()).isTrue();
        assertThat(model.generatedByEnrollmentDate()).isTrue();
        assertThat(model.autoGenerateEvent()).isFalse();
        assertThat(model.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(model.hideDueDate()).isTrue();
        assertThat(model.blockEntryForm()).isFalse();
        assertThat(model.minDaysFromStart()).isEqualTo(MIN_DAYS_FROM_START);
        assertThat(model.standardInterval()).isEqualTo(STANDARD_INTERVAL);
        assertThat(model.program()).isEqualTo(PROGRAM);
        assertThat(model.periodType()).isEqualTo(PERIOD_TYPE);
        assertThat(model.accessDataWrite()).isTrue();
        assertThat(model.remindCompleted()).isTrue();
        assertThat(model.description()).isEqualTo(DESCRIPTION);
        assertThat(model.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(model.featureType()).isEqualTo(FEATURE_TYPE);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ProgramStageModel model = ProgramStageModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .executionDateLabel(EXECUTION_DATE_LABEL)
                .allowGenerateNextVisit(toBoolean(ALLOW_GENERATE_NEXT_VISIT))
                .validCompleteOnly(toBoolean(VALID_COMPLETE_ONLY))
                .reportDateToUse(REPORT_DATE_TO_USE)
                .openAfterEnrollment(toBoolean(OPEN_AFTER_ENROLLMENT))
                .repeatable(toBoolean(REPEATABLE))
                .captureCoordinates(toBoolean(CAPTURE_COORDINATES))
                .formType(FORM_TYPE)
                .displayGenerateEventBox(toBoolean(DISPLAY_GENERATE_EVENT_BOX))
                .generatedByEnrollmentDate(toBoolean(GENERATED_BY_ENROLMENT_DATE))
                .autoGenerateEvent(toBoolean(AUTO_GENERATE_EVENT))
                .sortOrder(SORT_ORDER)
                .hideDueDate(toBoolean(HIDE_DUE_DATE))
                .blockEntryForm(toBoolean(BLOCK_ENTRY_FORM))
                .minDaysFromStart(MIN_DAYS_FROM_START)
                .standardInterval(STANDARD_INTERVAL)
                .program(PROGRAM)
                .periodType(PERIOD_TYPE)
                .accessDataWrite(toBoolean(ACCESS_DATA_WRITE))
                .remindCompleted(toBoolean(REMIND_COMPLETED))
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .featureType(FEATURE_TYPE)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.EXECUTION_DATE_LABEL)).isEqualTo(EXECUTION_DATE_LABEL);
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_GENERATE_NEXT_VISIT)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.VALID_COMPLETE_ONLY)).isFalse();
        assertThat(contentValues.getAsString(Columns.REPORT_DATE_TO_USE)).isEqualTo(REPORT_DATE_TO_USE);
        assertThat(contentValues.getAsBoolean(Columns.OPEN_AFTER_ENROLLMENT)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.REPEATABLE)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.CAPTURE_COORDINATES)).isTrue();
        assertThat(contentValues.getAsString(Columns.FORM_TYPE)).isEqualTo(FORM_TYPE.name());
        assertThat(contentValues.getAsBoolean(Columns.DISPLAY_GENERATE_EVENT_BOX)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.GENERATED_BY_ENROLMENT_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.AUTO_GENERATE_EVENT)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(Columns.HIDE_DUE_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.BLOCK_ENTRY_FORM)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.MIN_DAYS_FROM_START)).isEqualTo(MIN_DAYS_FROM_START);
        assertThat(contentValues.getAsInteger(Columns.STANDARD_INTERVAL)).isEqualTo(STANDARD_INTERVAL);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(contentValues.getAsString(Columns.PERIOD_TYPE)).isEqualTo(PERIOD_TYPE.name());
        assertThat(contentValues.getAsBoolean(Columns.ACCESS_DATA_WRITE)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.REMIND_COMPLETED)).isTrue();
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.FEATURE_TYPE)).isEqualTo(FEATURE_TYPE.name());
    }
}
