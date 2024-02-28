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
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.UnwrappedEqInFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams.QueryParams
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress
import org.koin.core.annotation.Singleton

@Singleton
class TrackedEntityInstanceDownloader internal constructor(
    scope: RepositoryScope,
    private val downloadCall: TrackedEntityInstanceDownloadCall,
) : BaseRepositoryImpl<TrackedEntityInstanceDownloader>(
    scope,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        TrackedEntityInstanceDownloader(
            s,
            downloadCall,
        )
    },
) {

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
        val params = ProgramDataDownloadParams.fromRepositoryScope(scope)
        return downloadCall.download(params).asObservable()
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }

    fun byUid(): UnwrappedEqInFilterConnector<TrackedEntityInstanceDownloader> {
        return cf.unwrappedEqIn(QueryParams.UID)
    }

    fun byProgramUid(programUid: String): TrackedEntityInstanceDownloader {
        return cf.baseString(QueryParams.PROGRAM).eq(programUid)
    }

    fun limitByOrgunit(limitByOrgunit: Boolean): TrackedEntityInstanceDownloader {
        return cf.bool(QueryParams.LIMIT_BY_ORGUNIT).eq(limitByOrgunit)
    }

    fun limitByProgram(limitByProgram: Boolean): TrackedEntityInstanceDownloader {
        return cf.bool(QueryParams.LIMIT_BY_PROGRAM).eq(limitByProgram)
    }

    fun limit(limit: Int): TrackedEntityInstanceDownloader {
        return cf.integer(QueryParams.LIMIT).eq(limit)
    }

    fun byProgramStatus(status: EnrollmentScope): TrackedEntityInstanceDownloader {
        return cf.baseString(QueryParams.PROGRAM_STATUS).eq(status.toString())
    }

    /**
     * If true, it overwrites existing TEIs in the device with the TEIs returned from the server. It does not modify
     * TEIs that are not returned from the server.
     *
     * @param overwrite True to overwrite
     * @return the new repository
     */
    fun overwrite(overwrite: Boolean): TrackedEntityInstanceDownloader {
        return cf.bool(QueryParams.OVERWRITE).eq(overwrite)
    }
}
