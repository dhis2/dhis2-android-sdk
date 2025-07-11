/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.imports

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackerImportConflictCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts().blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(2)
    }

    @Test
    fun filter_by_conflict() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byConflict().eq("conflict")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_value() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byValue().eq("value")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tracked_entity_instance() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byTrackedEntityInstanceUid().eq("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_enrollment() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byEnrollmentUid().eq("enroll2")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_event() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byEventUid().eq("event2")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tale_reference() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byTableReference().eq("table_reference")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_error_code() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byErrorCode().eq("error_code")
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_status() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byStatus().eq(ImportStatus.SUCCESS)
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created() {
        val trackerImportConflicts = d2.importModule().trackerImportConflicts()
            .byCreated().eq("2017-11-29T11:27:46.935".toJavaDate())
            .blockingGet()

        assertThat(trackerImportConflicts.size).isEqualTo(2)
    }
}
