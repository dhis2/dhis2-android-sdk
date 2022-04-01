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

package org.hisp.dhis.android.testapp.program;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramStageCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_description() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byDescription()
                        .eq("Description")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_display_description() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byDisplayDescription()
                        .eq("Display Description")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_execution_date_label() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byExecutionDateLabel()
                        .eq("Visit date")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_due_date_label() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byDueDateLabel()
                        .eq("Due date")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_allow_generate_next_visit() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byAllowGenerateNextVisit()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_valid_complete_only() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byValidCompleteOnly()
                        .isTrue()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_report_date_to_use() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byReportDateToUse()
                        .eq("report_date_to_use")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_open_after_enrollment() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byOpenAfterEnrollment()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_repeatable() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byRepeatable()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_feature_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byFeatureType()
                        .eq(FeatureType.POINT)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_form_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byFormType()
                        .eq(FormType.DEFAULT)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_display_generate_event_box() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byDisplayGenerateEventBox()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_generated_by_enrollment_data() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byGeneratedByEnrollmentDate()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_autogenerate_event() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byAutoGenerateEvent()
                        .isTrue()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_sort_order() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .bySortOrder()
                        .eq(1)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_hide_due_date() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byHideDueDate()
                        .isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_block_entry_form() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byBlockEntryForm()
                        .isTrue()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_min_days_from_start() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byMinDaysFromStart()
                        .eq(0)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_standard_interval() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byStandardInterval()
                        .eq(0)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_enable_user_assignment() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byEnableUserAssignment().isFalse()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_period_type() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byPeriodType()
                        .eq(PeriodType.Monthly)
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byProgramUid()
                        .eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_access_data_write() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byAccessDataWrite()
                        .isTrue()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_remind_completed() {
        List<ProgramStage> programStages =
                d2.programModule().programStages()
                        .byRemindCompleted()
                        .isTrue()
                        .blockingGet();

        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_color() {
        List<ProgramStage> programStages = d2.programModule().programStages()
                .byColor().eq("#444")
                .blockingGet();
        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_icon() {
        List<ProgramStage> programStages = d2.programModule().programStages()
                .byIcon().eq("program-stage-icon")
                .blockingGet();
        assertThat(programStages.size()).isEqualTo(1);
    }

    @Test
    public void order_by_sort_order() {
        List<ProgramStage> programStages = d2.programModule().programStages()
                .orderBySortOrder(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();
        assertThat(programStages.get(0).uid()).isEqualTo("dBwrot7S421");
        assertThat(programStages.get(0).sortOrder()).isEqualTo(2);
        assertThat(programStages.get(1).uid()).isEqualTo("dBwrot7S420");
        assertThat(programStages.get(1).sortOrder()).isEqualTo(1);
    }

}