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
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionStoreShould extends AbsStoreTestCase {

    private CategoryOptionStore store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionStoreImpl(databaseAdapter());

    }

    @Override
    @After
    public void tearDown() {
        clearTablesData();
    }

    @Test
    public void insert_category() throws Exception {

        long lastID = insertNewOption();

        assertEquals(lastID, 1);

    }

    @Test
    public void delete_category() throws Exception {
        insertNewOption();

        CategoryOption option = givenAOption();

        boolean wasDeleted = store.delete(option);

        assertTrue(wasDeleted);

        wasDeleted = store.delete(option);

        assertFalse(wasDeleted);
    }

    private long insertNewOption() {
        CategoryOption newOption = givenAOption();

        return store.insert(newOption);
    }

    private CategoryOption givenAOption() {
        Date today = new Date();

        return CategoryOption.builder()
                .uid("TNYQzTHdoxL")
                .code("MCH_AIDES")
                .created(today)
                .name("MCH Aides")
                .shortName("MCH Aides")
                .displayName("MCH Aides")
                .build();
    }

    private void clearTablesData() {
        databaseAdapter().delete(CategoryOptionModel.TABLE);
    }
}