package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos.get();
        assertThat(categoryOptionCombos.size(), is(3));
    }

    @Test
    public void filter_by_category_combo_A() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .get();
        assertThat(categoryOptionCombos.size(), is(1));
    }

    @Test
    public void filter_by_category_combo_B() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos
                .byCategoryComboUid().eq("p0KPaWEg3cf")
                .get();
        assertThat(categoryOptionCombos.size(), is(2));
    }
}