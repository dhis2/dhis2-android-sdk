package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import android.support.test.filters.MediumTest;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.audit.GenericClassParser;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandlerFactory;
import org.hisp.dhis.android.core.audit.MetadataAuditListener;
import org.hisp.dhis.android.core.audit.MetadataSyncedListener;
import org.hisp.dhis.android.core.audit.SyncedMetadata;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.utils.StoreUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryOptionComboChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private CategoryOptionStore categoryOptionStore;
    private CategoryOptionComboStore categoryOptionComboStore;
    private MetadataAuditListener metadataAuditListener;
    private CategoryHandler categoryHandler;
    private CategoryComboFactory categoryComboFactory;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        categoryComboFactory = new CategoryComboFactory(d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryOptionComboMetadataAuditHandler(categoryComboFactory));

        categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        categoryOptionComboStore = new CategoryOptionComboStoreImpl(databaseAdapter());

        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);

        CategoryCategoryOptionLinkStore categoryCategoryComboLinkStore =
                new CategoryCategoryOptionLinkStoreImpl(databaseAdapter());
        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore, categoryCategoryComboLinkStore);
        categoryHandler = new CategoryHandler(new CategoryStoreImpl(databaseAdapter()),
                categoryOptionHandler);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void update_category_option_if_audit_type_is_update() throws Exception {
        String filename = "category_combo_updated.json";

        givenAExistedCategoryPreviously();

        MetadataAudit<CategoryOptionCombo> metadataAudit =
                givenAMetadataAudit("audit/category_option_combo_update.json");

        dhis2MockServer.enqueueMockResponse(filename);

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(CategoryOptionCombo.class, metadataAudit);

        assertThat(addCategoryOptions(categoryOptionComboStore.queryByUId(metadataAudit.getUid())),
                is(getCategoryOptionComboFromJson(filename)));
    }

    private CategoryOptionCombo addCategoryOptions(CategoryOptionCombo categoryOptionCombo) {
        List<String> categoryUids =
                categoryComboFactory.getCategoryComboOptionCategoryLinkStore()
                        .queryByOptionComboUId(
                        categoryOptionCombo.uid());
        List<CategoryOption> categoryOptionList = new ArrayList<>();
        for (String uId : categoryUids) {
            categoryOptionList.add(categoryOptionStore.queryByUid(uId));
        }
        return categoryOptionCombo.toBuilder().categoryOptions(categoryOptionList).build();
    }

    private CategoryOptionCombo getCategoryOptionComboFromJson(String filename) throws IOException {
        CategoryCombo categoryCombo = parseEntities(filename).items().get(0);

        CategoryOptionCombo categoryOptionCombo = categoryCombo.categoryOptionCombos().get(0);
        return categoryOptionCombo;
    }

    private Payload<CategoryCombo> parseEntities(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, CategoryCombo.class);
    }

    private MetadataAudit<CategoryOptionCombo> givenAMetadataAudit(String fileName)
            throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, CategoryOptionCombo.class);
    }

    private void givenAExistedCategoryPreviously() throws IOException {
        List<CategoryOption> categoryOptions = new ArrayList<>();
        categoryOptions.add(CategoryOption.builder().uid("as6ygGvUGNg").build());
        Category category = Category.builder()
                .uid("gtuVl6NbXQV")
                .code("COMMODITIES")
                .name("Commodities")
                .created(StoreUtils.parse("2014-03-02T02:14:34.600"))
                .lastUpdated(StoreUtils.parse("2014-03-05T04:10:47.764"))
                .displayName("Commodities")
                .categoryOptions(categoryOptions)
                .build();

        categoryHandler.handle(category);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        List<CategoryOptionCombo> categoryOptionCombos = new ArrayList<>();
        CategoryCombo categoryCombo = CategoryCombo.builder().uid("SO8aY5wDjRJ").build();
        categoryOptionCombos.add(CategoryOptionCombo.builder().uid("bRowv6yZOF2")
                .categoryCombo(categoryCombo).categoryOptions(categoryOptions).build());
        categoryCombo = categoryCombo.toBuilder().categoryOptionCombos(
                categoryOptionCombos).build();

        categoryComboFactory.getCategoryComboHandler().handle(categoryCombo);
    }

}
