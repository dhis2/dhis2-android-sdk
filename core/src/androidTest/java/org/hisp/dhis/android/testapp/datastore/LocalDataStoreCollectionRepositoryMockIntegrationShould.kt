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
package org.hisp.dhis.android.testapp.datastore

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class LocalDataStoreCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        Truth.assertThat(d2.dataStoreModule().localDataStore().blockingGet().size).isEqualTo(2)
    }

    @Test
    fun filter_by_key() {
        val pair = d2.dataStoreModule().localDataStore()
            .byKey().eq("key1")
            .one()
            .blockingGet()

        Truth.assertThat(pair!!.key()).isEqualTo("key1")
        Truth.assertThat(pair.value()).isEqualTo("value1")
    }

    @Test
    fun filter_by_value() {
        val pair = d2.dataStoreModule().localDataStore()
            .byValue().eq("value2")
            .one()
            .blockingGet()

        Truth.assertThat(pair!!.key()).isEqualTo("key2")
        Truth.assertThat(pair.value()).isEqualTo("value2")
    }

    @Test
    fun return_object_repository() {
        val objectRepository = d2.dataStoreModule().localDataStore()
            .value("key1")
        Truth.assertThat(objectRepository.blockingExists()).isTrue()
        Truth.assertThat(objectRepository.blockingGet()!!.value()).isEqualTo("value1")
    }
}