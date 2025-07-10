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
package org.hisp.dhis.android.core.trackedentity

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.trackedentityinstance.TrackedEntityAttributeValueDTO
import org.hisp.dhis.android.network.trackedentityinstance.toDto
import org.junit.Test

class TrackedEntityAttributeValueShould : CoreObjectShould("trackedentity/tracked_entity_attribute_value.json") {

    @Test
    override fun map_from_json_string() {
        val trackedEntityAttributeValueDTO = deserialize(TrackedEntityAttributeValueDTO.serializer())
        val trackedEntityAttributeValue = trackedEntityAttributeValueDTO.toDomain("event")

        assertThat(trackedEntityAttributeValue.created()).isEqualTo("2019-12-12T07:35:11.366".toJavaDate())
        assertThat(trackedEntityAttributeValue.lastUpdated()).isEqualTo("2019-12-12T07:35:12.904".toJavaDate())
        assertThat(trackedEntityAttributeValue.value()).isEqualTo("11")
        assertThat(trackedEntityAttributeValue.trackedEntityAttribute()).isEqualTo("cejWyOfXge6")
    }

    @Test
    fun map_from_null_value() {
        val trackedEntityAttributeValueDTO = deserializePath(
            path = "trackedentity/tracked_entity_attribute_value_null.json",
            TrackedEntityAttributeValueDTO.serializer(),
        )
        val trackedEntityAttributeValue = trackedEntityAttributeValueDTO.toDomain("event")

        assertThat(trackedEntityAttributeValue.value()).isNull()
    }

    @Test
    fun map_from_missing_value() {
        val trackedEntityAttributeValueDTO = deserializePath(
            path = "trackedentity/tracked_entity_attribute_value_missing.json",
            TrackedEntityAttributeValueDTO.serializer(),
        )
        val trackedEntityAttributeValue = trackedEntityAttributeValueDTO.toDomain("event")

        assertThat(trackedEntityAttributeValue.value()).isNull()
    }

    @Test
    fun map_to_json_string() {
        val attributeValue = TrackedEntityAttributeValue.builder()
            .trackedEntityAttribute("cejWyOfXge6")
            .value("11")
            .created("2019-12-12T07:35:11.366".toJavaDate())
            .lastUpdated("2019-12-12T07:35:11.366".toJavaDate())
            .build()
        val attributeValueString = serialize(attributeValue.toDto(), TrackedEntityAttributeValueDTO.serializer())

        assertThat(attributeValueString).contains("\"value\":\"11\"")
    }

    @Test
    fun serialize_empty_values() {
        val attributeValue = TrackedEntityAttributeValue.builder()
            .trackedEntityAttribute("cejWyOfXge6")
            .value(null)
            .created("2019-12-12T07:35:11.366".toJavaDate())
            .lastUpdated("2019-12-12T07:35:11.366".toJavaDate())
            .build()
        val attributeValueString = serialize(attributeValue.toDto(), TrackedEntityAttributeValueDTO.serializer())

        assertThat(attributeValueString).contains("\"value\":null")
    }
}
