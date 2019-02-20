package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<ProgramRule> rules = d2.programModule().programRules
                .get();
        assertThat(rules.size(), is(3));
    }

    @Test
    public void filter_by_priority() {
        List<ProgramRule> rules = d2.programModule().programRules
                .byPriority().eq(2)
                .get();

        assertThat(rules.size(), is(2));
    }

    @Test
    public void filter_by_condition() {
        List<ProgramRule> rules = d2.programModule().programRules
                .byCondition().eq("#{hemoglobin} < 9")
                .get();

        assertThat(rules.size(), is(1));
    }

    @Test
    public void filter_by_program() {
        List<ProgramRule> rules = d2.programModule().programRules
                .byProgramUid().eq("lxAQ7Zs9VYR")
                .get();

        assertThat(rules.size(), is(3));
    }

    @Test
    public void filter_by_program_stage() {
        List<ProgramRule> rules = d2.programModule().programRules
                .byProgramStageUid().eq("dBwrot7S420")
                .get();

        assertThat(rules.size(), is(1));
    }

}