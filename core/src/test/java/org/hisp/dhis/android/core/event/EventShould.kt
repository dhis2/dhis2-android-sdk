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
package org.hisp.dhis.android.core.event

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.network.event.EventDTO
import org.junit.Test

class EventShould : CoreObjectShould("event/event.json") {
    @Test
    override fun map_from_json_string() {
        val eventDTO = deserialize(EventDTO.serializer())
        val event = eventDTO.toDomain()

        assertThat(event.uid()).isEqualTo("hnaWBxMw5j3")
        assertThat(event.status()).isEqualTo(EventStatus.COMPLETED)
        assertThat(event.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(event.program()).isEqualTo("eBAyeGv0exc")
        assertThat(event.programStage()).isEqualTo("Zj7UnCAulEk")
        assertThat(event.enrollment()).isEqualTo("RiLEKhWHlxZ")
        assertThat(event.geometry()!!.type()).isEqualTo(FeatureType.POINT)
        assertThat(event.geometry()!!.coordinates()).isEqualTo("[0.0,0.0]")
        assertThat(event.deleted()).isFalse()
        assertThat(event.assignedUser()).isEqualTo("aTwqot2S410")
        assertThat(event.completedBy()).isEqualTo("system")

        assertThat(event.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-09-08T21:40:22.000"))
        assertThat(event.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-11-15T14:55:22.995"))
        assertThat(event.eventDate()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-05-01T00:00:00.000"))
        assertThat(event.completedDate()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-11-15T00:00:00.000"))

        assertThat(event.trackedEntityDataValues()!![0].dataElement()).isEqualTo("vV9UWAZohSf")
        assertThat(event.trackedEntityDataValues()!![1].dataElement()).isEqualTo("K6uUAvq500H")
        assertThat(event.trackedEntityDataValues()!![2].dataElement()).isEqualTo("fWIAEtYVEGk")
        assertThat(event.trackedEntityDataValues()!![3].dataElement()).isEqualTo("msodh3rEMJa")
        assertThat(event.trackedEntityDataValues()!![4].dataElement()).isEqualTo("eMyVanycQSC")
        assertThat(event.trackedEntityDataValues()!![5].dataElement()).isEqualTo("oZg33kd9taw")
        assertThat(event.trackedEntityDataValues()!![6].dataElement()).isEqualTo("qrur9Dvnyt5")
        assertThat(event.trackedEntityDataValues()!![7].dataElement()).isEqualTo("GieVkTxp4HH")

        assertThat(event.notes()!![0].uid()).isEqualTo("eventNote1")
        assertThat(event.notes()!![1].uid()).isEqualTo("eventNote2")

        assertThat(event.relationships()!![0].uid()).isEqualTo("ZLrITbZfdnv")
    }
}
