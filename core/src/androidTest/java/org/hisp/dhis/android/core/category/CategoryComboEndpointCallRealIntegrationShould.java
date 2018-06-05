package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CategoryComboEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void download_categories_combos_and_relatives() throws Exception {

        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        downloadCategories();

        assertNotCombosInDB();
        assertThereAreNotCategoryCombosLinkInDB();

        Call<List<CategoryCombo>> categoryComboEndpointCall =
                CategoryComboEndpointCall.FACTORY.create(
                GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date()));
        List<CategoryCombo> categoryCombos = categoryComboEndpointCall.call();

        assertFalse(categoryCombos.isEmpty());

        assertDataIsProperlyParsedAndInsertedInTheDB();
    }

    private void assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB();
        assertThereAreCategoryCombosLinkInDB();
        assertThereAreCategoryOptionCombosInDB();
        assertThereAreCategoriesInDB();
    }

    private void downloadCategories() throws Exception {
        CategoryEndpointCall.FACTORY.create(
                        GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date())).call();
    }

    private void assertNotCombosInDB() {
        CategoryComboStore categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.queryAll();
        assertTrue(categoryCombos.isEmpty());
    }

    private void assertThereAreNotCategoryCombosLinkInDB() {
        CategoryCategoryComboLinkStore
                categoryCategoryComboLinkStore = new CategoryCategoryComboLinkStoreImpl(databaseAdapter());
        List<CategoryCategoryComboLink> categoryCategoryComboLinks = categoryCategoryComboLinkStore.queryAll();
        assertTrue(categoryCategoryComboLinks.isEmpty());
    }

    private void assertThereAreCombosInDB() {
        CategoryComboStore categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.queryAll();
        assertTrue(categoryCombos.size() > 0);
    }

    private void assertThereAreCategoryCombosLinkInDB() {
        CategoryCategoryComboLinkStore
                categoryCategoryComboLinkStore = new CategoryCategoryComboLinkStoreImpl(databaseAdapter());
        List<CategoryCategoryComboLink> categoryCategoryComboLinks = categoryCategoryComboLinkStore.queryAll();
        assertTrue(categoryCategoryComboLinks.size() > 0);
    }

    private void assertThereAreCategoryOptionCombosInDB() {
        CategoryOptionComboStore categoryOptionComboStore = new CategoryOptionComboStoreImpl(databaseAdapter());
        List<CategoryOptionCombo> categoryOptionCombos = categoryOptionComboStore.queryAll();
        assertTrue(categoryOptionCombos.size() > 0);
    }

    private void assertThereAreCategoriesInDB() {
        IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore = CategoryOptionStore.create(databaseAdapter());
        Set<String> categoryOptionUids = categoryOptionStore.selectUids();
        assertTrue(categoryOptionUids.size() > 0);
    }
}
