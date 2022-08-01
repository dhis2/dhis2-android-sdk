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

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.tracker.importer.internal.JobValidationError
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerConflictHelper
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType
import org.hisp.dhis.android.core.tracker.importer.internal.interpreters.InterpreterHelper
import org.hisp.dhis.android.core.tracker.importer.internal.interpreters.InterpreterSelector
import org.junit.Before
import org.junit.Test

class TrackerConflictHelperShould {

    private val context: Context = mock()
    private val interpreterHelper: InterpreterHelper = mock()
    private val selector = InterpreterSelector(interpreterHelper)

    @Before
    fun setUp() {
        whenever(context.getString(any())) doReturn "The %s event was not found in the server. (Event: %s)"
        whenever(interpreterHelper.programStageUid(any())) doReturn "programStageUid"
        whenever(interpreterHelper.programStageDisplayName(any())) doReturn "Antenatal care visiting"
    }

    @Test
    fun `Should return the import error display description when passing a valid ErrorCode`() {
        val trackerImportConflict = TrackerConflictHelper(context, selector).getConflictBuilder(errorReport).build()
        assertThat(trackerImportConflict.displayDescription())
            .isEqualTo("The Antenatal care visiting event was not found in the server. (Event: PXi7gfVIk1p)")
    }

    @Test
    fun `Should return the error message display description when passing a wrong ErrorCode`() {
        val trackerImportConflict = TrackerConflictHelper(context, selector)
            .getConflictBuilder(wrongCodeErrorReport).build()
        assertThat(trackerImportConflict.displayDescription())
            .isEqualTo("Event: `PXi7gfVIk1p`, do not exist.")
    }

    companion object {
        private val errorReport = JobValidationError(
            "PXi7gfVIk1p",
            TrackerImporterObjectType.EVENT,
            "E1032",
            "Event: `PXi7gfVIk1p`, do not exist."
        )
        private val wrongCodeErrorReport = errorReport.copy(errorCode = "WrongCode")
    }
}
