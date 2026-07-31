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
package org.hisp.dhis.android.core.event

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.call.internal.collectAndWrapException
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.event.internal.EventDownloadCall
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.tracker.exporter.TrackerD2Progress
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class EventDownloader internal constructor(
    private val call: EventDownloadCall,
    private val params: ProgramDataDownloadParams,
    val eventFilterCollectionRepository: EventFilterCollectionRepository,
) : BaseRepository {

    private val connectorFactory: ScopedFilterConnectorFactory<EventDownloader, ProgramDataDownloadParams> =
        ScopedFilterConnectorFactory { params ->
            EventDownloader(call, params, eventFilterCollectionRepository)
        }

    /**
     * Downloads and persists Events from the server. Only instances in capture scope are downloaded.
     * This method keeps track of the latest successful download in order to void downloading unmodified data.
     *
     * It makes use of paging with a best effort strategy: in case a page fails to be downloaded or persisted, it is
     * skipped and the rest of pages are persisted.
     *
     * @return -
     */
    fun flowDownload(): Flow<TrackerD2Progress> {
        return call.download(params)
    }

    @Deprecated(message = "Use rxDownload instead", ReplaceWith("rxDownload()"))
    fun download(): Observable<TrackerD2Progress> {
        return flowDownload().asObservable()
    }

    fun rxDownload(): Observable<TrackerD2Progress> {
        return flowDownload().asObservable()
    }

    fun blockingDownload() {
        flowDownload().collectAndWrapException()
    }

    fun byUid(): ListFilterConnector<EventDownloader, String> =
        connectorFactory.listConnector { uids -> params.copy(uids = uids) }

    fun byProgramUid(programUid: String): EventDownloader =
        connectorFactory.eqConnector<String> { programUid ->
            params.copy(program = programUid)
        }.eq(programUid)

    fun limitByOrgunit(limitByOrgunit: Boolean): EventDownloader =
        connectorFactory.eqConnector<Boolean> { limitByOrgunit ->
            params.copy(limitByOrgunit = limitByOrgunit)
        }.eq(limitByOrgunit)

    fun limitByProgram(limitByProgram: Boolean): EventDownloader =
        connectorFactory.eqConnector<Boolean> { limitByProgram ->
            params.copy(limitByProgram = limitByProgram)
        }.eq(limitByProgram)

    fun limit(limit: Int): EventDownloader =
        connectorFactory.eqConnector<Int> { limit ->
            params.copy(limit = limit)
        }.eq(limit)

    fun byFilterUid(): ListFilterConnector<EventDownloader, String> {
        return connectorFactory.listConnector { filterUids ->
            val eventFilters = eventFilterCollectionRepository
                .byUid().`in`(filterUids)
                .withEventDataFilters()
                .blockingGet()
            params.copy(eventFilters = eventFilters)
        }
    }

    fun byEventFilter(): ListFilterConnector<EventDownloader, EventFilter> =
        connectorFactory.listConnector { eventFilters ->
            params.copy(eventFilters = eventFilters)
        }

    /**
     * If true, the file resources (files and images) linked to the data values of the downloaded events are
     * downloaded as part of this call, right after each batch of events is persisted. Only the files that have not
     * been previously downloaded are requested, and a file that fails to be downloaded is ignored.
     *
     * This is the recommended way of downloading tracker file resources, as it keeps data and files in sync instead
     * of relying on a separate call once the data download has finished.
     *
     * The maximum file size is taken from the synchronization settings.
     *
     * @param downloadFileResources True to download the file resources
     * @return the new repository
     */
    fun downloadFileResources(downloadFileResources: Boolean): EventDownloader =
        connectorFactory.eqConnector<Boolean> { value ->
            params.copy(downloadFileResources = value ?: false)
        }.eq(downloadFileResources)
}
