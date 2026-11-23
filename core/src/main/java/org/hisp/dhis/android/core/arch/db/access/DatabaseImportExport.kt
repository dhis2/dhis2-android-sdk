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
package org.hisp.dhis.android.core.arch.db.access

import okio.Path
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.helpers.internal.runBlockingOnIO
import org.hisp.dhis.android.core.maintenance.D2Error
import java.io.File

interface DatabaseImportExport {

    @Deprecated(
        "Use blockingImportDatabase with an okio.Path.",
        ReplaceWith("blockingImportDatabase(file.toOkioPath())", "okio.Path.Companion.toOkioPath"),
    )
    fun importDatabase(file: File): DatabaseExportMetadata = blockingImportDatabase(file.toOkioPath())

    @Deprecated(message = "Use blockingImportDatabase instead", ReplaceWith("blockingImportDatabase(file)"))
    @Throws(D2Error::class)
    fun importDatabase(file: Path): DatabaseExportMetadata = blockingImportDatabase(file)

    @Throws(D2Error::class)
    fun blockingImportDatabase(file: Path): DatabaseExportMetadata = runBlockingOnIO { suspendImportDatabase(file) }

    @Throws(D2Error::class)
    suspend fun suspendImportDatabase(file: Path): DatabaseExportMetadata

    @Deprecated(
        "Use exportLoggedUserDatabaseToPath(), which returns an okio.Path.",
        ReplaceWith("exportLoggedUserDatabaseToPath().toFile()"),
    )
    suspend fun exportLoggedUserDatabase(): File = exportLoggedUserDatabaseToPath().toFile()

    @Throws(D2Error::class)
    suspend fun exportLoggedUserDatabaseToPath(): Path
}
