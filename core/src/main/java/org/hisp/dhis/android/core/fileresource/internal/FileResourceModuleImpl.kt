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
package org.hisp.dhis.android.core.fileresource.internal

import io.reactivex.Observable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.fileresource.FileResourceCollectionRepository
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceDownloader
import org.hisp.dhis.android.core.fileresource.FileResourceModule
import org.hisp.dhis.android.core.fileresource.FileResourceValueType
import org.koin.core.annotation.Singleton

@Singleton
internal class FileResourceModuleImpl(
    private val fileResources: FileResourceCollectionRepository,
    private val fileResourceDownloader: FileResourceDownloader,
) : FileResourceModule {

    @Deprecated(
        "Replace with fileResourceDownloader()",
        replaceWith = ReplaceWith(
            expression = "fileResourceDownloader()\n" +
                "            .byDomainType().eq(FileResourceDomainType.DATA_VALUE)\n" +
                "            .byDataDomainType().eq(FileResourceDataDomainType.TRACKER)\n" +
                "            .byValueType().eq(FileResourceValueType.IMAGE)\n" +
                "            .download()",
            "org.hisp.dhis.android.core.fileresource.FileResourceDomainType",
            "org.hisp.dhis.android.core.fileresource.FileResourceValueType",
        ),
    )
    override fun download(): Observable<D2Progress> {
        return fileResourceDownloader()
            .byDomainType().eq(FileResourceDomainType.DATA_VALUE)
            .byDataDomainType().eq(FileResourceDataDomainType.TRACKER)
            .byValueType().eq(FileResourceValueType.IMAGE)
            .download()
    }

    @Deprecated(
        "Replace with fileResourceDownloader()",
        replaceWith = ReplaceWith(
            expression = "fileResourceDownloader()\n" +
                "            .byDomainType().eq(FileResourceDomainType.DATA_VALUE)\n" +
                "            .byDataDomainType().eq(FileResourceDataDomainType.TRACKER)\n" +
                "            .byValueType().eq(FileResourceValueType.IMAGE)\n" +
                "            .blockingDownload()",
            "org.hisp.dhis.android.core.fileresource.FileResourceDomainType",
            "org.hisp.dhis.android.core.fileresource.FileResourceValueType",
        ),
    )
    override fun blockingDownload() {
        download().blockingSubscribe()
    }

    override fun fileResources(): FileResourceCollectionRepository {
        return fileResources
    }

    override fun fileResourceDownloader(): FileResourceDownloader {
        return fileResourceDownloader
    }
}
