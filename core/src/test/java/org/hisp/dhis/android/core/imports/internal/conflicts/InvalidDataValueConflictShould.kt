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
package org.hisp.dhis.android.core.imports.internal.conflicts

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.junit.Test

internal class InvalidDataValueConflictShould : BaseConflictShould() {

    @Test
    fun `Should match error messages`() {
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotNumeric(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotUnitInterval(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotPercentage(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotInteger(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotPositiveInteger(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotNegativeInteger(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotZeroOrPositiveInteger(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotBoolean(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotTrueOnly(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotValidDate(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotValidDatetime(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotCoordinate(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotUrl(dataElementUid))
        checkMatchAndDataElement(TrackedImportConflictSamples.valueNotFileResourceUid(dataElementUid))
    }

    @Test
    fun `Should create display description`() {
        whenever(dataElement.displayFormName()) doReturn "Data Element form name"

        val conflict = TrackedImportConflictSamples.valueNotNumeric(dataElementUid)
        val displayDescription = InvalidDataValueConflict.getDisplayDescription(conflict, context)
        assertThat(displayDescription == "Invalid value type for dataElement: Data Element form name").isTrue()
    }

    private fun checkMatchAndDataElement(conflict: ImportConflict) {
        assertThat(InvalidDataValueConflict.matches(conflict)).isTrue()
        assertThat(InvalidDataValueConflict.getDataElement(conflict) == dataElementUid).isTrue()
    }
}
