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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.CREATED;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeReservedValueManagerShould extends AbsStoreTestCase {

    private String organisationUnitUid = "org_unit_uid";
    private String ownerUid1 = "owner_uid1";
    private String ownerUid2 = "owner_uid2";

    // object to test
    private TrackedEntityAttributeReservedValueManager manager;

    @Before
    public void setUp() throws IOException {
        super.setUp();

        manager = TrackedEntityAttributeReservedValueManager.create(databaseAdapter());

        GenericHandler<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel> handler =
                TrackedEntityAttributeReservedValueHandler.create(databaseAdapter());

        List<TrackedEntityAttributeReservedValue> trackedEntityAttributeReservedValues = new ArrayList<>();
        TrackedEntityAttributeReservedValue reservedValue1 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid1, "key", "value1", CREATED, CREATED);
        TrackedEntityAttributeReservedValue reservedValue2 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid1, "key", "value2", CREATED, CREATED);
        TrackedEntityAttributeReservedValue reservedValue3 = TrackedEntityAttributeReservedValue.create(
                "owner_obj", ownerUid2, "key", "value3", CREATED, CREATED);
        trackedEntityAttributeReservedValues.add(reservedValue1);
        trackedEntityAttributeReservedValues.add(reservedValue2);
        trackedEntityAttributeReservedValues.add(reservedValue3);

        OrganisationUnit organisationUnit = OrganisationUnit.create(organisationUnitUid, "org_unit_code",
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null);

        handler.handleMany(trackedEntityAttributeReservedValues,
                new TrackedEntityAttributeReservedValueModelBuilder(organisationUnit));
    }

    @Test
    public void get_one_reserved_value() throws Exception {
        String value1 = manager.getValue(ownerUid1, organisationUnitUid);
        assertThat(value1, is("value1"));
    }

    @Test
    public void get_two_reserved_value() throws Exception {
        String value1 = manager.getValue(ownerUid1, organisationUnitUid);
        String value2 = manager.getValue(ownerUid1, organisationUnitUid);
        String value3 = manager.getValue(ownerUid2, organisationUnitUid);

        assertThat(value1, is("value1"));
        assertThat(value2, is("value2"));
        assertThat(value3, is("value3"));
    }

    @Test(expected = RuntimeException.class)
    public void throws_exception_when_no_more_values_reserved_value() throws Exception {
        manager.getValue(ownerUid1, organisationUnitUid);
        manager.getValue(ownerUid1, organisationUnitUid);
        manager.getValue(ownerUid1, organisationUnitUid);
    }

    @Test
    public void get_reserved_values() throws Exception {
        Set<TrackedEntityAttributeReservedValueModel> reservedValueModels =
                manager.getReservedValues(ownerUid1, organisationUnitUid);

        assertThat(reservedValueModels.size(), is(2));
    }
}