/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.persistence.configuration

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DatabaseAccountDBShould {

    private val json = Json { encodeDefaults = true }

    private fun account(authorizationType: AuthorizationType?): DatabaseAccount =
        DatabaseAccount.builder()
            .username("user")
            .serverUrl("https://dhis2.org")
            .databaseName("user.db")
            .encrypted(false)
            .databaseCreationDate(DateUtils.DATE_FORMAT.parse(DATE))
            .lastAccessDate(DateUtils.DATE_FORMAT.parse(DATE))
            .authorizationType(authorizationType)
            .build()

    @Test
    fun persist_basic_authorization_type_through_toDB_conversion() {
        val db = account(AuthorizationType.BASIC).toDB()

        assertThat(db.authorizationType).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun persist_open_id_connect_authorization_type_through_toDB_conversion() {
        val db = account(AuthorizationType.OPEN_ID_CONNECT).toDB()

        assertThat(db.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }

    @Test
    fun persist_oauth2_authorization_type_through_toDB_conversion() {
        val db = account(AuthorizationType.OAUTH2).toDB()

        assertThat(db.authorizationType).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun default_to_basic_authorization_type_when_persisting_null() {
        val db = account(null).toDB()

        assertThat(db.authorizationType).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun read_authorization_type_from_db_when_calling_toDomain() {
        val db = account(AuthorizationType.OAUTH2).toDB()

        val restored = db.toDomain()

        assertThat(restored.authorizationType()).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun preserve_authorization_type_across_full_serialization_roundtrip() {
        AuthorizationType.values().forEach { type ->
            val db = account(type).toDB()

            val encoded = json.encodeToString(DatabaseAccountDB.serializer(), db)
            val decoded = json.decodeFromString(DatabaseAccountDB.serializer(), encoded)

            assertThat(decoded.authorizationType).isEqualTo(type)
            assertThat(decoded.toDomain().authorizationType()).isEqualTo(type)
        }
    }

    @Test
    fun fall_back_to_basic_when_decoded_authorization_type_is_missing() {
        val legacyJson = """
            {
              "username":"user",
              "serverUrl":"https://dhis2.org",
              "databaseName":"user.db",
              "databaseCreationDate":"$DATE",
              "lastAccessDate":"$DATE",
              "encrypted":false,
              "syncState":null,
              "importDB":null,
              "loginConfig":null,
              "authorizationType":null
            }
        """.trimIndent()

        val decoded = Json.decodeFromString(DatabaseAccountDB.serializer(), legacyJson)

        assertThat(decoded.authorizationType).isNull()
        assertThat(decoded.toDomain().authorizationType()).isEqualTo(AuthorizationType.BASIC)
    }

    companion object {
        private const val DATE = "2024-01-01T00:00:00.000"
    }
}
