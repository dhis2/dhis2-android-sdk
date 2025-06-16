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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackedEntityInstanceFilterHandlerShould {
    private val trackedEntityInstanceFilterStore: TrackedEntityInstanceFilterStore = mock()
    private val objectStyle: ObjectStyle = mock()
    private val trackedEntityInstanceEventFilterHandler: TrackedEntityInstanceEventFilterHandler = mock()
    private val attributeValueFilterHandler: AttributeValueFilterHandler = mock()
    private val eventFilter: TrackedEntityInstanceEventFilter = mock()

    // object to test
    private lateinit var trackedEntityInstanceFilterHandler: TrackedEntityInstanceFilterHandler
    private lateinit var trackedEntityInstanceFilters: MutableList<TrackedEntityInstanceFilter>
    private lateinit var eventFilters: List<TrackedEntityInstanceEventFilter>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        trackedEntityInstanceFilterHandler = TrackedEntityInstanceFilterHandler(
            trackedEntityInstanceFilterStore,
            trackedEntityInstanceEventFilterHandler,
            attributeValueFilterHandler,
        )

        eventFilters = listOf(eventFilter)

        val trackedEntityInstanceFilter = TrackedEntityInstanceFilter.builder()
            .uid("test_tracked_entity_attribute_uid")
            .program(ObjectWithUid.create("program_uid"))
            .style(objectStyle)
            .name("name")
            .displayName("display_name")
            .eventFilters(eventFilters)
            .entityQueryCriteria(EntityQueryCriteria.builder().build())
            .build()

        whenever(trackedEntityInstanceFilterStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)

        trackedEntityInstanceFilters = mutableListOf(trackedEntityInstanceFilter)
    }

    @Test
    @Suppress("UnusedPrivateMember")
    fun extend_identifiable_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<TrackedEntityInstanceFilter> =
            TrackedEntityInstanceFilterHandler(
                trackedEntityInstanceFilterStore,
                trackedEntityInstanceEventFilterHandler,
                attributeValueFilterHandler,
            )
    }

    @Test
    fun handle_event_filters() = runTest {
        trackedEntityInstanceFilterHandler.handleMany(trackedEntityInstanceFilters)
        verify(trackedEntityInstanceEventFilterHandler).handleMany(eventFilters)
    }
}
