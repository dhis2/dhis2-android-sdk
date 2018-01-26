package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertTrue;
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

public class CategoryComboChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private CategoryComboStore categoryComboStore;
    private MetadataAuditListener metadataAuditListener;
    private CategoryHandler categoryHandler;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryComboMetadataAuditHandler(
                        new CategoryComboFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter()))));

        categoryComboStore = new CategoryComboStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);

        CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore = new CategoryCategoryOptionLinkStoreImpl(databaseAdapter());
        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore, categoryCategoryOptionLinkStore);
        categoryHandler = new CategoryHandler(new CategoryStoreImpl(databaseAdapter()), categoryOptionHandler);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void create_category_combo_in_database_if_audit_type_is_create() throws Exception {
        givenAExistedCategoryPreviously();

        MetadataAudit<CategoryCombo> metadataAudit =
                givenAMetadataAudit("audit/category_combo_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });
        CategoryCombo categoryCombo = (CategoryCombo) metadataAudit.getValue();
        metadataAuditListener.onMetadataChanged(CategoryCombo.class, metadataAudit);
        assertTrue(getCategoryCombo(metadataAudit.getUid()).equals(categoryCombo));
    }

    @Test
    @MediumTest
    public void update_category_combo_if_audit_type_is_update() throws Exception {
        String filename = "audit/category_combos.json";
        givenAExistedCategoryPreviously();

        givenAExistedCategoryComboPreviously();

        MetadataAudit<CategoryCombo> metadataAudit =
                givenAMetadataAudit("audit/category_combo_update.json");

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

        metadataAuditListener.onMetadataChanged(CategoryCombo.class, metadataAudit);

        assertThat(getCategoryCombo(metadataAudit.getUid()), is(getCategoryComboFromJson(filename)));
    }

    @Test
    @MediumTest
    public void delete_category_combo_in_database_if_audit_type_is_delete() throws Exception {
        givenAExistedCategoryPreviously();

        givenAExistedCategoryComboPreviously();

        MetadataAudit<CategoryCombo> metadataAudit =
                givenAMetadataAudit("audit/category_combo_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(CategoryCombo.class, metadataAudit);

        assertThat(categoryComboStore.queryAll().size(), is(0));
    }

    private MetadataAudit<CategoryCombo> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, CategoryCombo.class);
    }

    private void givenAExistedCategoryPreviously() throws IOException {
        Category category = Category.builder()
                .uid("gtuVl6NbXQV")
                .code("COMMODITIES")
                .name("Commodities")
                .created(StoreUtils.parse("2014-03-02T02:14:34.600"))
                .lastUpdated(StoreUtils.parse("2014-03-05T04:10:47.764"))
                .displayName("Commodities")
                .build();

      categoryHandler.handle(category);
    }

    private void givenAExistedCategoryComboPreviously() throws IOException {
        MetadataAudit<CategoryCombo> metadataAudit =
                givenAMetadataAudit("audit/category_combo_create.json");
        metadataAuditListener.onMetadataChanged(CategoryCombo.class, metadataAudit);
    }

    private Payload<CategoryCombo> parseEntities(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, CategoryCombo.class);
    }

    private Category getCategory(String uid) {
        Category category = new CategoryStoreImpl(databaseAdapter()).queryByUid(uid);

        return category;
    }

    private CategoryCombo getCategoryCombo(String uid) {

        CategoryCombo categoryCombo = categoryComboStore.queryByUid(uid);

        List<CategoryCategoryComboLink> categoryOptionUIdList = new CategoryCategoryComboLinkStoreImpl(databaseAdapter()).queryByCategoryComboUId(uid);
        List <Category> categoryList = new ArrayList<>();

        for(CategoryCategoryComboLink categoryCategoryComboLink: categoryOptionUIdList){
            categoryList.add(getCategory(categoryCategoryComboLink.category()));
        }
        return categoryCombo.toBuilder().categories(categoryList).build();
    }

    private CategoryCombo getCategoryComboFromJson(String filename) throws IOException {
        CategoryCombo categoryCombo = parseEntities(filename).items().get(0);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(getCategory(categoryCombo.categories().get(0).uid()));
        return categoryCombo.toBuilder().categories(categoryList).build();
    }

}
