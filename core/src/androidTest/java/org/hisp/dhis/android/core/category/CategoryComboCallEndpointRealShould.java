package org.hisp.dhis.android.core.category;


import static junit.framework.Assert.assertTrue;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
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

import retrofit2.Response;

public class CategoryComboCallEndpointRealShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    // @Test
    public void download_category_combos() throws Exception {

        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();

        downloadCategories();

        assertNotCombosInDB();
        assertNotCategoryCombosLinkInDB();

        CategoryComboCallEndpoint comboCallEndpoint = provideComboCallEndpoint();
        Response<Payload<CategoryCombo>> responseCategory = comboCallEndpoint.call();

        assertParseData(responseCategory);

        assertRelations();
    }

    private void assertParseData(Response<Payload<CategoryCombo>> responseCategory) {
        assertTrue(responseCategory.isSuccessful());
        assertTrue(hasCombos(responseCategory));
    }

    private void assertRelations() {
        assertThereAreCombosInDB();
        assertThereAreCategoryCombosLinkInDB();
        assertThereAreCategoryOptionCombosInDB();
    }

    private void downloadCategories() throws Exception {
        CategoryEndpointCall categoryEndpointCall = provideCategoryCallEndpoint();
        categoryEndpointCall.call();

    }

    @NonNull
    private CategoryEndpointCall provideCategoryCallEndpoint() {
        CategoryQuery query = CategoryQuery.defaultQuery();

        CategoryService categoryService = d2.retrofit().create(CategoryService.class);

        ResponseValidator<Category> validator = new ResponseValidator<>();

        Store<Category> store = new CategoryStoreImpl(databaseAdapter());

        Store<CategoryOption> categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());

        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore);

        Store<CategoryOptionLinkModel> categoryOptionLinkStore = new CategoryOptionLinkStoreImpl(
                databaseAdapter());

        Handler<Category> handler = new CategoryHandler(store, categoryOptionHandler,
                categoryOptionLinkStore);
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        Date serverDate = new Date();

        return new CategoryEndpointCall(query, categoryService, validator, handler, resourceHandler,
                databaseAdapter(), serverDate);

    }

    private void assertNotCombosInDB() {
        Cursor combos = selectAllCombosFromDB();
        assertTrue(combos.getCount() == 0);
    }

    private void assertNotCategoryCombosLinkInDB() {
        Cursor combos = selectAllCategoryCombosLinksFromDB();
        assertTrue(combos.getCount() == 0);
    }

    private void assertThereAreCombosInDB() {
        Cursor combos = selectAllCombosFromDB();
        assertTrue(combos.getCount() > 0);
    }

    private void assertThereAreCategoryCombosLinkInDB() {
        Cursor combos = selectAllCategoryCombosLinksFromDB();
        assertTrue(combos.getCount() > 0);
    }

    private void assertThereAreCategoryOptionCombosInDB() {
        Cursor combos = selectAllOptionCombosFromDB();
        assertTrue(combos.getCount() > 0);
    }

    private Cursor selectAllCombosFromDB() {
        final String[] PROJECTION = {
                CategoryComboModel.Columns.ID, CategoryComboModel.Columns.UID, CategoryComboModel
                .Columns.CODE, CategoryComboModel.Columns.NAME,
                CategoryComboModel.Columns.DISPLAY_NAME, CategoryComboModel.Columns.CREATED,
                CategoryComboModel.Columns.LAST_UPDATED,
                CategoryComboModel.Columns.IS_DEFAULT

        };

        String sqlQuery = SQLiteQueryBuilder.buildQueryString(false, CategoryComboModel.TABLE,
                PROJECTION, null,
                null, null, null, null);


        return databaseAdapter().query(sqlQuery);
    }

    private Cursor selectAllCategoryCombosLinksFromDB() {
        final String[] PROJECTION = {
                CategoryComboLinkModel.Columns.ID, CategoryComboLinkModel.Columns.CATEGORY,
                CategoryComboLinkModel.Columns.COMBO
        };
        String sqlQuery = SQLiteQueryBuilder.buildQueryString(false, CategoryComboLinkModel.TABLE,
                PROJECTION, null,
                null, null, null, null);


        return databaseAdapter().query(sqlQuery);
    }

    private Cursor selectAllOptionCombosFromDB() {
        final String[] PROJECTION = {
                CategoryOptionComboModel.Columns.ID,
                CategoryOptionComboModel.Columns.UID,
                CategoryOptionComboModel.Columns.CODE,
                CategoryOptionComboModel.Columns.NAME,
                CategoryOptionComboModel.Columns.DISPLAY_NAME,
                CategoryOptionComboModel.Columns.CREATED,
                CategoryOptionComboModel.Columns.LAST_UPDATED,
                CategoryOptionComboModel.Columns.CATEGORY_COMBO

        };

        String sqlQuery = SQLiteQueryBuilder.buildQueryString(false, CategoryOptionComboModel.TABLE,
                PROJECTION, null,
                null, null, null, null);


        return databaseAdapter().query(sqlQuery);
    }

    @NonNull
    private CategoryComboCallEndpoint provideComboCallEndpoint() {
        CategoryComboQuery query = CategoryComboQuery.defaultQuery();

        CategoryComboService comboService = d2.retrofit().create(CategoryComboService.class);
        Store<CategoryComboLinkModel> categoryComboLinkStore = new CategoryComboLinkStoreImpl(
                databaseAdapter());

        Store<CategoryOptionCombo> optionComboStore = new CategoryOptionComboStoreImpl(
                databaseAdapter());
        Handler<CategoryOptionCombo> optionComboHandler = new CategoryOptionComboHandler(
                optionComboStore);

        ResponseValidator<CategoryCombo> validator = new ResponseValidator<>();

        Store<CategoryCombo> store = new CategoryComboStoreImpl(databaseAdapter());

        Store<CategoryOptionComboLinkCategoryModel>
                categoryComboOptionLinkCategoryStore = new CategoryOptionComboLinkCategoryStoreImpl(
                databaseAdapter());

        Handler<CategoryCombo> handler = new CategoryComboHandler(store,
                categoryComboOptionLinkCategoryStore, categoryComboLinkStore,
                optionComboHandler);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
        Date serverDate = new Date();

        return new CategoryComboCallEndpoint(query, comboService, validator, handler,
                resourceHandler,
                databaseAdapter(), serverDate);

    }

    private boolean hasCombos(Response<Payload<CategoryCombo>> response) {
        return !response.body().items().isEmpty();
    }
}
