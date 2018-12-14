package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeReservedValueEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Call<List<TrackedEntityAttributeReservedValue>> reservedValueEndpointCall;
    private Integer numberToReserve = 5;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        reservedValueEndpointCall = createCall();
    }

    private Call<List<TrackedEntityAttributeReservedValue>> createCall() {
        OrganisationUnit organisationUnit =  OrganisationUnit.builder()
                .uid("orgUnitUid").code("ORG_UNIT").build();

        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(databaseAdapter());
        return new TrackedEntityAttributeReservedValueEndpointCallFactory(getGenericCallData(d2), apiCallExecutor).create(
                TrackedEntityAttributeReservedValueQuery.create("xs8A6tQJY0s",
                numberToReserve, organisationUnit, "pattern"));
    }

    // @Test
    public void download_reserved_values() throws Exception {
        login();
        reservedValueEndpointCall.call();
    }

    // @Test
    public void download_and_persist_reserved_values() throws Exception {
        login();
        reservedValueEndpointCall.call();

        List<TrackedEntityAttributeReservedValue> reservedValues = TrackedEntityAttributeReservedValueStore.create(
                databaseAdapter()).selectAll();

        assertThat(reservedValues.size()).isEqualTo(numberToReserve);
    }

    private void login() throws Exception {
        if (!d2.userModule().isLogged().call()) {
            d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();
        }
    }

    @Test
    public void stub() {
    }
}