/*
 *  Copyright (c) 2004-2025, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.datastore.LocalDataStoreObjectRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class LocalDataStoreObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun update_value() {
        val value = "new_value"
        val repository = objectRepository()

        repository.blockingSet(value)
        assertThat(repository.blockingGet()!!.value()).isEqualTo(value)

        repository.blockingDelete()
    }

    @Test
    fun delete_value() {
        val repository = objectRepository()

        repository.blockingSet("value")
        assertThat(repository.blockingExists()).isTrue()
        repository.blockingDelete()
        assertThat(repository.blockingExists()).isFalse()
    }

    @Test
    fun return_that_a_value_exists_only_if_it_has_been_created() {
        assertThat(d2.dataStoreModule().localDataStore().value("no_key").blockingExists()).isFalse()
        assertThat(d2.dataStoreModule().localDataStore().value("key1").blockingExists()).isTrue()
    }

    private fun objectRepository(): LocalDataStoreObjectRepository {
        return d2.dataStoreModule().localDataStore().value("new_key")
    }
}
