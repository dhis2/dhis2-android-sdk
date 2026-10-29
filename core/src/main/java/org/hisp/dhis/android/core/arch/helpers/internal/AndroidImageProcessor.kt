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

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Log
import androidx.core.graphics.scale
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.buffer
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper.Dimension
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.getExtension
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.koin.core.annotation.Singleton
import kotlin.random.Random

/**
 * [ImageProcessor] backed by the Android image codecs. The source paths are handed to the platform decoder, and the
 * results are written through the [FileSystem].
 *
 * Note that only JPEG and PNG are produced, because those are the only formats that the platform can encode and the
 * server accepts. Anything else that can be decoded (webp, heif, bmp, gif...) is converted by the compression.
 */
@Singleton
internal class AndroidImageProcessor(
    private val fileSystem: FileSystem,
) : ImageProcessor {

    override fun resize(source: Path, dimension: Dimension, outputDirectory: Path): Path {
        val bitmap = ImageFileHelper.decodeBitmap(source, dimension.dimension * DECODE_HEADROOM_FACTOR)
            ?: throw resizeError("${source.name} could not be decoded as an image")
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val scaleFactor = width / height
        val target = outputDirectory / "$RESIZED_PREFIX${dimension.name}-${source.name}"
        try {
            return if (scaleFactor > 1) {
                if (width < dimension.dimension) {
                    source
                } else {
                    writeResized(bitmap, dimension.dimension, (dimension.dimension / scaleFactor).toInt(), target)
                }
            } else {
                if (height < dimension.dimension) {
                    source
                } else {
                    writeResized(bitmap, (scaleFactor * dimension.dimension).toInt(), dimension.dimension, target)
                }
            }
        } finally {
            bitmap.recycle()
        }
    }

    @Suppress("ReturnCount")
    override fun compress(source: Path, targetSizeBytes: Long, outputDirectory: Path): Path {
        val mimeType = ImageFileHelper.decodeMimeType(source) ?: return source
        val isSupportedFormat = SERVER_SUPPORTED_MIME_TYPES.contains(mimeType)
        val sourceSize = fileSystem.metadataOrNull(source)?.size ?: 0L
        if (isSupportedFormat && sourceSize <= targetSizeBytes) {
            return source
        }

        val bitmap = ImageFileHelper.decodeBitmap(source, MAX_WORKING_DIMENSION_PX)
        if (bitmap == null) {
            Log.w(TAG, "${source.name} could not be decoded, it will be uploaded as it is")
            return source
        }

        val format = if (ImageFileHelper.usesTransparency(bitmap)) CompressFormat.PNG else CompressFormat.JPEG
        val compressed = try {
            BitmapCompressor.compress(bitmap, format, targetSizeBytes)
        } finally {
            bitmap.recycle()
        }

        return when {
            compressed == null -> {
                Log.w(TAG, "${source.name} could not be encoded, it will be uploaded as it is")
                source
            }

            isSupportedFormat && compressed.size >= sourceSize -> {
                Log.w(TAG, "Compressing ${source.name} does not make it smaller, it is left as it is")
                source
            }

            else -> {
                if (compressed.size > targetSizeBytes) {
                    Log.w(TAG, "${source.name} could not be compressed below $targetSizeBytes bytes")
                }
                writeCompressed(source, compressed, format, outputDirectory) ?: source
            }
        }
    }

    @Throws(D2Error::class)
    private fun writeResized(bitmap: Bitmap, dstWidth: Int, dstHeight: Int, target: Path): Path {
        val scaledBitmap = bitmap.scale(dstWidth, dstHeight)
        try {
            val format = compressFormatOf(target)
            val encoded = fileSystem.write(target) {
                scaledBitmap.compress(format, LOSSLESS_QUALITY, outputStream())
            }
            if (!encoded) {
                throw resizeError("${target.name} could not be encoded as $format")
            }
        } catch (e: IOException) {
            throw resizeError(e.message)
        } finally {
            if (scaledBitmap !== bitmap) {
                scaledBitmap.recycle()
            }
        }
        return target
    }

    /**
     * The compressed image is a volatile file, so it goes to the output directory given by the caller, usually a
     * cache directory. That directory is shared by every image being processed, so the name cannot be derived from
     * the source alone: two sources with the same name, or the same source being compressed twice concurrently, would
     * write over each other. A random suffix keeps the names apart, and the file is only written if it does not exist
     * yet. The prefix also keeps the source safe when the output directory is the one of the source and the extension
     * has not changed.
     */
    private fun writeCompressed(
        source: Path,
        content: ByteArray,
        format: CompressFormat,
        outputDirectory: Path,
    ): Path? {
        val nameWithoutExtension = source.name.substringBeforeLast('.')
        val suffix = Random.nextLong().toULong()
        val extension = if (format == CompressFormat.PNG) PNG_EXTENSION else JPEG_EXTENSION
        val target = outputDirectory / "$COMPRESSED_PREFIX$nameWithoutExtension-$suffix.$extension"
        var created = false
        return try {
            fileSystem.sink(target, mustCreate = true).buffer().use { sink ->
                created = true
                sink.write(content)
            }
            target
        } catch (e: IOException) {
            Log.w(TAG, "Could not write the compressed image, ${source.name} will be uploaded as it is", e)
            if (created) {
                try {
                    fileSystem.delete(target)
                } catch (deleteError: IOException) {
                    Log.w(TAG, "Could not delete the partially written ${target.name}", deleteError)
                }
            }
            null
        }
    }

    private fun compressFormatOf(file: Path): CompressFormat {
        val extension = getExtension(file.name)
        val isJpeg = extension != null && (extension == "jpeg" || extension == "jpg")
        return if (isJpeg) CompressFormat.JPEG else CompressFormat.PNG
    }

    private fun resizeError(description: String?): D2Error {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.SDK)
            .errorCode(D2ErrorCode.FAIL_RESIZING_IMAGE)
            .errorDescription(description ?: "Failed to resize image")
            .build()
    }

    private companion object {
        private val TAG = AndroidImageProcessor::class.java.simpleName

        /**
         * The image is decoded with some room above the requested dimension, so that the final downscaling is a
         * filtered pass over a bitmap that still holds more detail than the result, instead of relying only on the
         * power of two subsampling of the decoder, which can only land on the requested dimension by chance.
         */
        private const val DECODE_HEADROOM_FACTOR = 2

        private const val LOSSLESS_QUALITY = 100

        /**
         * Upper bound for the largest side of the working bitmap of the compression. A 50 MP picture would need
         * 200 MB as an ARGB_8888 bitmap, so it is subsampled by the decoder before being loaded.
         */
        private const val MAX_WORKING_DIMENSION_PX = 4096

        private const val RESIZED_PREFIX = "resized-"
        private const val COMPRESSED_PREFIX = "compressed-"
        private const val JPEG_EXTENSION = "jpg"
        private const val PNG_EXTENSION = "png"

        /**
         * Image mime types decodable by the platform that the server accepts as they are. Formats it cannot decode
         * (tiff, raw, pcx, pnm, jpeg2000) never reach the compression, and are also accepted by the server. Note that
         * webp, heif and avif are decodable but rejected by the server, so they are always converted.
         */
        private val SERVER_SUPPORTED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/x-ms-bmp",
            "image/vnd.wap.wbmp",
        )
    }
}
