package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
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
    public void allow_filter_by_short_uid() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byUid().eq("as6ygGvUGNg")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_code() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byCode().eq("default code")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byName().eq("default name")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_display_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byDisplayName().eq("default display name")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:24.777");
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byCreated().eq(created)
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_last_updated() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T20:37:48.666");
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byLastUpdated().eq(lastUpdated)
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_short_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byShortName().eq("default short name")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_display_short_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byDisplayShortName().eq("default display short name")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_description() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byDescription().eq("default description")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_filter_by_display_description() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byDisplayDescription().eq("default display description")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_combination_of_identifiable_and_nameable_filter() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byName().eq("default name")
                .byDisplayDescription().eq("default display description")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }

    @Test
    public void allow_combination_of_nameable_and_identifiable_filter() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions
                .byDisplayDescription().eq("default display description")
                .byName().eq("default name")
                .get();
        assertThat(categoryOptions.size(), is(1));
    }
}