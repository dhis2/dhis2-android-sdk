/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.tracker.importer.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipManagerImpl
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackerImporterBreakTheGlassHelperShould {

    private val conflictStore: TrackerImportConflictStore = mock()
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val programStore: ProgramStore = mock()
    private val ownershipManagerImpl: OwnershipManagerImpl = mock()

    private lateinit var helper: TrackerImporterBreakTheGlassHelper

    private val programUid = "program_uid"
    private val orgUnitUid = "orgunit_uid"

    @Before
    fun setUp() {
        helper = TrackerImporterBreakTheGlassHelper(
            conflictStore,
            userOrganisationUnitLinkStore,
            enrollmentStore,
            programStore,
            ownershipManagerImpl,
        )
    }

    @Test
    fun return_true_when_program_is_protected_and_orgunit_is_not_capture_scope() = runTest {
        mockProtectedProgram()
        mockNotCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isTrue()
    }

    @Test
    fun return_false_when_program_is_protected_but_orgunit_is_in_capture_scope() = runTest {
        mockProtectedProgram()
        mockCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_open_and_orgunit_is_not_capture_scope() = runTest {
        mockOpenProgram()
        mockNotCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_open_and_orgunit_is_capture_scope() = runTest {
        mockOpenProgram()
        mockCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_is_null() = runTest {
        val result = helper.isProtectedInSearchScope(null, orgUnitUid)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_orgunit_is_null() = runTest {
        mockProtectedProgram()

        val result = helper.isProtectedInSearchScope(programUid, null)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_both_are_null() = runTest {
        val result = helper.isProtectedInSearchScope(null, null)

        assertThat(result).isFalse()
    }

    @Test
    fun return_false_when_program_not_found() = runTest {
        whenever(programStore.selectByUid(programUid)).doReturn(null)
        mockNotCaptureScope()

        val result = helper.isProtectedInSearchScope(programUid, orgUnitUid)

        assertThat(result).isFalse()
    }

    private suspend fun mockProtectedProgram() {
        val program = Program.builder()
            .uid(programUid)
            .accessLevel(AccessLevel.PROTECTED)
            .build()
        whenever(programStore.selectByUid(programUid)).doReturn(program)
    }

    private suspend fun mockOpenProgram() {
        val program = Program.builder()
            .uid(programUid)
            .accessLevel(AccessLevel.OPEN)
            .build()
        whenever(programStore.selectByUid(programUid)).doReturn(program)
    }

    private suspend fun mockCaptureScope() {
        whenever(userOrganisationUnitLinkStore.isCaptureScope(orgUnitUid)).doReturn(true)
    }

    private suspend fun mockNotCaptureScope() {
        whenever(userOrganisationUnitLinkStore.isCaptureScope(orgUnitUid)).doReturn(false)
    }
}
