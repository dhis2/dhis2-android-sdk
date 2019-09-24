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

import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.AccessLevel;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(D2JunitRunner.class)
public class ProgramCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Program> programs = d2.programModule().programs
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_version() {
        List<Program> programs = d2.programModule().programs
                .byVersion().eq(3)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_only_enroll_once() {
        List<Program> programs = d2.programModule().programs
                .byOnlyEnrollOnce().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_enrollment_date_label() {
        List<Program> programs = d2.programModule().programs
                .byEnrollmentDateLabel().eq("Enrollment Date")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_display_incident_date() {
        List<Program> programs = d2.programModule().programs
                .byDisplayIncidentDate().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_incident_date_label() {
        List<Program> programs = d2.programModule().programs
                .byIncidentDateLabel().eq("Incident Date")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_registration() {
        List<Program> programs = d2.programModule().programs
                .byRegistration().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_select_enrollment_dates_in_future() {
        List<Program> programs = d2.programModule().programs
                .bySelectEnrollmentDatesInFuture().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_data_entry_method() {
        List<Program> programs = d2.programModule().programs
                .byDataEntryMethod().isTrue()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_ignore_overdue_events() {
        List<Program> programs = d2.programModule().programs
                .byIgnoreOverdueEvents().isTrue()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_select_incident_dates_in_future() {
        List<Program> programs = d2.programModule().programs
                .bySelectIncidentDatesInFuture().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_feature_type() {
        List<Program> programs = d2.programModule().programs
                .byFeatureType().eq(FeatureType.NONE)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_access_level() {
        List<Program> programs = d2.programModule().programs
                .byAccessLevel().eq(AccessLevel.PROTECTED)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_use_first_stage_during_registration() {
        List<Program> programs = d2.programModule().programs
                .byUseFirstStageDuringRegistration().isTrue()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_display_front_page_list() {
        List<Program> programs = d2.programModule().programs
                .byDisplayFrontPageList().isFalse()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_program_type() {
        List<Program> programs = d2.programModule().programs
                .byProgramType().eq(ProgramType.WITHOUT_REGISTRATION)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_related_program_uid() {
        List<Program> programs = d2.programModule().programs
                .byRelatedProgramUid().eq("lxAQ7Zs9VYR")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_tracked_entity_type_uid() {
        List<Program> programs = d2.programModule().programs
                .byTrackedEntityTypeUid().eq("nEenWmSyUEp")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_category_combo_uid() {
        List<Program> programs = d2.programModule().programs
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_access_data_write() {
        List<Program> programs = d2.programModule().programs
                .byAccessDataWrite().isTrue()
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_expiry_days() {
        List<Program> programs = d2.programModule().programs
                .byExpiryDays().eq(2)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_complete_events_expiry_days() {
        List<Program> programs = d2.programModule().programs
                .byCompleteEventsExpiryDays().eq(4)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_expiry_period_type() {
        List<Program> programs = d2.programModule().programs
                .byExpiryPeriodType().eq(PeriodType.BiMonthly)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_min_attributes_required_to_search() {
        List<Program> programs = d2.programModule().programs
                .byMinAttributesRequiredToSearch().eq(7)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_max_tei_count_to_return() {
        List<Program> programs = d2.programModule().programs
                .byMaxTeiCountToReturn().eq(20)
                .blockingGet();
        assertThat(programs.size(), is(1));
    }


    @Test
    public void filter_by_orgunit_uid() {
        List<Program> programs = d2.programModule().programs
                .byOrganisationUnitUid("DiszpKrYNg8")
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_orgunit_list() {
        List<Program> programs = d2.programModule().programs
                .byOrganisationUnitList(Collections.singletonList("DiszpKrYNg8"))
                .blockingGet();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void include_object_style_as_children_in_object_repository_when_all_selected() {
        Program program = d2.programModule().programs
                .withStyle().one().blockingGet();
        assertThat(program.style().icon(), is("program-icon"));
        assertThat(program.style().color(), is("#333"));
    }

    @Test
    public void include_object_style_as_children_in_object_repository_when_explicitly_selected() {
        Program program = d2.programModule().programs
                .withStyle()
                .one().blockingGet();
        assertThat(program.style().icon(), is("program-icon"));
        assertThat(program.style().color(), is("#333"));
    }

    @Test
    public void include_object_style_as_children_in_collection_repository_when_all_selected() {
        Program program = d2.programModule().programs
                .withStyle().blockingGet().get(0);
        assertThat(program.style().icon(), is("program-icon"));
        assertThat(program.style().color(), is("#333"));
    }

    @Test
    public void include_object_style_as_children_in_collection_repository_when_explicitly_selected() {
        Program program = d2.programModule().programs
                .withStyle()
                .blockingGet().get(0);
        assertThat(program.style().icon(), is("program-icon"));
        assertThat(program.style().color(), is("#333"));
    }

    @Test
    public void include_program_stages_as_children_when_all_selected() {
        Program program = d2.programModule().programs
                .withProgramStages().one().blockingGet();
        assertThat(program.programStages().size(), is(2));
        assertThat(program.programStages().get(0).name(), is("Antenatal care visit - Program rules demo"));
    }

    @Test
    public void include_program_stages_as_children_when_explicitly_selected() {
        Program program = d2.programModule().programs
                .withProgramStages().one().blockingGet();
        assertThat(program.programStages().size(), is(2));
        assertThat(program.programStages().get(0).name(), is("Antenatal care visit - Program rules demo"));
    }

    @Test
    public void not_include_style_as_children_when_only_program_stages_selected() {
        Program program = d2.programModule().programs
                .withProgramStages().one().blockingGet();
        assertThat(program.style() == null, is(true));
    }

    @Test
    public void include_program_rule_variables_as_children() {
        Program program = d2.programModule().programs
                .withProgramRuleVariables().one().blockingGet();
        assertThat(program.programRuleVariables().size(), is(2));
        assertThat(program.programRuleVariables().get(0).name(), is("hemoglobin"));
    }

    @Test
    public void include_program_indicators_as_children() {
        Program program = d2.programModule().programs
                .withProgramIndicators().one().blockingGet();
        assertThat(program.programIndicators().size(), is(2));
        assertThat(program.programIndicators().get(0).name(), is("Age at visit"));
    }

    @Test
    public void include_program_rules_as_children() {
        Program program = d2.programModule().programs
                .withProgramRules().one().blockingGet();
        assertThat(program.programRules().size(), is(3));
        assertThat(program.programRules().get(0).name(), is("Show error for high hemoglobin value"));
    }

    @Test
    public void include_program_tracked_entity_attributes_as_children() {
        Program program = d2.programModule().programs
                .withProgramTrackedEntityAttributes().one().blockingGet();
        assertThat(program.programTrackedEntityAttributes().size(), is(2));
        assertThat(program.programTrackedEntityAttributes().get(0).name(), is("Child Programme Gender"));
    }

    @Test
    public void include_program_sections_as_children() {
        Program program = d2.programModule().programs
                .withProgramSections().one().blockingGet();
        assertThat(program.programSections().size(), is(2));
        assertThat(program.programSections().get(0).name(), is("My Program Section"));
    }

    @Test
    public void include_category_combo_as_children() {
        Program program = d2.programModule().programs
                .withCategoryCombo().one().blockingGet();
        assertThat(program.categoryCombo().name(), is("Births"));
    }

    @Test
    public void include_related_program_as_children() {
        Program program = d2.programModule().programs
                .withRelatedProgram().one().blockingGet();
        assertThat(program.relatedProgram().name(), is("Antenatal care visit"));
    }

    @Test
    public void include_tracked_entity_type_as_children() {
        Program program = d2.programModule().programs
                .withTrackedEntityType().one().blockingGet();
        assertThat(program.trackedEntityType().name(), is("Person"));
    }
}