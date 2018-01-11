package org.hisp.dhis.android.core.deletedobject;


import static junit.framework.Assert.assertTrue;

import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
import org.hisp.dhis.android.core.common.CategoryCallFactory;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramStoreImpl;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.android.core.user.UserStore;
import org.hisp.dhis.android.core.user.UserStoreImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;

import retrofit2.Response;
import retrofit2.Retrofit;

public class DeletedObjectEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    @Mock
    UserStore userStore;
    @Mock
    CategoryStore categoryStore;
    @Mock
    CategoryComboStore categoryComboStore;
    @Mock
    CategoryOptionComboStore categoryOptionComboStore;
    @Mock
    OrganisationUnitStore organisationUnitStore;
    @Mock
    OptionSetStore optionSetStore;
    @Mock
    TrackedEntityStore trackedEntityStore;
    @Mock
    CategoryOptionStore categoryOptionStore;
    @Mock
    DataElementStore dataElementStore;
    @Mock
    OptionStore optionStore;
    @Mock
    ProgramIndicatorStore programIndicatorStore;
    @Mock
    ProgramRuleStore programRuleStore;
    @Mock
    ProgramRuleActionStore programRuleActionStore;
    @Mock
    ProgramRuleVariableStore programRuleVariableStore;
    @Mock
    ProgramStageStore programStageStore;
    @Mock
    ProgramStageDataElementStore programStageDataElementStore;
    @Mock
    ProgramStageSectionStore programStageSectionStore;
    @Mock
    ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    @Mock
    TrackedEntityAttributeStore trackedEntityAttributeStore;
    @Mock
    RelationshipTypeStore relationshipTypeStore;

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void persist_resource_date_after_call_deleted_object_endpoint_using_program() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        ProgramStoreImpl programStore = new ProgramStoreImpl(d2.databaseAdapter());
        DeletedObjectHandler deletedObjectHandler = new DeletedObjectHandler(userStore,
                categoryStore, categoryComboStore, categoryOptionComboStore,
                programStore, organisationUnitStore, optionSetStore, trackedEntityStore,
                categoryOptionStore, dataElementStore, optionStore, programIndicatorStore,
                programRuleStore,
                programRuleActionStore, programRuleVariableStore, programStageStore,
                programStageDataElementStore,
                programStageSectionStore, programTrackedEntityAttributeStore,
                trackedEntityAttributeStore,
                relationshipTypeStore);
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());
        //when
        Date date = new Date();
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService , d2.databaseAdapter(),
                resourceStore, deletedObjectHandler,
                date, Program.class.getSimpleName()).call();

        //then
        assertTrue(response.isSuccessful());

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM);
        assertPersistedDate(date, lastUpdated);
    }

    @Test
    @LargeTest
    public void call_deleted_object_endpoint_using_program() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        ProgramStoreImpl programStore = new ProgramStoreImpl(d2.databaseAdapter());
        DeletedObjectHandler deletedObjectHandler = new DeletedObjectHandler(userStore,
                categoryStore, categoryComboStore, categoryOptionComboStore,
                programStore, organisationUnitStore, optionSetStore, trackedEntityStore,
                categoryOptionStore, dataElementStore, optionStore, programIndicatorStore,
                programRuleStore,
                programRuleActionStore, programRuleVariableStore, programStageStore,
                programStageDataElementStore,
                programStageSectionStore, programTrackedEntityAttributeStore,
                trackedEntityAttributeStore,
                relationshipTypeStore);
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);

        //when
        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService , d2.databaseAdapter(),
                new ResourceStoreImpl(d2.databaseAdapter()),
                deletedObjectHandler, new Date(), Program.class.getSimpleName()).call();

        //then
        assertTrue(response.isSuccessful());
        assertTrue(hasDeletedObjects(response));
    }

    @Test
    @LargeTest
    public void call_deleted_object_endpoint_using_program_and_deleted_at_from_resources() throws Exception {
        //given
        Response responseLogIn = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(responseLogIn.isSuccessful()).isTrue();
        ProgramStoreImpl programStore = new ProgramStoreImpl(d2.databaseAdapter());
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(d2.databaseAdapter());
        CategoryComboStoreImpl categoryComboStore = new CategoryComboStoreImpl(d2.databaseAdapter());
        CategoryOptionComboStoreImpl categoryOptionComboStore = new CategoryOptionComboStoreImpl(d2.databaseAdapter());
        OrganisationUnitStoreImpl organisationUnitStore = new OrganisationUnitStoreImpl(d2.databaseAdapter());
        OptionSetStoreImpl optionSetStore = new OptionSetStoreImpl(d2.databaseAdapter());
        UserStoreImpl userStore = new UserStoreImpl(d2.databaseAdapter());
        TrackedEntityStoreImpl trackedEntityStore = new TrackedEntityStoreImpl(d2.databaseAdapter());
        CategoryOptionStoreImpl categoryOptionStore = new CategoryOptionStoreImpl(d2.databaseAdapter());
        DataElementStoreImpl dataElementStore = new DataElementStoreImpl(d2.databaseAdapter());
        OptionStoreImpl optionStore = new OptionStoreImpl(d2.databaseAdapter());



        DeletedObjectHandler deletedObjectHandler = new DeletedObjectHandler(userStore,
                categoryStore, categoryComboStore, categoryOptionComboStore,
                programStore, organisationUnitStore, optionSetStore, trackedEntityStore,
                categoryOptionStore, dataElementStore, optionStore, programIndicatorStore,
                programRuleStore,
                programRuleActionStore, programRuleVariableStore, programStageStore,
                programStageDataElementStore,
                programStageSectionStore, programTrackedEntityAttributeStore,
                trackedEntityAttributeStore,
                relationshipTypeStore);
        DeletedObjectService  deletedObjectService = d2.retrofit().create(DeletedObjectService.class);
        Date date = new Date();

        ResourceStoreImpl resourceStore = new ResourceStoreImpl(d2.databaseAdapter());

        Response<Payload<DeletedObject>> response = new DeletedObjectEndPointCall(
                deletedObjectService , d2.databaseAdapter(),
                resourceStore,
                deletedObjectHandler, date, Program.class.getSimpleName()).call();
        assertTrue(response.isSuccessful());
        assertTrue(hasDeletedObjects(response));

        String lastUpdated = resourceStore.getLastUpdated(ResourceModel.Type.DELETED_PROGRAM);
        assertPersistedDate(date, lastUpdated);

        response = new DeletedObjectEndPointCall(
                deletedObjectService , d2.databaseAdapter(),
                resourceStore,
                deletedObjectHandler, new Date(), Program.class.getSimpleName()).call();

        //then
        assertTrue(response.isSuccessful());
        assertTrue(hasEmptyDeleteObjects(response));
    }

    private boolean hasDeletedObjects(Response<Payload<DeletedObject>> response) {
        return !response.body().items().isEmpty();
    }

    private boolean hasEmptyDeleteObjects(Response<Payload<DeletedObject>> response) {
        return response.body().items().isEmpty();
    }

    private void assertPersistedDate(Date date, String lastUpdated) throws ParseException {
        SimpleDateFormat
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        Date lastUpdatedDate = simpleDateFormat.parse(lastUpdated);
        assertTrue(date.compareTo(lastUpdatedDate)==0);
    }
}
