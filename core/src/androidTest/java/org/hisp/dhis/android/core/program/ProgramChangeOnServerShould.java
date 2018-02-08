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

public class ProgramChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private ProgramStore programStore;
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
                new ProgramMetadataAuditHandler(
                        new ProgramFactory(d2.retrofit(), databaseAdapter(),
                                optionSetFactory.getOptionSetHandler(), dataElementFactory,
                                resourceHandler)));

        programStore = new ProgramStoreImpl(databaseAdapter());
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void create_program_in_database_if_audit_type_is_create() throws Exception {
        givenAMetadataInDatabase();

        MetadataAudit<Program> metadataAudit =
                givenAMetadataAudit("audit/program_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(Program.class, metadataAudit);

        Program createdProgram = programStore.queryByUid(metadataAudit.getUid());

        Program expectedProgram = metadataAudit.getValue();

        verifyProgram(createdProgram, expectedProgram);
    }

    @Test
    public void update_program_if_audit_type_is_update() throws Exception {
        givenAMetadataInDatabase();

        String filename = "programs_antenatal_care_visit_edited.json";

        MetadataAudit<Program> metadataAudit =
                givenAMetadataAudit("audit/program_update.json");

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

        metadataAuditListener.onMetadataChanged(Program.class, metadataAudit);

        Program editedProgram = programStore.queryByUid(metadataAudit.getUid());

        Program expectedProgram = getExpectedProgram(filename);

        verifyProgram(editedProgram, expectedProgram);
    }

    @Test
    public void delete_program_in_database_if_audit_type_is_delete() throws Exception {
        givenAMetadataInDatabase();

        MetadataAudit<Program> metadataAudit =
                givenAMetadataAudit("audit/program_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(Program.class, metadataAudit);

        assertThat(programStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    private MetadataAudit<Program> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, Program.class);
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

    private Program getExpectedProgram(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        Payload<Program> payloadExpected = parser.parse(json, Payload.class, Program.class);

        return payloadExpected.items().get(0);
    }

    private void verifyProgram(Program createdProgram, Program expectedProgram) {
        //compare without children because there are other tests (call, handler)
        //that verify the tree is saved in database
        assertThat(removeChildrenFromProgram(createdProgram),
                is(removeChildrenFromProgram(expectedProgram)));
    }

    private Program removeChildrenFromProgram(Program program) {
        program = program.toBuilder()
                .categoryCombo(null)
                .programStages(null)
                .programIndicators(null)
                .programRules(null)
                .programRuleVariables(null)
                .programTrackedEntityAttributes(null)
                .relationshipType(null)
                .trackedEntity(null).build();

        return program;
    }
}
