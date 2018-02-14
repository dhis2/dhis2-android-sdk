package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import android.support.test.filters.MediumTest;

import org.hamcrest.MatcherAssert;
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

public class CategoryChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private CategoryStore categoryStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new CategoryMetadataAuditHandler(
                        new CategoryFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter())),
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

        categoryStore = new CategoryStoreImpl(databaseAdapter());
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
    public void create_category_in_database_if_audit_type_is_create() throws Exception {
        MetadataAudit<Category> metadataAudit =
                givenAMetadataAudit("audit/category_create.json");

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


        Category createdCategory = categoryStore.queryAll().get(0);
        Category expectedCategory = metadataAudit.getValue();

        verifyCategory(createdCategory, expectedCategory);
    }

    @Test
    @MediumTest
    public void update_category_if_audit_type_is_update() throws Exception {
        String filename = "category_edited.json";

        givenAExistedCategoryPreviously();

        MetadataAudit<Category> metadataAudit =
                givenAMetadataAudit("audit/category_update.json");

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

        metadataAuditListener.onMetadataChanged(Category.class, metadataAudit);

        Category editedCategory = categoryStore.queryAll().get(0);
        Category expectedCategory = parseEntities(filename).items().get(0);

        verifyCategory(editedCategory, expectedCategory);
    }

    @Test
    @MediumTest
    public void delete_category_in_database_if_audit_type_is_delete() throws Exception {
        givenAExistedCategoryPreviously();

        MetadataAudit<Category> metadataAudit =
                givenAMetadataAudit("audit/category_delete.json");

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

        assertThat(categoryStore.queryAll().size(), is(0));
    }

    private MetadataAudit<Category> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, Category.class);
    }

    private void givenAExistedCategoryPreviously() throws IOException {
        MetadataAudit<Category> metadataAudit =
                givenAMetadataAudit("audit/category_create.json");
        metadataAuditListener.onMetadataChanged(Category.class, metadataAudit);
    }

    private Payload<Category> parseEntities(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, Category.class);
    }

    private void verifyCategory(Category createdCategory, Category expectedCategory) {
        //compare without children because there are other tests (call, handler)
        //that verify the tree is saved in database
        MatcherAssert.assertThat(removeChildrenFromCategory(createdCategory),
                is(removeChildrenFromCategory(expectedCategory)));
    }

    private Category removeChildrenFromCategory(Category category) {
        category = category.toBuilder()
                .categoryOptions(null).build();

        return category;
    }
}
