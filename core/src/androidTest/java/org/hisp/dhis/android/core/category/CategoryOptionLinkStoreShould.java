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

@RunWith(AndroidJUnit4.class)
public class CategoryOptionLinkStoreShould extends AbsStoreTestCase {

    private CategoryOptionLinkStore store;
    private Category newCategory;
    private CategoryOption newCategoryOption;
    private CategoryOptionLinkModel newCategoryOptionLinkModel;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionLinkStoreImpl(databaseAdapter());

    }

    @Test
    public void insert_a_category_option_link() throws Exception {
        givenACategory();
        givenACategoryOption();
        givenACategoryOptionLinkModel();

        whenInsertNewCategory();
        whenInsertNewOption();
        whenInsertNewCategoryOptionLink();

        thenAssertLastInsertedIDIsOne();
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

    private void givenACategoryOptionLinkModel() {
        newCategoryOptionLinkModel = CategoryOptionLinkModel.builder()
                .option("TNYQzTHdoxL")
                .category("KfdsGBcoiCa")
                .build();
    }

    private void whenInsertNewCategoryOptionLink() {
        lastInsertedID = store.insert(newCategoryOptionLinkModel);
    }

    private void whenInsertNewCategory() {
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewOption() {
        CategoryOptionStoreImpl optionStore = new CategoryOptionStoreImpl(databaseAdapter());
        optionStore.insert(newCategoryOption);
    }

    private void thenAssertLastInsertedIDIsOne(){
        assertEquals(lastInsertedID, 1);
    }
}