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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.internal.DataStoreEntryStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataStoreEntryObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun add_new_key() {
        val value = "new_value"
        val repository = d2.dataStoreModule().dataStoreEntries()
            .value("settings", "new_key")
        repository.blockingSet(value)

        assertThat(repository.blockingGet().value()).isEqualTo(value)
        repository.blockingDelete()
    }

    @Test
    fun add_new_namespace() {
        val value = "new_value"
        val repository = d2.dataStoreModule().dataStoreEntries()
            .value("new_namespace", "new_key")
        repository.blockingSet(value)

        assertThat(repository.blockingGet().value()).isEqualTo(value)
        repository.blockingDelete()
    }

    @Test
    fun delete_value_if_state_to_post() {
        val repository = d2.dataStoreModule().dataStoreEntries()
            .value("new_namespace", "new_key")
        repository.blockingSet("new_value")

        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet().syncState()).isEqualTo(State.TO_POST)

        repository.blockingDelete()
        assertThat(repository.blockingExists()).isFalse()
    }

    @Test
    fun set_state_to_delete_if_state_is_not_to_post() {
        val repository = d2.dataStoreModule().dataStoreEntries()
            .value("new_namespace", "new_key")
        repository.blockingSet("value")

        DataStoreEntryStoreImpl.create(databaseAdapter)
            .updateWhere(repository.blockingGet().toBuilder().syncState(State.ERROR).build())

        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet().syncState()).isEqualTo(State.ERROR)

        repository.blockingDelete()
        assertThat(repository.blockingExists()).isTrue()
        assertThat(repository.blockingGet().deleted()).isTrue()
        assertThat(repository.blockingGet().syncState()).isEqualTo(State.TO_UPDATE)

        DataStoreEntryStoreImpl.create(databaseAdapter)
            .deleteWhere(repository.blockingGet())
    }

    @Test
    fun set_not_deleted_when_updating_deleted_value() {
        val repository = d2.dataStoreModule().dataStoreEntries()
            .value("new_namespace", "new_key")
        repository.blockingSet("value")

        DataStoreEntryStoreImpl.create(databaseAdapter)
            .updateWhere(repository.blockingGet().toBuilder().syncState(State.TO_UPDATE).build())

        repository.blockingDelete()

        assertThat(repository.blockingGet().deleted()).isTrue()
        assertThat(repository.blockingGet().syncState()).isEqualTo(State.TO_UPDATE)

        repository.blockingSet("new_value")
        assertThat(repository.blockingGet().deleted()).isFalse()
        assertThat(repository.blockingGet().syncState()).isEqualTo(State.TO_UPDATE)

        DataStoreEntryStoreImpl.create(databaseAdapter)
            .deleteWhere(repository.blockingGet())
    }
}
