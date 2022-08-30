/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.BaseIntegrationTestWithDatabase;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeReservedValueStoreIntegrationShould extends BaseIntegrationTestWithDatabase {

    private TrackedEntityAttributeReservedValue expiredValue;
    private TrackedEntityAttributeReservedValue notExpiredValue;
    private TrackedEntityAttributeReservedValue temporalValidityExpiredValue;
    private TrackedEntityAttributeReservedValue notExpiredTemporalValidityExpiredValue;

    private Date serverDate;
    private final String orgUnitUid = "orgu1";
    private final String ownerUid = "owUid";

    // object to test
    private TrackedEntityAttributeReservedValueStoreInterface store;
    private IdentifiableObjectStore<OrganisationUnit> organisationUnitStore;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = TrackedEntityAttributeReservedValueStore.create(databaseAdapter());

        serverDate = parseDate("2018-05-13T12:35:36.743");
        Date expiredDate = parseDate("2018-05-12T12:35:36.743");
        Date notExpiredDate = parseDate("2018-05-17T12:35:36.743");

        OrganisationUnit organisationUnit = OrganisationUnit.builder().uid(orgUnitUid).build();
        organisationUnitStore = OrganisationUnitStore.create(databaseAdapter());
        organisationUnitStore.insert(organisationUnit);

        TrackedEntityAttributeReservedValue.Builder builder = TrackedEntityAttributeReservedValue.builder()
                .ownerObject("owObj")
                .ownerUid(ownerUid)
                .key("key")
                .organisationUnit(orgUnitUid)
                .created(new Date());

        expiredValue = builder.expiryDate(expiredDate).temporalValidityDate(null).value("v1").build();
        notExpiredValue = builder.id(1L).expiryDate(notExpiredDate).temporalValidityDate(null).value("v2").build();
        temporalValidityExpiredValue = builder.expiryDate(notExpiredDate).temporalValidityDate(expiredDate).value("v3")
                .build();
        notExpiredTemporalValidityExpiredValue = builder.id(1L).expiryDate(notExpiredDate).temporalValidityDate(notExpiredDate)
                .value("v3").build();
    }

    @After
    public void tearDown() {
        store.delete();
        organisationUnitStore.delete();
        super.tearDown();
    }

    @Test
    public void delete_expired_reserved_values() {
        store.insert(expiredValue);
        store.deleteExpired(serverDate);
        storeContains(expiredValue, false);
    }

    @Test
    public void delete_temporal_validity_expired_reserved_values() {
        store.insert(temporalValidityExpiredValue);
        store.deleteExpired(serverDate);
        storeContains(temporalValidityExpiredValue, false);
    }

    @Test
    public void not_delete_temporal_validity_not_expired_reserved_values() {
        store.insert(notExpiredTemporalValidityExpiredValue);
        store.deleteExpired(serverDate);
        storeContains(notExpiredTemporalValidityExpiredValue, true);
    }

    @Test
    public void not_delete_not_expired_reserved_values() {
        store.insert(notExpiredValue);
        store.deleteExpired(serverDate);
        storeContains(notExpiredValue, true);
    }

    @Test
    public void pop_inserted_value() {
        store.insert(notExpiredValue);
        TrackedEntityAttributeReservedValue returnedValue = store.popOne(ownerUid, orgUnitUid);
        assertThat(returnedValue.value()).isEqualTo(notExpiredValue.value());
    }

    @Test
    public void leave_store_empty_after_pop_only_value() {
        store.insert(notExpiredValue);
        TrackedEntityAttributeReservedValue value = store.popOne(ownerUid, orgUnitUid);
        storeContains(value, false);
    }

    private void storeContains(TrackedEntityAttributeReservedValue value, Boolean contains) {
        List<TrackedEntityAttributeReservedValue> values = store.selectAll();
        assertThat(values.contains(value)).isEqualTo(contains);
    }
}