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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeReservedValueStoreIntegrationShould extends AbsStoreTestCase {

    private TrackedEntityAttributeReservedValueModel expiredValue;
    private TrackedEntityAttributeReservedValueModel notExpiredValue;

    private Date serverDate;

    // object to test
    private TrackedEntityAttributeReservedValueStoreInterface store;
    private IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = TrackedEntityAttributeReservedValueStore.create(databaseAdapter());

        serverDate = parseDate("2018-05-13T12:35:36.743");
        Date expiredDate = parseDate("2018-05-12T12:35:36.743");
        Date notExpiredDate = parseDate("2018-05-17T12:35:36.743");

        String orgUnitUid = "orgu1";
        OrganisationUnitModel organisationUnit = OrganisationUnitModel.builder().uid(orgUnitUid).build();
        organisationUnitStore = OrganisationUnitStore.create(databaseAdapter());
        organisationUnitStore.insert(organisationUnit);

        TrackedEntityAttributeReservedValueModel.Builder builder = TrackedEntityAttributeReservedValueModel.builder()
                .ownerObject("owObj")
                .ownerUid("owUid")
                .key("key")
                .value("val")
                .organisationUnit(orgUnitUid)
                .created(new Date());

        expiredValue = builder.expiryDate(expiredDate).build();
        notExpiredValue = builder.expiryDate(notExpiredDate).build();
    }

    @After
    public void tearDown() throws IOException {
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
    public void not_delete_not_expired_reserved_values() {
        store.insert(notExpiredValue);
        store.deleteExpired(serverDate);
        storeContains(notExpiredValue, true);
    }

    private void storeContains(TrackedEntityAttributeReservedValueModel value, Boolean contains) {
        Set<TrackedEntityAttributeReservedValueModel> values
                = store.selectAll(TrackedEntityAttributeReservedValueModel.factory);
        assertThat(values.contains(value), is(contains));
    }
}