package org.hisp.dhis.android.core.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryComboUidsSeekerMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void seek_category_combos_uids() {
        Set<String> categories = new CategoryComboUidsSeeker(databaseAdapter).seekUids();

        assertThat(categories.size(), is(2));
        assertThat(categories.contains("m2jTvAj5kkm"), is(true));
        // Default category combo (p0KPaWEg3cf).
        assertThat(categories.contains("p0KPaWEg3cf"), is(true));
    }
}