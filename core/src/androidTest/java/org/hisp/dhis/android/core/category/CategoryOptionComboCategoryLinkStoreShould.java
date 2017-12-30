package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.common.CategoryOptionMockFactory.generatedCategoryOption;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboCategoryLinkStoreShould extends AbsStoreTestCase {

    private static final String UPDATED_CATEGORY_OPTION_UID = "UPDATED_CATEGORY_OPTION_UID";
    private static final String DEFAULT_CATEGORY_OPTION_UID = "DEFAULT_CATEGORY_OPTION_UID";
    private static final String DEFAULT_CATEGORY_OPTION_COMBO_UID =
            "DEFAULT_CATEGORY_OPTION_COMBO_UID";
    private CategoryOptionComboCategoryLinkStore store;
    private CategoryOption newCategoryOption;
    private CategoryOptionCombo newCategoryOptionCombo;
    private CategoryOptionComboCategoryLinkModel newCategoryOptionComboCategoryLink;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionComboCategoryLinkStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_option_combo_link_category() throws Exception {
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertNewCategoryOptionComboLinkWasInserted();
    }

    @Test
    public void delete_a_category_option_combo_category_link() {
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertNewCategoryOptionComboLinkWasInserted();

        whenDeleteACategoryOptionComboCategoryLink();

        thenAssertThatThereAreNotCategoryComboCategoryLinkInDB();
    }

    @Test
    public void delete_all_category_option_combo_category_link_from_db() {
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertNewCategoryOptionComboLinkWasInserted();

        whenDeleteAllCategoryOptionComboCategoryLinks();

        thenAssertThatThereAreNotCategoryComboCategoryLinkInDB();
    }

    @Test
    public void update_a_category_option_combo_category_link() {
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertNewCategoryOptionComboLinkWasInserted();

        whenUpdateANewCategoryOptionComboCategoryLink();

        thenAssertThatNewCategoryOptionComboCategoryLinkWasUpdated();

    }

    @Test
    public void query_all_category_option_combo_category_link() {
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertNewCategoryOptionComboLinkWasInserted();

        thenAssertThatQueryAllBringCategoryComboCategoryLinksInDB();
    }

    private void givenACategoryOption() {
        newCategoryOption = generatedCategoryOption(DEFAULT_CATEGORY_OPTION_UID);
    }

    private void givenACategoryOptionCombo() {
        newCategoryOptionCombo = generateCategoryOptionCombo(DEFAULT_CATEGORY_OPTION_COMBO_UID);
    }

    private void givenACategoryOptionComboCategoryLink() {
        newCategoryOptionComboCategoryLink = CategoryOptionComboCategoryLinkModel.builder()
                .category(DEFAULT_CATEGORY_OPTION_UID)
                .categoryOptionCombo(DEFAULT_CATEGORY_OPTION_COMBO_UID)
                .build();
    }

    private void whenInsertNewCategoryOption(CategoryOption categoryOption) {
        CategoryOptionStoreImpl store = new CategoryOptionStoreImpl(databaseAdapter());
        store.insert(categoryOption);
    }

    private void whenInsertNewCategoryOptionComboCategoryLink() {
        givenACategoryOption();
        givenACategoryOptionCombo();
        givenACategoryOptionComboCategoryLink();

        whenInsertNewCategoryOption(newCategoryOption);
        whenInsertNewCategoryOptionCombo();

        lastInsertedID = store.insert(newCategoryOptionComboCategoryLink);
    }

    private void whenInsertNewCategoryOptionCombo() {
        CategoryOptionComboStore optionStore = new CategoryOptionComboStoreImpl(databaseAdapter());
        optionStore.insert(newCategoryOptionCombo);
    }

    private void thenAssertNewCategoryOptionComboLinkWasInserted() {
        assertEquals(lastInsertedID, 1);
    }

    private CategoryOptionCombo generateCategoryOptionCombo(String uid) {
        return CategoryOptionCombo.builder()
                .uid(uid)
                .created(new Date())
                .name("SECHN, Male")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .build();
    }

    private void thenAssertThatThereAreNotCategoryComboCategoryLinkInDB() {
        List<CategoryOptionComboCategoryLinkModel> list = store.queryAll();
        assertTrue(list.isEmpty());
    }

    private void whenDeleteACategoryOptionComboCategoryLink() {
        store.delete(newCategoryOptionComboCategoryLink);
    }

    private void whenDeleteAllCategoryOptionComboCategoryLinks() {
        store.delete();
    }

    private void thenAssertThatQueryAllBringCategoryComboCategoryLinksInDB() {
        List<CategoryOptionComboCategoryLinkModel> list = store.queryAll();
        assertFalse(list.isEmpty());
    }

    private void whenUpdateANewCategoryOptionComboCategoryLink() {
        CategoryOption categoryOption = generatedCategoryOption(UPDATED_CATEGORY_OPTION_UID);
        whenInsertNewCategoryOption(categoryOption);

        CategoryOptionComboCategoryLinkModel updatedCategoryOptionComboCategoryLinkModel
                = CategoryOptionComboCategoryLinkModel
                .builder()
                .category(UPDATED_CATEGORY_OPTION_UID)
                .categoryOptionCombo(DEFAULT_CATEGORY_OPTION_COMBO_UID)
                .build();

        CategoryOptionComboCategoryLinkModel oldCategoryOptionComboCategoryLinkModel =
                newCategoryOptionComboCategoryLink;

        store.update(oldCategoryOptionComboCategoryLinkModel,
                updatedCategoryOptionComboCategoryLinkModel);

    }

    private void thenAssertThatNewCategoryOptionComboCategoryLinkWasUpdated() {
        List<CategoryOptionComboCategoryLinkModel> list = store.queryAll();
        assertTrue(list.get(0).category().equals(UPDATED_CATEGORY_OPTION_UID));
    }
}