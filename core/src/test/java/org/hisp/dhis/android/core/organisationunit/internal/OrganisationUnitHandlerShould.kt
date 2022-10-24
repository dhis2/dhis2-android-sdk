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

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrganisationUnitHandlerShould {
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit> = mock()
    private val organisationUnitProgramLinkStore: LinkStore<OrganisationUnitProgramLink> = mock()
    private val organisationUnitProgramLinkHandler: LinkHandler<ObjectWithUid, OrganisationUnitProgramLink> = mock()
    private val dataSetDataSetOrganisationUnitLinkHandler: LinkHandler<ObjectWithUid, DataSetOrganisationUnitLink> =
        mock()
    private val userOrganisationUnitLinkHandler: LinkHandler<OrganisationUnit, UserOrganisationUnitLink> = mock()
    private val organisationUnitGroupHandler: Handler<OrganisationUnitGroup> = mock()
    private val organisationUnitGroupLinkHandler:
        LinkHandler<OrganisationUnitGroup, OrganisationUnitOrganisationUnitGroupLink> = mock()
    private val organisationUnitGroup: OrganisationUnitGroup = mock()
    private val program: ObjectWithUid = mock()
    private val user: User = mock()

    private lateinit var pathTransformer: OrganisationUnitDisplayPathTransformer
    private lateinit var organisationUnitWithoutGroups: OrganisationUnit
    private lateinit var organisationUnitHandler: OrganisationUnitHandler
    private lateinit var organisationUnits: List<OrganisationUnit>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val programUid = "test_program_uid"
        pathTransformer = OrganisationUnitDisplayPathTransformer()
        organisationUnitHandler = OrganisationUnitHandlerImpl(
            organisationUnitStore, userOrganisationUnitLinkHandler, organisationUnitProgramLinkHandler,
            dataSetDataSetOrganisationUnitLinkHandler, organisationUnitGroupHandler,
            organisationUnitGroupLinkHandler
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
        organisationUnitHandler.handleMany(organisationUnits, pathTransformer)
    }

    @Test
    fun persist_program_organisation_unit_link_when_programs_uids() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits, pathTransformer)
        verify(organisationUnitProgramLinkHandler).handleMany(any(), any(), any())
    }

    @Test
    fun persist_program_organisation_unit_link_when_no_programs_uids() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits, pathTransformer)
        verifyNoMoreInteractions(organisationUnitProgramLinkStore)
    }

    @Test
    fun persist_organisation_unit_groups() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits, pathTransformer)
        verify(organisationUnitGroupHandler).handleMany(any())
    }

    @Test
    fun persist_organisation_unit_organisation_unit_group_link() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(organisationUnits, pathTransformer)
        verify(organisationUnitGroupLinkHandler).handleMany(any(), any(), any())
    }

    @Test
    fun dont_persist_organisation_unit_organisation_unit_group_link_when_no_organisation_unit_groups() {
        organisationUnitHandler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        organisationUnitHandler.handleMany(listOf(organisationUnitWithoutGroups), pathTransformer)
        verify(organisationUnitGroupLinkHandler, never()).handleMany(any(), any(), any())
    }
}
