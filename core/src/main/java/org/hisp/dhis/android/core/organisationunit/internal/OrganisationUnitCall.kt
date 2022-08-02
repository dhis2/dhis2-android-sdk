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
import io.reactivex.Flowable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@Reusable
internal class OrganisationUnitCall @Inject constructor(
    private val organisationUnitService: OrganisationUnitService,
    private val handler: OrganisationUnitHandler,
    private val pathTransformer: OrganisationUnitDisplayPathTransformer,
    private val collectionCleaner: CollectionCleaner<OrganisationUnit>
) {
    fun download(user: User): Single<List<OrganisationUnit>> {
        return Single.defer {
            handler.resetLinks()
            val rootSearchOrgUnits =
                OrganisationUnitTree.findRoots(UserInternalAccessor.accessTeiSearchOrganisationUnits(user))

            downloadSearchOrgUnits(rootSearchOrgUnits, user)
                .flatMap { searchOrgUnits ->
                    println("AAA Downloaded " + searchOrgUnits.size)
                    downloadDataCaptureOrgUnits(
                        rootSearchOrgUnits,
                        searchOrgUnits,
                        user
                    ).map { dataCaptureOrgUnits ->
                        val allOrgunits = searchOrgUnits + dataCaptureOrgUnits
                        collectionCleaner.deleteNotPresent(allOrgunits)
                        allOrgunits
                    }
                }
        }
    }

    private fun downloadSearchOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        user: User
    ): Single<List<OrganisationUnit>> {
        return downloadOrgUnits(getUids(rootSearchOrgUnits), user, OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
    }

    private fun downloadDataCaptureOrgUnits(
        rootSearchOrgUnits: Set<OrganisationUnit>,
        searchOrgUnits: List<OrganisationUnit>,
        user: User
    ): Single<List<OrganisationUnit>> {
        val allRootCaptureOrgUnits = OrganisationUnitTree.findRoots(UserInternalAccessor.accessOrganisationUnits(user))
        val rootCaptureOrgUnitsOutsideSearchScope =
            OrganisationUnitTree.findRootsOutsideSearchScope(allRootCaptureOrgUnits, rootSearchOrgUnits)
        linkCaptureOrgUnitsInSearchScope(
            OrganisationUnitTree.getCaptureOrgUnitsInSearchScope(
                searchOrgUnits,
                allRootCaptureOrgUnits,
                rootCaptureOrgUnitsOutsideSearchScope
            ), user
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
    ): Single<List<OrganisationUnit>> {
        handler.setData(user, scope)
        println("AAA Downloading for " + orgUnits.joinToString() )
        return Flowable.fromIterable(orgUnits)
            .flatMap { orgUnit -> downloadOrganisationUnitAndDescendants(orgUnit) }
            .reduce(emptyList()) { items, items2 ->
                items + items2
            }
    }

    private fun linkCaptureOrgUnitsInSearchScope(orgUnits: Set<OrganisationUnit>, user: User) {
        handler.setData(user, OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
        handler.addUserOrganisationUnitLinks(orgUnits)
    }

    private fun downloadOrganisationUnitAndDescendants(orgUnit: String): Flowable<List<OrganisationUnit>> {
        val page = AtomicInteger(1)
        return downloadPage(orgUnit, page)
            .repeat()
            .takeUntil { organisationUnits: List<OrganisationUnit> -> organisationUnits.size < PAGE_SIZE }
    }

    private fun downloadPage(orgUnit: String, page: AtomicInteger): Single<List<OrganisationUnit>> {
        var start: Long? = null
        var download: Long? = null
        var parse: Long? = null
        var handling: Long? = null
        return Single.defer {
            start = Date().time
            organisationUnitService.getOrganisationUnits(
                OrganisationUnitFields.allFields, OrganisationUnitFields.path.like(orgUnit),
                OrganisationUnitFields.ASC_ORDER, true, PAGE_SIZE, page.getAndIncrement()
            )
                .map { obj ->
                    download = Date().time
                    obj.items().also {
                        parse = Date().time
                    }
                }
                .doOnSuccess { items ->
                    handler.handleMany(items, pathTransformer).also {
                        handling = Date().time

                        println("AAA;${download!! - start!!};${parse!! - download!!};${handling!! - parse!!};${handling!! - start!!}")
                    }
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 500
    }
}