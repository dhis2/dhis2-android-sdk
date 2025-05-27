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
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackedEntityAttributeHandlerShould {
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore = mock()
    private val trackedEntityAttributeLegendSetLinkHandler: TrackedEntityAttributeLegendSetLinkHandler = mock()
    private val objectStyle: ObjectStyle = mock()
    private val access: Access = mock()

    // object to test
    private lateinit var trackedEntityAttributeHandler: TrackedEntityAttributeHandler
    private lateinit var trackedEntityAttributes: MutableList<TrackedEntityAttribute>
    private lateinit var trackedEntityAttribute: TrackedEntityAttribute

    @Before
    @Throws(Exception::class)
    fun setUp() {
        trackedEntityAttributeHandler = TrackedEntityAttributeHandler(
            trackedEntityAttributeStore,
            trackedEntityAttributeLegendSetLinkHandler,
        )

        trackedEntityAttribute = TrackedEntityAttribute.builder()
            .uid("test_tracked_entity_attribute_uid")
            .style(objectStyle)
            .name("name")
            .displayName("display_name")
            .formName("form_name")
            .access(access)
            .build()

        trackedEntityAttributes = mutableListOf(trackedEntityAttribute)
        whenever(access.read()).thenReturn(true)
    }

    @Test
    fun extend_identifiable_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<TrackedEntityAttribute> =
            TrackedEntityAttributeHandler(trackedEntityAttributeStore, trackedEntityAttributeLegendSetLinkHandler)
    }

    @Test
    fun delete_tea_if_no_read_access() = runTest {
        whenever(access.read()).thenReturn(false)
        trackedEntityAttributeHandler.handleMany(trackedEntityAttributes)
        verify(trackedEntityAttributeStore).deleteIfExists(trackedEntityAttribute.uid())
    }
}
