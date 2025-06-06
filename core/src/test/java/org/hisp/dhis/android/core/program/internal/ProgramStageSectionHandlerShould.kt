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
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramStageSectionHandlerShould {
    private val programStageSectionStore: ProgramStageSectionStoreImpl = mock()
    private val programStageSectionProgramIndicatorLinkHandler: ProgramStageSectionProgramIndicatorLinkHandler = mock()
    private val programStageSectionDataElementLinkHandler: ProgramStageSectionDataElementLinkHandler = mock()
    private val programStageSection: ProgramStageSection = mock()
    private val dataElement: DataElement = mock()
    private val programIndicator: ProgramIndicator = mock()

    // object to test
    private lateinit var programStageSectionHandler: ProgramStageSectionHandler
    private lateinit var programIndicators: MutableList<ProgramIndicator>

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        programStageSectionHandler = ProgramStageSectionHandler(
            programStageSectionStore,
            programStageSectionProgramIndicatorLinkHandler, programStageSectionDataElementLinkHandler,
        )

        whenever(programStageSection.uid()).thenReturn(PROGRAM_STAGE_SECTION_UID)
        val dataElements = mutableListOf(dataElement)
        programIndicators = mutableListOf(programIndicator)
        whenever(programStageSection.dataElements()).thenReturn(dataElements)
        whenever(programStageSection.programIndicators()).thenReturn(programIndicators)
        whenever(dataElement.uid()).thenReturn("data_element_uid")
        whenever(programIndicator.uid()).thenReturn("program_indicator_uid")
        whenever(programStageSectionStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun handle_program_stage_section_data_element_links() = runTest {
        programStageSectionHandler.handle(programStageSection)
        verify(programStageSectionDataElementLinkHandler).handleMany(any(), any<List<DataElement>>(), any())
    }

    @Test
    fun handle_program_stage_section_program_indicator_links() = runTest {
        programStageSectionHandler.handle(programStageSection)
        verify(programStageSectionProgramIndicatorLinkHandler).handleMany(any(), any<List<ProgramIndicator>>(), any())
    }

    companion object {
        private const val PROGRAM_STAGE_SECTION_UID = "test_program_stage_section_uid"
    }
}
