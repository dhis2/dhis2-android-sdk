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
import okio.Path
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class FileResourceDirectoriesShould {

    private val fileSystem = FakeFileSystem()
    private val platformDirectories = FakePlatformDirectories(fileSystem)
    private val databaseAdapter: DatabaseAdapter = mock()

    private val directories = FileResourceDirectories(fileSystem, platformDirectories, databaseAdapter)

    @Before
    fun setUp() {
        whenever(databaseAdapter.getDatabaseName()).thenReturn("$ACCOUNT.db")
    }

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun create_the_account_directories_named_after_the_database() {
        val filesDirectory = directories.fileResourceDirectory()
        val cacheDirectory = directories.fileCacheResourceDirectory()

        assertThat(filesDirectory).isEqualTo(platformDirectories.filesDir / "sdk_resources" / ACCOUNT)
        assertThat(cacheDirectory).isEqualTo(platformDirectories.cacheDir / "sdk_cache_resources" / ACCOUNT)
        assertThat(fileSystem.metadata(filesDirectory).isDirectory).isTrue()
        assertThat(fileSystem.metadata(cacheDirectory).isDirectory).isTrue()
    }

    @Test
    fun create_the_root_directories_without_reading_the_account() {
        val filesDirectory = directories.rootFileResourceDirectory()
        val cacheDirectory = directories.rootFileCacheResourceDirectory()

        assertThat(filesDirectory).isEqualTo(platformDirectories.filesDir / "sdk_resources")
        assertThat(cacheDirectory).isEqualTo(platformDirectories.cacheDir / "sdk_cache_resources")
        assertThat(fileSystem.metadata(filesDirectory).isDirectory).isTrue()
        assertThat(fileSystem.metadata(cacheDirectory).isDirectory).isTrue()
        verifyNoInteractions(databaseAdapter)
    }

    @Test
    fun keep_the_content_of_existing_directories() {
        val file = writeFile(directories.fileResourceDirectory() / "file.png")

        directories.fileResourceDirectory()

        assertThat(fileSystem.exists(file)).isTrue()
    }

    @Test
    fun delete_only_the_files_directory_of_the_current_account() {
        val file = writeFile(directories.fileResourceDirectory() / "file.png")
        val cacheFile = writeFile(directories.fileCacheResourceDirectory() / "cache.png")

        directories.deleteFileResourceDirectory()

        assertThat(fileSystem.exists(file.parent!!)).isFalse()
        assertThat(fileSystem.exists(cacheFile)).isTrue()
    }

    @Test
    fun delete_the_files_and_cache_directories_of_an_account() {
        val otherFilesDirectory = platformDirectories.filesDir / "sdk_resources" / OTHER_ACCOUNT
        val otherCacheDirectory = platformDirectories.cacheDir / "sdk_cache_resources" / OTHER_ACCOUNT
        writeFile(otherFilesDirectory / "file.png")
        writeFile(otherCacheDirectory / "cache.png")
        val currentFile = writeFile(directories.fileResourceDirectory() / "file.png")

        val account: DatabaseAccount = mock()
        whenever(account.databaseName()).thenReturn("$OTHER_ACCOUNT.db")
        directories.deleteFileResourceDirectories(account)

        assertThat(fileSystem.exists(otherFilesDirectory)).isFalse()
        assertThat(fileSystem.exists(otherCacheDirectory)).isFalse()
        assertThat(fileSystem.exists(currentFile)).isTrue()
    }

    @Test
    fun not_fail_when_deleting_missing_directories() {
        directories.deleteFileResourceDirectory()

        assertThat(fileSystem.exists(platformDirectories.filesDir / "sdk_resources" / ACCOUNT)).isFalse()
    }

    private fun writeFile(path: Path): Path {
        fileSystem.createDirectories(path.parent!!)
        fileSystem.write(path) { writeUtf8("content") }
        return path
    }

    private companion object {
        const val ACCOUNT = "server_user_unencrypted"
        const val OTHER_ACCOUNT = "server_other_unencrypted"
    }
}
