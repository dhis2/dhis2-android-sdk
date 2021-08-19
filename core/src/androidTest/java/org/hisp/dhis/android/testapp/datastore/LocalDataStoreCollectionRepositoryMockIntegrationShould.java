/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.testapp.datastore;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.datastore.KeyValuePair;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(D2JunitRunner.class)
public class LocalDataStoreCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        add_values();
        assertThat(d2.localDataStoreModule().localDataStore().blockingGet().size()).isEqualTo(2);
    }

    @Test
    public void filter_by_key() {
        add_values();
        KeyValuePair pair = d2.localDataStoreModule().localDataStore()
                .byKey().eq("key1")
                .one()
                .blockingGet();

        assertThat(pair.key()).isEqualTo("key1");
        assertThat(pair.value()).isEqualTo("value1");
    }

    @Test
    public void filter_by_value() {
        add_values();
        KeyValuePair pair = d2.localDataStoreModule().localDataStore()
                .byValue().eq("value2")
                .one()
                .blockingGet();

        assertThat(pair.key()).isEqualTo("key2");
        assertThat(pair.value()).isEqualTo("value2");
    }

    private void add_values() {
        try {
            d2.localDataStoreModule().localDataStore().blockingAdd(
                    KeyValuePair.builder()
                            .key("key1")
                            .value("value1")
                            .build());
            d2.localDataStoreModule().localDataStore().blockingAdd(
                    KeyValuePair.builder()
                            .key("key2")
                            .value("value2")
                            .build());
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }
        assertThat(d2.localDataStoreModule().localDataStore().blockingGet().size()).isEqualTo(2);
    }
}