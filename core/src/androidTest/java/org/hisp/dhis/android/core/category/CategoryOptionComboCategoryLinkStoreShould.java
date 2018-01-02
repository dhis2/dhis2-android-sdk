package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertEquals;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboCategoryLinkStoreShould extends AbsStoreTestCase {

    private CategoryOptionComboCategoryLinkStore store;
    private CategoryOption newCategoryOption;
    private CategoryOptionCombo newCategoryOptionCombo;
    private CategoryOptionComboCategoryLinkModel newCategoryOptionComboCategoryLink;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryOptionComboCategoryLinkStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_option_combo_link_category() throws Exception {
        givenACategoryOption();
        givenACategoryOptionCombo();
        givenACategoryOptionComboCategoryLink();

        whenInsertNewCategoryOption();
        whenInsertNewCategoryOptionCombo();
        whenInsertNewCategoryOptionComboCategoryLink();

        thenAssertLastInsertedIDIsOne();
    }

    private void givenACategoryOption() {
        Date today = new Date();

        newCategoryOption = CategoryOption.builder()
                .uid("TXGfLxZlInA")
                .code("SECHN")
                .created(today)
                .name("SECHN")
                .shortName("SECHN")
                .displayName("SECHN")
                .build();
    }

    private void givenACategoryOptionCombo() {
        Date today = new Date();

        newCategoryOptionCombo = CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .created(today)
                .name("SECHN, Male")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .build();
    }

    private void givenACategoryOptionComboCategoryLink() {
        newCategoryOptionComboCategoryLink = CategoryOptionComboCategoryLinkModel.builder()
                .optionCombo("NZAKyj67WW2")
                .category("TXGfLxZlInA")
                .build();
    }

    private void whenInsertNewCategoryOption() {
        CategoryOptionStoreImpl store = new CategoryOptionStoreImpl(databaseAdapter());
        store.insert(newCategoryOption);
    }

    private void whenInsertNewCategoryOptionComboCategoryLink() {
        lastInsertedID = store.insert(newCategoryOptionComboCategoryLink);
    }

    private void whenInsertNewCategoryOptionCombo() {
        CategoryOptionComboStore optionStore = new CategoryOptionComboStoreImpl(databaseAdapter());
        optionStore.insert(newCategoryOptionCombo);
    }

    private void thenAssertLastInsertedIDIsOne() {
        assertEquals(lastInsertedID, 1);
    }
}