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
import kotlinx.coroutines.test.runTest
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.wipe.internal.TableWiper
import org.hisp.dhis.android.persistence.fileresource.FileResourceTableInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class FileResourceModuleWiperShould {

    private val fileSystem = FakeFileSystem()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val tableWiper: TableWiper = mock()
    private val directories = FileResourceDirectories(fileSystem, FakePlatformDirectories(fileSystem), databaseAdapter)

    private val wiper = FileResourceModuleWiper(tableWiper, directories)

    @Before
    fun setUp() {
        whenever(databaseAdapter.getDatabaseName()).thenReturn("account.db")
    }

    @Test
    fun wipe_the_table_and_the_files_of_the_current_account() = runTest {
        val file = directories.fileResourceDirectory() / "file.png"
        fileSystem.write(file) { writeUtf8("content") }

        wiper.wipeData()

        verify(tableWiper).wipeTable(FileResourceTableInfo.TABLE_INFO)
        assertThat(fileSystem.exists(file)).isFalse()
    }

    @Test
    fun not_wipe_anything_when_wiping_metadata() = runTest {
        val file = directories.fileResourceDirectory() / "file.png"
        fileSystem.write(file) { writeUtf8("content") }

        wiper.wipeMetadata()

        verifyNoInteractions(tableWiper)
        assertThat(fileSystem.exists(file)).isTrue()
    }
}
