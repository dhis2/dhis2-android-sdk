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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.stores.internal.TrackerBaseSyncStore
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackerSyncLastUpdatedManagerShould {

    private val store: TrackerBaseSyncStore<TrackedEntityInstanceSync> = mock()
    private val params: ProgramDataDownloadParams = mock()

    private val program = "program_uid"
    private val orgUnits = listOf("org_unit_1", "org_unit_2")
    private val orgUnitsHash = orgUnits.toSet().hashCode()
    private val limit = 500
    private val workingListsHash = 12345
    private val lastUpdated = DateUtils.DATE_FORMAT.parse("2017-11-29T11:27:46.935")

    private lateinit var manager: TrackerSyncLastUpdatedManager<TrackedEntityInstanceSync>

    @Before
    fun setUp() = runTest {
        whenever(params.uids()).thenReturn(emptyList())
        manager = TrackerSyncLastUpdatedManager(store)
    }

    @Test
    fun return_last_updated_when_working_lists_hash_matches() = runTest {
        prepareWithSync(storedHash = workingListsHash)

        val result = manager.getLastUpdatedStr(createCommonParams(workingListsHash))

        assertThat(result).isNotNull()
    }

    @Test
    fun return_null_when_working_lists_hash_changes() = runTest {
        prepareWithSync(storedHash = workingListsHash)

        val result = manager.getLastUpdatedStr(createCommonParams(99999))

        assertThat(result).isNull()
    }

    @Test
    fun return_null_when_stored_hash_is_null_but_current_is_not() = runTest {
        prepareWithSync(storedHash = null)

        val result = manager.getLastUpdatedStr(createCommonParams(workingListsHash))

        assertThat(result).isNull()
    }

    @Test
    fun return_null_when_current_hash_is_null_but_stored_is_not() = runTest {
        prepareWithSync(storedHash = workingListsHash)

        val result = manager.getLastUpdatedStr(createCommonParams(null))

        assertThat(result).isNull()
    }

    @Test
    fun return_last_updated_when_both_hashes_are_null() = runTest {
        prepareWithSync(storedHash = null)

        val result = manager.getLastUpdatedStr(createCommonParams(null))

        assertThat(result).isNotNull()
    }

    @Test
    fun return_null_when_no_sync_record_exists() = runTest {
        prepareWithSync(sync = null)

        val result = manager.getLastUpdatedStr(createCommonParams(workingListsHash))

        assertThat(result).isNull()
    }

    @Test
    fun return_null_when_download_limit_increased() = runTest {
        prepareWithSync(storedHash = workingListsHash)

        val result = manager.getLastUpdatedStr(createCommonParams(workingListsHash).copy(limit = limit + 100))

        assertThat(result).isNull()
    }

    @Test
    fun return_last_updated_when_download_limit_decreased() = runTest {
        prepareWithSync(storedHash = workingListsHash)

        val result = manager.getLastUpdatedStr(createCommonParams(workingListsHash).copy(limit = limit - 100))

        assertThat(result).isNotNull()
    }

    private suspend fun prepareWithSync(storedHash: Int? = null, sync: TrackedEntityInstanceSync? = createSync(storedHash)) {
        whenever(store.selectAll()).thenReturn(listOfNotNull(sync))
        manager.prepare(null, params)
    }

    private fun createSync(hash: Int?): TrackedEntityInstanceSync {
        return TrackedEntityInstanceSync.builder()
            .program(program)
            .organisationUnitIdsHash(orgUnitsHash)
            .downloadLimit(limit)
            .workingListsHash(hash)
            .lastUpdated(lastUpdated)
            .build()
    }

    private fun createCommonParams(hash: Int?): TrackerQueryCommonParams {
        return TrackerQueryCommonParams(
            uids = emptyList(),
            programs = listOf(program),
            program = program,
            startDate = null,
            hasLimitByOrgUnit = false,
            ouMode = OrganisationUnitMode.SELECTED,
            orgUnitsBeforeDivision = orgUnits,
            limit = limit,
            workingListsHash = hash,
        )
    }
}
