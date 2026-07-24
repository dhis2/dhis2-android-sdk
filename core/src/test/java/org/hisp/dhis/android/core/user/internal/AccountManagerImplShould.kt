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
package org.hisp.dhis.android.core.user.internal

import android.content.Context
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2StateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectStateSecureStore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File
import java.nio.file.Files

@RunWith(JUnit4::class)
class AccountManagerImplShould {

    private val databasesConfigurationStore: DatabaseConfigurationInsecureStore = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val databaseManager: DatabaseManager = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val logOutCall: LogOutCall = mock()
    private val context: Context = mock()
    private val databaseConfigurationHelper: DatabaseConfigurationHelper = mock()
    private val oauth2StateSecureStore: OAuth2StateSecureStore = mock()
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore = mock()

    private lateinit var tempDir: File
    private lateinit var accountManager: AccountManagerImpl

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("account-manager-test-").toFile()
        whenever(context.filesDir).thenReturn(tempDir)
        whenever(context.cacheDir).thenReturn(tempDir)

        accountManager = AccountManagerImpl(
            databasesConfigurationStore,
            multiUserDatabaseManager,
            databaseManager,
            credentialsSecureStore,
            logOutCall,
            context,
            databaseConfigurationHelper,
            oauth2StateSecureStore,
            openIDConnectStateSecureStore,
        )
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun deleteCurrentAccount_throws_no_authenticated_user_when_no_credentials() {
        whenever(credentialsSecureStore.get()).thenReturn(null)

        val error = runCatching { accountManager.deleteCurrentAccount() }.exceptionOrNull()

        assertThat(error).isInstanceOf(D2Error::class.java)
        assertThat((error as D2Error).errorCode()).isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER)
        verify(oauth2StateSecureStore, never()).remove(any(), any())
    }

    @Test
    fun deleteCurrentAccount_removes_oauth2_state_after_deleting_database() {
        val account = mockDatabaseAccount(serverUrl = SERVER_URL, username = USERNAME, dbName = DB_NAME)
        seedConfiguration(listOf(account))
        whenever(credentialsSecureStore.get()).thenReturn(activeCredentials())

        accountManager.deleteCurrentAccount()

        verify(databaseManager).deleteDatabase(DB_NAME, false)
        verify(oauth2StateSecureStore).remove(SERVER_URL, USERNAME)
        verify(openIDConnectStateSecureStore).remove(SERVER_URL, USERNAME)
    }

    @Test
    fun deleteCurrentAccount_does_not_touch_oauth2_state_when_no_configuration() {
        whenever(credentialsSecureStore.get()).thenReturn(activeCredentials())
        whenever(databasesConfigurationStore.get()).thenReturn(null)

        accountManager.deleteCurrentAccount()

        verify(oauth2StateSecureStore, never()).remove(any(), any())
        verify(databaseManager, never()).deleteDatabase(any(), eq(false))
    }

    private fun activeCredentials(): Credentials =
        Credentials(USERNAME, SERVER_URL, "pwd", null)

    private fun mockDatabaseAccount(
        serverUrl: String,
        username: String,
        dbName: String,
        authorizationType: AuthorizationType? = AuthorizationType.BASIC,
    ): DatabaseAccount =
        mock {
            on { serverUrl() } doReturn serverUrl
            on { username() } doReturn username
            on { databaseName() } doReturn dbName
            on { encrypted() } doReturn false
            on { authorizationType() } doReturn authorizationType
        }

    private fun seedConfiguration(accounts: List<DatabaseAccount>) {
        val updatedConfiguration: DatabasesConfiguration = mock()
        val builder: DatabasesConfiguration.Builder = mock()
        whenever(builder.accounts(any())).thenReturn(builder)
        whenever(builder.build()).thenReturn(updatedConfiguration)

        val configuration: DatabasesConfiguration = mock()
        whenever(configuration.accounts()).thenReturn(accounts)
        whenever(configuration.toBuilder()).thenReturn(builder)

        whenever(databasesConfigurationStore.get()).thenReturn(configuration)
    }

    companion object {
        private const val USERNAME = "test_username"
        private const val SERVER_URL = "https://dhis-instance.org"
        private const val DB_NAME = "test_db.db"
    }
}
