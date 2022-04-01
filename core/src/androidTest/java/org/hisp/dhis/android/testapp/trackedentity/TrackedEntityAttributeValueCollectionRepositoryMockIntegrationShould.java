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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RunWith(D2JunitRunner.class)
public class TrackedEntityAttributeValueCollectionRepositoryMockIntegrationShould
        extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_all_tracked_entity_data_values() {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues().blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_tracked_entity_attribute() {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityAttribute().eq("cejWyOfXge6")
                .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_value() {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                .byValue().eq("4081507")
                .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_created() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.000");
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                .byCreated().eq(date)
                .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2018-01-10T13:40:28.000");
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                .byLastUpdated().eq(date)
                .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                        .byTrackedEntityInstance().eq("nWrB0TfWlvh")
                        .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_deleted() throws D2Error {
        TrackedEntityAttributeValueObjectRepository repository = d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("cejWyOfXge6", "nWrB0TfWlvh");

        TrackedEntityAttributeValue value = repository.blockingGet();

        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                d2.trackedEntityModule().trackedEntityAttributeValues()
                        .byDeleted().isTrue()
                        .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(0);

        repository.blockingDelete();

        trackedEntityAttributeValues = d2.trackedEntityModule().trackedEntityAttributeValues()
                        .byDeleted().isTrue()
                        .blockingGet();
        assertThat(trackedEntityAttributeValues.size()).isEqualTo(1);

        repository.blockingSet(value.value());
    }

    @Test
    public void return_tracked_entity_attribute_value_object_repository() {
        TrackedEntityAttributeValueObjectRepository objectRepository = d2.trackedEntityModule().trackedEntityAttributeValues()
                .value("cejWyOfXge6", "nWrB0TfWlvh");
        assertThat(objectRepository.blockingExists()).isEqualTo(Boolean.TRUE);
        assertThat(objectRepository.blockingGet().value()).isEqualTo("4081507");
    }
}