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
package org.hisp.dhis.android.core.arch.helpers

import android.content.Context
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.fileresource.FileResourceDirectories
import java.io.File

/**
 * Directories of the file resources of the logged-in user. D2 must be instantiated with a logged-in user, and the
 * `context` parameter is not used.
 */
@Deprecated("Use FileResourceModule.directories(), which works on okio.Path.")
@Suppress("UnusedParameter")
object FileResourceDirectoryHelper {

    /**
     * Directory where the Sdk saves the files associated with the file resources.
     */
    @Deprecated(
        "Use FileResourceModule.directories().fileResourceDirectory(), which is an okio.Path.",
        ReplaceWith(
            "D2Manager.getD2().fileResourceModule().directories().fileResourceDirectory().toFile()",
            "org.hisp.dhis.android.core.D2Manager",
        ),
    )
    @JvmStatic
    fun getFileResourceDirectory(context: Context): File {
        return directories().fileResourceDirectory().toFile()
    }

    /**
     * Directory for volatile files, such as camera photos or images to be resized. Android may delete its files when
     * the device runs low on storage, but it is not cleared automatically, so it has to be tidied up proactively.
     */
    @Deprecated(
        "Use FileResourceModule.directories().fileCacheResourceDirectory(), which is an okio.Path.",
        ReplaceWith(
            "D2Manager.getD2().fileResourceModule().directories().fileCacheResourceDirectory().toFile()",
            "org.hisp.dhis.android.core.D2Manager",
        ),
    )
    @JvmStatic
    fun getFileCacheResourceDirectory(context: Context): File {
        return directories().fileCacheResourceDirectory().toFile()
    }

    private fun directories(): FileResourceDirectories {
        return D2Manager.getD2().fileResourceModule().directories()
    }
}
