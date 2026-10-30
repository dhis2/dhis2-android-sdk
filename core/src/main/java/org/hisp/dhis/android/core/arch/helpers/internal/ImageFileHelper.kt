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
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import okio.IOException
import okio.Path

/**
 * Image decoding primitives used by [AndroidImageProcessor] to transform image files before uploading them. The
 * platform decoders read from a file system path, so the [Path]s are handed to them as they are.
 *
 * Decoding is always subsampled and never trusts the file extension: the image header is read first, which gives both
 * the real mime type and the dimensions needed to bound the memory used by the working bitmap.
 */
internal object ImageFileHelper {

    private val TAG = ImageFileHelper::class.java.simpleName

    private const val SAMPLE_SIZE_STEP = 2
    private const val MAX_SAMPLE_SIZE = 32
    private const val OPAQUE_ALPHA = 255
    private const val FULL_SIZE = 1f
    private const val MIN_DIMENSION_PX = 1
    private const val ROTATION_90 = 90f
    private const val ROTATION_180 = 180f
    private const val ROTATION_270 = 270f
    private const val NO_ROTATION = 0f

    /**
     * Read the mime type from the image header, ignoring the file extension.
     *
     * @return The mime type reported by the platform decoder, or null if the file is not an image this platform can
     * decode.
     */
    fun decodeMimeType(path: Path): String? {
        return decodeBounds(path)?.outMimeType
    }

    /**
     * Decode an image applying the EXIF orientation, subsampled so that its largest side is not much larger than
     * [maxDimensionPx]. Subsampling happens inside the decoder, so the full sized bitmap is never allocated.
     *
     * @return The decoded bitmap, or null if the file is not a decodable image or there is not enough memory for it.
     */
    @Suppress("ReturnCount")
    fun decodeBitmap(path: Path, maxDimensionPx: Int): Bitmap? {
        val bounds = decodeBounds(path) ?: return null
        var sampleSize = computeSampleSize(bounds, maxDimensionPx)

        while (sampleSize <= MAX_SAMPLE_SIZE) {
            try {
                val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                val bitmap = BitmapFactory.decodeFile(path.toString(), options)
                return bitmap?.let { applyExifRotation(path, it) }
            } catch (e: OutOfMemoryError) {
                Log.w(TAG, "Not enough memory to decode ${path.name} with sample size $sampleSize", e)
                sampleSize *= SAMPLE_SIZE_STEP
            }
        }
        return null
    }

    /**
     * Whether the bitmap actually contains translucent pixels. [Bitmap.hasAlpha] only tells whether the bitmap has an
     * alpha channel, which is the case for every image decoded from a PNG with an alpha channel even when all its
     * pixels are opaque, so the pixels are scanned when the flag is set.
     */
    @Suppress("ReturnCount")
    fun usesTransparency(bitmap: Bitmap): Boolean {
        if (!bitmap.hasAlpha()) {
            return false
        }
        val row = IntArray(bitmap.width)
        for (y in 0 until bitmap.height) {
            bitmap.getPixels(row, 0, bitmap.width, 0, y, bitmap.width, 1)
            if (row.any { Color.alpha(it) < OPAQUE_ALPHA }) {
                return true
            }
        }
        return false
    }

    /**
     * Scale both dimensions by [scale], preserving the aspect ratio. The source bitmap is returned as is when no
     * downscaling is required, so callers must check the identity before recycling the result.
     */
    fun scaleBitmap(source: Bitmap, scale: Float): Bitmap {
        if (scale >= FULL_SIZE) {
            return source
        }
        val width = (source.width * scale).toInt().coerceAtLeast(MIN_DIMENSION_PX)
        val height = (source.height * scale).toInt().coerceAtLeast(MIN_DIMENSION_PX)
        return source.scale(width, height)
    }

    private fun decodeBounds(path: Path): BitmapFactory.Options? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path.toString(), options)
        return options.takeIf { it.outWidth > 0 && it.outHeight > 0 }
    }

    /**
     * Largest power of two that keeps the decoded largest side greater than or equal to [maxDimensionPx]. Never
     * upsamples: images smaller than the bound are decoded at their original size.
     */
    private fun computeSampleSize(bounds: BitmapFactory.Options, maxDimensionPx: Int): Int {
        var sampleSize = 1
        val largestSide = maxOf(bounds.outWidth, bounds.outHeight)
        while (largestSide / (sampleSize * SAMPLE_SIZE_STEP) >= maxDimensionPx) {
            sampleSize *= SAMPLE_SIZE_STEP
        }
        return sampleSize
    }

    /**
     * Bake the EXIF orientation into the pixels. Re-encoding the bitmap drops the metadata, so an image that was only
     * upright thanks to its EXIF tag would otherwise be uploaded rotated.
     */
    private fun applyExifRotation(path: Path, bitmap: Bitmap): Bitmap {
        val degrees = readExifRotation(path)
        if (degrees == NO_ROTATION) {
            return bitmap
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) {
            bitmap.recycle()
        }
        return rotated
    }

    /**
     * The AndroidX [ExifInterface] is worth the dependency here because the platform one only parses the metadata of
     * Jpeg files, and heif images are precisely one of the cases this reaches: the server rejects them, so they are
     * always converted, and a camera writes the orientation of those as a tag rather than into the pixels. Reading
     * them with the platform class would silently report them as not rotated.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun readExifRotation(path: Path): Float {
        return try {
            val orientation = ExifInterface(path.toString())
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> ROTATION_90
                ExifInterface.ORIENTATION_ROTATE_180 -> ROTATION_180
                ExifInterface.ORIENTATION_ROTATE_270 -> ROTATION_270
                else -> NO_ROTATION
            }
        } catch (e: IOException) {
            Log.w(TAG, "Could not read the EXIF orientation of ${path.name}", e)
            NO_ROTATION
        } catch (e: RuntimeException) {
            Log.w(TAG, "Could not read the EXIF orientation of ${path.name}", e)
            NO_ROTATION
        }
    }
}
