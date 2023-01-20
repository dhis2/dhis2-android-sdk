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

package org.hisp.dhis.android.core.datastore

import dagger.Reusable
import io.reactivex.Observable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.datastore.internal.DataStoreEntryDownloadCall
import org.hisp.dhis.android.core.datastore.internal.DataStoreEntryDownloadParams
import javax.inject.Inject

@Reusable
class DataStoreDownloader @Inject internal constructor(
    private val call: DataStoreEntryDownloadCall,
    private val params: DataStoreEntryDownloadParams
): BaseRepository {
    private val connectorFactory: ScopedFilterConnectorFactory<DataStoreDownloader, DataStoreEntryDownloadParams> =
        ScopedFilterConnectorFactory { params ->
            DataStoreDownloader(call, params)
        }

    /**
     * Download and persist the content in the dataStore according to the filters specified.
     */
    fun download(): Observable<D2Progress> {
        return call.download(params)
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }

    fun byNamespace(): ListFilterConnector<DataStoreDownloader, String> {
        return connectorFactory.listConnector { list -> params.copy(namespaces = list) }
    }
}
