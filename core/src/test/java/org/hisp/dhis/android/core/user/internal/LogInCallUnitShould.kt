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
import org.hisp.dhis.android.core.common.BaseCallShould
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
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer

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
    private val openIDConnectTokenRefresher: OpenIDConnectTokenRefresher = mock()
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
        return LogInCall(
            coroutineAPICallExecutor, userNetworkHandler, credentialsSecureStore,
            userIdStore, userHandler, authenticatedUserStore, systemInfoCall, userStore,
            LogInDatabaseManager(multiUserDatabaseManager, generalSettingCall),
            LogInExceptions(credentialsSecureStore), accountManager, apiErrorCatcher,
            oauth2StateSecureStore, openIDConnectStateSecureStore, lazyOf(openIDConnectTokenRefresher),
        ).logIn(username, password, serverUrl)
    }

    private fun whenAPICall(answer: Answer<User>) {
        userNetworkHandler.stub {
            onBlocking { authenticate(any()) }.doAnswer(answer)
        }
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
        } catch (responseError: D2Error) {
            assertThat(responseError).isInstanceOf(D2Error::class.java)
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
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(false)
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
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(true)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        login()
        verifySuccessOffline()
    }

    @Test
    fun succeed_for_login_offline_if_server_has_a_trailing_slash() = runTest {
        whenAPICall { throw d2Error }
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(true)
        login()
        verifySuccessOffline()
    }

    @Test
    fun throw_original_d2_error_if_no_previous_database_offline() = runTest {
        whenAPICall { throw d2Error }
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(false)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(d2Error.errorCode()) { login() }
    }

    @Test
    fun throw_d2_error_if_no_previous_authenticated_user_offline() = runTest {
        whenAPICall { throw d2Error }
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(true)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE) { login() }
    }

    @Test
    fun throw_d2_error_if_logging_offline_with_bad_credentials() = runTest {
        whenAPICall { throw d2Error }
        whenever(authenticatedUser.hash()).thenReturn("different_hash")
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME)).thenReturn(true)
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
        verify(credentialsSecureStore).set(Credentials(USERNAME, SERVER_URL, PASSWORD, null))
        verify(userIdStore).set("test_uid")
    }

    // OAuth2 dispatcher tests

    @Test
    fun route_to_oauth2_path_with_bearer_when_state_exists_for_account() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        whenever(
            userNetworkHandler.authenticate(credentialsCaptor.capture()),
        ).thenReturn(apiUser)

        // password is null — must NOT throw because the OAuth2 path skips the null check
        instantiateCall(USERNAME, null, SERVER_URL)

        assertThat(credentialsCaptor.firstValue).isEqualTo("Bearer $ACCESS_TOKEN")
    }

    @Test
    fun persist_credentials_with_oauth2_state_and_null_password_after_oauth2_login() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, null, state),
        )
    }

    @Test
    fun reject_oauth2_login_when_pin_does_not_match_stored_hash() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        // Stored hash corresponds to a different PIN.
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, "correct"))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
    }

    @Test
    fun fall_back_to_offline_login_for_oauth2_when_authenticate_throws_offline() = runTest {
        val state = oauth2State(accessToken = ACCESS_TOKEN)
        whenever(oauth2StateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(state)
        whenAPICall { throw d2Error } // d2Error.isOffline = true (set in setUp)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME))
            .thenReturn(true)
        // OAuth2 accounts have hash() == null because password is null on both sides.
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, null, state),
        )
    }

    // OpenID Connect dispatcher tests

    @Test
    fun route_to_openid_path_with_refreshed_bearer_when_state_exists_for_account() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(openIDConnectTokenRefresher.blockingGetFreshTokenOrNull(openIdAuthState))
            .thenReturn(FRESH_ID_TOKEN)
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        whenever(
            userNetworkHandler.authenticate(credentialsCaptor.capture()),
        ).thenReturn(apiUser)

        // password is null — must NOT throw because the OpenID path skips the null check
        instantiateCall(USERNAME, null, SERVER_URL)

        verify(openIDConnectTokenRefresher).blockingGetFreshTokenOrNull(openIdAuthState)
        assertThat(credentialsCaptor.firstValue).isEqualTo("Bearer $FRESH_ID_TOKEN")
    }

    @Test
    fun fall_back_to_stored_id_token_when_refresh_returns_null() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(openIDConnectTokenRefresher.blockingGetFreshTokenOrNull(openIdAuthState)).thenReturn(null)
        whenever(openIdAuthState.idToken).thenReturn(ID_TOKEN)
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        whenever(
            userNetworkHandler.authenticate(credentialsCaptor.capture()),
        ).thenReturn(apiUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        assertThat(credentialsCaptor.firstValue).isEqualTo("Bearer $ID_TOKEN")
    }

    @Test
    fun persist_credentials_with_openid_state_and_null_password_after_openid_login() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(openIDConnectTokenRefresher.blockingGetFreshTokenOrNull(openIdAuthState))
            .thenReturn(FRESH_ID_TOKEN)
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, openIdAuthState, null),
        )
        verify(openIDConnectStateSecureStore).set(SERVER_URL, USERNAME, openIdAuthState)
    }

    @Test
    fun reject_openid_login_when_pin_does_not_match_stored_hash() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        whenever(openIDConnectTokenRefresher.blockingGetFreshTokenOrNull(openIdAuthState))
            .thenReturn(FRESH_ID_TOKEN)
        // Stored hash corresponds to a different PIN.
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, "correct"))
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        assertD2Error(D2ErrorCode.BAD_CREDENTIALS) {
            instantiateCall(USERNAME, "wrong-pin", SERVER_URL)
        }
    }

    @Test
    fun fall_back_to_offline_login_for_openid_when_authenticate_throws_offline() = runTest {
        whenever(openIDConnectStateSecureStore.get(SERVER_URL, USERNAME)).thenReturn(openIdAuthState)
        // Offline: refresh returns null and we fall back to the stored idToken.
        whenever(openIDConnectTokenRefresher.blockingGetFreshTokenOrNull(openIdAuthState)).thenReturn(null)
        whenever(openIdAuthState.idToken).thenReturn(ID_TOKEN)
        whenAPICall { throw d2Error } // d2Error.isOffline = true (set in setUp)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(SERVER_URL, USERNAME))
            .thenReturn(true)
        // OpenID accounts without PIN have hash() == null because password is null on both sides.
        whenever(authenticatedUser.hash()).thenReturn(null)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)

        instantiateCall(USERNAME, null, SERVER_URL)

        verify(credentialsSecureStore).set(
            Credentials(USERNAME, SERVER_URL, null, openIdAuthState, null),
        )
    }

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

    companion object {
        private const val USERNAME = "test_username"
        private const val UID = "test_uid"
        private const val PASSWORD = "test_password"
        private const val BASE_URL = "https://dhis-instance.org"
        private const val SERVER_URL = BASE_URL
        private const val ACCESS_TOKEN = "access-token-1"
        private const val ID_TOKEN = "id-token-1"
        private const val FRESH_ID_TOKEN = "fresh-id-token-1"
    }
}
