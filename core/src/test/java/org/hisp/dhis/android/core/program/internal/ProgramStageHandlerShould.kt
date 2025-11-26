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
import org.hisp.dhis.android.core.attribute.internal.ProgramStageAttributeValueLinkHandler
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor
import org.hisp.dhis.android.core.program.ProgramStageSection
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
class ProgramStageHandlerShould {
    private val programStageStore: ProgramStageStore = mock()
    private val programStageSectionHandler: ProgramStageSectionHandler = mock()
    private val programStageDataElementHandler: ProgramStageDataElementHandler = mock()
    private val programStage: ProgramStage = mock()
    private val dataAccess: DataAccess = mock()
    private val access: Access = mock()
    private val objectStyle: ObjectStyle = mock()
    private val programStageDataElement: ProgramStageDataElement = mock()
    private val programStageSection: ProgramStageSection = mock()
    private val programStageDataElementCleaner: ProgramStageDataElementOrphanCleaner = mock()
    private val programStageSectionCleaner: ProgramStageSectionOrphanCleaner = mock()
    private val programStageCleaner: ProgramStageSubCollectionCleaner = mock()
    private val programStageBuilder: ProgramStage.Builder = mock()
    private val programStageAttributeValueLinkHandler: ProgramStageAttributeValueLinkHandler = mock()

    // object to test
    private lateinit var programStageHandler: ProgramStageHandler
    private lateinit var programStageDataElements: List<ProgramStageDataElement>
    private lateinit var programStageSections: MutableList<ProgramStageSection>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        programStageHandler = ProgramStageHandler(
            programStageStore,
            programStageSectionHandler,
            programStageDataElementHandler,
            programStageDataElementCleaner,
            programStageSectionCleaner,
            programStageCleaner,
            programStageAttributeValueLinkHandler,
        )

        programStageSections = mutableListOf(programStageSection)
        programStageDataElements = listOf(programStageDataElement)

        whenever(programStage.uid()).thenReturn("test_program_stage_uid")
        whenever(programStage.style()).thenReturn(objectStyle)
        whenever(ProgramStageInternalAccessor.accessProgramStageDataElements(programStage))
            .thenReturn(programStageDataElements)
        whenever(ProgramStageInternalAccessor.accessProgramStageSections(programStage))
            .thenReturn(programStageSections)
        whenever(dataAccess.read()).thenReturn(true)
        whenever(access.data()).thenReturn(dataAccess)
        whenever(programStage.access()).thenReturn(access)
        whenever(programStage.toBuilder()).thenReturn(programStageBuilder)
        whenever(programStageBuilder.featureType(any())).thenReturn(programStageBuilder)
        whenever(programStageBuilder.build()).thenReturn(programStage)
        whenever(programStageStore.updateOrInsert(any<List<ProgramStage>>())).thenReturn(listOf(HandleAction.Insert))
    }

    @Test
    @Throws(Exception::class)
    fun call_program_stage_data_element_handler() = runTest {
        programStageHandler.handle(programStage)
        verify(programStageDataElementHandler).handleMany(programStageDataElements)
    }

    @Test
    @Throws(Exception::class)
    fun call_program_stage_section_handler() = runTest {
        programStageHandler.handle(programStage)
        verify(programStageSectionHandler).handleMany(eq(programStageSections), any())
    }

    @Test
    fun clean_orphan_data_elements_after_update() = runTest {
        whenever(programStageStore.updateOrInsert(any<List<ProgramStage>>())).thenReturn(listOf(HandleAction.Update))
        programStageHandler.handle(programStage)
        verify(programStageDataElementCleaner).deleteOrphan(programStage, programStageDataElements)
    }

    @Test
    fun not_clean_orphan_data_elements_after_insert() = runTest {
        whenever(programStageStore.updateOrInsert(any<List<ProgramStage>>())).thenReturn(listOf(HandleAction.Insert))
        programStageHandler.handle(programStage)
        verify(programStageDataElementCleaner, never()).deleteOrphan(programStage, programStageDataElements)
    }

    @Test
    fun clean_orphan_sections_after_update() = runTest {
        whenever(programStageStore.updateOrInsert(any<List<ProgramStage>>())).thenReturn(listOf(HandleAction.Update))
        programStageHandler.handle(programStage)
        verify(programStageSectionCleaner).deleteOrphan(programStage, programStageSections)
    }

    @Test
    fun not_clean_orphan_sections_after_insert() = runTest {
        whenever(programStageStore.updateOrInsert(any<List<ProgramStage>>())).thenReturn(listOf(HandleAction.Insert))
        programStageHandler.handle(programStage)
        verify(programStageSectionCleaner, never()).deleteOrphan(programStage, programStageSections)
    }

    @Test
    fun call_collection_cleaner_when_calling_handle_many() = runTest {
        val programStages = listOf(programStage)
        programStageHandler.handleMany(programStages)
        verify(programStageCleaner).deleteNotPresent(programStages)
    }
}
