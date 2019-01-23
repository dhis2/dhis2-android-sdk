package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class NameableCollectionFiltersMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void allow_filter_by_short_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryOption> repositoryWithUpdatedScope = d2.categoryModule().categoryOptions
                .byShortName().eq("default short name");
        List<CategoryOption> categoryOptions = repositoryWithUpdatedScope.get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_display_short_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryOption> repositoryWithUpdatedScope = d2.categoryModule().categoryOptions
                .byDisplayShortName().eq("default display short name");
        List<CategoryOption> categoryOptions = repositoryWithUpdatedScope.get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_description() {
        ReadOnlyIdentifiableCollectionRepository<CategoryOption> repositoryWithUpdatedScope = d2.categoryModule().categoryOptions
                .byDescription().eq("default description");
        List<CategoryOption> categoryOptions = repositoryWithUpdatedScope.get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_display_description() {
        ReadOnlyIdentifiableCollectionRepository<CategoryOption> repositoryWithUpdatedScope = d2.categoryModule().categoryOptions
                .byDisplayDescription().eq("default display description");
        List<CategoryOption> categoryOptions = repositoryWithUpdatedScope.get();
        assertThat(categoryOptions.size(), is(1));
    }
}