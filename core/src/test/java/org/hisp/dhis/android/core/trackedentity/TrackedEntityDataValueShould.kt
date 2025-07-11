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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.network.event.TrackedEntityDataValueDTO
import org.hisp.dhis.android.network.event.toDto
import org.junit.Test

class TrackedEntityDataValueShould :
    BaseObjectKotlinxShould("trackedentity/tracked_entity_data_value.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val trackedEntityDataValueDTO = deserialize(TrackedEntityDataValueDTO.serializer())
        val trackedEntityDataValue = trackedEntityDataValueDTO.toDomain("event")

        assertThat(trackedEntityDataValue.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2014-11-15T14:55:23.779"))
        assertThat(
            trackedEntityDataValue.lastUpdated(),
        ).isEqualTo(DateUtils.DATE_FORMAT.parse("2014-11-16T14:55:23.779"))

        assertThat(trackedEntityDataValue.storedBy()).isEqualTo("admin")
        assertThat(trackedEntityDataValue.dataElement()).isEqualTo("msodh3rEMJa")
        assertThat(trackedEntityDataValue.value()).isEqualTo("2013-05-18")
        assertThat(trackedEntityDataValue.providedElsewhere()).isFalse()
    }

    @Test
    fun serialize_empty_values() {
        val dataValue = TrackedEntityDataValue.builder()
            .dataElement("cejWyOfXge6")
            .value(null)
            .created("2019-12-12T07:35:11.366".toJavaDate())
            .lastUpdated("2019-12-12T07:35:11.366".toJavaDate())
            .build()
        val attributeValueString = serialize(dataValue.toDto(), TrackedEntityDataValueDTO.serializer())

        assertThat(attributeValueString).contains("\"value\":null")
    }
}
