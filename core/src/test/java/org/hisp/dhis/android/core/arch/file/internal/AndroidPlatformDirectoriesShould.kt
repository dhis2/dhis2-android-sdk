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

package org.hisp.dhis.android.core.arch.file.internal

import android.content.Context
import com.google.common.truth.Truth.assertThat
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.hisp.dhis.android.core.arch.ArchDIModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.modules
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File
import java.nio.file.Files

@RunWith(JUnit4::class)
class AndroidPlatformDirectoriesShould {

    private val context: Context = mock()

    private lateinit var tempDir: File
    private lateinit var directories: AndroidPlatformDirectories

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("platform-directories-test-").toFile()
        directories = AndroidPlatformDirectories(context)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun return_files_and_cache_directories_as_paths() {
        val filesDir = File(tempDir, "files")
        val cacheDir = File(tempDir, "cache")
        whenever(context.filesDir).thenReturn(filesDir)
        whenever(context.cacheDir).thenReturn(cacheDir)

        assertThat(directories.filesDir).isEqualTo(filesDir.toOkioPath())
        assertThat(directories.cacheDir).isEqualTo(cacheDir.toOkioPath())
    }

    @Test
    fun return_database_path_as_path() {
        val databaseFile = File(tempDir, "databases/$DB_NAME")
        whenever(context.getDatabasePath(DB_NAME)).thenReturn(databaseFile)

        assertThat(directories.databasePath(DB_NAME)).isEqualTo(databaseFile.toOkioPath())
    }

    @Test
    fun delegate_database_list_and_delete_to_context() {
        whenever(context.databaseList()).thenReturn(arrayOf(DB_NAME, "other.db"))
        whenever(context.deleteDatabase(DB_NAME)).thenReturn(true)

        assertThat(directories.databaseList()).containsExactly(DB_NAME, "other.db")
        assertThat(directories.deleteDatabase(DB_NAME)).isTrue()
        verify(context).deleteDatabase(DB_NAME)
    }

    @Test
    fun be_provided_with_the_system_file_system_by_koin() {
        val koin = koinApplication {
            modules(listOf(module { single { context } }))
            modules(ArchDIModule::class)
        }.koin

        assertThat(koin.get<PlatformDirectories>()).isInstanceOf(AndroidPlatformDirectories::class.java)
        assertThat(koin.get<FileSystem>()).isSameInstanceAs(FileSystem.SYSTEM)
    }

    private companion object {
        const val DB_NAME = "account.db"
    }
}
