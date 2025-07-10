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
package org.hisp.dhis.android.core.enrollment

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.tracker.NewEnrollmentDTO
import org.junit.Test

class NewTrackerImporterEnrollmentShould : CoreObjectShould("enrollment/new_tracker_importer_enrollment.json") {

    @Test
    override fun map_from_json_string() {
        val enrollmentDTO = deserialize(NewEnrollmentDTO.serializer())
        val enrollment = enrollmentDTO.toDomain()

        assertThat(enrollment.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2018-01-20T10:44:02.929"))
        assertThat(enrollment.createdAtClient()).isEqualTo(DateUtils.DATE_FORMAT.parse("2017-02-20T10:44:02.929"))
        assertThat(enrollment.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2018-01-20T10:44:33.776"))
        assertThat(enrollment.lastUpdatedAtClient()).isEqualTo(DateUtils.DATE_FORMAT.parse("2018-02-20T10:44:33.776"))
        assertThat(enrollment.uid()).isEqualTo("KpknKHptul0")
        assertThat(enrollment.organisationUnit()).isEqualTo("DiszpKrYNg8")
        assertThat(enrollment.program()).isEqualTo("IpHINAT79UW")
        assertThat(enrollment.enrollmentDate()).isEqualTo(DateUtils.DATE_FORMAT.parse("2023-01-10T00:00:00.000"))
        assertThat(enrollment.incidentDate()).isEqualTo(DateUtils.DATE_FORMAT.parse("2023-01-10T00:00:00.000"))
        assertThat(enrollment.completedDate()).isEqualTo(DateUtils.DATE_FORMAT.parse("2023-01-20T10:44:33.776"))
        assertThat(enrollment.followUp()).isEqualTo(true)
        assertThat(enrollment.status()).isEqualTo(EnrollmentStatus.COMPLETED)
        assertThat(enrollment.trackedEntityInstance()).isEqualTo("vOxUH373fy5")
        assertThat(enrollment.deleted()).isEqualTo(false)
    }
}
