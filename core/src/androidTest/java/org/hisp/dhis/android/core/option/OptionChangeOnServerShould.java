package org.hisp.dhis.android.core.option;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.hisp.dhis.android.core.common.ValueType;
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

public class OptionChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private OptionStore optionStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    private OptionSetFactory optionSetFactory;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        optionSetFactory = new OptionSetFactory(d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new OptionMetadataAuditHandler(optionSetFactory));

        optionStore = new OptionStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void ignore_option_if_audit_type_is_create() throws Exception {
        MetadataAudit<Option> metadataAudit =
                givenAMetadataAudit("audit/option_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(Option.class, metadataAudit);

        assertThat(optionStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    @Test
    public void update_option_if_audit_type_is_update() throws Exception {
        String filename = "option_sets.json";

        givenAPreExistingOption();

        MetadataAudit<Option> metadataAudit =
                givenAMetadataAudit("audit/option_update.json");

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

        metadataAuditListener.onMetadataChanged(Option.class, metadataAudit);

        assertThat(getOptionFromDatabase(metadataAudit.getUid()),
                is(getOptionExpected(metadataAudit.getUid())));
    }

    @Test
    public void delete_option_in_database_if_audit_type_is_delete() throws Exception {
        givenAPreExistingOption();

        MetadataAudit<Option> metadataAudit =
                givenAMetadataAudit("audit/option_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(Option.class, metadataAudit);

        assertThat(optionStore.queryByUid(metadataAudit.getUid()), is(nullValue()));

    }

    private MetadataAudit<Option> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, Option.class);
    }

    private void givenAPreExistingOption() throws IOException {
        OptionSet optionSet = OptionSet.builder()
                .uid("VQ2lai3OfVG")
                .valueType(ValueType.TEXT)
                .version(0)
                .build();

        optionSet = optionSet.toBuilder()
                .options(Arrays.asList(Option.builder()
                        .optionSet(optionSet)
                        .uid("Y1ILwhy5VDY")
                        .displayName("Example").build())).build();

        optionSetFactory.getOptionSetHandler().handleOptionSet(optionSet);
    }

    private Option getOptionExpected(String uid) throws IOException {
        String json = new AssetsFileReader().getStringFromFile("option_sets.json");

        GenericClassParser parser = new GenericClassParser();

        Payload<OptionSet> payloadExpected = parser.parse(json, Payload.class, OptionSet.class);

        for (OptionSet optionSet : payloadExpected.items()) {
            for (Option option : optionSet.options()) {
                if (option.uid().equals(uid)) {
                    return option;
                }
            }
        }

        return null;
    }

    private Option getOptionFromDatabase(String uid) {
        Option option = new OptionStoreImpl(databaseAdapter()).queryByUid(uid);

        return option;
    }
}
