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
package org.hisp.dhis.android.core.fileresource

import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDownloadParams

@Reusable
class FileResourceDownloader @Inject internal constructor(
    private val call: FileResourceDownloadCall,
    private val params: FileResourceDownloadParams
) : BaseRepository {

    private val connectorFactory: ScopedFilterConnectorFactory<FileResourceDownloader, FileResourceDownloadParams> =
        ScopedFilterConnectorFactory { params ->
            FileResourceDownloader(call, params)
        }

    /**
     * Download and persist file resources according to the filters specified. It only downloads the files that have
     * not been previously downloaded. In case a file fails to be downloaded, it is ignored.
     *
     * @return -
     */
    fun download(): Observable<D2Progress> {
        return call.download(params)
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }

    fun byValueType(): ListFilterConnector<FileResourceDownloader, FileResourceValueType> {
        return connectorFactory.listConnector { list -> params.copy(valueTypes = list) }
    }

    fun byElementType(): ListFilterConnector<FileResourceDownloader, FileResourceElementType> {
        return connectorFactory.listConnector { list -> params.copy(elementTypes = list) }
    }

    fun byDomainType(): ListFilterConnector<FileResourceDownloader, FileResourceDomainType> {
        return connectorFactory.listConnector { list -> params.copy(domainTypes = list) }
    }

    fun byMaxContentLength(): EqFilterConnector<FileResourceDownloader, Int> {
        return connectorFactory.eqConnector { value -> params.copy(maxContentLength = value) }
    }
}
