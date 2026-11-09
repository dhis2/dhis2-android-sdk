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

package org.hisp.dhis.android.core.configuration.internal

import com.google.common.truth.Truth.assertThat
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DatabaseRenamerShould {

    private val fileSystem = FakeFileSystem()
    private val platformDirectories = FakePlatformDirectories(fileSystem)

    private val renamer = DatabaseRenamer(fileSystem, platformDirectories)

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun rename_the_database_and_its_companion_files() {
        writeDatabaseFile(OLD_NAME, "database")
        writeDatabaseFile("$OLD_NAME-wal", "wal")
        writeDatabaseFile("$OLD_NAME-shm", "shm")

        val renamed = renamer.renameDatabase(OLD_NAME, NEW_NAME)

        assertThat(renamed).isTrue()
        assertThat(readDatabaseFile(NEW_NAME)).isEqualTo("database")
        assertThat(readDatabaseFile("$NEW_NAME-wal")).isEqualTo("wal")
        assertThat(readDatabaseFile("$NEW_NAME-shm")).isEqualTo("shm")
        assertThat(fileSystem.exists(platformDirectories.databasePath(OLD_NAME))).isFalse()
        assertThat(fileSystem.exists(platformDirectories.databasePath("$OLD_NAME-wal"))).isFalse()
        assertThat(fileSystem.exists(platformDirectories.databasePath("$NEW_NAME-journal"))).isFalse()
    }

    @Test
    fun return_false_when_the_database_does_not_exist() {
        val renamed = renamer.renameDatabase(OLD_NAME, NEW_NAME)

        assertThat(renamed).isFalse()
        assertThat(fileSystem.exists(platformDirectories.databasePath(NEW_NAME))).isFalse()
    }

    private fun writeDatabaseFile(name: String, content: String) {
        val path = platformDirectories.databasePath(name)
        fileSystem.createDirectories(path.parent!!)
        fileSystem.write(path) { writeUtf8(content) }
    }

    private fun readDatabaseFile(name: String): String {
        return fileSystem.read(platformDirectories.databasePath(name)) { readUtf8() }
    }

    private companion object {
        const val OLD_NAME = "old_unencrypted.db"
        const val NEW_NAME = "new_unencrypted.db"
    }
}
