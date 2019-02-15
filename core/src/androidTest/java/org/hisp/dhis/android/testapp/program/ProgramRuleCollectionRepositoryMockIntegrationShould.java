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
    public void include_program_rule_actions_as_children() {
        ProgramRule programRule = d2.programModule().programRules
                .one().getWithAllChildren();
        assertThat(programRule.programRuleActions().size(), is(1));
        assertThat(programRule.programRuleActions().get(0).content(), is("The hemoglobin value cannot be above 99"));
    }
}