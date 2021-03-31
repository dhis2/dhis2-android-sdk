/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.arch.helpers;

import android.content.Context;

import java.io.File;

public final class FileResourceDirectoryHelper {

    private FileResourceDirectoryHelper() {}

    /**
     * This method returns a {@link File} object whose path points to the Sdk resources directory where the Sdk will
     * save the files associated with the file resources.
     *
     * @param context The application context.
     * @return A {@link File} object whose path points to the Sdk resources directory.
     */
    public static File getFileResourceDirectory(Context context) {
        File file = new File(context.getFilesDir(), "sdk_resources");
        if (!file.exists() && file.mkdirs()) {
            return file;
        }
        return file;
    }

    /**
     * This method returns a {@link File} object whose path points to the Sdk cache resources directory. This should be
     * the place where volatile files are stored, such as camera photos or images to be resized. Since the directory
     * is contained in the cache directory, Android may auto-delete the files in the cache directory once the system
     * is about to run out of memory. Third party applications can also delete files from the cache directory.
     * Even the user can manually clear the cache from Settings. However, the fact that the cache can be cleared in
     * the methods explained above should not mean that the cache will automatically get cleared; therefore, the
     * cache will need to be tidied up from time to time proactively.
     *
     * @param context The application context.
     * @return A {@link File} object whose path points to the Sdk cache resources directory.
     */
    public static File getFileCacheResourceDirectory(Context context) {
        File file = new File(context.getCacheDir(), "sdk_cache_resources");
        if (!file.exists() && file.mkdirs()) {
            return file;
        }
        return file;
    }
}