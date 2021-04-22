/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.tracker.importer.internal.JobInfo
import org.junit.Test

class JobInfoShould : BaseObjectShould("tracker/importer/jobinfo.json"), ObjectShould {

    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val objectMapper = Inject.objectMapper()
        val jobInfo = objectMapper.readValue(jsonStream, JobInfo::class.java)

        assertThat(jobInfo.id).isEqualTo("id")
        assertThat(jobInfo.uid).isEqualTo("uid")
        assertThat(jobInfo.level).isEqualTo("INFO")
        assertThat(jobInfo.category).isEqualTo("TRACKER_IMPORT_JOB")
        assertThat(jobInfo.time).isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2021-01-25T12:09:18.571"))
        assertThat(jobInfo.message).isEqualTo("(android) Import:Done took 0.360910 sec.")
        assertThat(jobInfo.completed).isTrue()
    }
}
