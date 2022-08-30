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
package org.hisp.dhis.android.core.configuration.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class DatabasesConfigurationShould :
    BaseObjectShould("configuration/databases_configuration.json"),
    ObjectShould {

    @Test
    override fun map_from_json_string() {
        val configuration = deserialize(DatabasesConfiguration::class.java)

        assertThat(configuration.versionCode()).isEqualTo(260)
        assertThat(configuration.maxAccounts()).isEqualTo(3)
        assertThat(configuration.accounts().size).isEqualTo(2)

        val user1 = configuration.accounts()[0]
        assertThat(user1.username()).isEqualTo("user1")
        assertThat(user1.serverUrl()).isEqualTo("server1")
        assertThat(user1.databaseName()).isEqualTo("dbname1.db")
        assertThat(user1.encrypted()).isTrue()
        assertThat(user1.databaseCreationDate()).isEqualTo("2014-06-06T20:44:21.375")
    }

    @Test
    fun equal_when_deserialize_serialize_deserialize() {
        val configuration = deserialize(DatabasesConfiguration::class.java)

        val serialized = serialize(configuration)
        val deserialized = deserialize(serialized, DatabasesConfiguration::class.java)

        assertThat(deserialized).isEqualTo(configuration)
    }
}
