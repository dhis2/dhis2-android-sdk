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
import org.hisp.dhis.android.core.configuration.internal.migration.MigrationDatabaseNameHash
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(D2JunitRunner::class)
class MigrationDatabaseNameHashIntegrationShould {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val databaseConfigurationStore = DatabaseConfigurationInsecureStoreImpl(InMemoryUnsecureStore())
    private val nameGenerator = DatabaseNameGenerator()
    private val renamer = DatabaseRenamer(context)

    private lateinit var migration: MigrationDatabaseNameHash

    @Before
    fun setUp() {
        migration = MigrationDatabaseNameHash(
            context,
            databaseConfigurationStore,
            nameGenerator,
            renamer,
        )

        // Clean up any existing test files
        cleanupTestFiles()
    }

    @After
    fun tearDown() {
        cleanupTestFiles()
    }

    @Test
    fun migrate_database_with_collision_scenario() = runTest {
        // Setup: Create two accounts with URLs that would collide in old format
        val url1 = "https://play.dhis2.org/android-current"
        val url2 = "https://play.dhis2.org/android/current"
        val username = "admin"

        @Suppress("DEPRECATION")
        val oldDbName1 = nameGenerator.getOldDatabaseName(url1, username, false)

        @Suppress("DEPRECATION")
        val oldDbName2 = nameGenerator.getOldDatabaseName(url2, username, false)

        // These should collide (same old name)
        assertThat(oldDbName1).isEqualTo(oldDbName2)

        // Create physical database file for first account
        context.getDatabasePath(oldDbName1).apply {
            parentFile?.mkdirs()
            createNewFile()
        }

        // Create configuration with both accounts pointing to same DB
        val configuration = DatabasesConfiguration.builder()
            .accounts(
                listOf(
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url1)
                        .databaseName(oldDbName1)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .build(),
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url2)
                        .databaseName(oldDbName2)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .build(),
                ),
            )
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: Accounts now have different database names
        val migratedConfig = databaseConfigurationStore.get()
        assertThat(migratedConfig).isNotNull()
        assertThat(migratedConfig!!.accounts()).hasSize(2)

        val newDbName1 = migratedConfig.accounts()[0].databaseName()
        val newDbName2 = migratedConfig.accounts()[1].databaseName()

        // Names should be different now
        assertThat(newDbName1).isNotEqualTo(newDbName2)

        // Both should have hash suffix (8 hex chars)
        assertThat(newDbName1).matches(".*_[0-9a-f]{8}_unencrypted\\.db")
        assertThat(newDbName2).matches(".*_[0-9a-f]{8}_unencrypted\\.db")

        // First account should have the migrated database file
        assertThat(context.getDatabasePath(newDbName1).exists()).isTrue()
        // Second account DB file should not exist (collision resolved)
        assertThat(context.getDatabasePath(newDbName2).exists()).isFalse()
    }

    @Test
    fun migrate_file_resource_directories() = runTest {
        val url = "https://play.dhis2.org/demo"
        val username = "testuser"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)
        val oldSubfolder = oldDbName.removeSuffix(DatabaseNameGenerator.DB_SUFFIX)

        // Create old FileResource directory structure
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

        // Create database file
        context.getDatabasePath(oldDbName).apply {
            parentFile?.mkdirs()
            createNewFile()
        }

        // Create configuration
        val configuration = DatabasesConfiguration.builder()
            .accounts(
                listOf(
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url)
                        .databaseName(oldDbName)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .build(),
                ),
            )
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: FileResource directories renamed
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

        // Old directories should be gone
        assertThat(oldResourcesDir.exists()).isFalse()
        assertThat(oldCacheDir.exists()).isFalse()
    }

    @Test
    fun update_protected_db_name_for_pending_import() = runTest {
        val url = "https://play.dhis2.org/import-test"
        val username = "importuser"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)
        val oldProtectedName = "$oldDbName.protected"

        // Create protected DB file
        context.getDatabasePath(oldProtectedName).apply {
            parentFile?.mkdirs()
            createNewFile()
            writeText("protected db content")
        }

        // Create configuration with pending import
        val configuration = DatabasesConfiguration.builder()
            .accounts(
                listOf(
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url)
                        .databaseName(oldDbName)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .importDB(
                            DatabaseAccountImport.builder()
                                .status(DatabaseAccountImportStatus.PENDING_TO_IMPORT)
                                .protectedDbName(oldProtectedName)
                                .build(),
                        )
                        .build(),
                ),
            )
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: protectedDbName updated
        val migratedConfig = databaseConfigurationStore.get()
        val migratedAccount = migratedConfig!!.accounts()[0]
        val newDbName = migratedAccount.databaseName()
        val expectedNewProtectedName = "$newDbName.protected"

        assertThat(migratedAccount.importDB()).isNotNull()
        assertThat(migratedAccount.importDB()!!.protectedDbName()).isEqualTo(expectedNewProtectedName)

        // Protected file should be renamed
        assertThat(context.getDatabasePath(expectedNewProtectedName).exists()).isTrue()
        assertThat(context.getDatabasePath(expectedNewProtectedName).readText()).isEqualTo("protected db content")
        assertThat(context.getDatabasePath(oldProtectedName).exists()).isFalse()
    }

    @Test
    fun skip_migration_if_already_applied() = runTest {
        val url = "https://play.dhis2.org/test"
        val username = "testuser"

        // Create configuration with already migrated account (has hash)
        val newDbName = nameGenerator.getDatabaseName(url, username, false)

        context.getDatabasePath(newDbName).apply {
            parentFile?.mkdirs()
            createNewFile()
        }

        val configuration = DatabasesConfiguration.builder()
            .accounts(
                listOf(
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url)
                        .databaseName(newDbName)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .build(),
                ),
            )
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: Configuration unchanged (migration skipped)
        val configAfterMigration = databaseConfigurationStore.get()
        assertThat(configAfterMigration!!.accounts()[0].databaseName()).isEqualTo(newDbName)

        // DB file should still exist with same name
        assertThat(context.getDatabasePath(newDbName).exists()).isTrue()
    }

    @Test
    fun handle_accounts_without_physical_database_file() = runTest {
        val url = "https://play.dhis2.org/no-db"
        val username = "testuser"

        @Suppress("DEPRECATION")
        val oldDbName = nameGenerator.getOldDatabaseName(url, username, false)

        // Create configuration but NO physical database file
        val configuration = DatabasesConfiguration.builder()
            .accounts(
                listOf(
                    DatabaseAccount.builder()
                        .username(username)
                        .serverUrl(url)
                        .databaseName(oldDbName)
                        .encrypted(false)
                        .databaseCreationDate("2024-01-01")
                        .build(),
                ),
            )
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: Account migrated with new name even without physical file
        val migratedConfig = databaseConfigurationStore.get()
        val newDbName = migratedConfig!!.accounts()[0].databaseName()

        assertThat(newDbName).isNotEqualTo(oldDbName)
        assertThat(newDbName).matches(".*_[0-9a-f]{8}_unencrypted\\.db")

        // No physical file should exist
        assertThat(context.getDatabasePath(newDbName).exists()).isFalse()
        assertThat(context.getDatabasePath(oldDbName).exists()).isFalse()
    }

    @Test
    fun migrate_multiple_accounts_successfully() = runTest {
        val accounts = listOf(
            Triple("https://server1.org", "user1", false),
            Triple("https://server2.org", "user2", true),
            Triple("https://server3.org", "user3", false),
        )

        // Create old-format accounts with physical files
        val oldAccounts = accounts.map { (url, username, encrypted) ->
            @Suppress("DEPRECATION")
            val oldDbName = nameGenerator.getOldDatabaseName(url, username, encrypted)

            context.getDatabasePath(oldDbName).apply {
                parentFile?.mkdirs()
                createNewFile()
            }

            DatabaseAccount.builder()
                .username(username)
                .serverUrl(url)
                .databaseName(oldDbName)
                .encrypted(encrypted)
                .databaseCreationDate("2024-01-01")
                .build()
        }

        val configuration = DatabasesConfiguration.builder()
            .accounts(oldAccounts)
            .build()

        databaseConfigurationStore.set(configuration)

        // Execute migration
        migration.apply()

        // Verify: All accounts migrated
        val migratedConfig = databaseConfigurationStore.get()
        assertThat(migratedConfig!!.accounts()).hasSize(3)

        migratedConfig.accounts().forEach { account ->
            // Should have hash format
            val encryptionSuffix = if (account.encrypted()) "encrypted" else "unencrypted"
            assertThat(account.databaseName()).matches(".*_[0-9a-f]{8}_$encryptionSuffix\\.db")

            // Physical file should exist
            assertThat(context.getDatabasePath(account.databaseName()).exists()).isTrue()
        }

        // All new names should be unique
        val allNames = migratedConfig.accounts().map { it.databaseName() }
        assertThat(allNames.distinct()).hasSize(3)
    }

    private fun cleanupTestFiles() {
        // Clean up database files
        context.databaseList().forEach { dbName ->
            if (dbName.contains("play") || dbName.contains("demo") ||
                dbName.contains("server") || dbName.contains("test")
            ) {
                context.deleteDatabase(dbName)
            }
        }

        // Clean up FileResource directories
        FileResourceDirectoryHelper.deleteRootFileResourceDirectory(context)
    }
}
