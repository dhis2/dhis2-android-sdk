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
package org.hisp.dhis.android.core.configuration.internal

import okio.FileSystem
import okio.IOException
import okio.Path
import org.hisp.dhis.android.core.arch.file.internal.PlatformDirectories
import org.koin.core.annotation.Singleton

@Singleton
internal class DatabaseRenamer(
    private val fileSystem: FileSystem,
    private val platformDirectories: PlatformDirectories,
) {

    fun renameDatabase(from: String, to: String): Boolean {
        val suffixes = listOf("-shm", "-wal", "-lck", "-journal")
        for (suffix in suffixes) {
            val fromFile = platformDirectories.databasePath(from + suffix)

            if (fileSystem.exists(fromFile)) {
                move(fromFile, platformDirectories.databasePath(to + suffix))
            }
        }

        return move(platformDirectories.databasePath(from), platformDirectories.databasePath(to))
    }

    private fun move(from: Path, to: Path): Boolean {
        return try {
            fileSystem.atomicMove(from, to)
            true
        } catch (_: IOException) {
            false
        }
    }
}
