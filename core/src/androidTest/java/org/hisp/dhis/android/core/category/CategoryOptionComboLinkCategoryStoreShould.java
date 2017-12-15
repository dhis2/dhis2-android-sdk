package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboLinkCategoryStoreShould extends AbsStoreTestCase {

    private Store<CategoryOptionComboLinkCategoryModel> store;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionComboLinkCategoryStoreImpl(databaseAdapter());

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

        insertNewCategoryOption();

        insertNewOptionCombo();

        long lastID = insertNewOptionComboLink();

        assertEquals(lastID, 1);

    }

    private CategoryOptionComboLinkCategoryModel givenAOptionComboLink() {

        return CategoryOptionComboLinkCategoryModel.builder()
                .optionCombo("NZAKyj67WW2")
                .category("TXGfLxZlInA")
                .build();
    }

    private long insertNewOptionComboLink() {
        CategoryOptionComboLinkCategoryModel link = givenAOptionComboLink();
        return store.insert(link);
    }

    private void insertNewCategoryOption() {
        CategoryOption newCategoryOption = givenACategoryOption();
        CategoryOptionStoreImpl store = new CategoryOptionStoreImpl(databaseAdapter());
        store.insert(newCategoryOption);
    }

    private void insertNewOptionCombo() {
        CategoryOptionCombo newOptionCombo = givenAOptionCombo();
        CategoryOptionComboStoreImpl optionStore = new CategoryOptionComboStoreImpl(databaseAdapter());
        optionStore.insert(newOptionCombo);
    }

    private CategoryOption givenACategoryOption() {
        Date today = new Date();

        return CategoryOption.builder()
                .uid("TXGfLxZlInA")
                .code("SECHN")
                .created(today)
                .name("SECHN")
                .shortName("SECHN")
                .displayName("SECHN")
                .build();
    }

    private CategoryOptionCombo givenAOptionCombo() {
        Date today = new Date();

        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .created(today)
                .name("SECHN, Male")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .build();
    }
}