package org.hisp.dhis.android.core.trackedentity;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeReservedValueEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private TrackedEntityAttributeReservedValueEndpointCall reservedValueEndpointCall;
    private Integer numberToReserve = 5;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
        reservedValueEndpointCall = createCall();
    }

    private TrackedEntityAttributeReservedValueEndpointCall createCall() {
        GenericCallData data = GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date());

        OrganisationUnitModel organisationUnit =  OrganisationUnitModel.builder()
                .uid("orgUnitUid").code("ORG_UNIT").build();

        return TrackedEntityAttributeReservedValueEndpointCall.FACTORY.create(data, "xs8A6tQJY0s",
                numberToReserve, organisationUnit);
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

        Set<TrackedEntityAttributeReservedValueModel> reservedValues = TrackedEntityAttributeReservedValueStore.create(
                databaseAdapter()).selectAll(TrackedEntityAttributeReservedValueModel.factory);

        assertThat(reservedValues.size()).isEqualTo(numberToReserve);
    }

    private void login() throws Exception {
        if (!d2.isUserLoggedIn().call()) {
            d2.logIn(RealServerMother.user, RealServerMother.password).call();
        }
    }

    @Test
    public void stub() {
    }
}