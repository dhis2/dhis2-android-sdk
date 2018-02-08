package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.common.CategoryComboMother.generateCategoryCombo;
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
    private CategoryComboStore store;
    private CategoryCombo newCategoryCombo;
    private long lastInsertedId;
    private boolean wasDeleted;
    private CategoryCombo updatedCategoryCombo;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryComboStoreImpl(databaseAdapter());
    }

    @Test
    @MediumTest
    public void insert_a_category_combo() throws Exception {

        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();
    }

    @Test
    @MediumTest
    public void insert_and_delete_a_category_combo() throws Exception {

        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    @Test
    @MediumTest
    public void update_a_category_combo() {

        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        whenUpdateACategoryCombo();

        thenAssertThatCategoryComboWasUpdated();

    }

    @Test
    @MediumTest
    public void delete_all_categories_combos_from_db() {

        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();

        whenDeleteAllCategoriesCombosFromDB();

        thenAssertThereAreNotCategoryCombosInDB();

    }

    @Test
    @MediumTest
    public void query_all_category_combos() {

        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        thenAssertCategoryComboWasInserted();

        thenAssertQueryAllBringCategoryCombosFromDB();

    }

    private void givenACategoryCombo() {
        newCategoryCombo = generateCategoryCombo(DEFAULT_CATEGORY_COMBO_UID);
    }

    private void whenInsertNewCategoryCombo() {

        lastInsertedId = store.insert(newCategoryCombo);
    }

    private void whenDeleteCategoryComboInserted() {
        int numberOfRows = store.delete(newCategoryCombo.uid());
        wasDeleted = numberOfRows >= 1;
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
        assertEquals(list.get(0), updatedCategoryCombo);
    }

    private void whenUpdateACategoryCombo() {

        updatedCategoryCombo = generateCategoryCombo(DEFAULT_CATEGORY_COMBO_UID);

        store.update(updatedCategoryCombo);
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