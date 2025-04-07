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

package org.hisp.dhis.android.testapp.trackedentity;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceObjectRepository;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(D2JunitRunner.class)
public class TrackedEntityAttributeValueObjectRepositoryMockIntegrationShould
        extends BaseMockIntegrationTestFullDispatcher {

    TrackedEntityInstanceObjectRepository teiRepository;
    TrackedEntityAttributeValueObjectRepository attributeRepository;
    String attribute = "aejWyOfXge6";

    @Before
    public void setup() throws D2Error {
        TrackedEntityInstanceCreateProjection projection = TrackedEntityInstanceCreateProjection
                .create("DiszpKrYNg8", "nEenWmSyUEp");

        String uid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(projection);
        attributeRepository = objectRepository(uid);
        teiRepository = d2.trackedEntityModule().trackedEntityInstances().uid(uid);
    }

    @After
    public void teardown() throws D2Error {
        teiRepository.blockingDelete();
    }

    @Test
    public void create_value() throws D2Error {
        String value1 = "new_value";
        String value2 = "other_value";

        attributeRepository.blockingSet(value1);
        assertThat(attributeRepository.blockingGet().value()).isEqualTo(value1);
        assertThat(attributeRepository.blockingGet().syncState()).isEqualTo(State.TO_POST);

        attributeRepository.blockingSet(value2);
        assertThat(attributeRepository.blockingGet().value()).isEqualTo(value2);
        assertThat(attributeRepository.blockingGet().syncState()).isEqualTo(State.TO_POST);
    }

    @Test
    public void update_value() throws D2Error {
        String value1 = "new_value";
        String value2 = "other_value";

        attributeRepository.blockingSet(value1);
        assertThat(attributeRepository.blockingGet().value()).isEqualTo(value1);
        assertThat(attributeRepository.blockingGet().syncState()).isEqualTo(State.TO_POST);

        TrackedEntityAttributeValue value = attributeRepository.blockingGet();
        setDataValueState(value, State.ERROR);

        attributeRepository.blockingSet(value2);
        assertThat(attributeRepository.blockingGet().value()).isEqualTo(value2);
        assertThat(attributeRepository.blockingGet().syncState()).isEqualTo(State.TO_UPDATE);
    }

    @Test
    public void delete_value() throws D2Error {
        attributeRepository.blockingSet("value");
        assertThat(attributeRepository.blockingExists()).isEqualTo(Boolean.TRUE);

        attributeRepository.blockingDelete();
        assertThat(attributeRepository.blockingExists()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("no_attribute", "no_instance").blockingExists()).isEqualTo(Boolean.FALSE);

        assertThat(d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("cejWyOfXge6", "nWrB0TfWlvh").blockingExists()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void mark_a_value_as_deleted_using_the_delete_method() throws D2Error {
        attributeRepository.blockingSet("value");
        attributeRepository.blockingDelete();
        assertThat(attributeRepository.blockingExists()).isEqualTo(false);
        assertThat(attributeRepository.blockingGet()).isNull();
    }

    @Test
    public void mark_a_value_as_deleted_when_setting_a_null() throws D2Error {
        attributeRepository.blockingSet("value");
        attributeRepository.blockingSet(null);
        assertThat(attributeRepository.blockingExists()).isEqualTo(false);
        assertThat(attributeRepository.blockingGet().value()).isEqualTo(null);
        assertThat(attributeRepository.blockingGet().deleted()).isEqualTo(true);
    }

    private TrackedEntityAttributeValueObjectRepository objectRepository(String teiUid) {
        return d2.trackedEntityModule().trackedEntityAttributeValues()
                .value(attribute, teiUid);
    }

    static void setDataValueState(TrackedEntityAttributeValue value, State syncState) {
        TrackedEntityAttributeValueStore store = new TrackedEntityAttributeValueStoreImpl(databaseAdapter);

        store.updateWhere(
                value.toBuilder().syncState(syncState).build()
        );
    }
}
