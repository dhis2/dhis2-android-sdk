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
package org.hisp.dhis.android.core.fileresource

import io.reactivex.Observable
import okio.Path
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.maintenance.D2Error

interface FileResourceModule {
    @Deprecated(
        "Replace with fileResourceDownloader()",
        replaceWith = ReplaceWith(
            expression = "fileResourceDownloader()\n" +
                "            .byDomainType().eq(FileResourceDomainType.TRACKER)\n" +
                "            .byValueType().eq(FileResourceValueType.IMAGE)\n" +
                "            .download()",
            "org.hisp.dhis.android.core.fileresource.FileResourceDomainType",
            "org.hisp.dhis.android.core.fileresource.FileResourceValueType",
        ),
    )
    fun download(): Observable<D2Progress>

    @Deprecated(
        "Replace with fileResourceDownloader()",
        replaceWith = ReplaceWith(
            expression = "fileResourceDownloader()\n" +
                "            .byDomainType().eq(FileResourceDomainType.TRACKER)\n" +
                "            .byValueType().eq(FileResourceValueType.IMAGE)\n" +
                "            .blockingDownload()",
            "org.hisp.dhis.android.core.fileresource.FileResourceDomainType",
            "org.hisp.dhis.android.core.fileresource.FileResourceValueType",
        ),
    )
    fun blockingDownload()

    fun fileResources(): FileResourceCollectionRepository

    fun fileResourceDownloader(): FileResourceDownloader

    /**
     * Requires a logged-in user, since each account has its own directory.
     */
    fun fileResourceDirectory(): Path

    /**
     * Requires a logged-in user. It is not cleared automatically, so the app has to tidy it up.
     */
    fun fileCacheResourceDirectory(): Path

    /**
     * Returns [image] if it is already smaller than [dimension]. Otherwise the result is written to
     * [fileCacheResourceDirectory] and the caller should delete it.
     */
    @Throws(D2Error::class)
    fun resizeImage(image: Path, dimension: ImageDimension): Path

    /**
     * Compresses to 600 KB, the size used for uploads.
     */
    fun compressImage(image: Path): Path

    /**
     * Returns [image] if it was not compressed. Otherwise the result, a JPEG or a PNG, is written to
     * [fileCacheResourceDirectory] and the caller should delete it.
     */
    fun compressImage(image: Path, targetSizeBytes: Long): Path
}
