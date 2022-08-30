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
import org.hisp.dhis.android.core.tracker.importer.internal.JobImportCount
import org.hisp.dhis.android.core.tracker.importer.internal.JobReport
import org.hisp.dhis.android.core.tracker.importer.internal.JobValidationError
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType
import org.junit.Test

class JobReportErrorShould : BaseObjectShould("tracker/importer/jobreport-error.json"), ObjectShould {

    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val objectMapper = Inject.objectMapper()
        val jobReport = objectMapper.readValue(jsonStream, JobReport::class.java)

        assertThat(jobReport.status).isEqualTo("ERROR")
        assertThat(jobReport.stats).isEqualTo(JobImportCount(0, 0, 0, 1, 1))

        assertThat(jobReport.validationReport.errorReports.size).isEqualTo(1)

        val error = jobReport.validationReport.errorReports.first()
        assertThat(error).isEqualTo(
            JobValidationError(
                "PXi7gfVIk1p",
                TrackerImporterObjectType.EVENT,
                "E1033",
                "Event: `PXi7gfVIk1p`, Enrollment value is NULL."
            )
        )
    }
}
