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
package org.hisp.dhis.android.core.user.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccountImport
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccountImportStatus
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoCall
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2StateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectStateSecureStore
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer
import java.util.Date

@RunWith(JUnit4::class)
class LogInCallUnitShould : BaseCallShould() {
    private val userNetworkHandler: UserNetworkHandler = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val userHandler: UserHandler = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val userIdStore: UserIdInMemoryStore = mock()
    private val apiErrorCatcher: UserAuthenticateCallErrorCatcher = mock()

    private val credentialsCaptor: KArgumentCaptor<String> = argumentCaptor()

    private val apiUser: User = mock()
    private val dbUser: User = mock()
    private val systemInfoFromAPI: SystemInfo = mock()
    private val systemInfoFromDb: SystemInfo = mock()
    private val authenticatedUser: AuthenticatedUser = mock()
    private val credentials: Credentials = mock()
    private val userStore: UserStore = mock()
    private val systemInfoCall: SystemInfoCall = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val generalSettingCall: GeneralSettingCall = mock()
    private val accountManager: AccountManagerImpl = mock()
    private val oauth2StateSecureStore: OAuth2StateSecureStore = mock()
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore = mock()
    private val databasesConfigurationStore: DatabaseConfigurationInsecureStore = mock()
    private val openIdAuthState: AuthState = mock()

