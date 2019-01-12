package org.hisp.dhis.android.core.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void allow_access_to_all_programs_without_children() {
        List<Program> programs = d2.programModule().programs.get();
        assertThat(programs.size(), is(1));
        for (Program program : programs) {
            assertThat(program.programIndicators() == null, is(true));
            assertThat(program.programStages() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_one_program_set_without_children() {
        Program program = d2.programModule().programs.uid("lxAQ7Zs9VYR").get();
        assertThat(program.uid(), is("lxAQ7Zs9VYR"));
        assertThat(program.name(), is("Antenatal care visit"));
        assertThat(program.programIndicators() == null, is(true));
        assertThat(program.programStages() == null, is(true));
    }
}