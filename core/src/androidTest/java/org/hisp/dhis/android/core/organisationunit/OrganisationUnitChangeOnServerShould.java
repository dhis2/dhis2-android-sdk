package org.hisp.dhis.android.core.organisationunit;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserHandler;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreImpl;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class OrganisationUnitChangeOnServerShould extends AbsStoreTestCase {

    @Mock
    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    private OrganisationUnitStore organisationUnitStore;
    private OrganisationUnitHandler organisationUnitHandler;
    private UserHandler userHandler;
    private AuthenticatedUserStoreImpl authenticatedUserStore;
    private MetadataAuditListener metadataAuditListener;

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Before
    public void setup() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        MockitoAnnotations.initMocks(this);

        when(metadataAuditHandlerFactory.getByClass(any(Class.class))).thenReturn(
                new OrganisationUnitMetadataAuditHandler(
                        new OrganisationUnitFactory(d2.retrofit(), databaseAdapter(),
                                HandlerFactory.createResourceHandler(databaseAdapter()))));

        organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter());
        UserOrganisationUnitLinkStore userOrganisationStore = new UserOrganisationUnitLinkStoreImpl(databaseAdapter());
        OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore = new OrganisationUnitProgramLinkStoreImpl(databaseAdapter());
        organisationUnitStore = new OrganisationUnitStoreImpl(databaseAdapter());
        organisationUnitHandler = new OrganisationUnitHandler(organisationUnitStore,
                userOrganisationStore, organisationUnitProgramLinkStore);
        UserStore userStore = new UserStoreImpl(databaseAdapter());
        userHandler = new UserHandler(userStore);
        metadataAuditListener = new MetadataAuditListener(metadataAuditHandlerFactory);
        authenticatedUserStore = new AuthenticatedUserStoreImpl(databaseAdapter());
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

        assertThat(getOrganisationUnit(organisationUnitStore.queryByUid(metadataAudit.getUid())), is(metadataAudit.getValue()));
    }

    @Test
    public void delete_option_set_in_database_if_audit_type_is_delete() throws Exception {
        givenMetadataDependencies();

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

        assertThat(organisationUnitStore.queryByUid(metadataAudit.getUid()), is(nullValue()));
    }

    @Test
    public void update_option_set_if_audit_type_is_update() throws Exception {
        String filename = "audit/organisation_units.json";

        givenAExistedOrganisationUnitPreviously();

        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_create.json");

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

        assertThat(getOptionSet(metadataAudit.getUid()), is(parseExpected(
                filename).items().get(0)));
    }


    private void givenMetadataDependencies() {
        givenAExistedUser();
        givenAExistedOrganisationUnitParent();
    }

    private void givenAExistedOrganisationUnitParent() {
        OrganisationUnit organisationUnit = OrganisationUnit.builder().uid("ImspTQPwCqd")
                .displayName("Sierra Leone").code("OU_525").name("Sierra Leone")
                .created(parse("2012-11-13T12:20:53.028"))
                .lastUpdated(parse("2017-05-22T15:21:48.514")).build();

        organisationUnitHandler.handleOrganisationUnit(organisationUnit,
                OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, "xE7jOejl9FI");
    }

    private OrganisationUnit getOrganisationUnit(OrganisationUnit organisationUnit) {
        OrganisationUnit organisationUnitParent = organisationUnitStore.queryByUid("ImspTQPwCqd");
        if(organisationUnitParent!=null) {
            organisationUnitParent = organisationUnitParent.toBuilder().deleted(null).level(
                    null).build();
        }
        organisationUnit = organisationUnit.toBuilder().deleted(null).parent(organisationUnitParent).build();
        return organisationUnit;
    }

    private void givenAExistedUser() {
        User user = User.builder().uid("xE7jOejl9FI")
                .userCredentials(UserCredentials.builder().uid("creedentialuid")
                        .build()).build();
        userHandler.handleUser(user);
        authenticatedUserStore.insert("xE7jOejl9FI", "");
    }

    private OrganisationUnit getOptionSet(String uid) {
        OrganisationUnit organisationUnit = organisationUnitStore.queryByUid(uid);

        return organisationUnit;
    }
    private MetadataAudit<OrganisationUnit> givenAMetadataAudit(String fileName) throws IOException {
        AssetsFileReader assetsFileReader = new AssetsFileReader();

        String json = assetsFileReader.getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, MetadataAudit.class, OrganisationUnit.class);
    }

    private void givenAExistedOrganisationUnitPreviously() throws IOException {
        MetadataAudit<OrganisationUnit> metadataAudit =
                givenAMetadataAudit("audit/organisation_unit_create.json");
        metadataAuditListener.onMetadataChanged(OrganisationUnit.class, metadataAudit);
    }

    private Payload<OrganisationUnit> parseExpected(String fileName) throws IOException {
        String json = new AssetsFileReader().getStringFromFile(fileName);

        GenericClassParser parser = new GenericClassParser();

        return parser.parse(json, Payload.class, OrganisationUnit.class);
    }
}
