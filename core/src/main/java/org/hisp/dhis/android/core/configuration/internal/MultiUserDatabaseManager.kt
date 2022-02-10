/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import android.util.Log
import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository

@Reusable
internal class MultiUserDatabaseManager @Inject internal constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val databaseConfigurationSecureStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val configurationHelper: DatabaseConfigurationHelper,
    private val databaseAdapterFactory: DatabaseAdapterFactory,
    private val databaseExport: DatabaseExport,
    private val localDbRepository: LocalDbRepository
) {
    fun loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
        serverUrl: String,
        username: String,
        encrypt: Boolean
    ) {
        val existing = loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { encrypt },
            true
        )

        if (!existing) {
            createNew(serverUrl, username, encrypt)
        }
    }

    fun loadExistingKeepingEncryptionOtherwiseCreateNew(serverUrl: String, username: String, encrypt: Boolean) {
        val existing = loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseUserConfiguration -> obj.encrypted() },
            true
        )

        if (!existing) {
            createNew(serverUrl, username, encrypt)
        }
    }

    fun createNew(serverUrl: String, username: String, encrypt: Boolean) {
        val configuration = databaseConfigurationSecureStore.get()
        val pairsCount = configurationHelper.countServerUserPairs(configuration)
        if (pairsCount == maxServerUserPairs) {
            configurationHelper.getOldestServerUser(configuration)?.let { oldestConfig ->
                val updatedConfigurations =
                    configurationHelper.removeServerUserConfiguration(configuration, oldestConfig)
                databaseConfigurationSecureStore.set(updatedConfigurations)
                databaseAdapterFactory.deleteDatabase(oldestConfig)
            }
        }
        val userConfiguration = addNewConfigurationInternal(serverUrl, username, encrypt)
        localDbRepository.blockingClear()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration)
    }

    fun changeEncryptionIfRequired(credentials: Credentials, encrypt: Boolean) {
        loadExistingChangingEncryptionIfRequired(
            credentials.serverUrl,
            credentials.username,
            { encrypt },
            false
        )
    }

    fun loadExistingKeepingEncryption(serverUrl: String, username: String): Boolean {
        return loadExistingChangingEncryptionIfRequired(
            serverUrl,
            username,
            { obj: DatabaseUserConfiguration -> obj.encrypted() },
            true
        )
    }

    private fun loadExistingChangingEncryptionIfRequired(
        serverUrl: String,
        username: String,
        encryptionExtractor: (config: DatabaseUserConfiguration) -> Boolean,
        alsoOpenWhenEncryptionDoesntChange: Boolean
    ): Boolean {
        val existingUserConfiguration = getUserConfiguration(serverUrl, username) ?: return false
        val encrypt = encryptionExtractor(existingUserConfiguration)
        changeEncryptionIfRequired(serverUrl, existingUserConfiguration, encrypt)
        if (encrypt != existingUserConfiguration.encrypted() || alsoOpenWhenEncryptionDoesntChange) {
            val updatedUserConfiguration = addNewConfigurationInternal(
                serverUrl,
                username,
                encrypt
            )
            databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, updatedUserConfiguration)
        }
        return true
    }

    private fun changeEncryptionIfRequired(
        serverUrl: String,
        existingUserConfiguration: DatabaseUserConfiguration,
        encrypt: Boolean
    ) {
        if (encrypt != existingUserConfiguration.encrypted()) {
            Log.w(
                MultiUserDatabaseManager::class.java.name,
                "Encryption value changed for " + existingUserConfiguration.username() + ": " + encrypt
            )
            if (encrypt && !existingUserConfiguration.encrypted()) {
                databaseExport.encrypt(serverUrl, existingUserConfiguration)
            } else if (!encrypt && existingUserConfiguration.encrypted()) {
                databaseExport.decrypt(serverUrl, existingUserConfiguration)
            }
            databaseAdapterFactory.deleteDatabase(existingUserConfiguration)
        }
    }

    private fun getUserConfiguration(serverUrl: String, username: String): DatabaseUserConfiguration? {
        val configuration = databaseConfigurationSecureStore.get()
        return configurationHelper.getUserConfiguration(configuration, serverUrl, username)
    }

    private fun addNewConfigurationInternal(
        serverUrl: String,
        username: String,
        encrypt: Boolean
    ): DatabaseUserConfiguration {
        val updatedConfiguration = configurationHelper.setConfiguration(
            databaseConfigurationSecureStore.get(), serverUrl, username, encrypt
        )
        databaseConfigurationSecureStore.set(updatedConfiguration)
        return configurationHelper.getLoggedUserConfiguration(updatedConfiguration, username, serverUrl)
    }

    companion object {
        var maxServerUserPairs = 1
    }
}
