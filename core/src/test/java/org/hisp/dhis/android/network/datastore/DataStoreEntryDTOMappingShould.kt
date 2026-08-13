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
package org.hisp.dhis.android.network.datastore

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.hisp.dhis.android.network.common.JsonWrapper
import org.junit.Test

/**
 * `DataStoreEntry.value` is the caller-visible JSON of a dataStore entry, so this mapping is the
 * public contract of both dataStore read endpoints — `getNamespaceValues38` and
 * `getNamespaceKeyValue` each route through [DataStoreEntryDTO.toDomain].
 *
 * These tests pin that the stored value is the JSON payload itself. It previously stored the
 * `toString()` of the [org.hisp.dhis.android.network.common.JsonWrapper] value class, which
 * produced `JsonWrapper(json={...})` and could not be parsed by any consumer.
 */
class DataStoreEntryDTOMappingShould {

    private fun dtoOf(json: String) =
        DataStoreEntryDTO(key = "config", value = JsonWrapper(Json.parseToJsonElement(json)))

    @Test
    fun store_the_json_payload_and_not_the_wrapper_to_string() {
        val json = """{"plugins":[{"id":"org.myorg.my-plugin"}]}"""

        val entry = dtoOf(json).toDomain("dhis2AndroidPlugins")

        assertThat(entry.value()).isEqualTo(json)
        assertThat(entry.value()).doesNotContain("JsonWrapper")
    }

    @Test
    fun round_trip_a_json_object_unchanged() {
        val json = """{"a":1,"b":"two","c":[1,2,3],"d":{"e":true},"f":null}"""

        val entry = dtoOf(json).toDomain("ns")

        assertThat(entry.value()).isEqualTo(json)
    }

    @Test
    fun preserve_a_json_array_at_the_root() {
        val json = """[{"k":"v"},{"k":"w"}]"""

        assertThat(dtoOf(json).toDomain("ns").value()).isEqualTo(json)
    }

    @Test
    fun preserve_a_scalar_value() {
        assertThat(dtoOf("42").toDomain("ns").value()).isEqualTo("42")
        assertThat(dtoOf("\"text\"").toDomain("ns").value()).isEqualTo("\"text\"")
        assertThat(dtoOf("true").toDomain("ns").value()).isEqualTo("true")
    }

    @Test
    fun map_a_null_value_to_null_rather_than_the_string_null() {
        // The previous implementation called toString() on a nullable wrapper, storing the
        // four-character string "null" and making a genuinely absent value indistinguishable
        // from a JSON null.
        val entry = DataStoreEntryDTO(key = "config", value = null).toDomain("ns")

        assertThat(entry.value()).isNull()
    }

    @Test
    fun keep_the_namespace_and_key_it_was_given() {
        val entry = dtoOf("{}").toDomain("dhis2AndroidPlugins")

        assertThat(entry.namespace()).isEqualTo("dhis2AndroidPlugins")
        assertThat(entry.key()).isEqualTo("config")
    }

    @Test
    fun produce_a_value_that_parses_back_as_json() {
        // The whole point: a consumer must be able to deserialize what we stored.
        val json = """{"plugins":[{"id":"org.a","version":"1"}]}"""

        val stored = dtoOf(json).toDomain("ns").value()

        // Would throw if the stored text were not valid JSON.
        Json.parseToJsonElement(stored!!)
    }
}
