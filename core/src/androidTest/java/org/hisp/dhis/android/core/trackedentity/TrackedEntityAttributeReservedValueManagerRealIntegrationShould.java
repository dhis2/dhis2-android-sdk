/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.FUTURE_DATE;

public class TrackedEntityAttributeReservedValueManagerRealIntegrationShould extends AbsStoreTestCase {

    private TrackedEntityAttributeReservedValueStoreInterface store;
    private String organisationUnitUid = "org_unit_uid";
    private String ownerUid = "xs8A6tQJY0s";
    private D2 d2;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        login();

        store = TrackedEntityAttributeReservedValueStore.create(databaseAdapter());
        IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore =
                OrganisationUnitStore.create(databaseAdapter());
        TrackedEntityAttributeStore trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter());

        GenericHandler<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel> handler =
                TrackedEntityAttributeReservedValueHandler.create(databaseAdapter());

        List<TrackedEntityAttributeReservedValue> trackedEntityAttributeReservedValues = new ArrayList<>();
        TrackedEntityAttributeReservedValue reservedValue1 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid, "key", "value1", CREATED, FUTURE_DATE);
        TrackedEntityAttributeReservedValue reservedValue2 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid, "key", "value2", CREATED, FUTURE_DATE);
        TrackedEntityAttributeReservedValue reservedValue3 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid, "key", "value3", CREATED, FUTURE_DATE);
        trackedEntityAttributeReservedValues.add(reservedValue1);
        trackedEntityAttributeReservedValues.add(reservedValue2);
        trackedEntityAttributeReservedValues.add(reservedValue3);

        OrganisationUnitModel organisationUnit = OrganisationUnitModel.builder()
                .uid(organisationUnitUid).code("org_unit_code").build();

        organisationUnitStore.insert(organisationUnit);

        String pattern = "CURRENT_DATE(YYYYMM) + \"-\" + CURRENT_DATE(ww)";
        trackedEntityAttributeStore.insert(ownerUid, null, null, null, null, null,
                null, null, null, null, pattern, null,
                null, null, null, null, null, null,
                null, null, null, null, null);

        handler.handleMany(trackedEntityAttributeReservedValues,
                new TrackedEntityAttributeReservedValueModelBuilder(organisationUnit, ""));
    }

    //@Test
    public void get_one_reserved_value() throws D2CallException {
        String value1 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(value1, is("value1"));
    }

    //@Test
    public void get_two_reserved_value() throws D2CallException {
        String value1 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        String value2 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        String value3 = d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);

        assertThat(value1, is("value1"));
        assertThat(value2, is("value2"));
        assertThat(value3, is("value3"));
    }

    //@Test
    public void get_reserved_values() {
        assertThat(selectAll().size(), is(3));
    }

    //@Test
    public void sync_reserved_values() {
        d2.syncTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(100));
    }

    //@Test
    public void sync_pop_sync_again_and_have_100_reserved_values() throws D2CallException {
        d2.syncTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(100));
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
        d2.syncTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(100));
    }

    //@Test
    public void reserve_100_new_values_and_take_one() throws D2CallException {
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(99));
    }

    //@Test
    public void have_98_values_after_sync_and_take_two() throws D2CallException {
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        d2.popTrackedEntityAttributeReservedValue(ownerUid, organisationUnitUid);
        assertThat(selectAll().size(), is(98));
    }

    private Set<TrackedEntityAttributeReservedValueModel> selectAll() {
        return store.selectAll(TrackedEntityAttributeReservedValueModel.factory);
    }

    private void login() {
        try {
            if (!d2.isUserLoggedIn().call()) {
                d2.logIn(RealServerMother.user, RealServerMother.password).call();
            }
        } catch (Exception ignored) {
        }
    }
}