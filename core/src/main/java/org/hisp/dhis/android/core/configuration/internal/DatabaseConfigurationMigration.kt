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

import android.content.Context
import android.database.SQLException
import androidx.sqlite.db.SimpleSQLiteQuery
import org.hisp.dhis.android.BuildConfig
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore
import org.hisp.dhis.android.core.configuration.internal.migration.DatabaseConfigurationInsecureStoreOld
import org.hisp.dhis.android.core.configuration.internal.migration.Migration260
import org.hisp.dhis.android.persistence.configuration.ConfigurationStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class DatabaseConfigurationMigration(
    private val context: Context,
    private val databaseConfigurationStore: DatabaseConfigurationInsecureStore,
    private val credentialsStore: CredentialsSecureStore,
    private val insecureStore: InsecureStore,
    private val nameGenerator: DatabaseNameGenerator,
    private val renamer: DatabaseRenamer,
    private val databaseManager: DatabaseManager,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend fun apply() {
        var existingVersionCode: Long? = null
        val oldDatabaseExist = context.databaseList().contains(OLD_DBNAME)

        if (oldDatabaseExist) {
            // This is the initial database in the SDK, named like OLD_DBNAME.
            val databaseAdapter = databaseManager.createOrOpenUnencryptedDatabaseWithoutMigration(OLD_DBNAME)
            val username = getUsernameForOldDatabase(databaseAdapter)
            val serverUrl = getServerUrl(databaseAdapter)
            databaseAdapter.close()
            credentialsStore.remove()

            if (username == null || serverUrl == null) {
                context.deleteDatabase(OLD_DBNAME)
            } else {
                val databaseName = nameGenerator.getDatabaseName(serverUrl, username, false)
                renamer.renameDatabase(OLD_DBNAME, databaseName)
                val newConfiguration = DatabaseConfigurationTransformer.transform(serverUrl, databaseName, username)
                databaseConfigurationStore.set(newConfiguration)
            }
        } else {
            try {
                try {
                    // This is the main flow after versionCode 260
                    val configuration = databaseConfigurationStore.get()
                    existingVersionCode = configuration!!.versionCode()

                    migrateVersionCodeIfNeeded(configuration)
                } catch (e: RuntimeException) {
                    val configuration = tryOldDatabaseConfiguration()
                    databaseConfigurationStore.set(configuration!!)
                }
            } catch (e: RuntimeException) {
                databaseConfigurationStore.remove()
            }
        }

        if (databaseConfigurationStore.get() == null) {
            databaseConfigurationStore.set(DatabasesConfiguration.builder().build())
        }

        if (existingVersionCode == null) {
            Migration260(context, databaseConfigurationStore, databaseManager).apply()
        }
    }

    private fun migrateVersionCodeIfNeeded(configuration: DatabasesConfiguration) {
        if (configuration.versionCode() != BuildConfig.VERSION_CODE) {
            configuration.toBuilder().versionCode(BuildConfig.VERSION_CODE).build()
            databaseConfigurationStore.set(configuration)
        }
    }

    private fun tryOldDatabaseConfiguration(): DatabasesConfiguration? {
        val oldDatabaseConfigurationStore = DatabaseConfigurationInsecureStoreOld.get(insecureStore)

        return oldDatabaseConfigurationStore.get()?.let { config ->
            credentialsStore.setServerUrl(ServerUrlParser.removeTrailingApi(config.loggedServerUrl))

            val users = config.servers.flatMap { serverConf ->
                serverConf.users.map { userConf ->
                    DatabaseAccount.builder().username(userConf.username)
                        .serverUrl(ServerUrlParser.removeTrailingApi(serverConf.serverUrl))
                        .databaseName(userConf.databaseName).databaseCreationDate(userConf.databaseCreationDate)
                        .encrypted(userConf.encrypted).build()
                }
            }
            DatabasesConfiguration.builder().accounts(users).build()
        }
    }

    private suspend fun getUsernameForOldDatabase(databaseAdapter: DatabaseAdapter): String? {
        return try {
            val d2Dao = databaseAdapter.getCurrentDatabase().d2Dao()
            val roomQuery = SimpleSQLiteQuery("SELECT username FROM UserCredentials")
            d2Dao.queryStringValue(roomQuery)
        } catch (e: SQLException) {
            return null
        }
    }

    private suspend fun getServerUrl(databaseAdapter: DatabaseAdapter): String? {
        val store = ConfigurationStoreImpl(databaseAdapter)
        return try {
            store.selectFirst()?.serverUrl()
        } catch (e: SQLException) {
            null
        }
    }

    companion object {
        const val OLD_DBNAME = "dhis.db"
    }
}
