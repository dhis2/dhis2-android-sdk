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
import okio.Buffer
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.arch.helpers.FileCompressionHelper
import org.hisp.dhis.android.core.arch.helpers.internal.ImageProcessor
import org.hisp.dhis.android.core.fileresource.FileResource
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class FileResourceFilesShould {

    private val fileSystem = FakeFileSystem()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val directories = FileResourceDirectories(fileSystem, FakePlatformDirectories(fileSystem), databaseAdapter)
    private val imageProcessor: ImageProcessor = mock()

    private val files = FileResourceFiles(fileSystem, directories, imageProcessor)

    @Before
    fun setUp() {
        whenever(databaseAdapter.getDatabaseName()).thenReturn("account.db")
    }

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun save_a_copy_named_after_the_uid_keeping_the_extension() {
        val sourceFile = writeFile("/external/photo.jpg".toPath(), CONTENT)

        val savedFile = files.saveFile(sourceFile, UID)

        assertThat(savedFile).isEqualTo(directories.fileResourceDirectory() / "$UID.jpg")
        assertThat(readFile(savedFile)).isEqualTo(CONTENT)
        assertThat(fileSystem.exists(sourceFile)).isTrue()
    }

    @Test
    fun save_a_copy_without_extension_when_the_source_has_none() {
        val sourceFile = writeFile("/external/photo".toPath(), CONTENT)

        val savedFile = files.saveFile(sourceFile, UID)

        assertThat(savedFile.name).isEqualTo(UID)
    }

    @Test
    fun fail_to_save_a_missing_file() {
        assertThrows(IOException::class.java) {
            files.saveFile("/external/missing.jpg".toPath(), UID)
        }
    }

    @Test
    fun save_the_response_body_named_after_the_file_resource() {
        val fileResource = FileResource.builder().uid(UID).name("report.pdf").build()

        val savedFile = files.saveFileFromResponse(CONTENT.encodeToByteArray(), fileResource)

        assertThat(savedFile).isEqualTo(directories.fileResourceDirectory() / "$UID.pdf")
        assertThat(readFile(savedFile)).isEqualTo(CONTENT)
    }

    @Test
    fun rename_a_file_in_its_directory_keeping_the_extension() {
        val file = writeFile(directories.fileResourceDirectory() / "local.png", CONTENT)

        val renamedFile = files.renameFile(file, UID)

        assertThat(renamedFile).isEqualTo(file.parent!! / "$UID.png")
        assertThat(readFile(renamedFile)).isEqualTo(CONTENT)
        assertThat(fileSystem.exists(file)).isFalse()
    }

    @Test
    fun return_the_new_path_when_renaming_fails() {
        val missingFile = directories.fileResourceDirectory() / "missing.png"

        val renamedFile = files.renameFile(missingFile, UID)

        assertThat(renamedFile).isEqualTo(missingFile.parent!! / "$UID.png")
        assertThat(fileSystem.exists(renamedFile)).isFalse()
    }

    @Test
    fun get_the_file_of_a_file_resource_only_if_it_exists() {
        val file = writeFile(directories.fileResourceDirectory() / "$UID.png", CONTENT)
        val missingFile = directories.fileResourceDirectory() / "missing.png"

        assertThat(files.getFile(fileResourceWithPath(file.toString()))).isEqualTo(file)
        assertThat(files.getFile(fileResourceWithPath(missingFile.toString()))).isNull()
        assertThat(files.getFile(fileResourceWithPath(null))).isNull()
    }

    @Test
    fun read_the_bytes_of_a_file() {
        val file = writeFile(directories.fileResourceDirectory() / "$UID.png", CONTENT)

        assertThat(files.readBytes(file)).isEqualTo(CONTENT.encodeToByteArray())
    }

    @Test
    fun write_a_source_and_close_it() {
        val file = directories.fileResourceDirectory() / "$UID.txt"

        files.writeSource(Buffer().writeUtf8(CONTENT), file)

        assertThat(readFile(file)).isEqualTo(CONTENT)
    }

    @Test
    fun compress_images_into_the_cache_directory_of_the_account() {
        val image = "/external/photo.png".toPath()
        val compressedImage = directories.fileCacheResourceDirectory() / "compressed-photo.jpg"
        whenever(imageProcessor.compress(any(), any(), any())).thenReturn(compressedImage)

        val result = files.compressImage(image)

        assertThat(result).isEqualTo(compressedImage)
        verify(imageProcessor).compress(
            image,
            FileCompressionHelper.TARGET_SIZE_BYTES,
            directories.fileCacheResourceDirectory(),
        )
    }

    private fun fileResourceWithPath(path: String?): FileResource {
        return FileResource.builder().uid(UID).path(path).build()
    }

    private fun writeFile(path: Path, content: String): Path {
        fileSystem.createDirectories(path.parent!!)
        fileSystem.write(path) { writeUtf8(content) }
        return path
    }

    private fun readFile(path: Path): String {
        return fileSystem.read(path) { readUtf8() }
    }

    private companion object {
        const val UID = "fileResourceUid"
        const val CONTENT = "file content"
    }
}
