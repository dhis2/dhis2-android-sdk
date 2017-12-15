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
public class CategoryOptionLinkStoreShould extends AbsStoreTestCase {

    private Store<CategoryOptionLinkModel> store;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionLinkStoreImpl(databaseAdapter());

    }

    private void clearTablesData() {
        databaseAdapter().delete(CategoryOptionLinkModel.TABLE);
    }

    @After
    public void tearDown() {
        clearTablesData();
    }

    @Test
    public void insert_category() throws Exception {

        insertNewCategory();

        insertNewOption();

        long lastID = insertNewCategoryOptionLink();

        assertEquals(lastID, 1);

    }

    private CategoryOptionLinkModel givenACategoryOptionLink() {
        return CategoryOptionLinkModel.builder()
                .option("TNYQzTHdoxL")
                .category("KfdsGBcoiCa")
                .build();
    }

    private long insertNewCategoryOptionLink() {
        CategoryOptionLinkModel link = givenACategoryOptionLink();
        return store.insert(link);
    }

    private void insertNewCategory() {
        Category newCategory = givenACategory();
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void insertNewOption() {
        CategoryOption newOption = givenAOption();
        CategoryOptionStoreImpl optionStore = new CategoryOptionStoreImpl(databaseAdapter());
        optionStore.insert(newOption);
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
}