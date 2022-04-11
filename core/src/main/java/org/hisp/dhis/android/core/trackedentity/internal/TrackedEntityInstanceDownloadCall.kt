/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import io.reactivex.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Reusable
internal class TrackedEntityInstanceDownloadCall @Inject constructor(
    private val rxCallExecutor: RxAPICallExecutor,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    private val internalCall: TrackedEntityInstanceDownloadInternalCall
) {

    fun download(params: ProgramDataDownloadParams): Observable<D2Progress> {
        val observable = Observable.defer {
            val progressManager = D2ProgressManager(null)
            if (userOrganisationUnitLinkStore.count() == 0) {
                return@defer Observable.just(
                    progressManager.increaseProgress(TrackedEntityInstance::class.java, true)
                )
            } else {
                val relatives = RelationshipItemRelatives()
                return@defer Observable.concat(
                    systemInfoModuleDownloader.downloadWithProgressManager(progressManager),
                    internalCall.downloadTeis(params, progressManager, relatives),
                    downloadRelationships(progressManager, relatives)
                )
            }
        }
        return rxCallExecutor.wrapObservableTransactionally(observable, true)
    }

    private fun downloadRelationships(
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {
        return relationshipDownloadAndPersistCallFactory.downloadAndPersist(relatives).andThen(
            Observable.just(
                progressManager.increaseProgress(
                    TrackedEntityInstance::class.java, true
                )
            )
        )
    }
}
