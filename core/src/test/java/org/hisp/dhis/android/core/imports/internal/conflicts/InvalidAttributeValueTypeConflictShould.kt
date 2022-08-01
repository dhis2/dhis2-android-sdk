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

internal class InvalidAttributeValueTypeConflictShould : BaseConflictShould() {

    @Test
    fun `Should match error message and trackedEntityAttribute`() {
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidNumericAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidBooleanAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidTrueOnlyAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidDateAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidDatetimeAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidUsernameAttribute(attributeUid, value))
        checkMatchAndAttribute(TrackedImportConflictSamples.invalidAttributeOption(attributeUid, value, optionSetUid))

        assertThat(InvalidAttributeValueTypeConflict.matches(TrackedImportConflictSamples.invalidFileAttribute(value)))
            .isTrue()
        assertThat(
            InvalidAttributeValueTypeConflict
                .getTrackedEntityAttribute(TrackedImportConflictSamples.invalidFileAttribute(value)).isNullOrBlank()
        ).isTrue()
    }

    @Test
    fun `Should generate display description`() {
        checkDescription(TrackedImportConflictSamples.invalidNumericAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidBooleanAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidTrueOnlyAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidDateAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidDatetimeAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidUsernameAttribute(attributeUid, value))
        checkDescription(TrackedImportConflictSamples.invalidAttributeOption(attributeUid, value, optionSetUid))

        val fileConflictDescription = InvalidAttributeValueTypeConflict
            .getDisplayDescription(TrackedImportConflictSamples.invalidFileAttribute(value), context)
        assertThat(fileConflictDescription == TrackedImportConflictSamples.invalidFileAttribute(value).value()).isTrue()
    }

    private fun checkMatchAndAttribute(conflict: ImportConflict) {
        assertThat(InvalidAttributeValueTypeConflict.matches(conflict)).isTrue()
        assertThat(InvalidAttributeValueTypeConflict.getTrackedEntityAttribute(conflict) == attributeUid).isTrue()
    }

    private fun checkDescription(conflict: ImportConflict) {
        whenever(attribute.displayFormName()) doReturn "Attribute form name"
        val description = InvalidAttributeValueTypeConflict.getDisplayDescription(conflict, context)
        assertThat(description == "Invalid value type for attribute: Attribute form name").isTrue()
    }
}
