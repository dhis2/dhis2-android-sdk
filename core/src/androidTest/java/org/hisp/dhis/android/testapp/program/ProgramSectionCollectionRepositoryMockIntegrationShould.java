package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramSectionCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<ProgramSection> sections = d2.programModule().programSections
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void include_object_style_as_children() {
        ProgramSection section = d2.programModule().programSections
                .one().getWithAllChildren();
        assertThat(section.style().icon(), is("section-icon"));
        assertThat(section.style().color(), is("#555"));
    }
}