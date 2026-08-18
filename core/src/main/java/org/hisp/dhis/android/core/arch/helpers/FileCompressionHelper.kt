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
import android.util.Log
import org.hisp.dhis.android.core.arch.helpers.internal.ImageFileHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

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

    private val TAG = FileCompressionHelper::class.java.simpleName

    internal const val TARGET_SIZE_BYTES = 600 * 1024L // 600 KB

    /**
     * Quality of the lossy encodings, never lowered to reach the target. Below this JPEG starts showing visible
     * blocking, and since the artifacts survive every later downscaling while the resolution above the dimension the
     * image is displayed at is not used, spending the budget on quality is worth more than spending it on pixels.
     */
    private const val JPEG_QUALITY = 85

    /**
     * Lossless formats ignore the quality, so the value is irrelevant for them.
     */
    private const val LOSSLESS_QUALITY = 100

    private const val MAX_SCALE_REFINEMENTS = 4
    private const val FULL_SIZE = 1f

    /**
     * Fraction of the target the prediction aims at. Without this margin an attempt landing barely above the target
     * would predict a scale barely below the current one, converging to the boundary from above instead of crossing
     * it within the allowed refinements.
     */
    private const val SAFETY_RATIO = 0.95f

    /**
     * Upper bound for the largest side of the working bitmap. A 50 MP picture would need 200 MB as an ARGB_8888
     * bitmap, so it is subsampled by the decoder before being loaded.
     */
    private const val MAX_WORKING_DIMENSION_PX = 4096

    /**
     * Lower bound for the largest side of the result. Shrinking an image below this is not worth the loss, even if
     * that means missing the target.
     */
    private const val MIN_DIMENSION_PX = 256

    private const val COMPRESSED_PREFIX = "compressed-"
    private const val JPEG_EXTENSION = "jpg"
    private const val PNG_EXTENSION = "png"

    /**
     * Image mime types decodable by the platform that the server accepts as they are. Formats it cannot decode (tiff,
     * raw, pcx, pnm, jpeg2000) never reach the compression, and are also accepted by the server. Note that webp,
     * heif and avif are decodable but rejected by the server, so they are always converted.
     */
    private val SERVER_SUPPORTED_MIME_TYPES = setOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/x-ms-bmp",
        "image/vnd.wap.wbmp",
    )

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
    @Suppress("ReturnCount")
    fun compressFile(fileToCompress: File, targetSizeBytes: Long = TARGET_SIZE_BYTES): File {
        val mimeType = ImageFileHelper.decodeMimeType(fileToCompress) ?: return fileToCompress
        val isSupportedFormat = SERVER_SUPPORTED_MIME_TYPES.contains(mimeType)
        if (isSupportedFormat && fileToCompress.length() <= targetSizeBytes) {
            return fileToCompress
        }

        val source = ImageFileHelper.decodeBitmap(fileToCompress, MAX_WORKING_DIMENSION_PX)
        if (source == null) {
            Log.w(TAG, "${fileToCompress.name} could not be decoded, it will be uploaded as it is")
            return fileToCompress
        }

        val format = if (ImageFileHelper.usesTransparency(source)) CompressFormat.PNG else CompressFormat.JPEG
        val compressed = try {
            encodeBelowTarget(source, format, targetSizeBytes)
        } finally {
            source.recycle()
        }

        return when {
            compressed == null -> {
                Log.w(TAG, "${fileToCompress.name} could not be encoded, it will be uploaded as it is")
                fileToCompress
            }

            isSupportedFormat && compressed.size >= fileToCompress.length() -> {
                Log.w(TAG, "Compressing ${fileToCompress.name} does not make it smaller, it is left as it is")
                fileToCompress
            }

            else -> {
                if (compressed.size > targetSizeBytes) {
                    Log.w(TAG, "${fileToCompress.name} could not be compressed below $targetSizeBytes bytes")
                }
                writeOutput(fileToCompress, compressed, format) ?: fileToCompress
            }
        }
    }

    /**
     * Encode the bitmap trying to stay below the target: one encoding at full resolution, and a scale search when
     * that is already too big.
     *
     * @return The smallest encoded image found, or null if the platform could not encode the bitmap at all.
     */
    private fun encodeBelowTarget(source: Bitmap, format: CompressFormat, targetSizeBytes: Long): ByteArray? {
        val fullSize = encode(source, FULL_SIZE, qualityOf(format), format) ?: return null
        return if (fullSize.size <= targetSizeBytes) {
            fullSize
        } else {
            searchScale(source, format, targetSizeBytes, fullSize)
        }
    }

    /**
     * Look for the largest scale whose encoded size still fits in the target. Every attempt is measured and the next
     * scale is predicted from that measurement, which converges in one or two attempts because the encoded size grows
     * almost linearly with the pixel count. Gives up once the minimum dimension is reached, returning the smallest
     * result found even though it exceeds the target.
     */
    @Suppress("ReturnCount")
    private fun searchScale(
        source: Bitmap,
        format: CompressFormat,
        targetSizeBytes: Long,
        fullSize: ByteArray,
    ): ByteArray {
        val quality = qualityOf(format)
        val minScale = minScale(source)
        var smallest = fullSize
        var scale = sizeRatio(fullSize.size, targetSizeBytes).coerceAtLeast(minScale)

        repeat(MAX_SCALE_REFINEMENTS) {
            val attempt = encode(source, scale, quality, format) ?: return smallest
            if (attempt.size <= targetSizeBytes) {
                return attempt
            }
            if (attempt.size < smallest.size) {
                smallest = attempt
            }
            if (scale <= minScale) {
                return smallest
            }
            scale = (scale * sizeRatio(attempt.size, targetSizeBytes)).coerceAtLeast(minScale)
        }
        return smallest
    }

    /**
     * Encode the bitmap in memory, so that only the accepted attempt is written to disk.
     *
     * @return The encoded bytes, or null if the platform refused to encode the bitmap in the given format.
     */
    private fun encode(source: Bitmap, scale: Float, quality: Int, format: CompressFormat): ByteArray? {
        val scaled = ImageFileHelper.scaleBitmap(source, scale)
        return try {
            val out = ByteArrayOutputStream()
            if (scaled.compress(format, quality, out)) out.toByteArray() else null
        } finally {
            if (scaled !== source) {
                scaled.recycle()
            }
        }
    }

    private fun writeOutput(sourceFile: File, content: ByteArray, format: CompressFormat): File? {
        var outputFile: File? = null
        return try {
            outputFile = createOutputFile(sourceFile, format)
            FileOutputStream(outputFile).use { it.write(content) }
            outputFile
        } catch (e: IOException) {
            Log.w(TAG, "Could not write the compressed image, ${sourceFile.name} will be uploaded as it is", e)
            outputFile?.delete()
            null
        }
    }

    /**
     * The compressed image is a volatile file, so it goes to the cache directory. That directory is shared by every
     * image being added, so the name cannot be derived from the source alone: two sources with the same name, or the
     * same source being added twice concurrently, would write over each other. The prefix also keeps the source safe
     * when the cache directory is not available and the extension has not changed.
     */
    @Throws(IOException::class)
    private fun createOutputFile(sourceFile: File, format: CompressFormat): File {
        val directory = ImageFileHelper.getCacheDirectory() ?: sourceFile.absoluteFile.parentFile
        val prefix = "$COMPRESSED_PREFIX${sourceFile.nameWithoutExtension}-"
        return File.createTempFile(prefix, ".${extensionOf(format)}", directory)
    }

    private fun extensionOf(format: CompressFormat): String {
        return if (format == CompressFormat.PNG) PNG_EXTENSION else JPEG_EXTENSION
    }

    private fun qualityOf(format: CompressFormat): Int {
        return if (format == CompressFormat.PNG) LOSSLESS_QUALITY else JPEG_QUALITY
    }

    /**
     * The encoded size grows roughly with the pixel count, so scaling both dimensions by the square root of the size
     * ratio lands close to the target.
     */
    private fun sizeRatio(measuredSizeBytes: Int, targetSizeBytes: Long): Float {
        return sqrt(targetSizeBytes * SAFETY_RATIO / measuredSizeBytes).coerceAtMost(FULL_SIZE)
    }

    /**
     * Smallest scale allowed for this bitmap, which keeps its largest side at [MIN_DIMENSION_PX]. Images that are
     * already smaller than that are never downscaled.
     */
    private fun minScale(source: Bitmap): Float {
        val largestSide = maxOf(source.width, source.height)
        return minOf(MIN_DIMENSION_PX, largestSide).toFloat() / largestSide
    }
}
