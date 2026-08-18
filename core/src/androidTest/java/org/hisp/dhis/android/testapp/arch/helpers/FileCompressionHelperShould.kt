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
package org.hisp.dhis.android.testapp.arch.helpers

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.FileCompressionHelper
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(D2JunitRunner::class)
class FileCompressionHelperShould {

    private val createdFiles = mutableListOf<File>()

    @After
    fun tearDown() {
        createdFiles.forEach { it.delete() }
        createdFiles.clear()
    }

    @Test
    fun compress_png_below_target_size() {
        val target = 150 * 1024L
        val file = writeImage("png-to-compress.png", CompressFormat.PNG, noisyBitmap(1500, 1500))
        assertThat(file.length()).isGreaterThan(target)

        val compressedFile = compress(file, target)

        assertThat(compressedFile.exists()).isTrue()
        assertThat(compressedFile.length()).isAtMost(target)
    }

    @Test
    fun compress_jpeg_below_target_size() {
        val target = 150 * 1024L
        val file = writeImage("jpeg-to-compress.jpg", CompressFormat.JPEG, noisyBitmap(1500, 1500))
        assertThat(file.length()).isGreaterThan(target)

        val compressedFile = compress(file, target)

        assertThat(compressedFile.exists()).isTrue()
        assertThat(compressedFile.length()).isAtMost(target)
    }

    /**
     * The point of measuring every attempt instead of decaying a guessed scale: the result must use most of the
     * budget, not just fit in it, so that as little quality as possible is thrown away.
     */
    @Test
    fun land_close_to_the_target_size() {
        val target = 150 * 1024L
        val file = writeImage("png-close-to-target.png", CompressFormat.PNG, noisyBitmap(1500, 1500))

        val compressedFile = compress(file, target)

        assertThat(compressedFile.length()).isAtMost(target)
        assertThat(compressedFile.length()).isGreaterThan((target * CLOSENESS_RATIO).toLong())
    }

    @Test
    fun preserve_aspect_ratio_when_compressing() {
        val target = 100 * 1024L
        val file = writeImage("png-wide.png", CompressFormat.PNG, noisyBitmap(2048, 1024))

        val compressedFile = compress(file, target)

        val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
        val ratio = compressedBitmap.width.toFloat() / compressedBitmap.height.toFloat()
        assertThat(ratio).isWithin(0.05f).of(2.0f)
    }

    @Test
    fun return_the_original_file_when_it_is_already_below_target() {
        val file = writeImage("jpeg-small.jpg", CompressFormat.JPEG, noisyBitmap(100, 125))

        val compressedFile = compress(file, FileCompressionHelper.TARGET_SIZE_BYTES)

        assertThat(compressedFile).isEqualTo(file)
    }

    @Test
    fun return_original_file_when_it_cannot_be_decoded() {
        val notAnImage = File(directory(), "not-an-image.txt")
        createdFiles.add(notAnImage)
        FileOutputStream(notAnImage).use { it.write("this is not an image".toByteArray()) }

        val result = compress(notAnImage, 20 * 1024L)

        assertThat(result).isEqualTo(notAnImage)
    }

    @Test
    fun convert_an_opaque_png_to_jpeg() {
        val file = writeImage("png-opaque.png", CompressFormat.PNG, noisyBitmap(1024, 1024))

        val compressedFile = compress(file, 100 * 1024L)

        assertThat(compressedFile.extension).isEqualTo("jpg")
        assertThat(mimeTypeOf(compressedFile)).isEqualTo("image/jpeg")
    }

    @Test
    fun keep_png_when_the_image_has_transparent_pixels() {
        val file = writeImage("png-translucent.png", CompressFormat.PNG, noisyBitmap(1024, 1024, opaque = false))

        val compressedFile = compress(file, 300 * 1024L)

        assertThat(compressedFile.extension).isEqualTo("png")
        assertThat(mimeTypeOf(compressedFile)).isEqualTo("image/png")
        assertThat(BitmapFactory.decodeFile(compressedFile.absolutePath).hasAlpha()).isTrue()
    }

