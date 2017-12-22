package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CategoryComboLinkStoreShould extends AbsStoreTestCase {

    private CategoryComboLinkStore store;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryComboLinkStoreImpl(databaseAdapter());

    }

    @Test
    public void insert_category_combo() throws Exception {

        insertNewCategory();

        insertANewCategoryCombo();

        long lastID = insertNewCategoryOptionLink();

        assertEquals(lastID, 1);

    }

    private long insertNewCategoryOptionLink() {
        CategoryComboLinkModel link = CategoryComboLinkModel.builder()
                .category("KfdsGBcoiCa")
                .combo("m2jTvAj5kkm")
                .build();
        return store.insert(link);
    }

    private void insertNewCategory() {
        Category newCategory = givenACategory();
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void insertANewCategoryCombo() {
        CategoryCombo combo = givenACategoryCombo();
        CategoryComboStoreImpl comboStore = new CategoryComboStoreImpl(databaseAdapter());

        comboStore.insert(combo);
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
                .dataDimensionType("DISAGGREGATION").build();
    }

    private List<Category> givenAListOfCategories() {
        List<Category> list = new ArrayList<>();
        list.add(givenACategory());
        return list;
    }

    private CategoryCombo givenACategoryCombo() {
        Date today = new Date();

        return CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .categories(givenAListOfCategories())
                .build();
    }
}