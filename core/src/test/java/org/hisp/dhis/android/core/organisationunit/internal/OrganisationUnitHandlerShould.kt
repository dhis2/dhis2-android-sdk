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
package org.hisp.dhis.android.core.organisationunit.internal

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.internal.DataSetOrganisationUnitLinkHandler
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.*

@RunWith(JUnit4::class)
class OrganisationUnitHandlerShould {
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val organisationUnitProgramLinkStore: OrganisationUnitProgramLinkStore = mock()
    private val organisationUnitProgramLinkHandler: OrganisationUnitProgramLinkHandler = mock()
    private val dataSetDataSetOrganisationUnitLinkHandler: DataSetOrganisationUnitLinkHandler = mock()
    private val userOrganisationUnitLinkHandler: UserOrganisationUnitLinkHandler = mock()
    private val organisationUnitGroupHandler: OrganisationUnitGroupHandler = mock()
    private val organisationUnitGroupLinkHandler: OrganisationUnitOrganisationUnitGroupLinkHandler = mock()
    private val organisationUnitGroup: OrganisationUnitGroup = mock()
    private val program: ObjectWithUid = mock()
    private val user: User = mock()

    private lateinit var organisationUnitWithoutGroups: OrganisationUnit
    private lateinit var organisationUnitHandler: OrganisationUnitHandler
    private lateinit var organisationUnits: List<OrganisationUnit>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val programUid = "test_program_uid"
        organisationUnitHandler = OrganisationUnitHandler(
            organisationUnitStore,
            userOrganisationUnitLinkHandler,
            organisationUnitProgramLinkHandler,
            dataSetDataSetOrganisationUnitLinkHandler,
            organisationUnitGroupHandler,
            organisationUnitGroupLinkHandler,
        )
        whenever(user.uid()).doReturn("test_user_uid")
        whenever(program.uid()).doReturn(programUid)
        whenever(organisationUnitGroup.uid()).doReturn("test_organisation_unit_group_uid")
        val organisationUnitGroups = listOf(organisationUnitGroup)
        val builder = OrganisationUnit.builder()
            .uid("test_organisation_unit_uid")
            .programs(listOf(program))
        organisationUnitWithoutGroups = builder
            .build()
        val organisationUnitWithGroups = builder
            .organisationUnitGroups(organisationUnitGroups)
            .build()

        whenever(organisationUnitStore.updateOrInsert(any())).doReturn(HandleAction.Insert)

        organisationUnits = listOf(organisationUnitWithGroups)
    }

    @Test
    fun persist_user_organisation_unit_link() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits)
    }

    @Test
    fun persist_program_organisation_unit_link_when_programs_uids() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits)
        verify(organisationUnitProgramLinkHandler).handleMany(any(), any(), any())
    }

    @Test
    fun persist_program_organisation_unit_link_when_no_programs_uids() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits)
        verifyNoMoreInteractions(organisationUnitProgramLinkStore)
    }

    @Test
    fun persist_organisation_unit_groups() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits)
        verify(organisationUnitGroupHandler).handleMany(any())
    }

    @Test
    fun persist_organisation_unit_organisation_unit_group_link() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits)
        verify(organisationUnitGroupLinkHandler).handleMany(any(), any(), any())
    }

    @Test
    fun dont_persist_organisation_unit_organisation_unit_group_link_when_no_organisation_unit_groups() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(listOf(organisationUnitWithoutGroups))
        verify(organisationUnitGroupLinkHandler, never()).handleMany(any(), any(), any())
    }
}
