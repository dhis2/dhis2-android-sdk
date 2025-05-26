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
package org.hisp.dhis.android.core.organisationunit.internal

import android.util.Log
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink
import org.hisp.dhis.android.core.dataset.internal.DataSetOrganisationUnitLinkHandler
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkHandler
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkHelper
import org.koin.core.annotation.Singleton

@Singleton
internal class OrganisationUnitHandler(
    organisationUnitStore: OrganisationUnitStore,
    private val userOrganisationUnitLinkHandler: UserOrganisationUnitLinkHandler,
    private val organisationUnitProgramLinkHandler: OrganisationUnitProgramLinkHandler,
    private val dataSetOrganisationUnitLinkHandler: DataSetOrganisationUnitLinkHandler,
    private val organisationUnitGroupHandler: OrganisationUnitGroupHandler,
    private val organisationUnitGroupLinkHandler: OrganisationUnitOrganisationUnitGroupLinkHandler,
) : IdentifiableHandlerImpl<OrganisationUnit>(organisationUnitStore) {
    private var user: User? = null
    private var scope: OrganisationUnit.Scope? = null

    suspend fun resetLinks() {
        userOrganisationUnitLinkHandler.resetAllLinks()
        organisationUnitProgramLinkHandler.resetAllLinks()
        dataSetOrganisationUnitLinkHandler.resetAllLinks()
        organisationUnitGroupLinkHandler.resetAllLinks()
    }

    fun setData(user: User, scope: OrganisationUnit.Scope) {
        this.user = user
        this.scope = scope
    }

    override suspend fun beforeCollectionHandled(oCollection: Collection<OrganisationUnit>): Collection<OrganisationUnit> {
        return oCollection
    }

    override suspend fun beforeObjectHandled(o: OrganisationUnit): OrganisationUnit {
        return if (GeometryHelper.isValid(o.geometry())) {
            o
        } else {
            Log.i(
                this.javaClass.simpleName,
                "OrganisationUnit " + o.uid() + " has invalid geometryValue",
            )
            o.toBuilder().geometry(null).build()
        }
    }

    override suspend fun afterObjectHandled(o: OrganisationUnit, action: HandleAction) {
        addOrganisationUnitProgramLink(o)
        addOrganisationUnitDataSetLink(o)
        organisationUnitGroupHandler.handleMany(o.organisationUnitGroups())
        addOrganisationUnitOrganisationUnitGroupLink(o)
    }

    override suspend fun afterCollectionHandled(oCollection: Collection<OrganisationUnit>?) {
        oCollection?.let { addUserOrganisationUnitLinks(it) }
    }

    private suspend fun addOrganisationUnitProgramLink(organisationUnit: OrganisationUnit) {
        val orgUnitPrograms = organisationUnit.programs()
        if (orgUnitPrograms != null) {
            organisationUnitProgramLinkHandler.handleMany(
                organisationUnit.uid(),
                orgUnitPrograms,
            ) { program ->
                OrganisationUnitProgramLink.builder()
                    .organisationUnit(organisationUnit.uid())
                    .program(program.uid())
                    .build()
            }
        }
    }

    private suspend fun addOrganisationUnitDataSetLink(organisationUnit: OrganisationUnit) {
        val orgUnitDataSets = organisationUnit.dataSets()
        if (orgUnitDataSets != null) {
            dataSetOrganisationUnitLinkHandler.handleMany(
                organisationUnit.uid(),
                orgUnitDataSets,
            ) { dataSet ->
                DataSetOrganisationUnitLink.builder()
                    .dataSet(dataSet.uid())
                    .organisationUnit(organisationUnit.uid())
                    .build()
            }
        }
    }

    private suspend fun addOrganisationUnitOrganisationUnitGroupLink(organisationUnit: OrganisationUnit) {
        organisationUnit.organisationUnitGroups()?.let { orgunitGroups ->
            organisationUnitGroupLinkHandler.handleMany(
                organisationUnit.uid(),
                orgunitGroups,
            ) { organisationUnitGroup ->
                OrganisationUnitOrganisationUnitGroupLink.builder()
                    .organisationUnit(organisationUnit.uid())
                    .organisationUnitGroup(organisationUnitGroup.uid())
                    .build()
            }
        }
    }

    suspend fun addUserOrganisationUnitLinks(organisationUnits: Collection<OrganisationUnit>) {
        val builder = UserOrganisationUnitLink.builder()
            .organisationUnitScope(scope!!.name)
            .user(user!!.uid())

        // TODO MasterUid set to "" to avoid cleaning link table. Orgunits are paged, so the whole orguntit list is
        //  not available in the handler. Maybe the store should not be a linkStore.
        userOrganisationUnitLinkHandler.handleMany(
            "",
            organisationUnits,
        ) { orgUnit: OrganisationUnit ->
            builder
                .organisationUnit(orgUnit.uid())
                .root(UserOrganisationUnitLinkHelper.isRoot(scope!!, user!!, orgUnit))
                .userAssigned(UserOrganisationUnitLinkHelper.userIsAssigned(scope, user, orgUnit))
                .build()
        }
    }
}
