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
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ValueTypeRendering
import org.hisp.dhis.android.core.common.valuetype.rendering.internal.ValueTypeRenderingHandler
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementHandler
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramStageDataElementHandlerShould {
    private val programStageDataElementStore: ProgramStageDataElementStore = mock()
    private val dataElementHandler: DataElementHandler = mock()
    private val renderTypeHandler: ValueTypeRenderingHandler = mock()
    private val programStageDataElement: ProgramStageDataElement = mock()
    private val dataElement: DataElement = mock()
    private val valueTypeRendering: ValueTypeRendering = mock()

    // object to test
    private lateinit var handler: ProgramStageDataElementHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        handler = ProgramStageDataElementHandler(programStageDataElementStore, dataElementHandler, renderTypeHandler)
        whenever(programStageDataElement.uid()).thenReturn("program_stage_data_element")
        whenever(programStageDataElement.dataElement()).thenReturn(dataElement)
        whenever(dataElement.uid()).thenReturn("test_data_element_uid")
        whenever(programStageDataElement.renderType()).thenReturn(valueTypeRendering)
        whenever(programStageDataElementStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    @Throws(Exception::class)
    fun call_data_element_handler() = runTest {
        handler.handle(programStageDataElement)
        verify(dataElementHandler).handle(dataElement)
    }

    @Test
    @Throws(Exception::class)
    fun call_value_type_rendering_handler() = runTest {
        handler.handle(programStageDataElement)
        verify(renderTypeHandler).handle(
            programStageDataElement.renderType(),
            programStageDataElement.uid(),
            ProgramStageDataElementTableInfo.TABLE_INFO.name(),
        )
    }

    @Test
    @Suppress("UnusedPrivateMember")
    fun extend_identifiable_handler_impl() = runTest {
        val genericHandler: IdentifiableHandlerImpl<ProgramStageDataElement> =
            ProgramStageDataElementHandler(
                programStageDataElementStore,
                dataElementHandler,
                renderTypeHandler,
            )
    }
}
