package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryComboStoreShould extends AbsStoreTestCase {

    private CategoryComboStore store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryComboStoreImpl(databaseAdapter());

    }

    @Override
    @After
    public void tearDown() {
        clearTablesData();
    }

    @Test
    public void insert_category() throws Exception {

        long lastID = insertNewCombo();

        assertEquals(lastID, 1);

    }

    @Test
    public void delete_category() throws Exception {

        insertNewCombo();

        CategoryCombo combo = givenACombo();

        boolean wasDeleted = store.delete(combo);

        assertTrue(wasDeleted);

        wasDeleted = store.delete(combo);

        assertFalse(wasDeleted);
    }

    private long insertNewCombo() {
        CategoryCombo newCombo = givenACombo();

        return store.insert(newCombo);
    }

    private CategoryCombo givenACombo() {
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

    private void clearTablesData() {
        databaseAdapter().delete(CategoryOptionModel.TABLE);
    }

}