package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;

public class CategoryOptionChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private CategoryOptionStore categoryOptionStore;
    private MetadataAuditListener metadataAuditListener;

    private CategoryFactory categoryFactory;

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        categoryFactory = new CategoryFactory(d2.retrofit(),
                databaseAdapter(), HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryOptionMetadataAuditHandler(categoryFactory, DEFAULT_IS_TRANSLATION_ON,
                        DEFAULT_TRANSLATION_LOCALE));

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
    @MediumTest
    public void ignore_category_option_if_audit_type_is_create() throws Exception {
        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/categoryOption_create.json");

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
    @MediumTest
    public void update_category_option_if_audit_type_is_update() throws Exception {
        String filename = "category_edited.json";

        givenAExistedCategoryOptionPreviously();

        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/categoryOption_update.json");

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
                is(getCategoryOptionExpected(filename, metadataAudit.getUid())));
    }

    @Test
    @MediumTest
    public void delete_category_option_in_database_if_audit_type_is_delete() throws Exception {
        givenAExistedCategoryOptionPreviously();

        MetadataAudit<CategoryOption> metadataAudit =
                givenAMetadataAudit("audit/categoryOption_delete.json");

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

    private CategoryOption getCategoryOptionExpected(String fileName, String uid)
            throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

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

    private MetadataAudit<CategoryOption> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, CategoryOption.class);
    }

    private void givenAExistedCategoryOptionPreviously() throws IOException {
        String categoryOptionUid = "UOqJW6HPvvL";
        String categoryUid = "DkS8tTZCkNE";
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
}
