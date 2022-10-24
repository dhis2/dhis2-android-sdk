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
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class NewTrackerImporterEventShould : BaseObjectShould("event/new_tracker_importer_event.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val event = objectMapper.readValue(jsonStream, NewTrackerImporterEvent::class.java)

        assertThat(event.uid()).isEqualTo("EsHa0edW6uY")
        assertThat(event.status()).isEqualTo(EventStatus.COMPLETED)
        assertThat(event.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(event.program()).isEqualTo("IpHINAT79UW")
        assertThat(event.programStage()).isEqualTo("A03MvHHogjR")
        assertThat(event.enrollment()).isEqualTo("KpknKHptul0")
        assertThat(event.geometry()!!.type()).isEqualTo(FeatureType.POINT)
        assertThat(event.geometry()!!.coordinates()).isEqualTo("[-11.618041992187502, 9.508486893003065]")
        assertThat(event.deleted()).isFalse()
        assertThat(event.assignedUser()?.uid()).isEqualTo("DXyJmlo9rge")
        assertThat(event.createdAt()).isEqualTo(DateUtils.DATE_FORMAT.parse("2018-01-20T10:44:03.222"))
        assertThat(event.createdAtClient()).isEqualTo(DateUtils.DATE_FORMAT.parse("2018-01-20T10:44:03.222"))
        assertThat(event.updatedAt()).isEqualTo(DateUtils.DATE_FORMAT.parse("2022-09-28T14:23:29.021"))
        assertThat(event.updatedAtClient()).isEqualTo(DateUtils.DATE_FORMAT.parse("2022-09-28T14:23:29.021"))
        assertThat(event.occurredAt()).isEqualTo(DateUtils.DATE_FORMAT.parse("2022-01-10T00:00:00.000"))
        assertThat(event.scheduledAt()).isEqualTo(DateUtils.DATE_FORMAT.parse("2022-09-28T00:00:00.000"))
        assertThat(event.completedAt()).isEqualTo(DateUtils.DATE_FORMAT.parse("2022-01-20T00:00:00.000"))
    }
}
