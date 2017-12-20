package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionComboStoreImpl(databaseAdapter());

    }

    @Test
    public void insert_category() throws Exception {

        long lastID = insertNewOptionCombo();

        assertEquals(lastID, 1);

    }

    @Test
    public void delete_category() throws Exception {
        insertNewOptionCombo();

        CategoryOptionCombo optionCombo = givenAOptionCombo();

        boolean wasDeleted = store.delete(optionCombo);

        assertTrue(wasDeleted);

        wasDeleted = store.delete(optionCombo);

        assertFalse(wasDeleted);
    }

    private long insertNewOptionCombo() {
        CategoryOptionCombo newOptionCombo = givenAOptionCombo();

        return store.insert(newOptionCombo);
    }

    private CategoryOptionCombo givenAOptionCombo() {
        Date today = new Date();

        //noinspection ConstantConditions
        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code(null)
                .created(today)
                .name("MCH Aides")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryCombo(null)
                .build();
    }
}