    @Before
    @Throws(Exception::class)
    override fun setUp() = runTest {
        super.setUp()
        whenever(apiUser.uid()).thenReturn(UID)
        whenever(dbUser.uid()).thenReturn(UID)
        whenever(systemInfoFromAPI.serverDate()).thenReturn(serverDate)
        whenever(credentials.username).thenReturn(USERNAME)
        whenever(credentials.password).thenReturn(PASSWORD)
        whenever(authenticatedUser.user()).thenReturn(UID)
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PASSWORD))
        whenever(systemInfoFromAPI.contextPath()).thenReturn(BASE_URL)
        whenever(systemInfoFromDb.contextPath()).thenReturn(BASE_URL)
        systemInfoCall.stub {
            onBlocking { download(any()) }.doReturn(Unit)
        }
        whenAPICall { apiUser }
        whenever(userStore.selectFirst()).thenReturn(dbUser)
        whenever(userStore.selectByUid(any())).thenReturn(dbUser)
        whenever(d2Error.errorCode()).thenReturn(D2ErrorCode.SOCKET_TIMEOUT)
        whenever(d2Error.isOffline).thenReturn(true)
        generalSettingCall.stub {
            onBlocking { isDatabaseEncrypted() }.doReturn(false)
        }
    }

    private suspend fun login() = instantiateCall(USERNAME, PASSWORD, SERVER_URL)

    private suspend fun instantiateCall(username: String?, password: String?, serverUrl: String?): User {
        return logInCall().logIn(username, password, serverUrl)
    }

    private fun logInCall(): LogInCall {
        return LogInCall(
            coroutineAPICallExecutor, userNetworkHandler, credentialsSecureStore,
            userIdStore, userHandler, authenticatedUserStore, systemInfoCall, userStore,
            LogInDatabaseManager(multiUserDatabaseManager, generalSettingCall),
            LogInExceptions(credentialsSecureStore), accountManager, apiErrorCatcher,
            oauth2StateSecureStore, openIDConnectStateSecureStore,
            databasesConfigurationStore,
        )
    }

    private fun whenAPICall(answer: Answer<User>) {
        userNetworkHandler.stub {
            onBlocking { authenticate(any()) }.doAnswer(answer)
        }
    }

    /** The authorization type is matched loosely; the tests that care about it assert it explicitly. */
    private fun givenExistingDatabase(exists: Boolean = true) = runTest {
        whenever(
            multiUserDatabaseManager.loadExistingKeepingEncryption(eq(SERVER_URL), eq(USERNAME), anyOrNull()),
        ).thenReturn(exists)
    }

    @Test
    fun throw_d2_error_for_null_username() = runTest {
        assertD2Error(D2ErrorCode.LOGIN_USERNAME_NULL) { instantiateCall(null, PASSWORD, SERVER_URL) }
    }

    @Test
    fun throw_d2_error_for_null_password() = runTest {
        assertD2Error(D2ErrorCode.LOGIN_PASSWORD_NULL) { instantiateCall(USERNAME, null, SERVER_URL) }
    }

    @Test
    fun throw_d2_error_for_null_server_url() = runTest {
        assertD2Error(D2ErrorCode.SERVER_URL_NULL) { instantiateCall(USERNAME, PASSWORD, null) }
    }

    @Test
    fun throw_d2_error_for_wrong_server_url() = runTest {
        assertD2Error(D2ErrorCode.SERVER_URL_MALFORMED) { instantiateCall(USERNAME, PASSWORD, "this is no URL") }
    }

    private suspend fun <P> assertD2Error(
        errorCode: D2ErrorCode? = null,
        block: suspend () -> P,
    ) {
        try {
            block.invoke()
            fail("Expected a D2Error${errorCode?.let { " with code $it" }.orEmpty()}, but none was thrown")
        } catch (responseError: D2Error) {
            if (errorCode != null) assertThat(responseError.errorCode()).isEqualTo(errorCode)
        }
    }

    @Test
    fun invoke_server_with_correct_parameters_after_call() = runTest {
        whenever(
            userNetworkHandler.authenticate(
                credentialsCaptor.capture(),
            ),
        ).thenReturn(apiUser)
        login()
        assertThat(okhttp3.Credentials.basic(USERNAME, PASSWORD)).isEqualTo(credentialsCaptor.firstValue)
    }

    @Test
    @Throws(D2Error::class)
    fun not_invoke_stores_on_exception_on_call() = runTest {
        whenAPICall { throw d2Error }
        givenExistingDatabase(exists = false)
        whenever(d2Error.errorCode()).thenReturn(D2ErrorCode.UNEXPECTED)

        assertD2Error { login() }

        // stores must not be invoked
        verify(authenticatedUserStore, never()).updateOrInsertWhere(any())
        verifyNoMoreInteractions(userHandler)
    }

    @Test
    fun succeed_when_no_previous_user_or_system_info() = runTest {
        login()
        verifySuccess()
    }

    @Test
    fun throw_d2_error_if_user_already_signed_in() = runTest {
        whenever(credentialsSecureStore.get()).thenReturn(credentials)
        whenever(userIdStore.get()).thenReturn("userId")
        assertD2Error(D2ErrorCode.ALREADY_AUTHENTICATED) { login() }
    }

    @Test
    fun succeed_for_login_online_if_user_has_logged_out() = runTest {
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        login()
        verifySuccess()
    }

    // Offline support
    @Test
    fun succeed_for_login_offline_if_database_exists_and_authenticated_user_too() = runTest {
        whenAPICall { throw d2Error }
        givenExistingDatabase()
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        login()
        verifySuccessOffline()
    }

    @Test
    fun succeed_for_login_offline_if_server_has_a_trailing_slash() = runTest {
        whenAPICall { throw d2Error }
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        givenExistingDatabase()
        login()
        verifySuccessOffline()
    }

    @Test
    fun throw_original_d2_error_if_no_previous_database_offline() = runTest {
        whenAPICall { throw d2Error }
        givenExistingDatabase(exists = false)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(d2Error.errorCode()) { login() }
    }

    @Test
    fun throw_d2_error_if_no_previous_authenticated_user_offline() = runTest {
        whenAPICall { throw d2Error }
        givenExistingDatabase()
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE) { login() }
    }

    @Test
    fun throw_d2_error_if_logging_offline_with_bad_credentials() = runTest {
        whenAPICall { throw d2Error }
        whenever(authenticatedUser.hash()).thenReturn("different_hash")
        givenExistingDatabase()
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        assertD2Error(D2ErrorCode.BAD_CREDENTIALS) { login() }
    }

    private fun verifySuccess() = runTest {
        val authenticatedUserModel = AuthenticatedUser.builder()
            .user(UID)
            .hash(UserHelper.md5(USERNAME, PASSWORD))
            .build()
        verify(authenticatedUserStore).updateOrInsertWhere(authenticatedUserModel)
        verify(userHandler).handle(eq(apiUser))
    }

    private fun verifySuccessOffline() {
        verify(credentialsSecureStore).set(Credentials(USERNAME, SERVER_URL, PASSWORD, null, null))
        verify(userIdStore).set("test_uid")
    }

    // OAuth2 dispatcher tests

    @Test
    fun log_in_offline_when_the_oauth2_account_has_no_stored_state() = runTest {
        // An imported database, or a state that was wiped: there are no tokens, but the account must
        // still open for offline work.
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get()).thenReturn(oauth2Configuration())
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, PIN, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun keep_the_oauth2_authorization_type_when_the_account_has_no_stored_state() = runTest {
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get()).thenReturn(oauth2Configuration())
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, PIN, SERVER_URL)

        // Without an explicit type these credentials would look like a password session and their
        // requests would go out unauthenticated instead of reporting that authorization is required.
        val captor = argumentCaptor<Credentials>()
        verify(credentialsSecureStore).set(captor.capture())
        assertThat(captor.firstValue.authorizationType).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun reject_a_wrong_offline_code_when_the_oauth2_account_has_no_stored_state() = runTest {
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get()).thenReturn(oauth2Configuration())
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, "correct"))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
    }

    @Test
    fun import_the_database_of_an_oauth2_account_pending_to_import() = runTest {
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get()).thenReturn(oauth2Configuration())
        whenever(multiUserDatabaseManager.getAccount(SERVER_URL, USERNAME))
            .thenReturn(pendingToImportAccount())
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, PIN, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        // The offline code doubles as the password of the exported file.
        verifyBlocking(multiUserDatabaseManager) { importAndLoadDb(any(), eq(PIN)) }
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun report_a_wrong_offline_code_when_the_oauth2_import_fails() = runTest {
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get()).thenReturn(oauth2Configuration())
        whenever(multiUserDatabaseManager.getAccount(SERVER_URL, USERNAME))
            .thenReturn(pendingToImportAccount())
        multiUserDatabaseManager.stub {
            onBlocking { importAndLoadDb(any(), any()) }.doThrow(RuntimeException("wrong zip password"))
        }

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
    }

    @Test
    fun log_in_offline_when_the_openid_account_has_no_stored_state() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(null)
        whenever(databasesConfigurationStore.get())
            .thenReturn(configurationWith(AuthorizationType.OPEN_ID_CONNECT))
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, PIN, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun route_to_the_token_flow_when_a_state_exists_but_the_account_type_is_stale() = runTest {
        // Accounts created before the account type existed have it null and it is never backfilled.
        // The stored state must win, or these users would be pushed to the password flow.
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(databasesConfigurationStore.get()).thenReturn(configurationWith(null))
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, PIN, SERVER_URL)

        val captor = argumentCaptor<Credentials>()
        verify(credentialsSecureStore).set(captor.capture())
        assertThat(captor.firstValue.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun log_in_offline_when_the_stored_oauth2_tokens_are_already_discarded() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN).copy(accessToken = null, refreshToken = null)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, null, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun log_in_offline_without_contacting_server_when_oauth2_state_exists_for_account() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        // password is null — must NOT throw because the OAuth2 path skips the null check
        val user = instantiateCall(USERNAME, null, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun persist_credentials_with_oauth2_state_and_null_password_after_oauth2_login() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        // The PIN is set right after the first login, so it is still null when re-logging in.
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, null, null, state),
        )
    }

    @Test
    fun persist_credentials_with_pin_when_oauth2_login_pin_matches_stored_hash() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, PIN, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, PIN, null, state),
        )
    }

    @Test
    fun reject_oauth2_login_with_offline_code_error_when_pin_does_not_match_stored_hash() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        // Stored hash corresponds to a different PIN.
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, "correct"))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun reject_oauth2_login_with_offline_code_error_when_account_has_no_stored_hash_but_pin_is_given() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase()
        // The account has no PIN configured, so any offline code supplied by the user must be rejected.
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, PIN, SERVER_URL)
        }
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun throw_no_authenticated_user_error_for_oauth2_login_when_no_local_database_exists() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        givenExistingDatabase(exists = false)

        assertD2Error(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE) {
            instantiateCall(USERNAME, null, SERVER_URL)
        }
        verify(credentialsSecureStore, never()).set(any())
    }

    // OpenID Connect dispatcher tests. A second login is local: the tokens can only be renewed by
    // authorizing again in the browser, so it behaves exactly like the OAuth2 one.

    @Test
    fun correct_a_stale_recorded_account_type_when_opening_the_database_offline() = runTest {
        // The account was created before the type existed, so it reads as BASIC. The stored state is
        // what proves it is an OpenID one, and opening the database must carry that down so the
        // record stops lying.
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(databasesConfigurationStore.get()).thenReturn(configurationWith(null))
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, PIN, SERVER_URL)

        verifyBlocking(multiUserDatabaseManager) {
            loadExistingKeepingEncryption(SERVER_URL, USERNAME, AuthorizationType.OPEN_ID_CONNECT)
        }
    }

    @Test
    fun log_in_offline_without_contacting_server_when_openid_state_exists_for_account() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, PIN, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun persist_credentials_with_openid_state_and_pin_after_openid_login() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        givenExistingDatabase()
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, PIN, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(
                USERNAME,
                SERVER_URL,
                null,
                PIN,
                openIdAuthState,
                null,
                AuthorizationType.OPEN_ID_CONNECT,
            ),
        )
    }

    @Test
    fun reject_openid_login_with_offline_code_error_when_pin_does_not_match_stored_hash() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        givenExistingDatabase()
        // Stored hash corresponds to a different PIN.
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, "correct"))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        // The PIN is the offline code for both token-based types, so both report the same error.
        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun import_the_database_of_an_openid_account_pending_to_import() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(multiUserDatabaseManager.getAccount(SERVER_URL, USERNAME))
            .thenReturn(pendingToImportAccount(AuthorizationType.OPEN_ID_CONNECT))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = instantiateCall(USERNAME, PIN, SERVER_URL)

        assertThat(user).isEqualTo(dbUser)
        verifyBlocking(multiUserDatabaseManager) { importAndLoadDb(any(), eq(PIN)) }
        verifyBlocking(userNetworkHandler, never()) { authenticate(any()) }
    }

    @Test
    fun report_a_wrong_offline_code_when_the_openid_import_fails() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(multiUserDatabaseManager.getAccount(SERVER_URL, USERNAME))
            .thenReturn(pendingToImportAccount(AuthorizationType.OPEN_ID_CONNECT))
        multiUserDatabaseManager.stub {
            onBlocking { importAndLoadDb(any(), any()) }.doThrow(RuntimeException("wrong zip password"))
        }

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS_OFFLINE_CODE) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
    }

    // OAuth2 online re-login: the access and refresh tokens expired, the user authorized again and
    // the SDK is handed a brand new state for an account whose database already exists.

    @Test
    fun succeed_on_oauth2_online_relogin_when_the_account_has_a_pin() = runTest {
        whenever(apiUser.username()).thenReturn(USERNAME)
        // The account configured a PIN after the first login, so the stored hash is not null.
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val user = reLogInWithOAuth2Token(oauth2State(accessToken = ACCESS_TOKEN))

        assertThat(user).isEqualTo(apiUser)
        // The session must survive: a failed re-login also wipes the credentials.
        verify(credentialsSecureStore, never()).remove()
    }

    @Test
    fun preserve_the_stored_pin_hash_on_oauth2_online_relogin() = runTest {
        whenever(apiUser.username()).thenReturn(USERNAME)
        val storedHash = UserHelper.md5(USERNAME, PIN)
        whenever(authenticatedUser.hash()).thenReturn(storedHash)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        reLogInWithOAuth2Token(oauth2State(accessToken = ACCESS_TOKEN))

        // The token flow cannot ask for the PIN, so it must not overwrite the hash with null:
        // otherwise the user could no longer log in offline with the PIN they configured.
        verify(authenticatedUserStore).updateOrInsertWhere(
            AuthenticatedUser.builder().user(UID).hash(storedHash).build(),
        )
    }

    @Test
    fun keep_the_pin_in_the_credentials_on_oauth2_online_relogin() = runTest {
        whenever(apiUser.username()).thenReturn(USERNAME)
        val discardedState = oauth2State(accessToken = ACCESS_TOKEN)
            .copy(accessToken = null, refreshToken = null)
        whenever(credentialsSecureStore.get())
            .thenReturn(Credentials(USERNAME, SERVER_URL, null, PIN, null, discardedState))
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PIN))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        val freshState = oauth2State(accessToken = "access-token-2")
        reLogInWithOAuth2Token(freshState)

        verify(credentialsSecureStore).set(Credentials(USERNAME, SERVER_URL, null, PIN, null, freshState))
    }

    private suspend fun reLogInWithOAuth2Token(state: OAuth2State): User =
        logInCall().logInOAuth2(null, SERVER_URL, state)

    private fun oauth2State(accessToken: String): OAuth2State =
        OAuth2State(
            clientId = "client",
            keyId = "key",
            accessToken = accessToken,
            refreshToken = "refresh",
            expiresAt = 1_700_000_000L,
            scope = null,
            tokenEndpoint = "https://dhis-instance.org/oauth/token",
        )

    private fun oauth2Configuration(): DatabasesConfiguration =
        configurationWith(AuthorizationType.OAUTH2)

    private fun configurationWith(authorizationType: AuthorizationType?): DatabasesConfiguration =
        DatabasesConfiguration.builder()
            .accounts(listOf(account(authorizationType)))
            .build()

    private fun account(
        authorizationType: AuthorizationType?,
        importDB: DatabaseAccountImport? = null,
    ): DatabaseAccount =
        DatabaseAccount.builder()
            .username(USERNAME)
            .serverUrl(SERVER_URL)
            .databaseName("$USERNAME.db")
            .encrypted(false)
            .databaseCreationDate(Date())
            .authorizationType(authorizationType)
            .importDB(importDB)
            .build()

    private fun pendingToImportAccount(
        authorizationType: AuthorizationType = AuthorizationType.OAUTH2,
    ): DatabaseAccount =
        account(
            authorizationType,
            DatabaseAccountImport.builder()
                .status(DatabaseAccountImportStatus.PENDING_TO_IMPORT)
                .protectedDbName("$USERNAME-protected.db.zip")
                .build(),
        )

    companion object {
        private const val USERNAME = "test_username"
        private const val UID = "test_uid"
        private const val PASSWORD = "test_password"
        private const val BASE_URL = "https://dhis-instance.org"
        private const val SERVER_URL = BASE_URL
        private const val PIN = "1234"
        private const val ACCESS_TOKEN = "access-token-1"
        private const val ID_TOKEN = "id-token-1"
        private const val FRESH_ID_TOKEN = "fresh-id-token-1"
    }
}
