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
public class CategoryComboStoreShould extends AbsStoreTestCase {

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
        givenACategoryCombo();

        whenInsertNewCategoryCombo();

        thenAssertLastInsertedIDIsOne();
    }

    @Test
    public void insert_and_delete_a_category_combo() throws Exception {
        givenACategoryCombo();

        whenInsertNewCategoryCombo();
        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsDeleted();

        whenDeleteCategoryComboInserted();

        thenAssertStoreReturnsNotDeleted();
    }

    private void givenACategoryCombo() {
        newCategoryCombo = generateCategoryCombo();
    }

    private CategoryCombo generateCategoryCombo(){
        Date today = new Date();

        return CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .isDefault(false)
                .categories(new ArrayList<Category>())
                .build();
    }

    private void whenInsertNewCategoryCombo() {
        lastInsertedId = store.insert(newCategoryCombo);
    }

    private void whenDeleteCategoryComboInserted() {
        wasDeleted = store.delete(newCategoryCombo);
    }

    private void thenAssertLastInsertedIDIsOne() {
        assertEquals(lastInsertedId, 1);
    }

    private void thenAssertStoreReturnsDeleted() {
        assertTrue(wasDeleted);
    }

    private void thenAssertStoreReturnsNotDeleted() {
        assertFalse(wasDeleted);
    }
}