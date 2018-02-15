package org.hisp.dhis.android.core.option;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
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
import java.util.List;

public class OptionSetChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private OptionSetStore optionSetStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new OptionSetMetadataAuditHandler(
                        new OptionSetFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter())),
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

        optionSetStore = new OptionSetStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void create_option_set_in_database_if_audit_type_is_create() throws Exception {
        MetadataAudit<OptionSet> metadataAudit =
                givenAMetadataAudit("audit/optionSet_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(OptionSet.class, metadataAudit);

        assertThat(optionSetStore.queryByUid(metadataAudit.getUid()), is(metadataAudit.getValue()));
    }


    @Test
    public void update_option_set_if_audit_type_is_update() throws Exception {
        String filename = "option_sets.json";

        givenAExistedOptionSetPreviously();

        MetadataAudit<OptionSet> metadataAudit =
                givenAMetadataAudit("audit/optionSet_update.json");

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

        metadataAuditListener.onMetadataChanged(OptionSet.class, metadataAudit);

        assertThat(getOptionSet(metadataAudit.getUid()), is(parseExpected(
                filename).items().get(0)));
    }

    private OptionSet getOptionSet(String uid) {
        OptionSet optionSet = optionSetStore.queryByUid(uid);

        List<Option> options = new OptionStoreImpl(databaseAdapter()).queryByOptionSet(uid);

        optionSet = optionSet.toBuilder().options(options).build();

        return optionSet;
    }

    @Test
    public void delete_option_set_in_database_if_audit_type_is_delete() throws Exception {
        givenAExistedOptionSetPreviously();

        MetadataAudit<OptionSet> metadataAudit =
                givenAMetadataAudit("audit/optionSet_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(OptionSet.class, metadataAudit);

        assertThat(optionSetStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    private MetadataAudit<OptionSet> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, OptionSet.class);
    }

    private void givenAExistedOptionSetPreviously() throws IOException {
        MetadataAudit<OptionSet> metadataAudit =
                givenAMetadataAudit("audit/optionSet_create.json");
        metadataAuditListener.onMetadataChanged(OptionSet.class, metadataAudit);
    }

    private Payload<OptionSet> parseExpected(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, OptionSet.class);
    }
}
