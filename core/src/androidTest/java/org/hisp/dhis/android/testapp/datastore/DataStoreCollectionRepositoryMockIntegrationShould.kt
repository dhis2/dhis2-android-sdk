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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataStoreCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_namespace() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .byNamespace().eq("capture")
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(2)
    }

    @Test
    fun filter_by_key() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .byKey().eq("summary")
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(1)
    }

    @Test
    fun filter_by_value() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .byValue().isNotNull
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_sync_state() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(3)
    }

    @Test
    fun filter_by_sync_deleted() {
        val dataStoreEntries = d2.dataStoreModule().dataStore()
            .byDeleted().isFalse
            .blockingGet()

        assertThat(dataStoreEntries.size).isEqualTo(3)
    }

    /**
     * The tests above only count rows, which is why ANDROSDK-2376 stayed green for as long as it
     * did: an entry stored as `JsonWrapper(json={...})` is still a row, and is still non-null. The
     * tests below pin the downloaded value itself, end to end through the real download pipeline
     * the full dispatcher runs — response deserialization, [
     * org.hisp.dhis.android.network.datastore.DataStoreEntryDTO.toDomain] and the store.
     */
    @Test
    fun download_the_json_object_value_of_an_entry() {
        val value = d2.dataStoreModule().dataStore()
            .value("capture", "settings")
            .blockingGet()!!
            .value()

        assertThat(value).isEqualTo("""{"skipMigration":true}""")
    }

    @Test
    fun download_a_json_array_value_that_parses_back_as_json() {
        val value = d2.dataStoreModule().dataStore()
            .value("capture", "summary")
            .blockingGet()!!
            .value()!!

        assertThat(value).doesNotContain("JsonWrapper")

        val parsed = KotlinxJsonParser.instance.parseToJsonElement(value).jsonArray
        assertThat(parsed).hasSize(4)
        assertThat(parsed[0].jsonObject["id"]!!.jsonPrimitive.content).isEqualTo("fKHBpN5v8Sj")
    }

    @Test
    fun download_an_empty_json_object_value() {
        val value = d2.dataStoreModule().dataStore()
            .value("scorecard", "savedObjects")
            .blockingGet()!!
            .value()

        assertThat(value).isEqualTo("{}")
    }
}
