/*
 *  Copyright (c) 2004-2022, University of Oslo
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
import java.io.File
import java.io.IOException
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.*
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.configuration.internal.migration.DatabaseConfigurationInsecureStoreOld
import org.hisp.dhis.android.core.configuration.internal.migration.DatabaseServerConfigurationOld
import org.hisp.dhis.android.core.configuration.internal.migration.DatabaseUserConfigurationOld
import org.hisp.dhis.android.core.configuration.internal.migration.DatabasesConfigurationOld
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DatabaseConfigurationMigrationIntegrationShould {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private val insecureStore: InsecureStore = InMemoryUnsecureStore()
    private val secureStore: SecureStore = InMemorySecureStore()
    private val chunkedSecureStore = ChunkedSecureStore(secureStore)

    private val nameGenerator = DatabaseNameGenerator()
    private val renamer = DatabaseRenamer(context)
    private val databaseAdapterFactory = DatabaseAdapterFactory.create(context, secureStore)

    private val serverUrl = "https://server.org"
    private val serverUrlWithApi = "https://server.org/api/"
    private val username = "usnm"
    private val newName = nameGenerator.getDatabaseName(serverUrl, username, false)

    private val credentials = UserCredentials.builder()
        .id(1L)
        .uid("uid")
        .username(username)
        .user(ObjectWithUid.create("user"))
        .build()

    private lateinit var migration: DatabaseConfigurationMigration
    private lateinit var databasesConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration>
    private lateinit var credentialsSecureStore: CredentialsSecureStore

    @Before
    @Throws(IOException::class)
    fun setUp() {
        databasesConfigurationStore = DatabaseConfigurationInsecureStore[insecureStore]
        credentialsSecureStore = CredentialsSecureStoreImpl(chunkedSecureStore)

        migration = DatabaseConfigurationMigration(
            context, databasesConfigurationStore,
            credentialsSecureStore, insecureStore, nameGenerator, renamer, databaseAdapterFactory
        )

        FileResourceDirectoryHelper.deleteRootFileResourceDirectory(context)
    }

    @Test
    fun delete_empty_database() {
        val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DatabaseConfigurationMigration.OLD_DBNAME, false)
        assertThat(context.databaseList().contains(DatabaseConfigurationMigration.OLD_DBNAME)).isTrue()

        migration.apply()
        assertThat(context.databaseList().contains(DatabaseConfigurationMigration.OLD_DBNAME)).isFalse()

        assertThat(credentialsSecureStore.get()).isNull()
    }

    @Test
    fun rename_database_with_credentials() {
        val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DatabaseConfigurationMigration.OLD_DBNAME, false)

        setCredentialsAndServerUrl(databaseAdapter)
        assertThat(context.databaseList().contains(DatabaseConfigurationMigration.OLD_DBNAME)).isTrue()

        migration.apply()
        assertThat(context.databaseList().contains(DatabaseConfigurationMigration.OLD_DBNAME)).isFalse()
        assertThat(context.databaseList().contains(newName)).isTrue()

        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, newName, false)
        val credentialsStore = UserCredentialsStoreImpl.create(databaseAdapter)
        assertThat(credentialsStore.selectFirst()).isEqualTo(credentials)

        assertThat(credentialsSecureStore.get()).isNull()
    }

    @Test
    fun return_empty_new_configuration_if_existing_empty_database() {
        val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DatabaseConfigurationMigration.OLD_DBNAME, false)

        migration.apply()

        assertThat(databasesConfigurationStore.get()?.accounts()).isEmpty()
    }

    @Test
    fun migrate_from_old_database_configuration() {
        val oldDatabaseConfiguration = DatabasesConfigurationOld.builder()
            .loggedServerUrl(serverUrlWithApi)
            .servers(
                listOf(
                    DatabaseServerConfigurationOld.builder()
                        .serverUrl(serverUrlWithApi)
                        .users(
                            listOf(
                                DatabaseUserConfigurationOld.builder()
                                    .username(username)
                                    .databaseName(newName)
                                    .databaseCreationDate("2014-06-06T20:44:21.375")
                                    .encrypted(false)
                                    .build()
                            )
                        ).build()
                )
            ).build()

        DatabaseConfigurationInsecureStoreOld.get(insecureStore).set(oldDatabaseConfiguration)

        val rootSdkResources = FileResourceDirectoryHelper.getRootFileResourceDirectory(context)
        File(rootSdkResources, "sample.txt").createNewFile()

        migration.apply()

        val migrated = databasesConfigurationStore.get()

        assertThat(migrated.accounts().size).isEqualTo(1)
        migrated.accounts().first().let {
            assertThat(it.username()).isEqualTo(username)
            assertThat(it.serverUrl()).isEqualTo(serverUrl)
            assertThat(it.databaseName()).isEqualTo(newName)
        }

        assertThat(rootSdkResources.listFiles()?.filter { it.isFile }).isEmpty()
        assertThat(rootSdkResources.listFiles()?.filter { it.isDirectory }?.size).isEqualTo(1)

        val directoryFiles = rootSdkResources.listFiles()?.find { it.isDirectory }?.listFiles()
        assertThat(directoryFiles?.size).isEqualTo(1)
        assertThat(directoryFiles?.first()?.name).isEqualTo("sample.txt")
    }

    @Test
    fun migrate_empty_from_old_database_configuration() {
        DatabaseConfigurationInsecureStoreOld.get(insecureStore).set(null)

        migration.apply()

        assertThat(databasesConfigurationStore.get()?.accounts()).isEmpty()
    }

    private fun setCredentialsAndServerUrl(databaseAdapter: DatabaseAdapter) {
        databaseAdapter.setForeignKeyConstraintsEnabled(false)
        val credentialsStore = UserCredentialsStoreImpl.create(databaseAdapter)
        credentialsStore.insert(credentials)
        val configurationStore = ConfigurationStore.create(databaseAdapter)
        configurationStore.insert(Configuration.forServerUrl(serverUrl))
    }
}
