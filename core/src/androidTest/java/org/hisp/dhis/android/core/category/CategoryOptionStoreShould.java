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
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionStoreShould extends AbsStoreTestCase {

    private CategoryOptionStore store;
    private CategoryOption newCategoryOption;
    private long lastInsertedID;
    private boolean wasDeleted;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_option() throws Exception {
        givenACategoryOption();

        whenInsertNewcategoryOption();

        thenAssertLastInsertedIDIsOne();
    }

    @Test
    @MediumTest
    public void insert_and_delete_a_category_option() throws Exception {
        givenACategoryOption();

        whenInsertNewcategoryOption();
        whenDeleteCategoryOptionInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryOptionInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    private void givenACategoryOption() {
        Date today = new Date();

        newCategoryOption = CategoryOption.builder()
                .uid("TNYQzTHdoxL")
                .code("MCH_AIDES")
                .created(today)
                .name("MCH Aides")
                .shortName("MCH Aides")
                .displayName("MCH Aides")
                .build();
    }

    private void whenInsertNewcategoryOption() {
        lastInsertedID = store.insert(newCategoryOption);
    }

    private void whenDeleteCategoryOptionInserted() {
        wasDeleted = store.delete(newCategoryOption);
    }

    private void thenAssertLastInsertedIDIsOne(){
        assertEquals(lastInsertedID, 1);
    }

    private void thenAssertStoreReturnsDeleted(){
        assertTrue(wasDeleted);
    }

    private void thenAssertStoreReturnsNotDeleted(){
        assertFalse(wasDeleted);
    }
}