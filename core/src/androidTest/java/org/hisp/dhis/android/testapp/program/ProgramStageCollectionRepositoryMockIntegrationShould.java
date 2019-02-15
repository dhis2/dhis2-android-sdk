package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramStageCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<ProgramStage> stages = d2.programModule().programStages
                .get();
        assertThat(stages.size(), is(1));
    }

    @Test
    public void include_object_style_as_children() {
        ProgramStage stage = d2.programModule().programStages
                .one().getWithAllChildren();
        assertThat(stage.style().icon(), is("program-stage-icon"));
        assertThat(stage.style().color(), is("#444"));
    }
}