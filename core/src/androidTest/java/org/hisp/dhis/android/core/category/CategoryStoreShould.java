package org.hisp.dhis.android.core.category;

import static org.junit.Assert.*;

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
public class CategoryStoreShould extends AbsStoreTestCase {

    private Store<Category> store;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryStoreImpl(databaseAdapter());

    }

    private void clearTablesData() {
        databaseAdapter().delete(CategoryModel.TABLE);
    }

    @After
    public void tearDown() {
        clearTablesData();
    }

    @Test
    public void insert_category() throws Exception {

        long lastID = insertNewCategory();

        assertEquals(lastID, 1);

    }

    @Test
    public void delete_category() throws Exception {
        insertNewCategory();

        Category category = givenACategory();

        boolean wasDeleted = store.delete(category);

        assertTrue(wasDeleted);

        wasDeleted = store.delete(category);

        assertFalse(wasDeleted);
    }

    @Test
    public void update_category() throws Exception {
        Category oldCategory = givenACategory();

        insertNewCategory();

        Category newCategory = Category.builder().uid("2")
                .code("BIRTHS_ATTENDED")
                .categoryOptions(new ArrayList<CategoryOption>())
                .build();

        boolean wasUpdated = store.update(oldCategory, newCategory);

        assertTrue(wasUpdated);

        boolean wasDeleted = store.delete(newCategory);

        assertTrue(wasDeleted);


    }

    private long insertNewCategory() {
        Category newCategory = givenACategory();

        return store.insert(newCategory);
    }

    private Category givenACategory() {
        Date today = new Date();

        return Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .shortName("Births attended by")
                .displayName("Births attended by")
                .categoryOptions(new ArrayList<CategoryOption>())
                .dataDimensionType("DISAGGREGATION").build();
    }

}