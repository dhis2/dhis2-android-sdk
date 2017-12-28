package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboStoreShould extends AbsStoreTestCase {

    private CategoryOptionComboStore store;
    private CategoryOptionCombo newCategoryOptionCombo;
    private long lastInsertedID;
    private boolean wasDeleted;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionComboStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_option_combo() throws Exception {
        givenACategoryOptionCombo();

        whenInsertNewCategoryOptionCombo();

        thenAssertLastInsertedIDIsOne();
    }

    @Test
    @MediumTest
    public void insert_and_delete_a_category_option_combo() throws Exception {
        givenACategoryOptionCombo();

        whenInsertNewCategoryOptionCombo();
        whenDeleteCategoryOptionComboInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryOptionComboInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    private void givenACategoryOptionCombo() {
        Date today = new Date();

        //noinspection ConstantConditions
        newCategoryOptionCombo = CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code(null)
                .created(today)
                .name("MCH Aides")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryCombo(null)
                .build();
    }

    private void whenInsertNewCategoryOptionCombo() {
        lastInsertedID = store.insert(newCategoryOptionCombo);
    }

    private void whenDeleteCategoryOptionComboInserted(){
        wasDeleted = store.delete(newCategoryOptionCombo);
    }

    private void thenAssertLastInsertedIDIsOne(){
        assertEquals(lastInsertedID, 1);
    }

    private void thenAssertStoreReturnsDeleted() {
        assertTrue(wasDeleted);
    }

    private void thenAssertStoreReturnsNotDeleted() {
        assertFalse(wasDeleted);
    }
}