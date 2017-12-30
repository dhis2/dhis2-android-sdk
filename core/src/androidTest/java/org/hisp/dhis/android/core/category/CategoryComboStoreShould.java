package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.common.CategoryComboMockFactory.generateCategoryCombo;
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


@RunWith(AndroidJUnit4.class)
public class CategoryComboStoreShould extends AbsStoreTestCase {

    private static final String DEFAULT_CATEGORY_COMBO_UID = "DEFAULT_CATEGORY_COMBO_UID";
    private static final String UPDATED_CATEGORY_COMBO_UID = "UPDATED_CATEGORY_COMBO_UID";
    private CategoryComboStore store;
    private CategoryCombo newCategoryCombo;
    private long lastInsertedId;
    private boolean wasDeleted;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryComboStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void insert_a_category_combo() throws Exception {

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();
    }

    @Test
    public void insert_and_delete_a_category_combo() throws Exception {

        whenInsertNewCategoryCombo();

        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    @Test
    public void update_a_category_combo() {

        whenInsertNewCategoryCombo();

        whenUpdateACategoryCombo();

        thenAssertThatCategoryComboWasUpdated();

    }

    @Test
    public void delete_all_categories_combos_from_db() {

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();

        whenDeleteAllCategoriesCombosFromDB();

        thenAssertThereAreNotCategoryCombosInDB();

    }

    @Test
    public void query_all_category_combos(){

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();

        thenAssertQueryAllBringCategoryCombosFromDB();

    }

    private void givenACategoryCombo() {
        newCategoryCombo = generateCategoryCombo(DEFAULT_CATEGORY_COMBO_UID);
    }

    private void whenInsertNewCategoryCombo() {

        givenACategoryCombo();

        lastInsertedId = store.insert(newCategoryCombo);
    }

    private void whenDeleteCategoryComboInserted() {
        wasDeleted = store.delete(newCategoryCombo);
    }

    private void thenAssertCategoryComboWasInserted() {
        assertEquals(lastInsertedId, 1);
    }

    private void thenAssertStoreReturnsDeleted() {
        assertTrue(wasDeleted);
    }

    private void thenAssertStoreReturnsNotDeleted() {
        assertFalse(wasDeleted);
    }

    private void thenAssertThatCategoryComboWasUpdated() {
        List<CategoryCombo> list = store.queryAll();
        assertEquals(list.get(0).uid(), UPDATED_CATEGORY_COMBO_UID);
    }

    private void whenUpdateACategoryCombo() {

        CategoryCombo updatedCategoryCombo = generateCategoryCombo(UPDATED_CATEGORY_COMBO_UID);

        CategoryCombo oldCategoryCombo = newCategoryCombo;

        store.update(oldCategoryCombo, updatedCategoryCombo);
    }

    private void whenDeleteAllCategoriesCombosFromDB() {
        store.delete();
    }

    private void thenAssertThereAreNotCategoryCombosInDB() {
        List<CategoryCombo> list = store.queryAll();
        assertTrue(list.isEmpty());
    }

    private void thenAssertQueryAllBringCategoryCombosFromDB() {
        List<CategoryCombo> list = store.queryAll();
        assertFalse(list.isEmpty());
    }
}