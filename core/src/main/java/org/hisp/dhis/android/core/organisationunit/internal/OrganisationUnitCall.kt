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

import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import org.hisp.dhis.android.persistence.user.UserOrganisationUnitTableInfo
import org.koin.core.annotation.Singleton
import java.util.concurrent.atomic.AtomicInteger

@Singleton
internal class OrganisationUnitCall(
    private val networkHandler: OrganisationUnitNetworkHandler,
    private val handler: OrganisationUnitHandler,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val organisationUnitStore: OrganisationUnitStore,
    private val collectionCleaner: OrganisationUnitCollectionCleaner,
) {

    companion object {
        private const val PAGE_SIZE = 500
    }

    suspend fun download(user: User) {
        handler.resetLinks()
        val rootSearchOrgUnits =
            OrganisationUnitTree.findRoots(UserInternalAccessor.accessTeiSearchOrganisationUnits(user))

        downloadSearchOrgUnits(rootSearchOrgUnits, user)
        val searchOrgUnitIds = userOrganisationUnitLinkStore
            .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
        val searchOrgUnits = organisationUnitStore.selectByUids(searchOrgUnitIds)
        downloadDataCaptureOrgUnits(
            rootSearchOrgUnits,
            searchOrgUnits,
            user,
        )
        val assignedOrgunitIds = userOrganisationUnitLinkStore
            .selectStringColumnsWhereClause(
                UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT,
                "1",
            )
        collectionCleaner.deleteNotPresentByUid(assignedOrgunitIds)
    }

    private suspend fun downloadSearchOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        user: User,
    ) {
        return downloadOrgUnits(getUids(rootSearchOrgUnits), user, OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
    }

    private suspend fun downloadDataCaptureOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        searchOrgUnits: List<OrganisationUnit>,
        user: User,
    ) {
        val allRootCaptureOrgUnits = OrganisationUnitTree.findRoots(UserInternalAccessor.accessOrganisationUnits(user))
        val rootCaptureOrgUnitsOutsideSearchScope =
            OrganisationUnitTree.findRootsOutsideSearchScope(allRootCaptureOrgUnits, rootSearchOrgUnits)
        linkCaptureOrgUnitsInSearchScope(
            OrganisationUnitTree.getCaptureOrgUnitsInSearchScope(
                searchOrgUnits,
                allRootCaptureOrgUnits,
                rootCaptureOrgUnitsOutsideSearchScope,
            ),
            user,
        )
        downloadOrgUnits(
            getUids(rootCaptureOrgUnitsOutsideSearchScope),
            user,
            OrganisationUnit.Scope.SCOPE_DATA_CAPTURE,
        )
    }

    private suspend fun downloadOrgUnits(
        orgUnits: Set<String>,
        user: User,
        scope: OrganisationUnit.Scope,
    ) {
        if (orgUnits.isEmpty()) {
            return
        } else {
            handler.setData(user, scope)
            orgUnits.forEach { orgUnit -> downloadOrganisationUnitAndDescendants(orgUnit) }
        }
    }

    private suspend fun linkCaptureOrgUnitsInSearchScope(orgUnits: Set<OrganisationUnit>, user: User) {
        handler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        handler.addUserOrganisationUnitLinks(orgUnits)
    }

    private suspend fun downloadOrganisationUnitAndDescendants(orgUnit: String) {
        val page = AtomicInteger(1)

        while (true) {
            val downloadedPageSize = downloadPage(orgUnit, page)
            if (downloadedPageSize < PAGE_SIZE) {
                break
            }
        }
    }

    private suspend fun downloadPage(orgUnit: String, page: AtomicInteger): Int {
        val response = networkHandler.getOrganisationUnits(orgUnit, PAGE_SIZE, page.getAndIncrement())

        val orgunits = response.items
        handler.handleMany(orgunits)

        return orgunits.size
    }
}
