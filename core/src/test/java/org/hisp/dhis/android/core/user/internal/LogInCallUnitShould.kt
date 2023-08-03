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
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoCall
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*
import org.mockito.stubbing.OngoingStubbing
import retrofit2.Call
import retrofit2.HttpException


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class LogInCallUnitShould : BaseCallShould() {
    private val userService: UserService = mock()
    private val apiCallExecutor: APICallExecutor = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val userHandler: UserHandler = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val userIdStore: UserIdInMemoryStore = mock()
    private val authenticateAPICall: User = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val credentialsCaptor: KArgumentCaptor<String> = argumentCaptor()
    private val filterCaptor: KArgumentCaptor<Fields<User>> = argumentCaptor()

    private val user: User = mock()
    private val anotherUser: User = mock()
    private val loggedUser: User = mock()
    private val systemInfoFromAPI: SystemInfo = mock()
    private val systemInfoFromDb: SystemInfo = mock()
    private val authenticatedUser: AuthenticatedUser = mock()
    private val credentials: Credentials = mock()
    private val userStore: UserStore = mock()
    private val systemInfoCall: SystemInfoCall = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val generalSettingCall: GeneralSettingCall = mock()
    private val accountManager: AccountManagerImpl = mock()
    private val versionManager: DHISVersionManager = mock()

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        whenever(user.uid()).thenReturn(UID)
        whenever(loggedUser.uid()).thenReturn(UID)
        whenever(systemInfoFromAPI.serverDate()).thenReturn(serverDate)
        whenever(anotherUser.uid()).thenReturn("anotherUserUid")
        whenever(credentials.username).thenReturn(USERNAME)
        whenever(credentials.password).thenReturn(PASSWORD)
        whenever(authenticatedUser.user()).thenReturn(UID)
        whenever(authenticatedUser.hash()).thenReturn(UserHelper.md5(USERNAME, PASSWORD))
        whenever(systemInfoFromAPI.contextPath()).thenReturn(baseEndpoint)
        whenever(systemInfoFromDb.contextPath()).thenReturn(baseEndpoint)
        userService.stub {
            onBlocking { authenticate(any(), any()) }.doReturn(authenticateAPICall)
        }
        systemInfoCall.stub {
            onBlocking { download(any()) }.doReturn(Unit)
        }
        whenAPICall().thenReturn(user)
        whenever(userStore.selectFirst()).thenReturn(loggedUser)
        whenever(databaseAdapter.beginNewTransaction()).thenReturn(transaction)
        whenever(d2Error.errorCode()).thenReturn(D2ErrorCode.SOCKET_TIMEOUT)
        whenever(d2Error.isOffline).thenReturn(true)
        whenever(generalSettingCall.isDatabaseEncrypted()).thenReturn(Single.just(false))
        whenever(versionManager.getVersion()).thenReturn(DHISVersion.V2_39)

    }

    private suspend fun login() = instantiateCall(USERNAME, PASSWORD, serverUrl)

    private suspend fun instantiateCall(username: String?, password: String?, serverUrl: String?): User {
        return LogInCall(
            coroutineAPICallExecutor, userService, credentialsSecureStore,
            userIdStore, userHandler, authenticatedUserStore, systemInfoCall, userStore,
            LogInDatabaseManager(multiUserDatabaseManager, generalSettingCall),
            LogInExceptions(credentialsSecureStore), accountManager, versionManager
        ).logIn(username, password, serverUrl)

    }

    private fun whenAPICall(): OngoingStubbing<User?> = runBlocking{
        return@runBlocking whenever(coroutineAPICallExecutor.wrap { same(authenticateAPICall) }.getOrThrow())
    }

    @Test
    fun throw_d2_error_for_null_username() = runTest {
        assertD2Error(D2ErrorCode.LOGIN_USERNAME_NULL) { instantiateCall(null, PASSWORD, serverUrl) }
    }

    @Test
    fun throw_d2_error_for_null_password() = runTest {
        assertD2Error(D2ErrorCode.LOGIN_PASSWORD_NULL) { instantiateCall(USERNAME, null, serverUrl) }
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
        errorCode: D2ErrorCode,
        block: suspend () -> P
    ) {
        try {
            block.invoke()
        } catch (responseError: D2Error) {
            assertThat(responseError).isInstanceOf(D2Error::class.java)
            assertThat(responseError.errorCode()).isEqualTo(errorCode)
        }
    }

    @Test
    fun invoke_server_with_correct_parameters_after_call() = runTest {
        whenever(
            userService.authenticate(
                credentialsCaptor.capture(), filterCaptor.capture()
            )
        ).thenReturn(authenticateAPICall)
        login()
        assertThat(okhttp3.Credentials.basic(USERNAME, PASSWORD)).isEqualTo(credentialsCaptor.firstValue)
    }

    @Test
    @Throws(D2Error::class)
    fun not_invoke_stores_on_exception_on_call() = runTest {
        whenAPICall().thenThrow(d2Error)
        whenever(d2Error.errorCode()).thenReturn(D2ErrorCode.UNEXPECTED)

        assertThat(login()).isInstanceOf(D2Error::class.java)

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
    fun succeed_for_login_offline_if_database_exists_and_authenticated_user_too() {
        whenAPICall().thenThrow(d2Error)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(serverUrl, USERNAME)).thenReturn(true)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        runBlocking { login() }
        verifySuccessOffline()
    }

    @Test
    fun succeed_for_login_offline_if_server_has_a_trailing_slash() {
        whenAPICall().thenThrow(d2Error)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(serverUrl, USERNAME)).thenReturn(true)
        runBlocking { login() }
        verifySuccessOffline()
    }

    @Test
    fun throw_original_d2_error_if_no_previous_database_offline() = runTest {
        whenAPICall().thenThrow(d2Error)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(d2Error.errorCode()) { login() }
    }

    @Test
    fun throw_d2_error_if_no_previous_authenticated_user_offline() = runTest {
        whenAPICall().thenThrow(d2Error)
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(serverUrl, USERNAME)).thenReturn(true)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(null)
        assertD2Error(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE) { login() }
    }

    @Test
    fun throw_d2_error_if_logging_offline_with_bad_credentials() = runTest {
        whenAPICall().thenThrow(d2Error)
        whenever(authenticatedUser.hash()).thenReturn("different_hash")
        whenever(multiUserDatabaseManager.loadExistingKeepingEncryption(serverUrl, USERNAME)).thenReturn(true)
        whenever(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser)
        assertD2Error(D2ErrorCode.BAD_CREDENTIALS) { login() }
    }

    private fun verifySuccess() {
        val authenticatedUserModel = AuthenticatedUser.builder()
            .user(UID)
            .hash(UserHelper.md5(USERNAME, PASSWORD))
            .build()
        verify(authenticatedUserStore).updateOrInsertWhere(authenticatedUserModel)
        verify(userHandler).handle(eq(user))
    }

    private fun verifySuccessOffline() {
        verify(credentialsSecureStore).set(Credentials(USERNAME, serverUrl, PASSWORD, null))
        verify(userIdStore).set("test_uid")
    }

    companion object {
        private const val USERNAME = "test_username"
        private const val UID = "test_uid"
        private const val PASSWORD = "test_password"
        private const val baseEndpoint = "https://dhis-instance.org"
        private const val serverUrl = baseEndpoint
    }
}
