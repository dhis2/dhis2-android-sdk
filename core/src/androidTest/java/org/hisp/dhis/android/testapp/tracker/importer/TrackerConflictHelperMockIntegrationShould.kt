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
package org.hisp.dhis.android.testapp.tracker.importer

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.tracker.importer.internal.JobValidationError
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerConflictHelper
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackerConflictHelperMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun generate_correct_display_descriptions_if_passing_correct_error_report() {
        val trackerImportConflict = TrackerConflictHelper(
            InstrumentationRegistry.getInstrumentation().context,
            objects.d2DIComponent.interpreterSelector()
        ).getConflictBuilder(errorReport).build()
        assertThat(trackerImportConflict.displayDescription())
            .isEqualTo(
                "The Antenatal care visit - Program rules demo event was not found in the server." +
                    " (Event: event1)"
            )
    }

    @Test
    fun return_default_message_when_passing_wrong_error_report() {
        val trackerImportConflict = TrackerConflictHelper(
            InstrumentationRegistry.getInstrumentation().context,
            objects.d2DIComponent.interpreterSelector()
        ).getConflictBuilder(wrongCodeErrorReport).build()
        assertThat(trackerImportConflict.displayDescription())
            .isEqualTo("Event: `event1`, Enrollment value is NULL.")
    }

    @Test
    fun generate_correct_display_descriptions_for_E1000_error() {
        val trackerImportConflict = TrackerConflictHelper(
            InstrumentationRegistry.getInstrumentation().context,
            objects.d2DIComponent.interpreterSelector()
        ).getConflictBuilder(errorReportE1000).build()
        assertThat(trackerImportConflict.displayDescription())
            .isEqualTo("You do not have access to Ngelehun CHC")
    }

    companion object {
        private val errorReport = JobValidationError(
            "event1",
            TrackerImporterObjectType.EVENT,
            "E1032",
            "Event: `event1`, Enrollment value is NULL."
        )

        private val errorReportE1000 = JobValidationError(
            "teiUid",
            TrackerImporterObjectType.TRACKED_ENTITY,
            "E1000",
            "User: `User (DXyJmlo9rge)`, has no write access to OrganisationUnit: `OrganisationUnit (DiszpKrYNg8)`."
        )
        private val wrongCodeErrorReport = errorReport.copy(errorCode = "WrongCode")
    }
}
