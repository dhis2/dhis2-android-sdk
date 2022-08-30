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
package org.hisp.dhis.android.core.organisationunit

import dagger.Reusable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

@Reusable
class OrganisationUnitService @Inject constructor(
    private val organisationUnitRepository: OrganisationUnitCollectionRepository
) {

    fun blockingIsDateInOrgunitRange(organisationUnitUid: String, date: Date): Boolean {
        val organisationUnit = organisationUnitRepository.uid(organisationUnitUid).blockingGet() ?: return true

        return organisationUnit.openingDate()?.before(date) ?: true &&
            organisationUnit.closedDate()?.after(date) ?: true
    }

    fun isDateInOrgunitRange(organisationUnitUid: String, date: Date): Single<Boolean> {
        return Single.just(blockingIsDateInOrgunitRange(organisationUnitUid, date))
    }

    fun blockingIsInCaptureScope(organisationUnitUid: String): Boolean {
        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .byUid().eq(organisationUnitUid)
            .blockingGet().isNotEmpty()
    }

    fun isInCaptureScope(organisationUnitUid: String): Single<Boolean> {
        return Single.just(blockingIsInCaptureScope(organisationUnitUid))
    }
}
