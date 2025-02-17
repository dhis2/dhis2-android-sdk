/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.datastore.internal

import com.google.common.truth.Truth.assertThat
import io.ktor.http.HttpStatusCode
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataStorePostCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun post_dataStore_success() {
        // Given user sets valid value
        dhis2MockServer.enqueueMockResponse(
            code = HttpStatusCode.Created.value,
            fileName = "datastore/actions/namespace_key_post_201.json",
        )
        provideDataStore("config", "key", "{\"enabled\": true}")

        assertExistWithState(State.TO_POST)
        d2.dataStoreModule().dataStore().blockingUpload()
        assertExistWithState(State.SYNCED)
    }

    @Test
    fun put_if_already_exists() {
        dhis2MockServer.enqueueMockResponse(
            code = HttpStatusCode.Conflict.value,
            fileName = "datastore/actions/namespace_key_post_already_exists_409.json",
        )
        dhis2MockServer.enqueueMockResponse("datastore/actions/namespace_key_put_200.json")
        provideDataStore("config", "key", "{\"enabled\": true}")

        assertExistWithState(State.TO_POST)
        d2.dataStoreModule().dataStore().blockingUpload()
        assertExistWithState(State.SYNCED)
    }

    @Test
    fun post_if_not_found() {
        dhis2MockServer.enqueueMockResponse(
            code = HttpStatusCode.NotFound.value,
            fileName = "datastore/actions/namespace_key_put_not_found_404.json",
        )
        dhis2MockServer.enqueueMockResponse(
            code = HttpStatusCode.Created.value,
            fileName = "datastore/actions/namespace_key_post_201.json",
        )
        provideDataStore("config", "key", "{\"enabled\": true}")
        setState(State.TO_UPDATE)

        assertExistWithState(State.TO_UPDATE)
        d2.dataStoreModule().dataStore().blockingUpload()
        assertExistWithState(State.SYNCED)
    }

    @Test
    fun delete_locally() {
        dhis2MockServer.enqueueMockResponse("datastore/actions/namespace_key_deleted_200.json")
        provideDataStore("config", "key", "{\"enabled\": true}")
        setState(State.TO_UPDATE)
        d2.dataStoreModule().dataStore().value("config", "key").blockingDelete()

        assertExistWithState(State.TO_UPDATE)
        d2.dataStoreModule().dataStore().blockingUpload()
        assertThat(d2.dataStoreModule().dataStore().blockingIsEmpty()).isTrue()
    }

    @Test
    fun delete_locally_even_if_not_found() {
        dhis2MockServer.enqueueMockResponse(
            code = HttpStatusCode.NotFound.value,
            fileName = "datastore/actions/namespace_key_deleted_not_found_404.json",
        )
        provideDataStore("config", "key", "{\"enabled\": true}")
        setState(State.TO_UPDATE)
        d2.dataStoreModule().dataStore().value("config", "key").blockingDelete()

        assertExistWithState(State.TO_UPDATE)
        d2.dataStoreModule().dataStore().blockingUpload()
        assertThat(d2.dataStoreModule().dataStore().blockingIsEmpty()).isTrue()
    }

    private fun assertExistWithState(state: State) {
        val entries = d2.dataStoreModule().dataStore().blockingGet()
        assertThat(entries.size).isEqualTo(1)
        assertThat(entries[0].syncState()).isEqualTo(state)
    }

    private fun provideDataStore(namespace: String, key: String, value: String) {
        d2.dataStoreModule().dataStore()
            .value(namespace, key)
            .blockingSet(value)
    }

    private fun setState(state: State) {
        val entries = d2.dataStoreModule().dataStore().blockingGet()

        val store = DataStoreEntryStoreImpl(databaseAdapter)
        entries.forEach {
            store.setState(it, state)
        }
    }
}
