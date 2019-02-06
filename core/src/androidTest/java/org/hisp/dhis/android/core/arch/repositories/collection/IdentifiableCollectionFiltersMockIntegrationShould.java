package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboCollectionRepository;
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
public class IdentifiableCollectionFiltersMockIntegrationShould extends MockIntegrationShould {

    private final String BEFORE_DATE = "2007-12-24T12:24:25.203";
    private final String IN_BETWEEN_DATE = "2016-04-16T18:04:34.745";
    private final String AFTER_DATE =  "2017-12-24T12:24:25.203";

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_objects_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void get_objects_with_equal_name_using_one() {
        ReadOnlyObjectRepository<CategoryCombo> objectRepository = d2.categoryModule().categoryCombos
                .byName().eq("Births")
                .one();
        CategoryCombo combo = objectRepository.get();
        assertThat(combo.uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_children_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.getWithAllChildren();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
        assertThat(combos.get(0).categories().isEmpty(), is(false));
    }

    @Test
    public void find_objects_with_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().eq("BIRTHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_uid() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byUid().eq("m2jTvAj5kkm");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_display_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byDisplayName().eq("Births Display");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Deaths");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().eq("DEATHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void find_objects_with_like_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_like_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.203");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCreated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_equal_last_updated() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-18T16:04:34.745");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_last_updated_before_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(0));
    }

    @Test
    public void find_objects_with_last_updated_before_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(DEFAULT_UID));
    }

    @Test
    public void find_objects_with_last_updated_before_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_last_updated_after_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_last_updated_after_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_last_updated_after_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(0));
    }

    @Test
    public void combine_date_and_string_filters() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created)
                .byName().like("%t%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }
}