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
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.FileCompressionHelper
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@RunWith(D2JunitRunner::class)
class FileCompressionHelperShould {

    @Test
    fun compress_png_below_target_size() {
        val target = 20 * 1024L
        val file = getFile(CompressFormat.PNG, getNoisyBitmap(1024, 1024))
        assertThat(file.length()).isGreaterThan(target)

        val compressedFile = FileCompressionHelper.compressFile(file, target)

        assertThat(compressedFile.exists()).isTrue()
        assertThat(compressedFile.length()).isAtMost(target)
    }

    @Test
    fun compress_jpeg_below_target_size() {
        val target = 20 * 1024L
        val file = getFile(CompressFormat.JPEG, getNoisyBitmap(1024, 1024))
        assertThat(file.length()).isGreaterThan(target)

        val compressedFile = FileCompressionHelper.compressFile(file, target)

        assertThat(compressedFile.exists()).isTrue()
        assertThat(compressedFile.length()).isAtMost(target)
    }

    @Test
    fun preserve_aspect_ratio_when_compressing() {
        val target = 20 * 1024L
        val file = getFile(CompressFormat.PNG, getNoisyBitmap(2048, 1024))

        val compressedFile = FileCompressionHelper.compressFile(file, target)

        val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
        val ratio = compressedBitmap.width.toFloat() / compressedBitmap.height.toFloat()
        assertThat(ratio).isWithin(0.05f).of(2.0f)
    }

    @Test
    fun do_not_upscale_when_file_is_already_below_target() {
        val file = getFile(CompressFormat.PNG, getNoisyBitmap(100, 125))

        val compressedFile = FileCompressionHelper.compressFile(file, 600 * 1024L)

        val compressedBitmap = BitmapFactory.decodeFile(compressedFile.absolutePath)
        assertThat(compressedBitmap.width).isEqualTo(100)
        assertThat(compressedBitmap.height).isEqualTo(125)
    }

    @Test
    fun return_original_file_when_it_cannot_be_decoded() {
        val notAnImage = File(
            FileResourceDirectoryHelper.getRootFileResourceDirectory(context()),
            "not-an-image.txt",
        )
        FileOutputStream(notAnImage).use { it.write("this is not an image".toByteArray()) }

        val result = FileCompressionHelper.compressFile(notAnImage, 20 * 1024L)

        assertThat(result).isEqualTo(notAnImage)
    }

    companion object {
        private fun context() = InstrumentationRegistry.getInstrumentation().context

        private fun getFile(compressFormat: CompressFormat, bitmap: Bitmap): File {
            val imageFile = File(
                FileResourceDirectoryHelper.getRootFileResourceDirectory(context()),
                "image-to-compress." + compressFormat.name.lowercase(Locale.getDefault()),
            )
            FileOutputStream(imageFile).use { os ->
                bitmap.compress(compressFormat, 100, os)
                os.flush()
            }
            return imageFile
        }

        /**
         * Builds a high-entropy bitmap whose pixels vary per position, so that the encoded file does not collapse to a
         * trivial size and the compression loop is actually exercised.
         */
        private fun getNoisyBitmap(width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val pixels = IntArray(width * height) { i ->
                val x = i % width
                val y = i / width
                Color.rgb(
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
