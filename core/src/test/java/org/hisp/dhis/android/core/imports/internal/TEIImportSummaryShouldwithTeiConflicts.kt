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
package org.hisp.dhis.android.core.imports.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.network.trackedentityinstance.TEIImportSummaryDTO
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TEIImportSummaryShouldwithTeiConflicts :
    BaseObjectKotlinxShould("imports/import_summary_with_tei_conflicts.json"),
    ObjectShould {

    @Test
    override fun map_from_json_string() {
        val importSummaryDTO = deserialize(TEIImportSummaryDTO.serializer())
        val importSummary = importSummaryDTO.toDomain()

        assertThat(importSummary.status()).isEqualTo(ImportStatus.ERROR)
        assertThat(importSummary.responseType()).isEqualTo("ImportSummary")
        assertThat(importSummary.importCount()).isNotNull()
        assertThat(importSummary.importCount().imported()).isEqualTo(0)
        assertThat(importSummary.importCount().updated()).isEqualTo(0)
        assertThat(importSummary.importCount().ignored()).isEqualTo(1)
        assertThat(importSummary.importCount().deleted()).isEqualTo(0)

        assertThat(importSummary.conflicts()).isNotNull()
        assertThat(importSummary.conflicts()!!.size).isEqualTo(1)

        val importConflict = importSummary.conflicts()!![0]

        assertThat(importConflict).isNotNull()
        assertThat(importConflict.value())
            .isEqualTo("Value '201921212' is not a valid date for attribute iESIqZ0R0R0")
        assertThat(importConflict.`object`()).isEqualTo("Attribute.value")
    }

    @Test
    @Throws(Exception::class)
    fun importSummary_shouldParseImportSummaryWithEnrollmentConflictsFromJson() {
        // TODO Test
    }
}
