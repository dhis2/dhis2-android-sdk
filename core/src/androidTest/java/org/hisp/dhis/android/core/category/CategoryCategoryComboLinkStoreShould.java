package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;

import android.support.test.filters.MediumTest;
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
public class CategoryCategoryComboLinkStoreShould extends AbsStoreTestCase {

    private CategoryCategoryComboLinkStore store;
    private Category newCategory;
    private CategoryCombo newCategoryCombo;
    private CategoryCategoryComboLinkModel newCategoryCategoryComboLinkModel;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryCategoryComboLinkStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_combo_link() throws Exception {
        givenACategory();
        givenACategoryCombo();
        givenACategoryComboLinkModel();

        whenInsertNewCategory();
        whenInsertNewCategoryCombo();
        whenInsertNewCategoryComboLink();

        thenAssertLastInsertedIDIsOne();
    }

    private void givenACategory() {
        newCategory = generateCategory();
    }

    private Category generateCategory(){
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

    private void givenACategoryCombo() {
        Date today = new Date();

        newCategoryCombo = CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .categories(generateAListOfCategories())
                .build();
    }

    private void givenACategoryComboLinkModel(){
        newCategoryCategoryComboLinkModel = CategoryCategoryComboLinkModel.builder()
                .category("KfdsGBcoiCa")
                .combo("m2jTvAj5kkm")
                .build();
    }

    private void whenInsertNewCategory() {
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewCategoryCombo() {
        CategoryComboStoreImpl comboStore = new CategoryComboStoreImpl(databaseAdapter());
        comboStore.insert(newCategoryCombo);
    }

    private void whenInsertNewCategoryComboLink() {
        lastInsertedID = store.insert(newCategoryCategoryComboLinkModel);
    }

    private List<Category> generateAListOfCategories() {
        List<Category> list = new ArrayList<>();
        list.add(generateCategory());
        return list;
    }

    private void thenAssertLastInsertedIDIsOne() {
        assertEquals(lastInsertedID, 1);
    }
}