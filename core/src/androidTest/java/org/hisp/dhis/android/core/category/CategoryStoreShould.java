package org.hisp.dhis.android.core.category;

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
import java.util.ArrayList;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryStoreShould extends AbsStoreTestCase {

    private CategoryStore store;
    private Category newCategory;
    private Category newCategoryModified;
    private long lastInsertedID;
    private boolean wasDeleted;
    private boolean wasUpdated;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category() throws Exception {
        givenACategory();

        whenInsertNewCategory();

        thenAssertLastInsertedIDIsOne();
    }

    @Test
    @MediumTest
    public void insert_and_delete_a_category() throws Exception {
        givenACategory();

        whenInsertNewCategory();
        whenDeleteCategoryInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    @Test
    @MediumTest
    public void insert_update_and_delete_a_category() throws Exception {
        givenACategory();
        givenThatCategoryButModified();

        whenInsertNewCategory();
        whenUpdateCategory();

        thenAssertStoreReturnsUpdated();

        whenDeleteCategoryInserted();

        thenAssertStoreReturnsDeleted();
    }

    private void givenACategory() {
        Date today = new Date();

        newCategory = Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .shortName("Births attended by")
                .displayName("Births attended by")
                .categoryOptions(new ArrayList<CategoryOption>())
                .dataDimensionType("DISAGGREGATION").build();
    }

    private void givenThatCategoryButModified(){
        newCategoryModified = Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED_MODIFIED")
                .categoryOptions(new ArrayList<CategoryOption>())
                .build();
    }

    private void whenInsertNewCategory() {
        lastInsertedID = store.insert(newCategory);
    }

    private void whenDeleteCategoryInserted() {
        wasDeleted = store.delete(newCategory);
    }

    private void whenUpdateCategory() {
        wasUpdated = store.update(newCategory);
    }

    private void thenAssertLastInsertedIDIsOne() {
        assertEquals(lastInsertedID, 1);
    }

    private void thenAssertStoreReturnsDeleted() {
        assertTrue(wasDeleted);
    }

    private void thenAssertStoreReturnsNotDeleted() {
        assertFalse(wasDeleted);
    }

    private void thenAssertStoreReturnsUpdated() {
        assertTrue(wasUpdated);
    }
}