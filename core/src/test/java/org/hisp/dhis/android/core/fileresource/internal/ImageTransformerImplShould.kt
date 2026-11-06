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

package org.hisp.dhis.android.core.fileresource.internal

import com.google.common.truth.Truth.assertThat
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.arch.helpers.internal.ImageProcessor
import org.hisp.dhis.android.core.fileresource.ImageDimension
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ImageTransformerImplShould {

    private val fileSystem = FakeFileSystem()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val directories =
        FileResourceDirectoriesImpl(fileSystem, FakePlatformDirectories(fileSystem), databaseAdapter)
    private val imageProcessor: ImageProcessor = mock()

    private val transformer = ImageTransformerImpl(imageProcessor, directories)

    private val image = "/external/photo.png".toPath()

    @Before
    fun setUp() {
        whenever(databaseAdapter.getDatabaseName()).thenReturn("account.db")
    }

    @Test
    fun compress_images_into_the_cache_directory_of_the_account() {
        val compressedImage = directories.fileCacheResourceDirectory() / "compressed-photo.jpg"
        whenever(imageProcessor.compress(any(), any(), any())).thenReturn(compressedImage)

        val result = transformer.compress(image)

        assertThat(result).isEqualTo(compressedImage)
        verify(imageProcessor).compress(
            image,
            ImageTransformerImpl.IMAGE_TARGET_SIZE_BYTES,
            directories.fileCacheResourceDirectory(),
        )
    }

    @Test
    fun compress_images_to_the_given_target_size() {
        whenever(imageProcessor.compress(any(), any(), any())).thenReturn(image)

        transformer.compress(image, TARGET_SIZE_BYTES)

        verify(imageProcessor).compress(image, TARGET_SIZE_BYTES, directories.fileCacheResourceDirectory())
    }

    @Test
    fun resize_images_into_the_cache_directory_of_the_account() {
        val resizedImage = directories.fileCacheResourceDirectory() / "resized-SMALL-photo.png"
        whenever(imageProcessor.resize(any(), any(), any())).thenReturn(resizedImage)

        val result = transformer.resize(image, ImageDimension.SMALL)

        assertThat(result).isEqualTo(resizedImage)
        verify(imageProcessor).resize(image, ImageDimension.SMALL, directories.fileCacheResourceDirectory())
    }

    private companion object {
        const val TARGET_SIZE_BYTES = 100 * 1024L
    }
}
