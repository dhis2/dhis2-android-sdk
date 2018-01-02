package org.hisp.dhis.android.core.category;


import static junit.framework.Assert.assertTrue;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.CategoryCallFactory;
import org.hisp.dhis.android.core.common.CategoryComboCallFactory;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class CategoryComboEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void download_categories_combos_and_relatives() throws Exception {

        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        downloadCategories();

        assertNotCombosInDB();
        assertThereAreNotCategoryCombosLinkInDB();

        CategoryComboEndpointCall categoryComboEndpointCall = CategoryComboCallFactory.create(
                d2.retrofit(), databaseAdapter());
        Response<Payload<CategoryCombo>> responseCategory = categoryComboEndpointCall.call();

        assertResponseIsCorrect(responseCategory);

        assertDataIsProperlyParsedAndInsertedInTheDB();
    }

    private void assertResponseIsCorrect(Response<Payload<CategoryCombo>> responseCategory) {
        assertTrue(responseCategory.isSuccessful());
        assertTrue(hasCombos(responseCategory));
    }

    private void assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB();
        assertThereAreCategoryCombosLinkInDB();
        assertThereAreCategoryOptionCombosInDB();
        assertThereAreCategorysInDB();
    }

    private void downloadCategories() throws Exception {
        CategoryEndpointCall categoryEndpointCall = CategoryCallFactory.create(d2.retrofit(),
                databaseAdapter());
        categoryEndpointCall.call();
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

    private void assertThereAreCategorysInDB() {
        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        List<CategoryOption> categoryOptions = categoryOptionStore.queryAll();
        assertTrue(categoryOptions.size() > 0);
    }

    private boolean hasCombos(Response<Payload<CategoryCombo>> response) {
        return !response.body().items().isEmpty();
    }
}
