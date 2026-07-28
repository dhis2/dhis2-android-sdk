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

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sqrt

/**
 * Compresses image files before uploading them to the server, keeping the quality loss to a minimum.
 *
 * The strategy is to iteratively downscale the image until the encoded file drops below a target size. JPEG images
 * are encoded with a fixed quality; PNG images are lossless, so for them the size reduction relies solely on the
 * scaling. The first scale factor is estimated from the size ratio so that, in most cases, a single pass is enough.
 */
object FileCompressionHelper {

    private const val JPEG_QUALITY = 80
    internal const val TARGET_SIZE_BYTES = 600 * 1024L // 600 KB
    private const val SCALE_STEP = 0.8f // reduce dimensions by 20% on each retry
    private const val MAX_ITERATIONS = 10
    private const val FULL_SIZE = 1f
    private const val MIN_DIMENSION_PX = 1

    /**
     * Compress an image file so that its encoded size does not exceed [targetSizeBytes]. The aspect ratio is always
     * preserved. If the file cannot be decoded as an image, the original file is returned unchanged.
     *
     * @param fileToCompress  Image file to compress.
     * @param targetSizeBytes Maximum size, in bytes, the resulting file should have.
     * @return A new [File] with the compressed image, or the original file if it could not be decoded.
     */
    @JvmStatic
    @JvmOverloads
    fun compressFile(fileToCompress: File, targetSizeBytes: Long = TARGET_SIZE_BYTES): File {
        val source = BitmapFactory.decodeFile(fileToCompress.absolutePath) ?: return fileToCompress
        val format = resolveCompressFormat(fileToCompress)
        val outputFile = File(fileToCompress.parent, "compressed-${fileToCompress.name}")

        try {
            compressUntilBelowTarget(source, format, outputFile, targetSizeBytes, fileToCompress.length())
        } finally {
            source.recycle()
        }
        return outputFile
    }

    private fun compressUntilBelowTarget(
        source: Bitmap,
        format: CompressFormat,
        outputFile: File,
        targetSizeBytes: Long,
        originalSizeBytes: Long,
    ) {
        var scale = estimateInitialScale(originalSizeBytes, targetSizeBytes)
        var iteration = 0
        do {
            writeScaledBitmap(source, scale, format, outputFile)
            if (outputFile.length() <= targetSizeBytes) {
                return
            }
            scale *= SCALE_STEP
            iteration++
        } while (iteration < MAX_ITERATIONS)
    }

    private fun writeScaledBitmap(source: Bitmap, scale: Float, format: CompressFormat, outputFile: File) {
        val scaled = scaleBitmap(source, scale)
        try {
            FileOutputStream(outputFile).use { out ->
                scaled.compress(format, JPEG_QUALITY, out)
            }
        } finally {
            if (scaled !== source) {
                scaled.recycle()
            }
        }
    }

    private fun scaleBitmap(source: Bitmap, scale: Float): Bitmap {
        if (scale >= FULL_SIZE) {
            return source
        }
        val width = (source.width * scale).toInt().coerceAtLeast(MIN_DIMENSION_PX)
        val height = (source.height * scale).toInt().coerceAtLeast(MIN_DIMENSION_PX)
        return source.scale(width, height)
    }

    /**
     * Estimate the first scale factor from the size ratio. Encoded size grows roughly with the pixel count (width x
     * height), so scaling each dimension by the square root of the target ratio lands close to the target in one pass.
     */
    private fun estimateInitialScale(originalSizeBytes: Long, targetSizeBytes: Long): Float {
        return if (originalSizeBytes > targetSizeBytes) {
            sqrt(targetSizeBytes.toFloat() / originalSizeBytes)
        } else {
            FULL_SIZE
        }
    }

    private fun resolveCompressFormat(file: File): CompressFormat {
        return when (FileResourceUtil.getExtension(file.name)?.lowercase()) {
            "png" -> CompressFormat.PNG
            else -> CompressFormat.JPEG
        }
    }
}
