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

package org.hisp.dhis.android.testapp.fileresource

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.fileresource.ImageDimension
import org.hisp.dhis.android.core.fileresource.internal.FileResourceDirectories
import org.hisp.dhis.android.core.fileresource.internal.FileResourceFiles.Companion.IMAGE_TARGET_SIZE_BYTES
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random

@RunWith(D2JunitRunner::class)
class FileResourceModuleMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val fileSystem = FileSystem.SYSTEM
    private val sourceDirectory =
        InstrumentationRegistry.getInstrumentation().context.cacheDir.toOkioPath() / "file-resource-module-test"
    private val createdFiles = mutableListOf<Path>()

    @Before
    fun setUp() {
        fileSystem.createDirectories(sourceDirectory)
    }

    @After
    fun tearDown() {
        createdFiles.forEach { fileSystem.delete(it) }
        fileSystem.deleteRecursively(sourceDirectory)
    }

    @Test
    fun return_the_directories_of_the_logged_account() {
        val subfolder = FileResourceDirectories.subfolderName(databaseAdapter.getDatabaseName())

        val directory = d2.fileResourceModule().fileResourceDirectory()
        val cacheDirectory = d2.fileResourceModule().fileCacheResourceDirectory()

        assertThat(directory.name).isEqualTo(subfolder)
        assertThat(directory.parent!!.name).isEqualTo(FileResourceDirectories.FILES_DIR)
        assertThat(fileSystem.metadata(directory).isDirectory).isTrue()
        assertThat(cacheDirectory.name).isEqualTo(subfolder)
        assertThat(cacheDirectory.parent!!.name).isEqualTo(FileResourceDirectories.CACHE_DIR)
        assertThat(fileSystem.metadata(cacheDirectory).isDirectory).isTrue()
    }

    @Test
    @Suppress("DEPRECATION")
    fun return_the_same_directories_as_the_deprecated_helper() {
        assertThat(d2.fileResourceModule().fileResourceDirectory().toFile())
            .isEqualTo(FileResourceDirectoryHelper.getFileResourceDirectory(d2.context()))
        assertThat(d2.fileResourceModule().fileCacheResourceDirectory().toFile())
            .isEqualTo(FileResourceDirectoryHelper.getFileCacheResourceDirectory(d2.context()))
    }

    @Test
    fun resize_an_image_into_the_cache_directory() {
        val image = writeImage("to-resize.png", noisyBitmap(2048, 1024))

        val resized = created(d2.fileResourceModule().resizeImage(image, ImageDimension.SMALL))

        assertThat(resized.parent).isEqualTo(d2.fileResourceModule().fileCacheResourceDirectory())
        val resizedBitmap = BitmapFactory.decodeFile(resized.toString())
        assertThat(resizedBitmap.width).isEqualTo(256)
        assertThat(resizedBitmap.height).isEqualTo(128)
    }

    @Test
    fun return_the_image_when_it_is_smaller_than_the_dimension() {
        val image = writeImage("small.png", noisyBitmap(100, 125))

        assertThat(d2.fileResourceModule().resizeImage(image, ImageDimension.LARGE)).isEqualTo(image)
    }

    @Test
    fun compress_an_image_into_the_cache_directory() {
        val target = 150 * 1024L
        val image = writeImage("to-compress.png", noisyBitmap(1500, 1500))
        assertThat(fileSystem.metadata(image).size!!).isGreaterThan(target)

        val compressed = created(d2.fileResourceModule().compressImage(image, target))

        assertThat(compressed.parent).isEqualTo(d2.fileResourceModule().fileCacheResourceDirectory())
        assertThat(fileSystem.metadata(compressed).size!!).isAtMost(target)
    }

    @Test
    fun compress_an_image_to_the_upload_size_by_default() {
        val image = writeImage("to-compress-by-default.png", noisyBitmap(1500, 1500))
        assertThat(fileSystem.metadata(image).size!!).isGreaterThan(IMAGE_TARGET_SIZE_BYTES)

        val compressed = created(d2.fileResourceModule().compressImage(image))

        assertThat(compressed).isNotEqualTo(image)
        assertThat(fileSystem.metadata(compressed).size!!).isAtMost(IMAGE_TARGET_SIZE_BYTES)
    }

    private fun created(file: Path): Path {
        return file.also { createdFiles.add(it) }
    }

    private fun writeImage(name: String, bitmap: Bitmap): Path {
        val image = sourceDirectory / name
        try {
            fileSystem.write(image) { bitmap.compress(CompressFormat.PNG, LOSSLESS_QUALITY, outputStream()) }
        } finally {
            bitmap.recycle()
        }
        return image
    }

    private companion object {
        const val LOSSLESS_QUALITY = 100
        const val OPAQUE = 0xFF000000.toInt()
        const val RGB_RANGE = 0xFFFFFF
        const val SEED = 42L

        /**
         * Random opaque pixels, which barely compress, so that the encoded file stays well above the target sizes.
         */
        fun noisyBitmap(width: Int, height: Int): Bitmap {
            val random = Random(SEED)
            val pixels = IntArray(width * height) { OPAQUE or random.nextInt(RGB_RANGE) }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        }
    }
}
