package org.hisp.dhis.android.core.trackedentity;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
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
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class TrackedEntityAttributeChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private TrackedEntityAttributeStore trackedEntityAttributeStore;
    private MetadataAuditListener metadataAuditListener;
    private OptionSetFactory optionSetFactory;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new TrackedEntityAttributeMetadataAuditHandler(
                        new TrackedEntityAttributeFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter())),
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

        optionSetFactory = new OptionSetFactory(d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        trackedEntityAttributeStore = new TrackedEntityAttributeStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void create_tracked_entity_attribute_in_database_if_audit_type_is_create()
            throws Exception {
        givenAExistedOptionDependencyPreviously();

        MetadataAudit<TrackedEntityAttribute> metadataAudit =
                givenAMetadataAudit("audit/tracked_entity_attribute_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(TrackedEntityAttribute.class, metadataAudit);

        TrackedEntityAttribute createdTrackedEntityAttribute =
                trackedEntityAttributeStore.queryAll().get(0);

        TrackedEntityAttribute expectedTrackedEntityAttribute = metadataAudit.getValue();

        verifyTrackedEntityAttribute(createdTrackedEntityAttribute, expectedTrackedEntityAttribute);
    }

    @Test
    public void update_tracked_entity_attribute_if_audit_type_is_update() throws Exception {
        String filename = "tracked_entity_attribute_updated.json";

        givenAExistedOptionDependencyPreviously();
        givenAExistedTrackedEntityAttributePreviously();

        MetadataAudit<TrackedEntityAttribute> metadataAudit =
                givenAMetadataAudit("audit/tracked_entity_attribute_update.json");

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

        metadataAuditListener.onMetadataChanged(TrackedEntityAttribute.class, metadataAudit);

        TrackedEntityAttribute editedTrackedEntityAttribute =
                trackedEntityAttributeStore.queryAll().get(0);

        TrackedEntityAttribute expectedTrackedEntityAttribute =
                parseTrackedEntities(filename).items().get(0);

        verifyTrackedEntityAttribute(editedTrackedEntityAttribute, expectedTrackedEntityAttribute);
    }

    @Test
    public void delete_tracked_entity_attribute_in_database_if_audit_type_is_delete()
            throws Exception {
        givenAExistedOptionDependencyPreviously();
        givenAExistedTrackedEntityAttributePreviously();

        MetadataAudit<TrackedEntityAttribute> metadataAudit =
                givenAMetadataAudit("audit/tracked_entity_attribute_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(TrackedEntityAttribute.class, metadataAudit);

        assertThat(trackedEntityAttributeStore.queryAll().size(), is(0));
    }

    private MetadataAudit<TrackedEntityAttribute> givenAMetadataAudit(String fileName)
            throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, TrackedEntityAttribute.class);
    }

    private void givenAExistedTrackedEntityAttributePreviously() throws IOException {
        MetadataAudit<TrackedEntityAttribute> metadataAudit =
                givenAMetadataAudit("audit/tracked_entity_attribute_create.json");
        metadataAuditListener.onMetadataChanged(TrackedEntityAttribute.class, metadataAudit);
    }

    private Payload<TrackedEntityAttribute> parseTrackedEntities(String fileName)
            throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, TrackedEntityAttribute.class);
    }

    private void givenAExistedOptionDependencyPreviously() throws IOException {
        OptionSet optionSet = OptionSet.builder()
                .name("Age category")
                .lastUpdated(parse("2015-08-06T14:23:38.789"))
                .created(parse("2014-06-22T10:59:26.564"))
                .displayName("Age category")
                .uid("VQ2lai3OfVG")
                .build();

        optionSetFactory.getOptionSetHandler().handleOptionSet(optionSet);
    }

    private void verifyTrackedEntityAttribute(TrackedEntityAttribute createdTrackedEntityAttribute,
            TrackedEntityAttribute expectedTrackedEntityAttribute) {
        //compare without children because there are other tests (call, handler)
        //that verify the tree is saved in database
        assertThat(removeChildrenFromTrackedEntityAttribute(createdTrackedEntityAttribute),
                is(removeChildrenFromTrackedEntityAttribute(expectedTrackedEntityAttribute)));
    }

    private TrackedEntityAttribute removeChildrenFromTrackedEntityAttribute(
            TrackedEntityAttribute trackedEntityAttribute) {
        trackedEntityAttribute = trackedEntityAttribute.toBuilder()
                .optionSet(null).build();

        return trackedEntityAttribute;
    }
}
