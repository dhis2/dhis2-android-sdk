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

import android.content.Context
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseExport
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfigurationUtil.buildUserConfiguration
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MultiUserDatabaseManagerUnitShould : BaseCallShould() {

    private val context: Context = mock()
    private val databaseConfigurationSecureStore: ObjectKeyValueStore<DatabasesConfiguration> = mock()
    private val configurationHelper: DatabaseConfigurationHelper = mock()
    private val databaseExport: DatabaseExport = mock()
    private val databaseAdapterFactory: DatabaseAdapterFactory = mock()

    private val username = "username"
    private val serverUrl = "https://dhis2.org"

    private val unencryptedDbName = "un.db"
    private val encryptedDbName = "en.db"

    private val userConfigurationUnencrypted = DatabaseAccount.builder()
        .databaseName(unencryptedDbName)
        .username(username)
        .serverUrl(serverUrl)
        .encrypted(false)
        .databaseCreationDate(DATE)
        .build()

    private val userConfigurationEncrypted = DatabaseAccount.builder()
        .databaseName(encryptedDbName)
        .username(username)
        .serverUrl(serverUrl)
        .encrypted(true)
        .databaseCreationDate(DATE)
        .build()

    private val unencryptedConfiguration = DatabasesConfiguration.builder()
        .accounts(listOf(userConfigurationUnencrypted))
        .build()

    private val encryptedConfiguration = DatabasesConfiguration.builder()
        .accounts(listOf(userConfigurationEncrypted))
        .build()

    private lateinit var manager: MultiUserDatabaseManager

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        manager = MultiUserDatabaseManager(
            context, databaseAdapter, databaseConfigurationSecureStore, configurationHelper,
            databaseAdapterFactory, databaseExport
        )
    }

    @Test
    fun create_new_db_when_no_previous_configuration_on_loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew() {
        val encrypt = false
        whenever(configurationHelper.addAccount(null, serverUrl, username, encrypt))
            .doReturn(unencryptedConfiguration)

        manager.loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(serverUrl, username, encrypt)

        verify(databaseAdapterFactory).createOrOpenDatabase(databaseAdapter, userConfigurationUnencrypted)
    }

    @Test
    fun copy_database_when_changing_encryption_on_loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew() {
        val encrypt = true
        whenever(databaseConfigurationSecureStore.get()).doReturn(unencryptedConfiguration)
        whenever(configurationHelper.addAccount(unencryptedConfiguration, serverUrl, username, encrypt))
            .doReturn(encryptedConfiguration)

        manager.loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(serverUrl, username, encrypt)

        verify(databaseAdapterFactory).createOrOpenDatabase(databaseAdapter, userConfigurationEncrypted)
        verify(databaseExport).encrypt(serverUrl, userConfigurationUnencrypted)
        verify(databaseAdapterFactory).deleteDatabase(userConfigurationUnencrypted)
    }

    @Test
    fun not_create_database_when_non_existing_when_calling_loadExistingKeepingEncryption() {
        manager.loadExistingKeepingEncryption(serverUrl, username)
        verifyNoMoreInteractions(databaseAdapterFactory)
    }

    @Test
    fun open_database_when_existing_when_calling_loadExistingKeepingEncryption() {
        whenever(databaseConfigurationSecureStore.get()).doReturn(unencryptedConfiguration)
        whenever(configurationHelper.addAccount(unencryptedConfiguration, serverUrl, username, false))
            .doReturn(unencryptedConfiguration)

        manager.loadExistingKeepingEncryption(serverUrl, username)

        verify(databaseAdapterFactory).createOrOpenDatabase(databaseAdapter, userConfigurationUnencrypted)
    }

    @Test
    fun remove_exceeding_configuration_on_creating_new_one() {
        val configuration = DatabasesConfiguration.builder()
            .maxAccounts(1)
            .accounts(
                listOf(
                    buildUserConfiguration("user1", "2021-06-01T00:01:04.000"),
                    buildUserConfiguration("user2", "2021-09-02T00:01:04.000"),
                    buildUserConfiguration("user3", "2020-08-05T00:01:04.000"),
                    buildUserConfiguration("user4", "2020-08-09T00:01:04.000")
                )
            )
            .build()

        whenever(databaseConfigurationSecureStore.get()).doReturn(configuration)

        val newUsername = "new_username"
        val newServerUrl = "new_server_url"
        val newConfiguration = buildUserConfiguration(newUsername, "2021-06-01T00:01:04.000", newServerUrl)
        whenever(configurationHelper.addAccount(configuration, newServerUrl, newUsername, false))
            .doReturn(DatabasesConfiguration.builder().accounts(listOf(newConfiguration)).build())

        manager.createNew(newServerUrl, newUsername, false)

        verify(databaseConfigurationSecureStore, times(2)).set(any())
        verify(databaseAdapterFactory, times(4)).deleteDatabase(any())
        verify(databaseAdapterFactory, times(1)).createOrOpenDatabase(any(), any())
    }

    companion object {
        private const val DATE = "2014-06-06T20:44:21.375"
    }
}
