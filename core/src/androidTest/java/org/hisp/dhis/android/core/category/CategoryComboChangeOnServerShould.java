package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
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

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryComboMetadataAuditHandler(
                        new CategoryComboFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter())),
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

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
        givenACategoryComboDependenciesPreviously();

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

        metadataAuditListener.onMetadataChanged(CategoryCombo.class, metadataAudit);

        CategoryCombo categoryCombo = metadataAudit.getValue();
        assertThat(getOnlyCategoryCombo(getCategoryCombo(metadataAudit.getUid())), is(getOnlyCategoryCombo(categoryCombo)));
    }

    @Test
    @MediumTest
    public void update_category_combo_if_audit_type_is_update() throws Exception {
        String filename = "category_combo_updated.json";

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

        assertUpdate(filename, metadataAudit);

    }

    @Test
    @MediumTest
    public void delete_category_combo_in_database_if_audit_type_is_delete() throws Exception {

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

    private void givenACategoryComboDependenciesPreviously() throws IOException {
        List <CategoryOption> categoryOptions = new ArrayList<>();
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
    }

    private void givenAExistedCategoryComboPreviously() throws IOException {

        givenACategoryComboDependenciesPreviously();

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
        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        List<String>categoryOptionUidList = new CategoryCategoryOptionLinkStoreImpl(databaseAdapter()).queryCategoryOptionUidListFromCategoryUid(uid);
        List<CategoryOption> categoryOptions = new ArrayList<>();
        for (String categoryOptionUid : categoryOptionUidList) {
            categoryOptions.add(categoryOptionStore.queryByUid(categoryOptionUid));
        }
        return category.toBuilder().categoryOptions(categoryOptions).build();

    }

    private CategoryCombo getCategoryCombo(String uid) {

        CategoryCombo categoryCombo = categoryComboStore.queryByUid(uid);

        List<CategoryCategoryComboLink> categoryOptionUIdList = new CategoryCategoryComboLinkStoreImpl(databaseAdapter()).queryByCategoryComboUId(uid);
        List <Category> categoryList = new ArrayList<>();

        for(CategoryCategoryComboLink categoryCategoryComboLink: categoryOptionUIdList){
            categoryList.add(getCategory(categoryCategoryComboLink.category()));
        }

        List<CategoryOptionCombo> categoryOptionCombos = new CategoryOptionComboStoreImpl(databaseAdapter()).queryByCategoryComboUId(uid);
        if(categoryOptionCombos.size()==0){
            categoryOptionCombos = null;
        }

        return categoryCombo.toBuilder().categories(categoryList).categoryOptionCombos(categoryOptionCombos).build();
    }

    private CategoryCombo getCategoryComboFromJson(String filename) throws IOException {
        CategoryCombo categoryCombo = parseEntities(filename).items().get(0);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(getCategory(categoryCombo.categories().get(0).uid()));
        return categoryCombo.toBuilder().categories(categoryList).build();
    }

    private CategoryCombo getOnlyCategoryCombo(CategoryCombo categoryComboFromJson) {
        return categoryComboFromJson.toBuilder().categories(null).categoryOptionCombos(null).build();
    }

    private CategoryOptionCombo getOnlyCategoryOptionCombo(CategoryOptionCombo categoryOptionComboFromJson) {
        return CategoryOptionCombo.create(categoryOptionComboFromJson.uid(),categoryOptionComboFromJson.code(),
                categoryOptionComboFromJson.name(), categoryOptionComboFromJson.displayName(),
                categoryOptionComboFromJson.created(), categoryOptionComboFromJson.lastUpdated(),
                null, null);
    }

    private void assertUpdate(String filename, MetadataAudit<CategoryCombo> metadataAudit)
            throws IOException {
        CategoryCombo categoryComboPersistedWithoutDependencies = getOnlyCategoryCombo(getCategoryCombo(metadataAudit.getUid()));

        //verify updated Category Combo
        assertThat(categoryComboPersistedWithoutDependencies,
                is(getOnlyCategoryCombo(getCategoryComboFromJson(filename))));

        CategoryOptionCombo categoryOptionComboWithoutDependencies =
                getOnlyCategoryOptionCombo(getCategoryComboFromJson(filename).categoryOptionCombos().get(0));
        CategoryCombo categoryComboPersisted = getCategoryCombo(metadataAudit.getUid());

        CategoryOptionCombo categoryOptionComboPersistedWithoutDependencies =
                getOnlyCategoryOptionCombo(categoryComboPersisted.categoryOptionCombos().get(0));

        //verify updated Category Option Combo
        assertThat(categoryOptionComboPersistedWithoutDependencies,
                is(categoryOptionComboWithoutDependencies));
    }
}
