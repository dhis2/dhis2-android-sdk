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
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserCall

@Reusable
internal class OrganisationUnitModuleDownloader @Inject constructor(
    private val organisationUnitCall: OrganisationUnitCall,
    private val userCall: UserCall,
    private val organisationUnitLevelEndpointCall: OrganisationUnitLevelEndpointCall,
    private val rxCallExecutor: RxAPICallExecutor,
    private val dataSetLinkCleaner: LinkCleaner<DataSet>,
    private val programLinkCleaner: LinkCleaner<Program>
) {
    fun downloadMetadata(user: User): Completable {
        return organisationUnitLevelEndpointCall.download()
            .flatMapCompletable { organisationUnitCall.download(user) }
    }

    fun refreshOrganisationUnits(): Completable {
        return rxCallExecutor.wrapCompletableTransactionally(
            Single
                .fromCallable { userCall.call() }
                .flatMapCompletable { user -> downloadMetadata(user) }
                .andThen(cleanLinksFromDB()),
            cleanForeignKeys = true
        )
    }

    private fun cleanLinksFromDB(): Completable {
        return Completable.fromCallable {
            dataSetLinkCleaner.deleteNotPresentInDb()
            programLinkCleaner.deleteNotPresentInDb()
        }
    }
}
