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

package org.hisp.dhis.android.core.util

import com.google.common.truth.Truth.assertThat
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FileExtensionsShould {

    private val fileSystem = FakeFileSystem()
    private val directory = "/app/files".toPath()

    @Test
    fun delete_an_existing_file() {
        val file = directory / "file.txt"
        fileSystem.createDirectories(directory)
        fileSystem.write(file) { writeUtf8("content") }

        fileSystem.deleteIfExists(file)

        assertThat(fileSystem.exists(file)).isFalse()
    }

    @Test
    fun not_fail_when_the_file_does_not_exist() {
        fileSystem.deleteIfExists(directory / "missing.txt")
    }

    @Test
    fun throw_d2_error_when_the_file_cannot_be_deleted() {
        fileSystem.createDirectories(directory)
        fileSystem.write(directory / "file.txt") { writeUtf8("content") }

        val error = assertThrows(D2Error::class.java) { fileSystem.deleteIfExists(directory) }

        assertThat(error.errorCode()).isEqualTo(D2ErrorCode.UNEXPECTED)
        assertThat(fileSystem.exists(directory)).isTrue()
    }

    @Test
    fun copy_a_file_creating_the_target_directories() {
        val source = writeFile(directory / "source.txt", "content")
        val target = "/app/databases/target.txt".toPath()

        fileSystem.copyTo(source, target)

        assertThat(readFile(target)).isEqualTo("content")
        assertThat(fileSystem.exists(source)).isTrue()
    }

    @Test
    fun not_overwrite_an_existing_file_by_default() {
        val source = writeFile(directory / "source.txt", "content")
        val target = writeFile(directory / "target.txt", "existing")

        assertThrows(IOException::class.java) { fileSystem.copyTo(source, target) }

        assertThat(readFile(target)).isEqualTo("existing")
    }

    @Test
    fun overwrite_an_existing_file_when_requested() {
        val source = writeFile(directory / "source.txt", "content")
        val target = writeFile(directory / "target.txt", "existing")

        fileSystem.copyTo(source, target, overwrite = true)

        assertThat(readFile(target)).isEqualTo("content")
    }

    private fun writeFile(path: Path, content: String): Path {
        fileSystem.createDirectories(path.parent!!)
        fileSystem.write(path) { writeUtf8(content) }
        return path
    }

    private fun readFile(path: Path): String {
        return fileSystem.read(path) { readUtf8() }
    }
}
