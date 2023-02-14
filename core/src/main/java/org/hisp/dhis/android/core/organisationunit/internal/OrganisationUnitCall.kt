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

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Reusable
internal class OrganisationUnitCall @Inject constructor(
    private val organisationUnitService: OrganisationUnitService,
    private val handler: OrganisationUnitHandler,
    private val pathTransformer: OrganisationUnitDisplayPathTransformer,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val collectionCleaner: CollectionCleaner<OrganisationUnit>
) {
    fun download(user: User): Completable {
        return Completable.defer {
            handler.resetLinks()
            val rootSearchOrgUnits =
                OrganisationUnitTree.findRoots(UserInternalAccessor.accessTeiSearchOrganisationUnits(user))

            downloadSearchOrgUnits(rootSearchOrgUnits, user)
                .andThen(
                    Completable.defer {
                        val searchOrgUnitIds = userOrganisationUnitLinkStore
                            .queryOrganisationUnitUidsByScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
                        val searchOrgUnits = organisationUnitStore.selectByUids(searchOrgUnitIds)
                        downloadDataCaptureOrgUnits(
                            rootSearchOrgUnits,
                            searchOrgUnits,
                            user
                        )
                    }
                ).doOnComplete {
                    val assignedOrgunitIds = userOrganisationUnitLinkStore
                        .selectStringColumnsWhereClause(
                            UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                            "1"
                        )
                    collectionCleaner.deleteNotPresentByUid(assignedOrgunitIds)
                }
        }
    }

    private fun downloadSearchOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        user: User
    ): Completable {
        return downloadOrgUnits(getUids(rootSearchOrgUnits), user, OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
    }

    private fun downloadDataCaptureOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        searchOrgUnits: List<OrganisationUnit>,
        user: User
    ): Completable {
        val allRootCaptureOrgUnits = OrganisationUnitTree.findRoots(UserInternalAccessor.accessOrganisationUnits(user))
        val rootCaptureOrgUnitsOutsideSearchScope =
            OrganisationUnitTree.findRootsOutsideSearchScope(allRootCaptureOrgUnits, rootSearchOrgUnits)
        linkCaptureOrgUnitsInSearchScope(
            OrganisationUnitTree.getCaptureOrgUnitsInSearchScope(
                searchOrgUnits,
                allRootCaptureOrgUnits,
                rootCaptureOrgUnitsOutsideSearchScope
            ),
            user
        )
        return downloadOrgUnits(
            getUids(rootCaptureOrgUnitsOutsideSearchScope),
            user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE
        )
    }

    private fun downloadOrgUnits(
        orgUnits: Set<String>,
        user: User,
        scope: OrganisationUnit.Scope
    ): Completable {
        return if (orgUnits.isEmpty()) {
            Completable.complete()
        } else {
            handler.setData(user, scope)
            Flowable.fromIterable(orgUnits)
                .flatMapCompletable { orgUnit -> downloadOrganisationUnitAndDescendants(orgUnit) }
        }
    }

    private fun linkCaptureOrgUnitsInSearchScope(orgUnits: Set<OrganisationUnit>, user: User) {
        handler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        handler.addUserOrganisationUnitLinks(orgUnits)
    }

    private fun downloadOrganisationUnitAndDescendants(orgUnit: String): Completable {
        val page = AtomicInteger(1)
        return downloadPage(orgUnit, page)
            .repeat()
            .takeUntil { organisationUnits -> organisationUnits.size < PAGE_SIZE }
            .ignoreElements()
    }

    private fun downloadPage(orgUnit: String, page: AtomicInteger): Single<List<OrganisationUnit>> {
        return Single.defer {
            organisationUnitService.getOrganisationUnits(
                OrganisationUnitFields.allFields, OrganisationUnitFields.path.like(orgUnit),
                OrganisationUnitFields.ASC_ORDER, true, PAGE_SIZE, page.getAndIncrement()
            )
                .map { obj -> obj.items() }
                .doOnSuccess { items -> handler.handleMany(items, pathTransformer) }
        }
    }

    companion object {
        private const val PAGE_SIZE = 500
    }
}
