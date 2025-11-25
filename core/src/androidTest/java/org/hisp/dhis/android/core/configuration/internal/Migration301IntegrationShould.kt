/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.InMemoryUnsecureStore
import org.hisp.dhis.android.core.configuration.internal.migration.Migration301
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(D2JunitRunner::class)
class Migration301IntegrationShould {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val databaseConfigurationStore = DatabaseConfigurationInsecureStoreImpl(InMemoryUnsecureStore())
    private val nameGenerator = DatabaseNameGenerator()
    private val renamer = DatabaseRenamer(context)

    private lateinit var migration: Migration301

    @Before
    fun setUp() {
        migration = Migration301(
            context,
            databaseConfigurationStore,
            nameGenerator,
            renamer,
        )

        cleanupTestFiles()
    }

    @After
    fun tearDown() {
        cleanupTestFiles()
    }

    @Test
    fun migrate_database_with_collision_scenario() = runTest {
        val url1 = "https://play.dhis2.org/android-current"
        val url2 = "https://play.dhis2.org/android/current"
        val username = "admin"

        @Suppress("DEPRECATION")
        val oldDbName1 = nameGenerator.getOldDatabaseName(url1, username, false)

        @Suppress("DEPRECATION")
        val oldDbName2 = nameGenerator.getOldDatabaseName(url2, username, false)
        assertThat(oldDbName1).isEqualTo(oldDbName2)

        createDatabaseFile(oldDbName1)

        runMigration(
            listOf(
                createAccount(url1, username, oldDbName1, creationDate = "2025-01-01"),
                createAccount(url2, username, oldDbName2, creationDate = "2025-01-01"),
            ),
        )

        val migratedConfig = databaseConfigurationStore.get()
        assertThat(migratedConfig).isNotNull()
        assertThat(migratedConfig!!.accounts()).hasSize(2)

        val newDbName1 = migratedConfig.accounts()[0].databaseName()
        val newDbName2 = migratedConfig.accounts()[1].databaseName()

        assertThat(newDbName1).isNotEqualTo(newDbName2)
        assertHashedDbName(newDbName1, encrypted = false)
        assertHashedDbName(newDbName2, encrypted = false)

        assertThat(context.getDatabasePath(newDbName1).exists()).isTrue()
        assertThat(context.getDatabasePath(newDbName2).exists()).isFalse()
    }

