package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<Program> programs = d2.programModule().programs
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_version() {
        List<Program> programs = d2.programModule().programs
                .byVersion().eq(3)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_only_enroll_once() {
        List<Program> programs = d2.programModule().programs
                .byOnlyEnrollOnce().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_enrollment_date_label() {
        List<Program> programs = d2.programModule().programs
                .byEnrollmentDateLabel().eq("Enrollment Date")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_display_incident_date() {
        List<Program> programs = d2.programModule().programs
                .byDisplayIncidentDate().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_incident_date_label() {
        List<Program> programs = d2.programModule().programs
                .byIncidentDateLabel().eq("Incident Date")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_registration() {
        List<Program> programs = d2.programModule().programs
                .byRegistration().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_select_enrollment_dates_in_future() {
        List<Program> programs = d2.programModule().programs
                .bySelectEnrollmentDatesInFuture().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_data_entry_method() {
        List<Program> programs = d2.programModule().programs
                .byDataEntryMethod().isTrue()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_ignore_overdue_events() {
        List<Program> programs = d2.programModule().programs
                .byIgnoreOverdueEvents().isTrue()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_relationship_from_A() {
        List<Program> programs = d2.programModule().programs
                .byRelationshipFromA().isTrue()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_select_incident_dates_in_future() {
        List<Program> programs = d2.programModule().programs
                .bySelectIncidentDatesInFuture().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_capture_coordinates() {
        List<Program> programs = d2.programModule().programs
                .byCaptureCoordinates().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_use_first_stage_during_registration() {
        List<Program> programs = d2.programModule().programs
                .byUseFirstStageDuringRegistration().isTrue()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_display_front_page_list() {
        List<Program> programs = d2.programModule().programs
                .byDisplayFrontPageList().isFalse()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_program_type() {
        List<Program> programs = d2.programModule().programs
                .byProgramType().eq(ProgramType.WITHOUT_REGISTRATION)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_relationship_type_uid() {
        List<Program> programs = d2.programModule().programs
                .byRelationshipTypeUid().eq("V2kkHafqs8G")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_relationship_text() {
        List<Program> programs = d2.programModule().programs
                .byRelationshipText().eq("Relationship text")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_related_program_uid() {
        List<Program> programs = d2.programModule().programs
                .byRelatedProgramUid().eq("lxAQ7Zs9VYR")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_tracked_entity_type_uid() {
        List<Program> programs = d2.programModule().programs
                .byTrackedEntityTypeUid().eq("nEenWmSyUEp")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_category_combo_uid() {
        List<Program> programs = d2.programModule().programs
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_access_data_write() {
        List<Program> programs = d2.programModule().programs
                .byAccessDataWrite().isTrue()
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_expiry_days() {
        List<Program> programs = d2.programModule().programs
                .byExpiryDays().eq(2)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_complete_events_expiry_days() {
        List<Program> programs = d2.programModule().programs
                .byCompleteEventsExpiryDays().eq(4)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_expiry_period_type() {
        List<Program> programs = d2.programModule().programs
                .byExpiryPeriodType().eq(PeriodType.BiMonthly)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_min_attributes_required_to_search() {
        List<Program> programs = d2.programModule().programs
                .byMinAttributesRequiredToSearch().eq(7)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void filter_by_max_tei_count_to_return() {
        List<Program> programs = d2.programModule().programs
                .byMaxTeiCountToReturn().eq(20)
                .get();
        assertThat(programs.size(), is(1));
    }

    @Test
    public void include_object_style_as_chidren() {
        Program program = d2.programModule().programs
                .one().getWithAllChildren();
        assertThat(program.style().icon(), is("program-icon"));
        assertThat(program.style().color(), is("#333"));
    }

    @Test
    public void include_program_stages_as_chidren() {
        Program program = d2.programModule().programs
                .one().getWithAllChildren();
        assertThat(program.programStages().size(), is(1));
    }
}