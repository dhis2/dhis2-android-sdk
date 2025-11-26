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
package org.hisp.dhis.android.core.maintenance

import com.google.common.collect.Lists
import com.google.common.truth.Truth.*
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramRule
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PerformanceHintsServiceShould {
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit> = mock()
    private val programStore: IdentifiableObjectStore<Program> = mock()
    private val programRuleStore: IdentifiableObjectStore<ProgramRule> = mock()
    private val programRule1: ProgramRule = mock()
    private val programRule2: ProgramRule = mock()
    private val programRule3: ProgramRule = mock()
    private val programRule4: ProgramRule = mock()

    private lateinit var program1: Program
    private lateinit var program2: Program

    private lateinit var performanceHintsService: PerformanceHintsService

    @Before
    @Throws(IOException::class)
    fun setUp() = runTest {
        program1 = Program.builder().uid("p1").build()
        program2 = Program.builder().uid("p2").build()

        whenever(programRule1.program()).thenReturn(ObjectWithUid.create(program1.uid()))
        whenever(programRule2.program()).thenReturn(ObjectWithUid.create(program2.uid()))
        whenever(programRule3.program()).thenReturn(ObjectWithUid.create(program2.uid()))
        whenever(programRule4.program()).thenReturn(ObjectWithUid.create(program2.uid()))

        whenever(organisationUnitStore.count()).thenReturn(0)
        whenever(programStore.selectByUid("p1")).thenReturn(program1)
        whenever(programStore.selectByUid("p2")).thenReturn(program2)
        whenever(programRuleStore.selectAll()).thenReturn(emptyList())

        performanceHintsService = PerformanceHintsService(
            organisationUnitStore, programStore, programRuleStore,
            ORGANISATION_UNIT_THRESHOLD, PROGRAM_RULES_PER_PROGRAM_THRESHOLD,
        )
    }

    @Test
    fun no_organisation_unit_vulnerable_when_no_organisation_units() {
        assertThat(performanceHintsService.areThereExcessiveOrganisationUnits()).isFalse()
        assertThat(performanceHintsService.areThereVulnerabilities()).isFalse()
    }

    @Test
    fun no_organisation_unit_vulnerable_when_organisation_units_under_threshold() = runTest {
        whenever(organisationUnitStore.count()).thenReturn(1)
        assertThat(performanceHintsService.areThereExcessiveOrganisationUnits()).isFalse()
        assertThat(performanceHintsService.areThereVulnerabilities()).isFalse()
    }

    @Test
    fun no_organisation_unit_vulnerable_when_organisation_units_equal_to_threshold() = runTest {
        whenever(organisationUnitStore.count()).thenReturn(3)
        assertThat(performanceHintsService.areThereExcessiveOrganisationUnits()).isFalse()
        assertThat(performanceHintsService.areThereVulnerabilities()).isFalse()
    }

    @Test
    fun no_organisation_unit_vulnerable_when_organisation_units_over_threshold() = runTest {
        whenever(organisationUnitStore.count()).thenReturn(4)
        assertThat(performanceHintsService.areThereExcessiveOrganisationUnits()).isTrue()
        assertThat(performanceHintsService.areThereVulnerabilities()).isTrue()
    }

    @Test
    fun no_program_rule_vulnerable_when_no_programs_nor_program_rules() {
        assertThat(performanceHintsService.areThereProgramsWithExcessiveProgramRules()).isFalse()
        assertThat(performanceHintsService.programsWithExcessiveProgramRules.size).isEqualTo(0)
        assertThat(performanceHintsService.areThereVulnerabilities()).isFalse()
    }

    @Test
    fun no_program_rule_vulnerable_when_one_program_under_threshold() = runTest {
        whenever(programStore.selectAll()).thenReturn(Lists.newArrayList(program1))
        whenever(programRuleStore.selectAll()).thenReturn(Lists.newArrayList(programRule1))

        assertThat(performanceHintsService.areThereProgramsWithExcessiveProgramRules()).isFalse()
        assertThat(performanceHintsService.programsWithExcessiveProgramRules.size).isEqualTo(0)
        assertThat(performanceHintsService.areThereVulnerabilities()).isFalse()
    }

    @Test
    fun is_program_rule_vulnerable_when_one_program_over_threshold() = runTest {
        whenever(programStore.selectAll()).thenReturn(listOf(program1, program2))
        whenever(programRuleStore.selectAll())
            .thenReturn(listOf(programRule1, programRule2, programRule3, programRule4))

        assertThat(performanceHintsService.areThereProgramsWithExcessiveProgramRules()).isTrue()

        val programs = performanceHintsService.programsWithExcessiveProgramRules

        assertThat(programs.size).isEqualTo(1)
        assertThat(programs[0]?.uid()).isEqualTo("p2")

        assertThat(performanceHintsService.areThereVulnerabilities()).isTrue()
    }

    companion object {
        private const val ORGANISATION_UNIT_THRESHOLD = 3
        private const val PROGRAM_RULES_PER_PROGRAM_THRESHOLD = 2
    }
}
