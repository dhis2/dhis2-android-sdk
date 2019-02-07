package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
        downloadTrackedEntityInstances();
    }

    @Test
    public void allow_access_to_all_teis_without_children() {
        List<TrackedEntityInstance> trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstances.get();
        assertThat(trackedEntityInstances.size(), is(1));
        for (TrackedEntityInstance tei: trackedEntityInstances) {
            assertThat(tei.uid(), is("nWrB0TfWlvh"));
            assertThat(tei.organisationUnit(), is("DiszpKrYNg8"));
            assertThat(tei.trackedEntityAttributeValues() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_one_tei_without_children() {
        TrackedEntityInstance tei = d2.trackedEntityModule().trackedEntityInstances.uid("nWrB0TfWlvh").get();
        assertThat(tei.uid(), is("nWrB0TfWlvh"));
        assertThat(tei.organisationUnit(), is("DiszpKrYNg8"));
        assertThat(tei.trackedEntityAttributeValues() == null, is(true));
    }
}