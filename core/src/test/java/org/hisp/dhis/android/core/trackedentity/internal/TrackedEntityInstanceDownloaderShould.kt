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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackedEntityInstanceDownloaderShould {
    private val call: TrackedEntityInstanceDownloadCall = mock()
    private val programStageWorkingListCollectionRepository: ProgramStageWorkingListCollectionRepository = mock()
    private val trackedEntityInstanceFilterCollectionRepository: TrackedEntityInstanceFilterCollectionRepository =
        mock()
    private val trackedEntityInstanceFilterCollectionRepositoryList: TrackedEntityInstanceFilterCollectionRepository =
        mock()
    private val connectorTei: StringFilterConnector<TrackedEntityInstanceFilterCollectionRepository> = mock()
    private val connectorWl: StringFilterConnector<ProgramStageWorkingListCollectionRepository> = mock()

    private val paramsCapture: KArgumentCaptor<ProgramDataDownloadParams> = argumentCaptor()

    private lateinit var params: ProgramDataDownloadParams
    private lateinit var downloader: TrackedEntityInstanceDownloader

    private val filterUid = "filterUid"
    private val filterUidList = listOf("filterUid0", "filterUid1", "filterUid2")
    private val teiFilter = TrackedEntityInstanceFilter.builder().uid(filterUid).build()
    private val teiFilterList = filterUidList.map { TrackedEntityInstanceFilter.builder().uid(it).build() }

    @Before
    fun setUp() {
        params = ProgramDataDownloadParams.builder().build()
        downloader = TrackedEntityInstanceDownloader(
            call,
            params,
            programStageWorkingListCollectionRepository,
            trackedEntityInstanceFilterCollectionRepository,
        )

        whenever(programStageWorkingListCollectionRepository.byUid()).thenReturn(connectorWl)
        whenever(connectorWl.`in`(listOf(filterUid))).thenReturn(programStageWorkingListCollectionRepository)
        whenever(connectorWl.`in`(filterUidList)).thenReturn(programStageWorkingListCollectionRepository)
        whenever(programStageWorkingListCollectionRepository.withAttributeValueFilters()).thenReturn(
            programStageWorkingListCollectionRepository,
        )
        whenever(programStageWorkingListCollectionRepository.withDataFilters()).thenReturn(
            programStageWorkingListCollectionRepository,
        )
        whenever(programStageWorkingListCollectionRepository.blockingGet()).thenReturn(emptyList())

        whenever(trackedEntityInstanceFilterCollectionRepository.byUid()).thenReturn(connectorTei)
        whenever(connectorTei.`in`(listOf(filterUid))).thenReturn(trackedEntityInstanceFilterCollectionRepository)
        whenever(connectorTei.`in`(filterUidList)).thenReturn(trackedEntityInstanceFilterCollectionRepositoryList)
        whenever(trackedEntityInstanceFilterCollectionRepository.withTrackedEntityInstanceEventFilters()).thenReturn(
            trackedEntityInstanceFilterCollectionRepository,
        )
        whenever(trackedEntityInstanceFilterCollectionRepository.withAttributeValueFilters()).thenReturn(
            trackedEntityInstanceFilterCollectionRepository,
        )
        whenever(
            trackedEntityInstanceFilterCollectionRepositoryList.withTrackedEntityInstanceEventFilters(),
        ).thenReturn(
            trackedEntityInstanceFilterCollectionRepositoryList,
        )
        whenever(trackedEntityInstanceFilterCollectionRepositoryList.withAttributeValueFilters()).thenReturn(
            trackedEntityInstanceFilterCollectionRepositoryList,
        )
        whenever(trackedEntityInstanceFilterCollectionRepository.blockingGet()).thenReturn(listOf(teiFilter))
        whenever(trackedEntityInstanceFilterCollectionRepositoryList.blockingGet()).thenReturn(teiFilterList)
    }

    @Test
    fun should_create_call_with_parsed_params() {
        downloader
            .byProgramUid("program-uid")
            .limitByOrgunit(true)
            .limitByProgram(true)
            .limit(500)
            .byProgramStatus(EnrollmentScope.ONLY_ACTIVE)
            .overwrite(true)
            .download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.program()).isEqualTo("program-uid")
        assertThat(params.limitByOrgunit()).isTrue()
        assertThat(params.limitByProgram()).isTrue()
        assertThat(params.limit()).isEqualTo(500)
        assertThat(params.programStatus()).isEqualTo(EnrollmentScope.ONLY_ACTIVE)
        assertThat(params.overwrite()).isTrue()
    }

    @Test
    fun should_parse_uid_eq_params() {
        downloader.byUid().eq("uid").download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.uids().size).isEqualTo(1)
        assertThat(params.uids()[0]).isEqualTo("uid")
    }

    @Test
    fun should_parse_uid_in_params() {
        downloader.byUid().`in`("uid0", "uid1", "uid2").download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.uids().size).isEqualTo(3)
        assertThat(params.uids()[0]).isEqualTo("uid0")
        assertThat(params.uids()[1]).isEqualTo("uid1")
        assertThat(params.uids()[2]).isEqualTo("uid2")
    }

    @Test
    fun should_parse_filter_uid_eq_params() {
        downloader.byFilterUid().eq("filterUid").download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.trackedEntityInstanceFilters()?.size).isEqualTo(1)
        assertThat(params.trackedEntityInstanceFilters()?.get(0)?.uid()).isEqualTo("filterUid")
    }

    @Test
    fun should_parse_filter_uid_in_params() {
        downloader.byFilterUid().`in`("filterUid0", "filterUid1", "filterUid2").download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.trackedEntityInstanceFilters()?.size).isEqualTo(3)
        assertThat(params.trackedEntityInstanceFilters()?.get(0)?.uid()).isEqualTo("filterUid0")
        assertThat(params.trackedEntityInstanceFilters()?.get(1)?.uid()).isEqualTo("filterUid1")
        assertThat(params.trackedEntityInstanceFilters()?.get(2)?.uid()).isEqualTo("filterUid2")
    }

    @Test
    fun should_parse_tracked_entity_instance_filter_params() {
        val trackedEntityInstanceFilter: TrackedEntityInstanceFilter = mock()
        downloader.byTrackedEntityInstanceFilter().eq(trackedEntityInstanceFilter).download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.trackedEntityInstanceFilters()).isEqualTo(listOf(trackedEntityInstanceFilter))
    }

    @Test
    fun should_parse_program_stage_working_list_params() {
        val programStageWorkingList1: ProgramStageWorkingList = mock()
        val programStageWorkingList2: ProgramStageWorkingList = mock()
        downloader.byProgramStageWorkingList().`in`(listOf(programStageWorkingList1, programStageWorkingList2))
            .download()

        verify(call).download(paramsCapture.capture())

        val params = paramsCapture.firstValue
        assertThat(params.programStageWorkingLists())
            .isEqualTo(listOf(programStageWorkingList1, programStageWorkingList2))
    }
}