    /**
     * The server rejects webp, so it must be converted even when it is already small enough to be uploaded as it is.
     */
    @Test
    @Suppress("DEPRECATION")
    fun convert_webp_even_when_it_is_below_the_target() {
        val file = writeImage("image-webp.webp", CompressFormat.WEBP, noisyBitmap(300, 300))
        assertThat(file.length()).isLessThan(FileCompressionHelper.TARGET_SIZE_BYTES)

        val compressedFile = compress(file, FileCompressionHelper.TARGET_SIZE_BYTES)

        assertThat(compressedFile).isNotEqualTo(file)
        assertThat(compressedFile.extension).isEqualTo("jpg")
        assertThat(mimeTypeOf(compressedFile)).isEqualTo("image/jpeg")
    }

    /**
     * Re-encoding drops the EXIF metadata, so the orientation has to be baked into the pixels: a landscape image
     * flagged as rotated must come out as a portrait one.
     */
    @Test
    @Suppress("DEPRECATION")
    fun apply_the_exif_orientation() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        val file = writeImage("jpeg-rotated.jpg", CompressFormat.JPEG, noisyBitmap(600, 300))
        ExifInterface(file.absolutePath).apply {
            setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
            saveAttributes()
        }

        val compressedFile = compress(file, 10 * 1024L)

        val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
        assertThat(compressedBitmap.height).isGreaterThan(compressedBitmap.width)
    }

    /**
     * An unreachable target must not degrade the image indefinitely: the largest side is kept at the minimum
     * dimension and the resulting file is returned even though it exceeds the target.
     */
    @Test
    fun stop_shrinking_at_the_minimum_dimension() {
        val target = 1024L
        val file = writeImage("png-unreachable.png", CompressFormat.PNG, noisyBitmap(1024, 1024))

        val compressedFile = compress(file, target)

        val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
        assertThat(maxOf(compressedBitmap.width, compressedBitmap.height)).isEqualTo(MIN_DIMENSION_PX)
        assertThat(compressedFile.length()).isGreaterThan(target)
    }

    private fun compress(file: File, target: Long): File {
        return FileCompressionHelper.compressFile(file, target).also { createdFiles.add(it) }
    }

    private fun writeImage(name: String, compressFormat: CompressFormat, bitmap: Bitmap): File {
        val imageFile = File(directory(), name)
        createdFiles.add(imageFile)
        try {
            FileOutputStream(imageFile).use { os ->
                bitmap.compress(compressFormat, LOSSLESS_QUALITY, os)
                os.flush()
            }
        } finally {
            bitmap.recycle()
        }
        return imageFile
    }

    private fun mimeTypeOf(file: File): String? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        return options.outMimeType
    }

    companion object {
        private const val MIN_DIMENSION_PX = 256
        private const val LOSSLESS_QUALITY = 100
        private const val CLOSENESS_RATIO = 0.7

        private fun directory() =
            FileResourceDirectoryHelper.getRootFileResourceDirectory(
                InstrumentationRegistry.getInstrumentation().context,
            )

        /**
         * Builds a high-entropy bitmap whose pixels vary per position, so that the encoded file does not collapse to a
         * trivial size and the compression loop is actually exercised. When [opaque] is false, the alpha channel also
         * varies, which is what makes the image worth keeping as a lossless PNG.
         */
        private fun noisyBitmap(width: Int, height: Int, opaque: Boolean = true): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val pixels = IntArray(width * height) { i ->
                val x = i % width
                val y = i / width
                val alpha = if (opaque) 0xFF else (x * 7 + y * 3) and 0xFF
                Color.argb(
                    alpha,
                    (x * 37 + y * 17) and 0xFF,
                    (x * 91 + y * 53) and 0xFF,
                    (x * 13 + y * 191) and 0xFF,
                )
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        }
    }
}
