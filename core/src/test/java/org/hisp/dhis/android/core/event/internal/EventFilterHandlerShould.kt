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
package org.hisp.dhis.android.core.event.internal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.event.EventDataFilter
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventQueryCriteria
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventFilterHandlerShould {
    private val eventFilterStore: EventFilterStore = mock()
    private val eventDataFilterHandler: EventDataFilterHandler = mock()
    private val eventQueryCriteria: EventQueryCriteria = mock()
    private val eventDataFilter: EventDataFilter = mock()

    // object to test
    private lateinit var eventFilters: MutableList<EventFilter>
    private lateinit var eventDataFilters: List<EventDataFilter?>
    private lateinit var eventFilterHandler: EventFilterHandler

    @Before
    fun setUp() {
        eventFilterHandler = EventFilterHandler(
            eventFilterStore,
            eventDataFilterHandler,
        )

        eventDataFilters = listOf(eventDataFilter)
        whenever(eventQueryCriteria.dataFilters()).thenReturn(eventDataFilters)

        val eventFilter = EventFilter.builder()
            .uid("test_tracked_entity_attribute_uid")
            .program("program_uid")
            .name("name")
            .displayName("display_name")
            .eventQueryCriteria(eventQueryCriteria)
            .build()

        whenever(eventFilterStore.updateOrInsert(any())).doReturn(HandleAction.Insert)

        eventFilters = mutableListOf(eventFilter)
    }

    @Test
    @Suppress("UnusedPrivateMember")
    fun extend_identifiable_handler_impl() {
        val genericHandler = EventFilterHandler(
            eventFilterStore,
            eventDataFilterHandler,
        )
    }

    @Test
    fun handle_event_filters() {
        eventFilterHandler.handleMany(eventFilters)
        verify(eventDataFilterHandler).handleMany(eventDataFilters.filterNotNull())
    }
}
