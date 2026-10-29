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

package org.hisp.dhis.android.core.arch.helpers

import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.helpers.internal.ImageProcessorLocator
import java.io.File

/**
 * Compresses image files before uploading them to the server, keeping the quality loss to a minimum.
 *
 * Files that are already below the target size and in a format the server accepts are returned untouched. Otherwise
 * the image is re-encoded at a fixed quality and the size is reduced by downscaling: every attempt is measured and
 * the next scale is predicted from that measurement, so the result lands just below the target instead of well
 * under it.
 *
 * Note that only JPEG and PNG are produced, because those are the only formats that the platform can encode and the
 * server accepts. Anything else that can be decoded (webp, heif, bmp, gif...) is converted, and the caller is
 * responsible for exposing the resulting file with the extension it now has: the content type is derived from the
 * name, and it must agree with the actual bytes.
 */
object FileCompressionHelper {

    internal const val TARGET_SIZE_BYTES = 600 * 1024L // 600 KB

    /**
     * Compress an image file so that its encoded size does not exceed [targetSizeBytes]. The aspect ratio and the
     * EXIF orientation are always preserved.
     *
     * The original file is returned unchanged when it cannot be decoded as an image, when it is already below the
     * target in a format the server accepts, or when compressing it would not make it any smaller. Otherwise a new
     * file is returned, written in the Sdk cache directory when available; the caller owns it and should delete it
     * once consumed. Note that the returned file may be a JPEG or a PNG regardless of the input format, so its
     * extension may differ from the one of [fileToCompress].
     *
     * @param fileToCompress  Image file to compress.
     * @param targetSizeBytes Maximum size, in bytes, the resulting file should have.
     * @return A [File] with the compressed image, or [fileToCompress] if it was not compressed.
     */
    @JvmStatic
    @JvmOverloads
    fun compressFile(fileToCompress: File, targetSizeBytes: Long = TARGET_SIZE_BYTES): File {
        val source = fileToCompress.toOkioPath()
        val outputDirectory = ImageProcessorLocator.outputDirectory(fileToCompress)
        val compressed = ImageProcessorLocator.processor.compress(source, targetSizeBytes, outputDirectory)
        return if (compressed == source) fileToCompress else compressed.toFile()
    }
}
