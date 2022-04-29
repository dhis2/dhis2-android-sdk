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

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class TrackedEntityDataValueObjectRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void update_value() throws D2Error {
        String value = "new_value";

        TrackedEntityDataValueObjectRepository repository = objectRepository();

        repository.blockingSet(value);
        assertThat(repository.blockingGet().value()).isEqualTo(value);

        repository.blockingDelete();
    }

    @Test
    public void delete_value() throws D2Error {
        TrackedEntityDataValueObjectRepository repository = objectRepository();

        repository.blockingSet("value");
        assertThat(repository.blockingExists()).isEqualTo(Boolean.TRUE);
        repository.blockingDelete();
        assertThat(repository.blockingExists()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(d2.trackedEntityModule().trackedEntityDataValues()
                .value("no_event", "no_data_element").blockingExists()).isEqualTo(Boolean.FALSE);

        List<TrackedEntityDataValue> d = d2.trackedEntityModule().trackedEntityDataValues().blockingGet();

        assertThat(d2.trackedEntityModule().trackedEntityDataValues()
                .value("single1", "jDx8LZlznYu").blockingExists()).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void mark_a_value_as_deleted_using_the_delete_method() throws D2Error {
        TrackedEntityDataValueObjectRepository repository = objectRepository();

        repository.blockingSet("value");
        repository.blockingDelete();
        assertValueIsDeleted(repository);
    }

    @Test
    public void mark_a_value_as_deleted_when_setting_a_null() throws D2Error {
        TrackedEntityDataValueObjectRepository repository = objectRepository();

        repository.blockingSet("value");
        repository.blockingSet(null);
        assertValueIsDeleted(repository);
    }

    private void assertValueIsDeleted(TrackedEntityDataValueObjectRepository objectRepository) throws D2Error {
        assertThat(objectRepository.blockingExists()).isEqualTo(false);
        assertThat(objectRepository.blockingGet().value()).isEqualTo(null);
        assertThat(objectRepository.blockingGet().deleted()).isEqualTo(true);
        objectRepository.blockingSet("1");
        assertThat(objectRepository.blockingGet().deleted()).isEqualTo(false);
        objectRepository.blockingSet(null);
    }

    private TrackedEntityDataValueObjectRepository objectRepository() {
        return d2.trackedEntityModule().trackedEntityDataValues().value("event1", "bx6fsa0t90x");
    }
}