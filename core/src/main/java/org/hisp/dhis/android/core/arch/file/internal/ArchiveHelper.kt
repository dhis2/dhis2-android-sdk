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

package org.hisp.dhis.android.core.arch.file.internal

import okio.IOException
import okio.Path
import org.hisp.dhis.android.core.maintenance.D2Error

/**
 * Zip archives used by the database export and import. Okio can only read zip files, and encrypted zip files have no
 * multiplatform library, so each platform provides its own implementation.
 */
internal interface ArchiveHelper {

    /**
     * Write [files] into a new archive at [zipFile], each one as an entry named after the file. A partially written
     * archive is deleted when writing fails.
     *
     * @throws IOException if a file cannot be read or the archive cannot be written.
     */
    fun zip(files: List<Path>, zipFile: Path)

    /**
     * Extract the entries at the root of [zipFile] named in [entryNames] into [directory]. Any other entry is ignored,
     * so an archive cannot write anywhere else or leave unexpected files behind (Zip Slip). Requested entries missing
     * from the archive are not extracted.
     *
     * @throws IOException if [zipFile] is not a zip file or it cannot be read.
     * @throws IllegalArgumentException if one of [entryNames] is not a plain file name.
     */
    fun unzip(zipFile: Path, directory: Path, entryNames: Set<String>)

    /**
     * Write [file] into a new archive at [zipFile], encrypted with AES-256 and [password].
     */
    fun createEncrypted(file: Path, zipFile: Path, password: String)

    /**
     * Extract the single entry of the encrypted [zipFile] into [target], whatever the name of the entry.
     *
     * @throws D2Error if [zipFile] does not contain exactly one entry.
     */
    fun extractEncrypted(zipFile: Path, target: Path, password: String)
}
