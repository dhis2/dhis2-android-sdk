/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.arch.helpers.internal

import android.util.Log
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.D2Manager
import java.io.File

/**
 * Gives the public static image helpers (`FileResizerHelper`, `FileCompressionHelper`) access to an [ImageProcessor].
 * Those helpers are called outside the dependency injection of D2, even before it is instantiated, so they cannot get
 * their dependencies injected. Code inside the Sdk gets the [ImageProcessor] injected instead.
 */
internal object ImageProcessorLocator {

    private val TAG = ImageProcessorLocator::class.java.simpleName

    val processor: ImageProcessor by lazy { AndroidImageProcessor(FileSystem.SYSTEM) }

    /**
     * Where the results of processing [source] are written: the cache directory of the logged account when D2 is
     * instantiated, since they are volatile files, and the directory of [source] otherwise.
     */
    @Suppress("TooGenericExceptionCaught")
    fun outputDirectory(source: File): Path {
        if (!D2Manager.isD2Instantiated()) {
            val parent = requireNotNull(source.absoluteFile.parentFile) { "${source.name} has no parent directory" }
            return parent.toOkioPath()
        }
        val directories = D2Manager.getD2().d2DIComponent.fileResourceDirectories
        return try {
            directories.fileCacheResourceDirectory()
        } catch (e: RuntimeException) {
            Log.w(TAG, "Could not resolve the account cache directory", e)
            directories.rootFileCacheResourceDirectory()
        }
    }
}
