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

package org.hisp.dhis.android.core.configuration.internal.migration

import com.google.common.truth.Truth.assertThat
import okio.Path
import okio.fakefilesystem.FakeFileSystem
import org.hisp.dhis.android.core.arch.file.internal.FakePlatformDirectories
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.storage.internal.InMemoryUnsecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStoreImpl
import org.hisp.dhis.android.core.configuration.internal.DatabaseNameGenerator
import org.hisp.dhis.android.core.configuration.internal.DatabaseRenamer
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Migration301Should {

    private val fileSystem = FakeFileSystem()
    private val platformDirectories = FakePlatformDirectories(fileSystem)
    private val databaseConfigurationStore = DatabaseConfigurationInsecureStoreImpl(InMemoryUnsecureStore())
    private val nameGenerator = DatabaseNameGenerator()

    private val migration = Migration301(
        fileSystem,
        platformDirectories,
        databaseConfigurationStore,
        nameGenerator,
        DatabaseRenamer(fileSystem, platformDirectories),
    )

    private val newDbName = nameGenerator.getDatabaseName(SERVER_URL, USERNAME, false)
    private val oldSubfolder = OLD_DB_NAME.removeSuffix(".db")
    private val newSubfolder = newDbName.removeSuffix(".db")
    private val resourcesDir = platformDirectories.filesDir / "sdk_resources"
    private val cacheDir = platformDirectories.cacheDir / "sdk_cache_resources"

    @After
    fun tearDown() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun rename_the_database_its_companion_files_and_its_resource_directories() {
        writeFile(platformDirectories.databasePath(OLD_DB_NAME), "database")
        writeFile(platformDirectories.databasePath("$OLD_DB_NAME-wal"), "wal")
        writeFile(resourcesDir / oldSubfolder / "image.png", "resource")
        writeFile(cacheDir / oldSubfolder / "cache.png", "cache")

        runMigration()

        assertThat(migratedAccount().databaseName()).isEqualTo(newDbName)
        assertThat(readFile(platformDirectories.databasePath(newDbName))).isEqualTo("database")
        assertThat(readFile(platformDirectories.databasePath("$newDbName-wal"))).isEqualTo("wal")
        assertThat(readFile(resourcesDir / newSubfolder / "image.png")).isEqualTo("resource")
        assertThat(readFile(cacheDir / newSubfolder / "cache.png")).isEqualTo("cache")
        assertThat(fileSystem.exists(platformDirectories.databasePath(OLD_DB_NAME))).isFalse()
        assertThat(fileSystem.exists(resourcesDir / oldSubfolder)).isFalse()
        assertThat(fileSystem.exists(cacheDir / oldSubfolder)).isFalse()
    }

    @Test
    fun not_overwrite_an_existing_target_database() {
        writeFile(platformDirectories.databasePath(OLD_DB_NAME), "old database")
        writeFile(platformDirectories.databasePath(newDbName), "new database")

        runMigration()

        assertThat(readFile(platformDirectories.databasePath(OLD_DB_NAME))).isEqualTo("old database")
        assertThat(readFile(platformDirectories.databasePath(newDbName))).isEqualTo("new database")
    }

    @Test
    fun keep_the_account_unchanged_when_a_resource_directory_cannot_be_renamed() {
        writeFile(resourcesDir / oldSubfolder / "image.png", "resource")
        writeFile(resourcesDir / newSubfolder / "other.png", "other")

        runMigration()

        assertThat(migratedAccount().databaseName()).isEqualTo(OLD_DB_NAME)
        assertThat(readFile(resourcesDir / oldSubfolder / "image.png")).isEqualTo("resource")
    }

    private fun runMigration() {
        val account = DatabaseAccount.builder()
            .username(USERNAME)
            .serverUrl(SERVER_URL)
            .databaseName(OLD_DB_NAME)
            .encrypted(false)
            .databaseCreationDate(DateUtils.SIMPLE_DATE_FORMAT.parse("2024-01-01"))
            .build()
        databaseConfigurationStore.set(DatabasesConfiguration.builder().accounts(listOf(account)).build())

        migration.apply()
    }

    private fun migratedAccount(): DatabaseAccount {
        return databaseConfigurationStore.get()!!.accounts().single()
    }

    private fun writeFile(path: Path, content: String) {
        fileSystem.createDirectories(path.parent!!)
        fileSystem.write(path) { writeUtf8(content) }
    }

    private fun readFile(path: Path): String {
        return fileSystem.read(path) { readUtf8() }
    }

    private companion object {
        const val SERVER_URL = "https://play.dhis2.org/test"
        const val USERNAME = "user"
        const val OLD_DB_NAME = "play-dhis2-org-test_user_unencrypted.db"
    }
}
