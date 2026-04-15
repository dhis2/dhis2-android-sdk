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
package org.hisp.dhis.android.core.organisationunit

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.koin.core.annotation.Singleton
import java.util.*

@Singleton
class OrganisationUnitService(
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
) {

    fun blockingIsDateInOrgunitRange(organisationUnitUid: String, date: Date): Boolean {
        return runBlocking { suspendIsDateInOrgunitRange(organisationUnitUid, date) }
    }

    @Deprecated(
        message = "Use rxIsDateInOrgunitRange instead",
        ReplaceWith("rxIsDateInOrgunitRange(organisationUnitUid, date)"),
    )
    fun isDateInOrgunitRange(organisationUnitUid: String, date: Date): Single<Boolean> {
        return rxSingle { suspendIsDateInOrgunitRange(organisationUnitUid, date) }
    }

    fun rxIsDateInOrgunitRange(organisationUnitUid: String, date: Date): Single<Boolean> {
        return rxSingle { suspendIsDateInOrgunitRange(organisationUnitUid, date) }
    }

    suspend fun suspendIsDateInOrgunitRange(organisationUnitUid: String, date: Date): Boolean {
        val organisationUnit = organisationUnitRepository.uid(organisationUnitUid).suspendGet() ?: return true

        return organisationUnit.openingDate()?.before(date) ?: true &&
            organisationUnit.closedDate()?.after(date) ?: true
    }

    fun blockingIsInCaptureScope(organisationUnitUid: String): Boolean = runBlocking {
        suspendIsInCaptureScope(organisationUnitUid)
    }

    @Deprecated(message = "Use rxIsInCaptureScope instead", ReplaceWith("rxIsInCaptureScope(organisationUnitUid)"))
    fun isInCaptureScope(organisationUnitUid: String): Single<Boolean> {
        return rxSingle { suspendIsInCaptureScope(organisationUnitUid) }
    }

    fun rxIsInCaptureScope(organisationUnitUid: String): Single<Boolean> {
        return rxSingle { suspendIsInCaptureScope(organisationUnitUid) }
    }

    suspend fun suspendIsInCaptureScope(organisationUnitUid: String): Boolean {
        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .byUid().eq(organisationUnitUid)
            .suspendGet().isNotEmpty()
    }

    internal suspend fun isInSearchScope(organisationUnitUid: String): Boolean {
        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_TEI_SEARCH)
            .byUid().eq(organisationUnitUid)
            .suspendGet().isNotEmpty()
    }
}
