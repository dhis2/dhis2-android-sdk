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
package org.hisp.dhis.android.core.tracker.importer

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.text.ParseException
import org.hisp.dhis.android.core.Inject
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.tracker.importer.internal.*
import org.junit.Test

class JobReportSuccessShould : BaseObjectShould("tracker/importer/jobreport-success.json"), ObjectShould {

    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val objectMapper = Inject.objectMapper()
        val jobReport = objectMapper.readValue(jsonStream, JobReport::class.java)

        assertThat(jobReport.status).isEqualTo("OK")
        assertThat(jobReport.validationReport.errorReports).isEmpty()
        assertThat(jobReport.stats).isEqualTo(JobImportCount(1, 2, 3, 4, 10))

        val bundleReport = jobReport.bundleReport!!
        assertThat(bundleReport.status).isEqualTo("OK")

        assertThat(bundleReport.stats)
            .isEqualTo(JobImportCount(5, 6, 7, 8, 26))

        assertThat(bundleReport.typeReportMap.trackedEntity).isEqualTo(
            JobTypeReport(
                "TRACKED_ENTITY",
                JobImportCount(3, 3, 2, 2, 10),
                emptyList()
            )
        )
        assertThat(bundleReport.typeReportMap.event).isEqualTo(
            JobTypeReport(
                "EVENT",
                JobImportCount(2, 2, 2, 2, 8),
                listOf(JobObjectReport(emptyList(), 0, TrackerImporterObjectType.EVENT, "UavzrupW3lZ"))
            )
        )
        assertThat(bundleReport.typeReportMap.relationship).isEqualTo(
            JobTypeReport(
                "RELATIONSHIP",
                JobImportCount(1, 1, 1, 1, 4),
                emptyList()
            )
        )
        assertThat(bundleReport.typeReportMap.enrollment).isEqualTo(
            JobTypeReport(
                "ENROLLMENT",
                JobImportCount(0, 0, 0, 0, 0),
                emptyList()
            )
        )
    }
}
