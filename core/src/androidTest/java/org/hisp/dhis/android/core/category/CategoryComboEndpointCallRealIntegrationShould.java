package org.hisp.dhis.android.core.category;


import static junit.framework.Assert.assertTrue;

import android.support.annotation.NonNull;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.CategoryCallFactory;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;
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

    //@Test
    public void download_categories_combos_and_relatives() throws Exception {

        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        downloadCategories();

        assertNotCombosInDB();
        assertThereAreNotCategoryCombosLinkInDB();

        CategoryComboEndpointCall categoryComboCallEndpoint = provideCategoryComboCallEndpoint();
        Response<Payload<CategoryCombo>> responseCategory = categoryComboCallEndpoint.call();

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
        CategoryComboLinkStore categoryComboLinkStore = new CategoryComboLinkStoreImpl(databaseAdapter());
        List<CategoryComboLink> categoryComboLinks = categoryComboLinkStore.queryAll();
        assertTrue(categoryComboLinks.isEmpty());
    }

    private void assertThereAreCombosInDB() {
        CategoryComboStore categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.queryAll();
        assertTrue(categoryCombos.size() > 0);
    }

    private void assertThereAreCategoryCombosLinkInDB() {
        CategoryComboLinkStore categoryComboLinkStore = new CategoryComboLinkStoreImpl(databaseAdapter());
        List<CategoryComboLink> categoryComboLinks = categoryComboLinkStore.queryAll();
        assertTrue(categoryComboLinks.size() > 0);
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

    @NonNull
    private CategoryComboEndpointCall provideCategoryComboCallEndpoint() {
        CategoryComboQuery query = CategoryComboQuery.defaultQuery();

        CategoryComboService comboService = d2.retrofit().create(CategoryComboService.class);
        CategoryComboLinkStore categoryComboLinkStore = new CategoryComboLinkStoreImpl(
                databaseAdapter());

        CategoryOptionComboStore optionComboStore = new CategoryOptionComboStoreImpl(
                databaseAdapter());
        CategoryOptionComboHandler optionComboHandler = new CategoryOptionComboHandler(
                optionComboStore);

        ResponseValidator<CategoryCombo> validator = new ResponseValidator<>();

        CategoryComboStore store = new CategoryComboStoreImpl(databaseAdapter());

        CategoryOptionComboLinkCategoryStore
                categoryComboOptionLinkCategoryStore = new CategoryOptionComboLinkCategoryStoreImpl(
                databaseAdapter());

        CategoryComboHandler handler = new CategoryComboHandler(store,
                categoryComboOptionLinkCategoryStore, categoryComboLinkStore,
                optionComboHandler);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        Date serverDate = new Date();

        return new CategoryComboEndpointCall(query, comboService, validator, handler,
                resourceHandler,
                databaseAdapter(), serverDate);

    }

    private boolean hasCombos(Response<Payload<CategoryCombo>> response) {
        return !response.body().items().isEmpty();
    }
}
