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
package org.hisp.dhis.android.core.arch.json.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import java.util.*
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory.objectMapper
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.junit.Before
import org.junit.Test

class ObjectMapperFactoryShould {

    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        objectMapper = objectMapper()
    }

    @Test
    fun serialize_date_format() {
        val dateStr = "2020-12-01T12:34:56.123"
        val date = BaseIdentifiableObject.DATE_FORMAT.parse(dateStr)

        val serialized = objectMapper.writeValueAsString(date)
        val deserializedDate = objectMapper.readValue(serialized, Date::class.java)

        assertThat(serialized).isEqualTo("\"$dateStr\"")
        assertThat(deserializedDate).isEqualTo(date)
    }

    @Test
    fun deserialize_multiple_date_format() {
        listOf(
            "2020-12-01T12:34:56.123Z" to "2020-12-01T12:34:56.123",
            "2020-12-01T12:34:56.123" to "2020-12-01T12:34:56.123",
            "2020-12-01T12:34:56.12" to "2020-12-01T12:34:56.012",
            "2020-12-01T12:34:56.1" to "2020-12-01T12:34:56.001",
            "2020-12-01T12:34:56" to "2020-12-01T12:34:56.000",
            "2020-12-01T12:34:56" to "2020-12-01T12:34:56.000",
            "2020-12-01T12:34" to "2020-12-01T12:34:00.000",
            "2020-12-01" to "2020-12-01T00:00:00.000"
        ).forEach { (source, expected) ->
            val sourceDate = objectMapper.readValue("\"$source\"", Date::class.java)
            val expectedDate = BaseIdentifiableObject.parseDate(expected)

            assertThat(sourceDate).isEqualTo(expectedDate)
        }
    }
}
