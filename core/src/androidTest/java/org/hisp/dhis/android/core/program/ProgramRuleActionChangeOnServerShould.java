package org.hisp.dhis.android.core.program;

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
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class ProgramRuleActionChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private ProgramRuleActionStore programRuleActionStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter());

        OptionSetFactory optionSetFactory = new OptionSetFactory(d2.retrofit(), databaseAdapter(),
                resourceHandler);

        DataElementFactory dataElementFactory = new DataElementFactory(d2.retrofit(),
                databaseAdapter(), resourceHandler);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new ProgramRuleActionMetadataAuditHandler(
                        new ProgramFactory(d2.retrofit(), databaseAdapter(),
                                optionSetFactory.getOptionSetHandler(), dataElementFactory,
                                resourceHandler)));

        programRuleActionStore = new ProgramRuleActionStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void ignore_program_rule_action_if_audit_type_is_create() throws Exception {
        MetadataAudit<ProgramRuleAction> metadataAudit =
                givenAMetadataAudit("audit/programRuleAction_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(ProgramRuleAction.class, metadataAudit);

        assertThat(programRuleActionStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    @Test
    public void update_program_rule_action_if_audit_type_is_update() throws Exception {
        givenAMetadataInDatabase();

        String filename = "programs_antenatal_care_visit_edited.json";

        dhis2MockServer.enqueueMockResponse(filename);

        MetadataAudit<ProgramRuleAction> metadataAudit =
                givenAMetadataAudit("audit/programRuleAction_update.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(ProgramStage.class, metadataAudit);

        ProgramRuleAction programRuleActionUpdated = programRuleActionStore.queryByUid(metadataAudit.getUid());

        ProgramRuleAction ProgramRuleActionExpected = getExpectedProgramStage(filename);

        verifyEqualsProgramRuleAction(programRuleActionUpdated, ProgramRuleActionExpected);
    }

    @Test
    public void delete_program_rule_action_in_database_if_audit_type_is_delete() throws Exception {
        givenAMetadataInDatabase();

        MetadataAudit<ProgramRuleAction> metadataAudit =
                givenAMetadataAudit("audit/programRuleAction_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(ProgramRuleAction.class, metadataAudit);

        assertThat(programRuleActionStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    private MetadataAudit<ProgramRuleAction> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, ProgramRuleAction.class);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");
        d2.syncMetaData().call();
    }

    private ProgramRuleAction getExpectedProgramStage(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        Payload<Program> payloadExpected = parser.parse(json, Payload.class, Program.class);

        return payloadExpected.items().get(0).programRules().get(0).programRuleActions().get(0);
    }

    private void verifyEqualsProgramRuleAction(ProgramRuleAction programRuleActionUpdated,
            ProgramRuleAction programRuleActionExpected) {
        assertThat(removeForeignKeysFromProgramRuleAction(programRuleActionUpdated),
                is(removeForeignKeysFromProgramRuleAction(programRuleActionExpected)));
    }

    private ProgramRuleAction removeForeignKeysFromProgramRuleAction(ProgramRuleAction programRuleAction) {
        //compare without dependencies (FKs) because there are other tests (call, handler)
        //that verify the whole tree is saved in database
        programRuleAction = programRuleAction.toBuilder()
                .programRule(null)
                .programStageSection(null)
                .programStage(null)
                .programIndicator(null)
                .trackedEntityAttribute(null)
                .build();

        return programRuleAction;
    }
}