    @Test
    fun migrate_file_resource_directories() = runTest {
        val url = "https://play.dhis2.org/demo"
        val username = "test_user"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)
        val oldSubfolder = oldDbName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)

        val oldResourcesDir = File(context.filesDir, "sdk_resources/$oldSubfolder")
        oldResourcesDir.mkdirs()
        File(oldResourcesDir, "test_file.jpg").apply {
            createNewFile()
            writeText("test content")
        }

        val oldCacheDir = File(context.cacheDir, "sdk_cache_resources/$oldSubfolder")
        oldCacheDir.mkdirs()
        File(oldCacheDir, "test_cache.jpg").apply {
            createNewFile()
            writeText("cached content")
        }

        createDatabaseFile(oldDbName)
        runMigration(listOf(createAccount(url, username, oldDbName)))

        val migratedConfig = databaseConfigurationStore.get()
        val newDbName = migratedConfig!!.accounts()[0].databaseName()
        val newSubfolder = newDbName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)

        val newResourcesDir = File(context.filesDir, "sdk_resources/$newSubfolder")
        val newCacheDir = File(context.cacheDir, "sdk_cache_resources/$newSubfolder")

        assertThat(newResourcesDir.exists()).isTrue()
        assertThat(File(newResourcesDir, "test_file.jpg").exists()).isTrue()
        assertThat(File(newResourcesDir, "test_file.jpg").readText()).isEqualTo("test content")

        assertThat(newCacheDir.exists()).isTrue()
        assertThat(File(newCacheDir, "test_cache.jpg").exists()).isTrue()
        assertThat(File(newCacheDir, "test_cache.jpg").readText()).isEqualTo("cached content")

        assertThat(oldResourcesDir.exists()).isFalse()
        assertThat(oldCacheDir.exists()).isFalse()
    }

    @Test
    fun update_protected_db_name_for_pending_import() = runTest {
        val url = "https://play.dhis2.org/import-test"
        val username = "import_user"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)
        val oldProtectedName = "$oldDbName.protected"

        context.getDatabasePath(oldProtectedName).apply {
            parentFile?.mkdirs()
            createNewFile()
            writeText("protected db content")
        }

        val importDB = DatabaseAccountImport.builder()
            .status(DatabaseAccountImportStatus.PENDING_TO_IMPORT)
            .protectedDbName(oldProtectedName)
            .build()

        runMigration(listOf(createAccount(url, username, oldDbName, importDB = importDB)))

        val migratedAccount = databaseConfigurationStore.get()!!.accounts()[0]
        val newDbName = migratedAccount.databaseName()
        val expectedNewProtectedName = "$newDbName.protected"

        assertThat(migratedAccount.importDB()).isNotNull()
        assertThat(migratedAccount.importDB()!!.protectedDbName()).isEqualTo(expectedNewProtectedName)
        assertThat(context.getDatabasePath(expectedNewProtectedName).exists()).isTrue()
        assertThat(context.getDatabasePath(expectedNewProtectedName).readText()).isEqualTo("protected db content")
        assertThat(context.getDatabasePath(oldProtectedName).exists()).isFalse()
    }

    @Test
    fun skip_migration_if_already_applied() = runTest {
        val url = "https://play.dhis2.org/test"
        val username = "test_user"

        val newDbName = nameGenerator.getDatabaseName(url, username, false)
        createDatabaseFile(newDbName)

        runMigration(listOf(createAccount(url, username, newDbName)))

        val configAfterMigration = databaseConfigurationStore.get()
        assertThat(configAfterMigration!!.accounts()[0].databaseName()).isEqualTo(newDbName)
        assertThat(context.getDatabasePath(newDbName).exists()).isTrue()
    }

    @Test
    fun handle_accounts_without_physical_database_file() = runTest {
        val url = "https://play.dhis2.org/no-db"
        val username = "test_user"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)

        runMigration(listOf(createAccount(url, username, oldDbName)))

        val newDbName = databaseConfigurationStore.get()!!.accounts()[0].databaseName()

        assertThat(newDbName).isNotEqualTo(oldDbName)
        assertHashedDbName(newDbName, encrypted = false)
        assertThat(context.getDatabasePath(newDbName).exists()).isFalse()
        assertThat(context.getDatabasePath(oldDbName).exists()).isFalse()
    }

    @Test
    fun migrate_multiple_accounts_successfully() = runTest {
        val accountData = listOf(
            Triple("https://server1.org", "user1", false),
            Triple("https://server2.org", "user2", true),
            Triple("https://server3.org", "user3", false),
        )

        val accounts = accountData.map { (url, username, encrypted) ->
            @Suppress("DEPRECATION")
            val oldDbName = nameGenerator.getOldDatabaseName(url, username, encrypted)
            createDatabaseFile(oldDbName)
            createAccount(url, username, oldDbName, encrypted)
        }

        runMigration(accounts)

        val migratedConfig = databaseConfigurationStore.get()
        assertThat(migratedConfig!!.accounts()).hasSize(3)

        migratedConfig.accounts().forEach { account ->
            assertHashedDbName(account.databaseName(), account.encrypted())
            assertThat(context.getDatabasePath(account.databaseName()).exists()).isTrue()
        }

        val allNames = migratedConfig.accounts().map { it.databaseName() }
        assertThat(allNames.distinct()).hasSize(3)
    }

    private fun createDatabaseFile(dbName: String) {
        context.getDatabasePath(dbName).apply {
            parentFile?.mkdirs()
            createNewFile()
        }
    }

    private fun createAccount(
        url: String,
        username: String,
        dbName: String,
        encrypted: Boolean = false,
        creationDate: String = "2024-01-01",
        importDB: DatabaseAccountImport? = null,
    ): DatabaseAccount {
        return DatabaseAccount.builder()
            .username(username)
            .serverUrl(url)
            .databaseName(dbName)
            .encrypted(encrypted)
            .databaseCreationDate(creationDate)
            .importDB(importDB)
            .build()
    }

    private fun runMigration(accounts: List<DatabaseAccount>) {
        val configuration = DatabasesConfiguration.builder()
            .accounts(accounts)
            .build()
        databaseConfigurationStore.set(configuration)
        migration.apply()
    }

    private fun assertHashedDbName(dbName: String, encrypted: Boolean) {
        val encryptionSuffix = if (encrypted) "encrypted" else "unencrypted"
        assertThat(dbName).matches(".*_[0-9a-f]{8}_$encryptionSuffix\\.db")
    }

    private fun cleanupTestFiles() {
        context.databaseList().forEach { dbName ->
            if (dbName.contains("play") || dbName.contains("demo") ||
                dbName.contains("server") || dbName.contains("test")
            ) {
                context.deleteDatabase(dbName)
            }
        }

        FileResourceDirectoryHelper.deleteRootFileResourceDirectory(context)
    }
}
