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
package org.hisp.dhis.android.core.trackedentity.internal

import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress
import org.koin.core.annotation.Singleton

@Singleton
class TrackedEntityInstanceDownloader internal constructor(
    private val call: TrackedEntityInstanceDownloadCall,
    private val params: ProgramDataDownloadParams,
) : BaseRepository {

    private val connectorFactory:
        ScopedFilterConnectorFactory<TrackedEntityInstanceDownloader, ProgramDataDownloadParams> =
        ScopedFilterConnectorFactory { params ->
            TrackedEntityInstanceDownloader(call, params)
        }

    /**
     * Downloads and persists TrackedEntityInstances from the server. Only instances in capture scope are downloaded.
     * This method keeps track of the latest successful download in order to void downloading unmodified data.
     *
     * It makes use of paging with a best effort strategy: in case a page fails to be downloaded or persisted, it is
     * skipped and the rest of pages are persisted.
     *
     * @return An Observable that notifies about the progress.
     */
    fun download(): Observable<TrackerD2Progress> {
        return call.download(params).asObservable()
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }

    fun byUid(): ListFilterConnector<TrackedEntityInstanceDownloader, String> =
        connectorFactory.listConnector { uids -> params.toBuilder().uids(uids).build() }

    fun byProgramUid(programUid: String): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<String> { programUid ->
            params.toBuilder().program(programUid).build()
        }.eq(programUid)

    fun limitByOrgunit(limitByOrgunit: Boolean): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<Boolean> { limitByOrgunit ->
            params.toBuilder().limitByOrgunit(limitByOrgunit).build()
        }.eq(limitByOrgunit)

    fun limitByProgram(limitByProgram: Boolean): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<Boolean> { limitByProgram ->
            params.toBuilder().limitByProgram(limitByProgram).build()
        }.eq(limitByProgram)

    fun limit(limit: Int): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<Int> { limit ->
            params.toBuilder().limit(limit).build()
        }.eq(limit)

    fun byProgramStatus(status: EnrollmentScope): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<EnrollmentScope> { status ->
            params.toBuilder().programStatus(status).build()
        }.eq(status)

    /**
     * If true, it overwrites existing TEIs in the device with the TEIs returned from the server. It does not modify
     * TEIs that are not returned from the server.
     *
     * @param overwrite True to overwrite
     * @return the new repository
     */
    fun overwrite(overwrite: Boolean): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<Boolean> { overwrite ->
            params.toBuilder().overwrite(overwrite).build()
        }.eq(overwrite)

    fun byFilterUids(): ListFilterConnector<TrackedEntityInstanceDownloader, String> =
        connectorFactory.listConnector { filterUids -> params.toBuilder().filterUids(filterUids).build() }

    fun byTrackedEntityInstanceFilter(filter: TrackedEntityInstanceFilter): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<TrackedEntityInstanceFilter> { trackedEntityInstanceFilter ->
            params.toBuilder().trackedEntityInstanceFilter(trackedEntityInstanceFilter).build()
        }.eq(filter)

    fun byProgramStageWorkingList(workingList: ProgramStageWorkingList): TrackedEntityInstanceDownloader =
        connectorFactory.eqConnector<ProgramStageWorkingList> { programStageWorkingList ->
            params.toBuilder().programStageWorkingList(programStageWorkingList).build()
        }.eq(workingList)

}
