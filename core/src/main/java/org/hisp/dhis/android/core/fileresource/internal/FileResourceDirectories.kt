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

package org.hisp.dhis.android.core.fileresource.internal

import android.util.Log
import okio.FileSystem
import okio.IOException
import okio.Path
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.PlatformDirectories
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseNameGenerator
import org.koin.core.annotation.Singleton

/**
 * Directories where the files associated with the file resources are stored. Each account has its own subfolder,
 * named after its database, inside the root directories.
 *
 * Getters create the directory if it does not exist yet.
 */
@Singleton
internal class FileResourceDirectories(
    private val fileSystem: FileSystem,
    private val platformDirectories: PlatformDirectories,
    private val databaseAdapter: DatabaseAdapter,
) {

    private val filesRoot: Path
        get() = platformDirectories.filesDir / FILES_DIR

    private val cacheRoot: Path
        get() = platformDirectories.cacheDir / CACHE_DIR

    fun fileResourceDirectory(): Path {
        return createIfMissing(filesRoot / currentSubfolder())
    }

    fun fileCacheResourceDirectory(): Path {
        return createIfMissing(cacheRoot / currentSubfolder())
    }

    fun rootFileResourceDirectory(): Path {
        return createIfMissing(filesRoot)
    }

    fun rootFileCacheResourceDirectory(): Path {
        return createIfMissing(cacheRoot)
    }

    fun deleteFileResourceDirectory() {
        deleteRecursively(filesRoot / currentSubfolder())
    }

    fun deleteFileResourceDirectories(databaseAccount: DatabaseAccount) {
        val subfolder = subfolderName(databaseAccount.databaseName())
        deleteRecursively(filesRoot / subfolder)
        deleteRecursively(cacheRoot / subfolder)
    }

    private fun currentSubfolder(): String {
        return subfolderName(databaseAdapter.getDatabaseName())
    }

    private fun createIfMissing(directory: Path): Path {
        if (!fileSystem.exists(directory)) {
            try {
                fileSystem.createDirectories(directory)
            } catch (e: IOException) {
                Log.w(TAG, "Could not create directory $directory", e)
            }
        }
        return directory
    }

    private fun deleteRecursively(directory: Path) {
        try {
            fileSystem.deleteRecursively(directory)
        } catch (e: IOException) {
            Log.w(TAG, "Could not delete directory $directory", e)
        }
    }

    companion object {
        const val FILES_DIR = "sdk_resources"
        const val CACHE_DIR = "sdk_cache_resources"

        private const val TAG = "FileResourceDirectories"

        fun subfolderName(databaseName: String): String {
            return databaseName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)
        }
    }
}
