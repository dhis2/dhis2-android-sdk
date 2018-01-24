package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryCategoryOptionChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private CategoryStore categoryStore;
    private CategoryOptionStore categoryOptionStore;
    private MetadataAuditListener metadataAuditListener;

    private CategoryFactory categoryFactory;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        categoryFactory = new CategoryFactory(d2.retrofit(),
                databaseAdapter(), HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryOptionMetadataAuditHandler(categoryFactory));

        categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);

    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void ignore_category_option_if_audit_type_is_create() throws Exception {
        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/category_option_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(CategoryOption.class, metadataAudit);
        assertThat(categoryOptionStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    @Test
    public void update_category_option_if_audit_type_is_update() throws Exception {
        String filename = "audit/categories.json";

        givenAExistedCategoryOptionPreviously();

        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/category_option_update.json");

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

        metadataAuditListener.onMetadataChanged(CategoryOption.class, metadataAudit);

        assertThat(categoryOptionStore.queryByUid(metadataAudit.getUid()),
                is(getCategoryOptionExpected(metadataAudit.getUid())));
    }


    private CategoryOption getCategoryOptionExpected(String uid) throws IOException {
        String json = new AssetsFileReader().getStringFromFile("audit/categories.json");

        GenericClassParser parser = new GenericClassParser();

        Payload<Category> payloadExpected = parser.parse(json, Payload.class, Category.class);

        for (Category category : payloadExpected.items()) {
            for (CategoryOption option : category.categoryOptions()) {
                if (option.uid().equals(uid)) {
                    return option;
                }
            }
        }

        return null;
    }
    @Test
    public void delete_category_option_in_database_if_audit_type_is_delete() throws Exception {
        givenAExistedCategoryOptionPreviously();

        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/category_option_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(Category.class, metadataAudit);

        assertThat(categoryOptionStore.queryAll().size(), is(0));
    }

    private MetadataAudit<CategoryOption> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, CategoryOption.class);
    }

    private void givenAExistedCategoryOptionPreviously() throws IOException {
        String categoryOptionUid="UOqJW6HPvvL";
        String categoryUid="DkS8tTZCkNE";
        Category category = Category.builder()
                .uid(categoryUid)
                .build();

        category = category.toBuilder()
                .categoryOptions(Arrays.asList(CategoryOption.builder()
                        .uid(categoryOptionUid)
                        .displayName("Example").build()))
                .build();

        categoryFactory.getCategoryHandler().handle(category);
    }

    private Payload<CategoryOption> parseEntities(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, CategoryOption.class);
    }

    private Category getCategory(String uid) {
        Category category = categoryStore.queryByUid(uid);

        List<String> categoryOptionUIdList = new CategoryCategoryOptionLinkStoreImpl(databaseAdapter()).queryCategoryOptionUidListFromCategoryUid(uid);
        List<CategoryOption> categoryOptions = new ArrayList<>();
        for(String categoryOptionUid:categoryOptionUIdList){
            CategoryOption categoryOption = new CategoryOptionStoreImpl(databaseAdapter()).queryByUid(categoryOptionUid);
            categoryOptions.add(categoryOption);
        }

        category = category.toBuilder().categoryOptions(categoryOptions)
                //todo persist shortname/displayshortname/description in database
                .shortName("new cat 001dis")
                .displayShortName("new cat 001dis")
                .build();

        return category;
    }
}
