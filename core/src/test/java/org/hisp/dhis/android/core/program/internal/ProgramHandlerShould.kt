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
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.attribute.internal.ProgramAttributeValueLinkHandler
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramInternalAccessor
import org.hisp.dhis.android.core.program.ProgramRuleVariable
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramHandlerShould {
    private val programStore: ProgramStore = mock()
    private val programRuleVariableHandler: ProgramRuleVariableHandler = mock()
    private val programTrackedEntityAttributeHandler: ProgramTrackedEntityAttributeHandler = mock()
    private val programSectionHandler: ProgramSectionHandler = mock()
    private val orphanCleaner: ProgramOrphanCleaner = mock()
    private val collectionCleaner: ProgramCollectionCleaner = mock()
    private val linkCleaner: ProgramOrganisationUnitLinkCleaner = mock()
    private val programAttributeValueLinkHandler: ProgramAttributeValueLinkHandler = mock()
    private val program: Program = mock()
    private val dataAccess: DataAccess = mock()
    private val access: Access = mock()
    private val relatedProgram: ObjectWithUid = mock()
    private val trackedEntityType: TrackedEntityType = mock()
    private val programTrackedEntityAttributes: List<ProgramTrackedEntityAttribute> = mock()
    private val programRuleVariable: ProgramRuleVariable = mock()
    private val programSections: List<ProgramSection> = mock()

    private val attributeValues: MutableList<AttributeValue> = ArrayList()
    private var programRuleVariables: List<ProgramRuleVariable> = mock()
    private var attributeValue: ObjectWithUid = ObjectWithUid.create("Att_Uid")

    // object to test
    private lateinit var programHandler: ProgramHandler

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        programHandler = ProgramHandler(
            programStore,
            programRuleVariableHandler,
            programTrackedEntityAttributeHandler,
            programSectionHandler,
            orphanCleaner,
            collectionCleaner,
            linkCleaner,
            programAttributeValueLinkHandler,
        )

        whenever(program.uid()).thenReturn("test_program_uid")
        whenever(program.code()).thenReturn("test_program_code")
        whenever(program.name()).thenReturn("test_program_name")
        whenever(program.displayName()).thenReturn("test_program_display_name")
        whenever(program.shortName()).thenReturn("test_program")
        whenever(program.displayShortName()).thenReturn("test_program")
        whenever(program.description()).thenReturn("A test program for the integration tests.")
        whenever(program.displayDescription()).thenReturn("A test program for the integration tests.")

        // Program attributes:
        whenever(program.version()).thenReturn(1)
        whenever(program.onlyEnrollOnce()).thenReturn(true)
        whenever(program.displayEnrollmentDateLabel()).thenReturn("enrollment date")
        whenever(program.displayIncidentDate()).thenReturn(true)
        whenever(program.displayIncidentDateLabel()).thenReturn("incident date label")
        whenever(program.registration()).thenReturn(true)
        whenever(program.selectEnrollmentDatesInFuture()).thenReturn(true)
        whenever(program.dataEntryMethod()).thenReturn(true)
        whenever(program.ignoreOverdueEvents()).thenReturn(false)
        whenever(program.selectIncidentDatesInFuture()).thenReturn(true)
        whenever(program.useFirstStageDuringRegistration()).thenReturn(true)
        whenever(program.displayFrontPageList()).thenReturn(true)
        whenever(program.programType()).thenReturn(ProgramType.WITH_REGISTRATION)
        whenever(program.relatedProgram()).thenReturn(relatedProgram)
        whenever(program.trackedEntityType()).thenReturn(trackedEntityType)

        programRuleVariables = listOf(programRuleVariable)

        whenever(ProgramInternalAccessor.accessProgramTrackedEntityAttributes(program))
            .thenReturn(programTrackedEntityAttributes)
        whenever(ProgramInternalAccessor.accessProgramRuleVariables(program)).thenReturn(programRuleVariables)
        whenever(ProgramInternalAccessor.accessProgramSections(program)).thenReturn(programSections)
        whenever(program.access()).thenReturn(access)
        whenever(access.data()).thenReturn(dataAccess)
        whenever(dataAccess.read()).thenReturn(true)
        whenever(dataAccess.write()).thenReturn(true)

        val attribute: ObjectWithUid = ObjectWithUid.create("Att_Uid")
        val attValue = AttributeValue.builder()
            .value("5")
            .attribute(attribute)
            .build()

        attributeValues.add(attValue)

        whenever(program.attributeValues()).thenReturn(attributeValues)
        whenever(programStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun call_program_tracked_entity_attributes_handler() = runTest {
        programHandler.handle(program)
        verify(programTrackedEntityAttributeHandler).handleMany(any<List<ProgramTrackedEntityAttribute>>())
    }

    @Test
    fun call_program_rule_variable_handler() = runTest {
        programHandler.handle(program)
        verify(programRuleVariableHandler).handleMany(programRuleVariables)
    }

    @Test
    fun call_program_section_handler() = runTest {
        programHandler.handle(program)
        verify(programSectionHandler).handleMany(any<List<ProgramSection>>())
    }

    @Test
    fun clean_orphan_options_after_update() = runTest {
        whenever(programStore.updateOrInsert(any())).thenReturn(HandleAction.Update)
        programHandler.handle(program)
        verify(orphanCleaner).deleteOrphan(program)
    }

    @Test
    fun not_clean_orphan_options_after_insert() = runTest {
        whenever(programStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
        programHandler.handle(program)
        verify(orphanCleaner, never()).deleteOrphan(program)
    }

    @Test
    fun call_collection_cleaner_when_calling_handle_many() = runTest {
        val programs = listOf(program)
        programHandler.handleMany(programs)
        verify(collectionCleaner).deleteNotPresent(programs)
    }

    @Test
    fun not_store_tracker_program_without_tracked_entity_type() = runTest {
        whenever(program.programType()).thenReturn(ProgramType.WITH_REGISTRATION)
        whenever(program.trackedEntityType()).thenReturn(null)
        programHandler.handleMany(listOf(program))
        verifyNoMoreInteractions(programStore)
    }

    @Test
    fun call_attribute_handlers() = runTest {
        programHandler.handleMany(listOf(program))
        verify(programAttributeValueLinkHandler).handleMany(eq(program.uid()), eq(listOf(attributeValue)), any())
    }
}
