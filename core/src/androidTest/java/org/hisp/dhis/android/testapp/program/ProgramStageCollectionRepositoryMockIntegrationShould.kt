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
package org.hisp.dhis.android.testapp.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ValidationStrategy
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramStageCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val programStages = d2.programModule().programStages().blockingGet()

        assertThat(programStages.size).isEqualTo(2)
    }

    @Test
    fun filter_by_description() {
        val programStages = d2.programModule().programStages()
            .byDescription()
            .eq("Description")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_description() {
        val programStages = d2.programModule().programStages()
            .byDisplayDescription()
            .eq("Display Description")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_execution_date_label() {
        val programStages = d2.programModule().programStages()
            .byExecutionDateLabel()
            .eq("Visit date")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_due_date_label() {
        val programStages = d2.programModule().programStages()
            .byDueDateLabel()
            .eq("Due date")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_allow_generate_next_visit() {
        val programStages = d2.programModule().programStages()
            .byAllowGenerateNextVisit()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_valid_complete_only() {
        val programStages = d2.programModule().programStages()
            .byValidCompleteOnly()
            .isTrue
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_report_date_to_use() {
        val programStages = d2.programModule().programStages()
            .byReportDateToUse()
            .eq("report_date_to_use")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_open_after_enrollment() {
        val programStages = d2.programModule().programStages()
            .byOpenAfterEnrollment()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_repeatable() {
        val programStages = d2.programModule().programStages()
            .byRepeatable()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_feature_type() {
        val programStages = d2.programModule().programStages()
            .byFeatureType()
            .eq(FeatureType.POINT)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_form_type() {
        val programStages = d2.programModule().programStages()
            .byFormType()
            .eq(FormType.DEFAULT)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_generate_event_box() {
        val programStages = d2.programModule().programStages()
            .byDisplayGenerateEventBox()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_generated_by_enrollment_data() {
        val programStages = d2.programModule().programStages()
            .byGeneratedByEnrollmentDate()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_autogenerate_event() {
        val programStages = d2.programModule().programStages()
            .byAutoGenerateEvent()
            .isTrue
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_sort_order() {
        val programStages = d2.programModule().programStages()
            .bySortOrder()
            .eq(1)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_hide_due_date() {
        val programStages = d2.programModule().programStages()
            .byHideDueDate()
            .isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_block_entry_form() {
        val programStages = d2.programModule().programStages()
            .byBlockEntryForm()
            .isTrue
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_min_days_from_start() {
        val programStages = d2.programModule().programStages()
            .byMinDaysFromStart()
            .eq(0)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_standard_interval() {
        val programStages = d2.programModule().programStages()
            .byStandardInterval()
            .eq(0)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_enable_user_assignment() {
        val programStages = d2.programModule().programStages()
            .byEnableUserAssignment().isFalse
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_period_type() {
        val programStages = d2.programModule().programStages()
            .byPeriodType()
            .eq(PeriodType.Monthly)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val programStages = d2.programModule().programStages()
            .byProgramUid()
            .eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(2)
    }

    @Test
    fun filter_by_access_data_write() {
        val programStages = d2.programModule().programStages()
            .byAccessDataWrite()
            .isTrue
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_remind_completed() {
        val programStages = d2.programModule().programStages()
            .byRemindCompleted()
            .isTrue
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_validation_strategy() {
        val programStages = d2.programModule().programStages()
            .byValidationStrategy()
            .eq(ValidationStrategy.ON_UPDATE_AND_INSERT)
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_stage_label() {
        val programStages = d2.programModule().programStages()
            .byProgramStageLabel().eq("ProgramStage Label")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_event_label() {
        val programStages = d2.programModule().programStages()
            .byEventLabel().eq("Event Label")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_color() {
        val programStages = d2.programModule().programStages()
            .byColor().eq("#444")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_icon() {
        val programStages = d2.programModule().programStages()
            .byIcon().eq("visit_icon")
            .blockingGet()

        assertThat(programStages.size).isEqualTo(1)
    }

    @Test
    fun order_by_sort_order() {
        val programStages = d2.programModule().programStages()
            .orderBySortOrder(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(programStages[0].uid()).isEqualTo("dBwrot7S421")
        assertThat(programStages[0].sortOrder()).isEqualTo(2)
        assertThat(programStages[1].uid()).isEqualTo("dBwrot7S420")
        assertThat(programStages[1].sortOrder()).isEqualTo(1)
    }

    @Test
    fun include_attributeValues_as_children() {
        val programStageWithAttributeValues = d2.programModule().programStages()
            .withAttributes()
            .one()
            .blockingGet()

        val attributeValues = programStageWithAttributeValues!!.attributeValues()
        assertThat(attributeValues!!.size).isEqualTo(2)
        assertThat(attributeValues[0].attribute().uid()).isEqualTo("b0vcadVrn08")
        assertThat(attributeValues[0].value()).isEqualTo("Direct 2")
        assertThat(attributeValues[1].attribute().uid()).isEqualTo("qXS2NDUEAOS")
        assertThat(attributeValues[1].value()).isEqualTo("Direct")
    }
}
