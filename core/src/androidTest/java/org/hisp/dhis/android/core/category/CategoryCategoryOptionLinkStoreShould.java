package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.common.CategoryMother.generateCategory;
import static org.hisp.dhis.android.core.common.CategoryOptionMother.generatedCategoryOption;
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
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class CategoryCategoryOptionLinkStoreShould extends AbsStoreTestCase {

    private static final String DEFAULT_CATEGORY_OPTION_UID = "TNYQzTHdoxL";
    private static final String DEFAULT_CATEGORY_UID = "KfdsGBcoiCa";
    private static final String UPDATED_CATEGORY_UID = "category_uid";
    private CategoryCategoryOptionLinkStore store;
    private Category newCategory;
    private CategoryOption newCategoryOption;
    private CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryCategoryOptionLinkStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_option_link() throws Exception {

        givenAllTheCategoryOptionLinkDependencies();

        whenInsertNewCategoryOptionLink();

        thenAssertThatNewCategoryOptionLinkWasInserted();
    }

    @Test
    public void delete_a_category_option_link() {

        givenAllTheCategoryOptionLinkDependencies();

        whenInsertNewCategoryOptionLink();

        thenAssertThatNewCategoryOptionLinkWasInserted();

        whenDeleteNewCategoryOptionLink();

        thenAssertThereAreNotItemsOnCategoryOptionLinkTable();

    }

    @Test
    public void delete_all_elements_on_category_option_link_table() {

        givenAllTheCategoryOptionLinkDependencies();

        whenInsertNewCategoryOptionLink();

        thenAssertThatNewCategoryOptionLinkWasInserted();

        whenDeleteAllElementsOnCategoryComboLinkTable();

        thenAssertThereAreNotItemsOnCategoryOptionLinkTable();
    }

    @Test
    public void update_a_category_option_link() {

        givenAllTheCategoryOptionLinkDependencies();

        whenInsertNewCategoryOptionLink();

        thenAssertThatNewCategoryOptionLinkWasInserted();

        whenUpdateANewCategoryOptionLink();

        thenAssertThatCategoryOptionWasUpdated();

    }

    @Test
    public void query_all_category_options_link() {

        givenAllTheCategoryOptionLinkDependencies();

        whenInsertNewCategoryOptionLink();

        thenAssertThatNewCategoryOptionLinkWasInserted();

        thenAssertQueryAllBringData();

    }

    private void thenAssertQueryAllBringData() {
        List<CategoryCategoryOptionLinkModel> items = store.queryAll();
        assertFalse(items.isEmpty());
    }

    private void thenAssertThatCategoryOptionWasUpdated() {

        List<CategoryCategoryOptionLinkModel> list = store.queryAll();
        assertTrue(Objects.equals(list.get(0).category(), UPDATED_CATEGORY_UID));
    }

    private void whenDeleteAllElementsOnCategoryComboLinkTable() {
        store.delete();
    }

    private void thenAssertThereAreNotItemsOnCategoryOptionLinkTable() {
        assertTrue(store.queryAll().isEmpty());
    }

    private void whenDeleteNewCategoryOptionLink() {
        store.delete(newCategoryCategoryOptionLinkModel);
    }

    private void givenACategory() {
        newCategory = generateCategory(DEFAULT_CATEGORY_UID);
    }

    private void givenACategoryOption(String uid) {
        newCategoryOption = generateANewCategoryOption(uid);
    }

    private CategoryOption generateANewCategoryOption(String uid) {
        return generatedCategoryOption(uid);
    }

    private void givenACategoryOptionLinkModel() {
        newCategoryCategoryOptionLinkModel = CategoryCategoryOptionLinkModel.builder()
                .categoryOption(DEFAULT_CATEGORY_OPTION_UID)
                .category(DEFAULT_CATEGORY_UID)
                .build();
    }

    private void whenInsertNewCategoryOptionLink() {

        whenInsertNewCategory();

        whenInsertNewOption();

        lastInsertedID = store.insert(newCategoryCategoryOptionLinkModel);
    }

    private void givenAllTheCategoryOptionLinkDependencies() {
        givenACategory();
        givenACategoryOption(DEFAULT_CATEGORY_OPTION_UID);
        givenACategoryOptionLinkModel();
    }

    private void whenInsertNewCategory() {
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewOption() {
        CategoryOptionStoreImpl optionStore = new CategoryOptionStoreImpl(databaseAdapter());
        optionStore.insert(newCategoryOption);
    }

    private void thenAssertThatNewCategoryOptionLinkWasInserted() {
        assertEquals(lastInsertedID, 1);
    }

    private void whenUpdateANewCategoryOptionLink() {
        Category category = generateCategory(UPDATED_CATEGORY_UID);

        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(category);

        CategoryCategoryOptionLinkModel categoryOptionLinkModelToBeUpdated =
                CategoryCategoryOptionLinkModel
                        .builder()
                        .categoryOption(DEFAULT_CATEGORY_OPTION_UID)
                        .category(UPDATED_CATEGORY_UID).build();

        CategoryCategoryOptionLinkModel oldCategoryCategoryOptionLinkModel =
                newCategoryCategoryOptionLinkModel;

        store.update(oldCategoryCategoryOptionLinkModel, categoryOptionLinkModelToBeUpdated);
    }
}