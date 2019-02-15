package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramIndicatorCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<ProgramIndicator> indicators = d2.programModule().programIndicators
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void include_legend_sets_as_children() {
        ProgramIndicator programIndicators = d2.programModule().programIndicators
                .one().getWithAllChildren();
        assertThat(programIndicators.legendSets().size(), is(1));
        assertThat(programIndicators.legendSets().get(0).name(), is("Age 15y interval"));
    }
}