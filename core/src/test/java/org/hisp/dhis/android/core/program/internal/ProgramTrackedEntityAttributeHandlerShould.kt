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
package org.hisp.dhis.android.core.program.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueTypeRendering
import org.hisp.dhis.android.core.common.valuetype.rendering.internal.ValueTypeRenderingHandler
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeTableInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramTrackedEntityAttributeHandlerShould {
    private val programTrackedEntityAttributeStore: ProgramTrackedEntityAttributeStore = mock()
    private var programTrackedEntityAttributes: MutableList<ProgramTrackedEntityAttribute> = mock()
    private val programTrackedEntityAttribute: ProgramTrackedEntityAttribute = mock()
    private val renderTypeHandler: ValueTypeRenderingHandler = mock()
    private val program: ObjectWithUid = mock()
    private val valueTypeRendering: ValueTypeRendering = mock()
    private val trackedEntityAttribute: ObjectWithUid = mock()

    // object to test
    private lateinit var handler: Handler<ProgramTrackedEntityAttribute>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        handler = ProgramTrackedEntityAttributeHandler(programTrackedEntityAttributeStore, renderTypeHandler)
        programTrackedEntityAttributes = ArrayList()
        programTrackedEntityAttributes.add(programTrackedEntityAttribute)

        whenever(programTrackedEntityAttribute.trackedEntityAttribute()).thenReturn(trackedEntityAttribute)
        whenever(programTrackedEntityAttribute.uid()).thenReturn("program_tracked_entity_attribute_uid")
        whenever(programTrackedEntityAttribute.renderType()).thenReturn(valueTypeRendering)
        whenever(trackedEntityAttribute.uid()).thenReturn("tracked_entity_attribute_uid")
        whenever(programTrackedEntityAttribute.program()).thenReturn(program)
        whenever(program.uid()).thenReturn("program_uid")
        whenever(programTrackedEntityAttributeStore.updateOrInsert(any<List<ProgramTrackedEntityAttribute>>())).thenReturn(
            listOf(HandleAction.Insert)
        )
    }

    @Test
    @Suppress("UnusedPrivateMember")
    fun extend_identifiable_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<ProgramTrackedEntityAttribute> =
            ProgramTrackedEntityAttributeHandler(programTrackedEntityAttributeStore, renderTypeHandler)
    }

    @Test
    fun call_render_type_handler() = runTest {
        handler.handleMany(programTrackedEntityAttributes)
        verify(renderTypeHandler).handle(
            valueTypeRendering,
            programTrackedEntityAttribute.uid(),
            ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name(),
        )
    }
}
