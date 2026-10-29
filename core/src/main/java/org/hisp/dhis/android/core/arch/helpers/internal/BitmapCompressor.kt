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
    import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

/**
 * Encodes a bitmap in memory so that the result stays below a target size, keeping the quality loss to a minimum.
 *
 * The bitmap is encoded at a fixed quality and the size is reduced by downscaling: every attempt is measured and the
 * next scale is predicted from that measurement, so the result lands just below the target instead of well under it.
 */
internal object BitmapCompressor {

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
     * Lower bound for the largest side of the result. Shrinking an image below this is not worth the loss, even if
     * that means missing the target.
     */
    private const val MIN_DIMENSION_PX = 256

    /**
     * Encode the bitmap trying to stay below the target: one encoding at full resolution, and a scale search when
     * that is already too big.
     *
     * @return The smallest encoded image found, or null if the platform could not encode the bitmap at all.
     */
    fun compress(source: Bitmap, format: CompressFormat, targetSizeBytes: Long): ByteArray? {
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
