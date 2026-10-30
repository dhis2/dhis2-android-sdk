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
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper.Dimension
import org.hisp.dhis.android.core.arch.helpers.internal.AndroidImageProcessor
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class AndroidImageProcessorShould {

    private val fileSystem = FileSystem.SYSTEM
    private val processor = AndroidImageProcessor(fileSystem)

    private val workingDirectory =
        InstrumentationRegistry.getInstrumentation().context.cacheDir.toOkioPath() / "image-processor-test"
    private val sourceDirectory = workingDirectory / "source"
    private val outputDirectory = workingDirectory / "output"

    @Before
    fun setUp() {
        fileSystem.createDirectories(sourceDirectory)
        fileSystem.createDirectories(outputDirectory)
    }

    @After
    fun tearDown() {
        fileSystem.deleteRecursively(workingDirectory)
    }

    @Test
    fun write_the_compressed_image_in_the_output_directory() {
        val source = writeImage("photo.png", CompressFormat.PNG, noisyBitmap(1024, 1024))

        val compressed = processor.compress(source, TARGET_SIZE_BYTES, outputDirectory)

        assertThat(compressed.parent).isEqualTo(outputDirectory)
        assertThat(compressed.name).startsWith("compressed-photo-")
        assertThat(compressed.name).endsWith(".jpg")
        assertThat(fileSystem.metadata(compressed).size).isAtMost(TARGET_SIZE_BYTES)
        assertThat(fileSystem.exists(source)).isTrue()
    }

    @Test
    fun not_overwrite_a_previous_result_when_compressing_the_same_image_again() {
        val source = writeImage("photo.png", CompressFormat.PNG, noisyBitmap(1024, 1024))

        val first = processor.compress(source, TARGET_SIZE_BYTES, outputDirectory)
        val second = processor.compress(source, TARGET_SIZE_BYTES, outputDirectory)

        assertThat(second).isNotEqualTo(first)
        assertThat(fileSystem.exists(first)).isTrue()
        assertThat(fileSystem.exists(second)).isTrue()
    }

    @Test
    fun return_the_source_without_writing_anything_when_it_is_already_below_the_target() {
        val source = writeImage("photo.jpg", CompressFormat.JPEG, noisyBitmap(100, 125))

        val compressed = processor.compress(source, TARGET_SIZE_BYTES, outputDirectory)

        assertThat(compressed).isEqualTo(source)
        assertThat(fileSystem.list(outputDirectory)).isEmpty()
    }

    @Test
    fun write_the_resized_image_in_the_output_directory() {
        val source = writeImage("photo.png", CompressFormat.PNG, noisyBitmap(2048, 1024))

        val resized = processor.resize(source, Dimension.SMALL, outputDirectory)

        assertThat(resized).isEqualTo(outputDirectory / "resized-SMALL-photo.png")
        val resizedBitmap = BitmapFactory.decodeFile(resized.toString())
        assertThat(resizedBitmap.width).isEqualTo(256)
        assertThat(resizedBitmap.height).isEqualTo(128)
    }

    @Test
    fun return_the_source_when_it_is_smaller_than_the_dimension() {
        val source = writeImage("photo.png", CompressFormat.PNG, noisyBitmap(100, 125))

        val resized = processor.resize(source, Dimension.LARGE, outputDirectory)

        assertThat(resized).isEqualTo(source)
        assertThat(fileSystem.list(outputDirectory)).isEmpty()
    }

    private fun writeImage(name: String, compressFormat: CompressFormat, bitmap: Bitmap): Path {
        val image = sourceDirectory / name
        try {
            fileSystem.write(image) { bitmap.compress(compressFormat, LOSSLESS_QUALITY, outputStream()) }
        } finally {
            bitmap.recycle()
        }
        return image
    }

    private companion object {
        const val TARGET_SIZE_BYTES = 100 * 1024L
        const val LOSSLESS_QUALITY = 100

        /**
         * High-entropy bitmap, so that the encoded file does not collapse to a trivial size.
         */
        fun noisyBitmap(width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val pixels = IntArray(width * height) { i ->
                val x = i % width
                val y = i / width
                Color.rgb((x * 37 + y * 17) and 0xFF, (x * 91 + y * 53) and 0xFF, (x * 13 + y * 191) and 0xFF)
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        }
    }
}
