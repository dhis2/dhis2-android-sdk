package org.hisp.dhis.android.core.organisationunit;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;

public class OrganisationUnitChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private MetadataAuditListener metadataAuditListener;
    private OrganisationUnitFactory organisationUnitFactory;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        organisationUnitFactory = new OrganisationUnitFactory(d2.retrofit(), databaseAdapter(),
                HandlerFactory.createResourceHandler(databaseAdapter()));

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new OrganisationUnitMetadataAuditHandler(organisationUnitFactory,
                        DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE));

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
        givenMetadataDependencies();

        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_create.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(OrganisationUnit.class, metadataAudit);

        assertThat(
                getOrganisationUnitWithParent(getOrganisationUnitFromDatabase(
                        metadataAudit.getUid())), is(metadataAudit.getValue()));
    }

    @Test
    public void update_option_set_if_audit_type_is_update() throws Exception {
        String filename = "organisation_unit_updated.json";

        givenMetadataDependencies();

        givenAExistedOrganisationUnitPreviously();

        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_update.json");

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

        metadataAuditListener.onMetadataChanged(OrganisationUnit.class, metadataAudit);

        assertThat(
                getOrganisationUnitWithParent(
                        getOrganisationUnitFromDatabase(metadataAudit.getUid())),
                is(getOrganisationUnitWithParent(parseExpected(filename).items().get(0))));
    }

    @Test
    public void delete_option_set_in_database_if_audit_type_is_delete() throws Exception {
        givenMetadataDependencies();
        givenAExistedOrganisationUnitPreviously();

        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_delete.json");

        metadataAuditListener.setMetadataSyncedListener(new MetadataSyncedListener() {
            @Override
            public void onSynced(SyncedMetadata syncedMetadata) {
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        });

        metadataAuditListener.onMetadataChanged(OrganisationUnit.class, metadataAudit);

        assertThat(organisationUnitFactory.getOrganisationUnitStore().queryByUid(
                metadataAudit.getUid()), is(nullValue()));
    }


    private void givenAExistedOrganisationUnitPreviously() throws IOException {
        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_create.json");
        metadataAuditListener.onMetadataChanged(OrganisationUnit.class, metadataAudit);
    }

    private void givenMetadataDependencies() throws Exception {
        givenAExistedUser();
        givenAExistedOrganisationUnitParent();
    }

    private void givenAExistedUser() throws Exception {
        dhis2MockServer.enqueueMockResponse("login.json", new Date());
        d2.logIn("user", "password").call();
    }

    private void givenAExistedOrganisationUnitParent() {
        OrganisationUnit organisationUnit = OrganisationUnit.builder().uid("ImspTQPwCqd")
                .displayName("Sierra Leone").code("OU_525").name("Sierra Leone")
                .created(parse("2012-11-13T12:20:53.028"))
                .lastUpdated(parse("2017-05-22T15:21:48.514")).build();

        organisationUnitFactory.getOrganisationUnitHandler().handleOrganisationUnit(
                organisationUnit,
                OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, "DXyJmlo9rge", new Date());
    }

    private OrganisationUnit getOrganisationUnitWithParent(OrganisationUnit organisationUnit) {
        OrganisationUnit organisationUnitParent =
                organisationUnitFactory.getOrganisationUnitStore().queryByUid("ImspTQPwCqd");
        if (organisationUnitParent != null) {
            organisationUnitParent = organisationUnitParent.toBuilder().deleted(null).level(
                    null).build();
        }
        organisationUnit = organisationUnit.toBuilder().deleted(null).parent(
                organisationUnitParent).build();
        return organisationUnit;
    }

    private OrganisationUnit getOrganisationUnitFromDatabase(String uid) {
        OrganisationUnit organisationUnit =
                organisationUnitFactory.getOrganisationUnitStore().queryByUid(uid);

        return organisationUnit;
    }

    private MetadataAudit<OrganisationUnit> givenAMetadataAudit(String fileName)
            throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, OrganisationUnit.class);
    }

    private Payload<OrganisationUnit> parseExpected(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, OrganisationUnit.class);
    }
}
