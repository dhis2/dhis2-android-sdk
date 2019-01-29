package org.hisp.dhis.android.core.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<Category> categories = d2.categoryModule().categories.get();
        assertThat(categories.size(), is(4));
    }

    @Test
    public void filter_by_name() {
        List<Category> categories = d2.categoryModule().categories
                .byName().like("%e%")
                .get();
        assertThat(categories.size(), is(3));
    }

    @Test
    public void filter_by_data_dimension_type() {
        List<Category> categories = d2.categoryModule().categories
                .byDataDimensionType().eq("DISAGGREGATION")
                .get();
        assertThat(categories.size(), is(4));
    }
}