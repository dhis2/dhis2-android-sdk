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
package org.hisp.dhis.android.core.dataset.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandHandler
import org.hisp.dhis.android.core.dataset.Section
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class SectionHandlerShould {
    private val sectionStore: SectionStore = mock()
    private val sectionDataElementLinkHandler: SectionDataElementLinkHandler = mock()
    private val greyedFieldsHandler: DataElementOperandHandler = mock()
    private val sectionGreyedFieldsLinkHandler: SectionGreyedFieldsLinkHandler = mock()
    private val sectionIndicatorLinkHandler: SectionIndicatorLinkHandler = mock()
    private val sectionGreyedFieldsStore: SectionGreyedFieldsLinkStore = mock()
    private val section: Section = mock()

    // object to test
    private lateinit var sectionHandler: SectionHandler
    private lateinit var dataElements: MutableList<DataElement>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        sectionHandler = SectionHandler(
            sectionStore,
            sectionDataElementLinkHandler,
            greyedFieldsHandler,
            sectionGreyedFieldsLinkHandler,
            sectionIndicatorLinkHandler,
            sectionGreyedFieldsStore,
        )

        whenever(section.uid()).thenReturn("section_uid")

        dataElements = mutableListOf(DataElement.builder().uid("dataElement_uid").build())
        whenever(section.dataElements()).thenReturn(dataElements)

        val greyedFields: List<DataElementOperand> = emptyList()
        whenever(section.greyedFields()).thenReturn(greyedFields)
        whenever(sectionStore.updateOrInsert(section)).thenReturn(HandleAction.Insert)
    }

    @Test
    fun passingNullArguments_shouldNotPerformAnyAction() = runTest {
        sectionHandler.handle(null)

        verify(sectionStore, never()).delete(any())
        verify(sectionStore, never()).update(any())
        verify(sectionStore, never()).insert(any<List<Section>>())
        verify(sectionStore, never()).insert(any<Section>())
    }

    @Test
    fun handlingSection_shouldHandleLinkedDataElements() = runTest {
        sectionHandler.handle(section)
        verify(sectionDataElementLinkHandler).handleMany(eq(section.uid()), eq(dataElements), any())
        verify(sectionGreyedFieldsLinkHandler).handleMany(eq(section.uid()), any<List<DataElementOperand>>(), any())
    }
}
