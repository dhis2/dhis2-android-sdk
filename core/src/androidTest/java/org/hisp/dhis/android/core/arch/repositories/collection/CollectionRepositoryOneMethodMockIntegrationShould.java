package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CollectionRepositoryOneMethodMockIntegrationShould extends MockIntegrationShould {

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void get_first_object_without_filters() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .one().get();
        assertThat(combo.uid(), is(BIRTH_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_one_object() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byName().eq("Births")
                .one().get();
        assertThat(combo.uid(), is(BIRTH_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_other_object() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byIsDefault().isTrue()
                .one().get();
        assertThat(combo.uid(), is(DEFAULT_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_no_objects() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byName().eq("Wrong name")
                .one().get();
        assertThat(combo == null, is(true));
    }

    @Test
    public void get_with_all_children_returns_object_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .one().getWithAllChildren();
        assertThat(combo.uid(), is(BIRTH_UID));
        assertThat(combo.categories().size(), is(2));
        assertThat(combo.categoryOptionCombos().size(), is(1));
    }
}