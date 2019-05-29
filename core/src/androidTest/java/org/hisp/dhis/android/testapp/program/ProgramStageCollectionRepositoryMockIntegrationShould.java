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

package org.hisp.dhis.android.testapp.program;

import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramStageCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .get();

        assertThat(programStages.size(), is(2));
    }

    @Test
    public void include_object_style_as_children() {
        ProgramStage programStage =
                d2.programModule().programStages
                        .withStyle()
                        .one().get();

        assertThat(programStage.style().icon(), is("program-stage-icon"));
        assertThat(programStage.style().color(), is("#444"));
    }

    @Test
    public void include_program_stage_data_elements_as_children() {
        ProgramStage programStage =
                d2.programModule().programStages
                        .withProgramStageDataElements()
                        .one().get();

        assertThat(programStage.programStageDataElements().size(), is(3));
    }

    @Test
    public void include_program_stage_section_as_children() {
        ProgramStage programStage =
                d2.programModule().programStages
                        .withProgramStageSections()
                        .one()
                        .get();

        assertThat(programStage.programStageSections().size(), is(1));
    }

    @Test
    public void filter_by_description() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byDescription()
                        .eq("Description")
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_display_description() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byDisplayDescription()
                        .eq("Display Description")
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_execution_date_label() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byExecutionDateLabel()
                        .eq("Visit date")
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_allow_generate_next_visit() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byAllowGenerateNextVisit()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_valid_complete_only() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byValidCompleteOnly()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_report_date_to_use() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byReportDateToUse()
                        .eq("report_date_to_use")
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_open_after_enrollment() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byOpenAfterEnrollment()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_repeatable() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byRepeatable()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_capture_coordinates() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byCaptureCoordinates()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_feature_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byFeatureType()
                        .eq(FeatureType.POINT)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_form_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byFormType()
                        .eq(FormType.DEFAULT)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_display_generate_event_box() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byDisplayGenerateEventBox()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_generated_by_enrollment_data() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byGeneratedByEnrollmentDate()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_autogenerate_event() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byAutoGenerateEvent()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_sort_order() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .bySortOrder()
                        .eq(1)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_hide_due_date() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byHideDueDate()
                        .isFalse()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_block_entry_form() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byBlockEntryForm()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_min_days_from_start() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byMinDaysFromStart()
                        .eq(0)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_standard_interval() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byStandardInterval()
                        .eq(0)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_period_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byPeriodType()
                        .eq(PeriodType.Monthly)
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_program() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byProgramUid()
                        .eq("lxAQ7Zs9VYR")
                        .get();

        assertThat(programStages.size(), is(2));
    }

    @Test
    public void filter_by_access_data_write() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byAccessDataWrite()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

    @Test
    public void filter_by_remind_completed() {
        List<ProgramStage> programStages =
                d2.programModule().programStages
                        .byRemindCompleted()
                        .isTrue()
                        .get();

        assertThat(programStages.size(), is(1));
    }

}