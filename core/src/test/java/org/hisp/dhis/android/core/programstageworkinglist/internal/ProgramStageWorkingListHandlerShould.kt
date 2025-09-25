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
package org.hisp.dhis.android.core.programstageworkinglist.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageQueryCriteria
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListAttributeValueFilter
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListEventDataFilter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramStageWorkingListHandlerShould {
    private val eventDataFilter: ProgramStageWorkingListEventDataFilter = mock()
    private val attributeValueFilter: ProgramStageWorkingListAttributeValueFilter = mock()
    private val queryCriteria: ProgramStageQueryCriteria = mock()

    // object to test
    private lateinit var workingLists: List<ProgramStageWorkingList>
    private lateinit var eventDataFilters: List<ProgramStageWorkingListEventDataFilter>
    private lateinit var attributeValueFilters: List<ProgramStageWorkingListAttributeValueFilter>

    private val programStageWorkingListStore: ProgramStageWorkingListStore = mock()
    private val eventDataFilterHandler: ProgramStageWorkingListEventDataFilterHandler = mock()
    private val attributeValueFilterHandler: ProgramStageWorkingListAttributeValueFilterHandler = mock()

    private lateinit var handler: ProgramStageWorkingListHandler

    @Before
    fun setUp() = runTest {
        handler = ProgramStageWorkingListHandler(
            programStageWorkingListStore,
            eventDataFilterHandler,
            attributeValueFilterHandler,
        )

        eventDataFilters = listOf(eventDataFilter)
        attributeValueFilters = listOf(attributeValueFilter)

        whenever(queryCriteria.dataFilters()).doReturn(eventDataFilters)
        whenever(queryCriteria.attributeValueFilters()).doReturn(attributeValueFilters)

        val workingList = ProgramStageWorkingList.builder()
            .uid("test_tracked_entity_attribute_uid")
            .name("name")
            .displayName("display_name")
            .program(ObjectWithUid.create("program_uid"))
            .programStage(ObjectWithUid.create("proram_stage_uid"))
            .programStageQueryCriteria(queryCriteria)
            .build()

        whenever(programStageWorkingListStore.updateOrInsert(any<List<ProgramStageWorkingList>>())).doReturn(
            listOf(
                HandleAction.Insert,
            ),
        )
        workingLists = listOf(workingList)
    }

    @Test
    fun extend_identifiable_handler_impl() {
        ProgramStageWorkingListHandler(
            programStageWorkingListStore,
            eventDataFilterHandler,
            attributeValueFilterHandler,
        )
    }

    @Test
    fun handle_event_filters() = runTest {
        handler.handleMany(workingLists)
        verify(eventDataFilterHandler).handleMany(eq(eventDataFilters), any())
    }

    @Test
    fun handle_attribute_filters() = runTest {
        handler.handleMany(workingLists)
        verify(attributeValueFilterHandler).handleMany(eq(attributeValueFilters), any())
    }
}
