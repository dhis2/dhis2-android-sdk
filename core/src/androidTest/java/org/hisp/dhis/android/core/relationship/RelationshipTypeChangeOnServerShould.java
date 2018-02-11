package org.hisp.dhis.android.core.relationship;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
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

public class RelationshipTypeChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private RelationshipTypeStore relationshipTypeStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        RelationshipTypeFactory relationshipTypeFactory = new RelationshipTypeFactory(d2.retrofit(),
                databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new RelationshipTypeMetadataAuditHandler(relationshipTypeFactory,
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

        relationshipTypeStore = new RelationshipTypeStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void create_relation_ship_types_in_database_if_audit_type_is_create() throws Exception {
        MetadataAudit<RelationshipType> metadataAudit =
                givenAMetadataAudit("audit/relationship_type_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(RelationshipType.class, metadataAudit);

        RelationshipType createdRelationshipType =
                relationshipTypeStore.queryByUid(metadataAudit.getUid());

        RelationshipType expectedRelationshipType = metadataAudit.getValue();

        assertThat(createdRelationshipType, is(expectedRelationshipType));
    }

    @Test
    public void update_relationship_type_set_if_audit_type_is_update() throws Exception {
        String filename = "relationship_type_updated.json";

        givenAExistedRelationshipTypePreviously();

        MetadataAudit<RelationshipType> metadataAudit =
                givenAMetadataAudit("audit/relationship_type_update.json");

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

        metadataAuditListener.onMetadataChanged(RelationshipType.class, metadataAudit);

        RelationshipType editedRelationshipType =
                relationshipTypeStore.queryByUid(metadataAudit.getUid());

        RelationshipType expectedRelationshipType = parseExpected(filename).items().get(0);

        assertThat(editedRelationshipType, is(expectedRelationshipType));
    }

    @Test
    public void delete_relationship_type_set_in_database_if_audit_type_is_delete()
            throws Exception {
        givenAExistedRelationshipTypePreviously();

        MetadataAudit<RelationshipType> metadataAudit =
                givenAMetadataAudit("audit/relationship_type_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(RelationshipType.class, metadataAudit);

        assertThat(relationshipTypeStore.queryByUid(metadataAudit.getUid()), is(nullValue()));

    }

    private MetadataAudit<RelationshipType> givenAMetadataAudit(String fileName)
            throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, RelationshipType.class);
    }

    private void givenAExistedRelationshipTypePreviously() throws IOException {
        MetadataAudit<RelationshipType> metadataAudit =
                givenAMetadataAudit("audit/relationship_type_create.json");

        metadataAuditListener.onMetadataChanged(RelationshipType.class, metadataAudit);
    }

    private Payload<RelationshipType> parseExpected(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, RelationshipType.class);
    }
}
