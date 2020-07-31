/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.wipe.internal.WipeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static okhttp3.Credentials.basic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.arch.helpers.UserHelper.md5;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class UserAuthenticateCallUnitShould extends BaseCallShould {

    @Mock
    private UserService userService;

    @Mock
    private APICallExecutor apiCallExecutor;

    @Mock
    private Handler<User> userHandler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;

    @Mock
    private ObjectKeyValueStore<Credentials> credentialsSecureStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> authenticateAPICall;

    @Captor
    private ArgumentCaptor<String> credentialsCaptor;

    @Captor
    private ArgumentCaptor<Fields<User>> filterCaptor;

    @Mock
    private User user;

    @Mock
    private User anotherUser;

    @Mock
    private User loggedUser;

    @Mock
    private SystemInfo systemInfoFromAPI;

    @Mock
    private SystemInfo systemInfoFromDb;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Mock
    private Credentials credentials;

    @Mock
    private IdentifiableObjectStore<User> userStore;

    @Mock
    private ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;

    @Mock
    private WipeModule wipeModule;

    @Mock
    private MultiUserDatabaseManager multiUserDatabaseManager;

    @Mock
    private GeneralSettingCall generalSettingCall;

    @Mock
    private UserAuthenticateCallErrorCatcher apiCallErrorCatcher;

    // call we are testing
    private Single<User> logInSingle;

    private static final String USERNAME = "test_username";
    private static final String UID = "test_uid";
    private static final String PASSWORD = "test_password";

    private static final String baseEndpoint = "https://dhis-instance.org";
    private static final String baseEndpointWithAPI = baseEndpoint + "/api/";
    private static final String serverUrl = baseEndpoint;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        when(user.uid()).thenReturn(UID);
        when(loggedUser.uid()).thenReturn(UID);
        when(systemInfoFromAPI.serverDate()).thenReturn(serverDate);

        when(anotherUser.uid()).thenReturn("anotherUserUid");

        when(credentials.username()).thenReturn(USERNAME);
        when(credentials.password()).thenReturn(PASSWORD);

        when(authenticatedUser.user()).thenReturn(UID);
        when(authenticatedUser.hash()).thenReturn(md5(USERNAME, PASSWORD));

        when(systemInfoFromAPI.contextPath()).thenReturn(baseEndpoint);
        when(systemInfoFromDb.contextPath()).thenReturn(baseEndpoint);

        when(userService.authenticate(any(String.class), any(Fields.class))).thenReturn(authenticateAPICall);

        when(systemInfoRepository.download()).thenReturn(Completable.complete());
        whenAPICall().thenReturn(user);

        when(userStore.selectFirst()).thenReturn(loggedUser);
        when(systemInfoRepository.blockingGet()).thenReturn(systemInfoFromDb);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(d2Error.errorCode()).thenReturn(D2ErrorCode.SOCKET_TIMEOUT);
        when(d2Error.isOffline()).thenReturn(true);
        when(generalSettingCall.isDatabaseEncrypted()).thenReturn(Single.just(false));

        logInSingle = instantiateCall(USERNAME, PASSWORD, serverUrl);
    }

    private Single<User> instantiateCall(String username, String password, String serverUrl) {
        return new UserAuthenticateCallFactory(databaseAdapter, apiCallExecutor,
                userService, credentialsSecureStore, userHandler, resourceHandler, authenticatedUserStore,
                systemInfoRepository, userStore, wipeModule, multiUserDatabaseManager, generalSettingCall, apiCallErrorCatcher).logIn(username, password, serverUrl);
    }

    private OngoingStubbing<User> whenAPICall() throws D2Error {
        return when(apiCallExecutor.executeObjectCallWithErrorCatcher(same(authenticateAPICall), any(APICallErrorCatcher.class)));
    }

    @Test
    public void throw_d2_error_for_null_username() {
        TestObserver<User> testObserver = instantiateCall(null, PASSWORD, serverUrl).test();
        assertD2Error(testObserver, D2ErrorCode.LOGIN_USERNAME_NULL);
    }

    @Test
    public void throw_d2_error_for_null_password() {
        TestObserver<User> testObserver = instantiateCall(USERNAME, null, serverUrl).test();
        assertD2Error(testObserver, D2ErrorCode.LOGIN_PASSWORD_NULL);
    }

    @Test
    public void throw_d2_error_for_null_server_url() {
        TestObserver<User> testObserver = instantiateCall(USERNAME, PASSWORD, null).test();
        assertD2Error(testObserver, D2ErrorCode.SERVER_URL_NULL);
    }

    @Test
    public void throw_d2_error_for_wrong_server_url() {
        TestObserver<User> testObserver = instantiateCall(USERNAME, PASSWORD, "this is no URL").test();
        assertD2Error(testObserver, D2ErrorCode.SERVER_URL_MALFORMED);
    }

    private void assertD2Error(TestObserver<User> testObserver, D2ErrorCode code) {
        testObserver.awaitTerminalEvent();
        Throwable error = testObserver.errors().get(0);
        assertThat(error).isInstanceOf(D2Error.class);
        assertThat(((D2Error) error).errorCode()).isEqualTo(code);
        testObserver.dispose();
    }

    @Test
    public void invoke_server_with_correct_parameters_after_call() {
        when(userService.authenticate(
                credentialsCaptor.capture(), filterCaptor.capture())
        ).thenReturn(authenticateAPICall);

        logInSingle.blockingGet();

        assertThat(basic(USERNAME, PASSWORD))
                .isEqualTo(credentialsCaptor.getValue());
    }

    @Test
    public void not_invoke_stores_on_exception_on_call() throws D2Error {
        whenAPICall().thenThrow(d2Error);
        when(d2Error.errorCode()).thenReturn(D2ErrorCode.UNEXPECTED);

        TestObserver<User> testObserver = logInSingle.test();
        testObserver.awaitTerminalEvent();

        assertThat(testObserver.errorCount()).isEqualTo(1);
        testObserver.dispose();

        verifyNoTransactionCompleted();

        // stores must not be invoked
        verify(authenticatedUserStore, never()).updateOrInsertWhere(any(AuthenticatedUser.class));
        verifyNoMoreInteractions(userHandler);
    }

    @Test
    public void succeed_when_no_previous_user_or_system_info() {
        logInSingle.blockingGet();
        verifySuccess();
    }

    @Test
    public void not_wipe_db_when_no_previous_user_or_system_info() throws Exception {
        logInSingle.blockingGet();

        verify(wipeModule, never()).wipeEverything();
        verifySuccess();
    }

    @Test
    public void not_wipe_db_when_previously_user() throws Exception {
        when(userStore.selectFirst()).thenReturn(user);

        logInSingle.blockingGet();

        verify(wipeModule, never()).wipeEverything();
        verifySuccess();
    }

    @Test
    public void wipe_db_when_account_disabled() throws Exception {
        whenAPICall().thenThrow(d2Error);
        when(d2Error.errorCode()).thenReturn(D2ErrorCode.USER_ACCOUNT_DISABLED);
        when(d2Error.isOffline()).thenReturn(false);

        TestObserver<User> testObserver = logInSingle.test();
        testObserver.awaitTerminalEvent();

        assertThat(testObserver.errorCount()).isEqualTo(1);
        testObserver.dispose();

        verify(wipeModule).wipeEverything();
    }

    @Test
    public void throw_d2_error_if_user_already_signed_in() {
        when(credentialsSecureStore.get()).thenReturn(credentials);
        TestObserver<User> testObserver = logInSingle.test();
        assertD2Error(testObserver, D2ErrorCode.ALREADY_AUTHENTICATED);
    }

    @Test
    public void succeed_for_login_online_if_user_has_logged_out() {
        when(credentialsSecureStore.get()).thenReturn(null);
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);
        logInSingle.blockingGet();
        verifySuccess();
    }

    // Offline support

    @Test
    public void succeed_for_login_offline_if_database_exists_and_authenticated_user_too() throws Exception {
        whenAPICall().thenThrow(d2Error);

        when(credentialsSecureStore.get()).thenReturn(null);
        when(multiUserDatabaseManager.loadExistingKeepingEncryption(baseEndpointWithAPI, USERNAME)).thenReturn(true);
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);

        logInSingle.test().awaitTerminalEvent();
        verifySuccessOffline();
    }

    @Test
    public void succeed_for_login_offline_if_server_has_a_trailing_slash() throws Exception {
        whenAPICall().thenThrow(d2Error);
        
        when(credentialsSecureStore.get()).thenReturn(null);
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);
        when(multiUserDatabaseManager.loadExistingKeepingEncryption(baseEndpointWithAPI, USERNAME)).thenReturn(true);

        Single<User> loginCall = instantiateCall(USERNAME, PASSWORD, serverUrl + "/");

        loginCall.test().awaitTerminalEvent();
        verifySuccessOffline();
    }

    @Test
    public void throw_d2_error_if_no_previous_authenticated_user_offline() throws Exception {
        whenAPICall().thenThrow(d2Error);

        when(authenticatedUserStore.selectFirst()).thenReturn(null);

        TestObserver<User> testObserver = logInSingle.test();
        assertD2Error(testObserver, D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE);
    }

    @Test
    public void throw_d2_error_if_logging_offline_with_bad_credentials() throws Exception {
        whenAPICall().thenThrow(d2Error);

        when(credentialsSecureStore.get()).thenReturn(null);
        when(authenticatedUser.hash()).thenReturn("different_hash");
        when(multiUserDatabaseManager.loadExistingKeepingEncryption(baseEndpointWithAPI, USERNAME)).thenReturn(true);
        when(authenticatedUserStore.selectFirst()).thenReturn(authenticatedUser);

        TestObserver<User> testObserver = logInSingle.test();
        assertD2Error(testObserver, D2ErrorCode.BAD_CREDENTIALS);
    }

    private void verifySuccess() {
        AuthenticatedUser authenticatedUserModel =
                AuthenticatedUser.builder()
                .user(UID)
                .hash(md5(USERNAME, PASSWORD))
                .build();
        verifyTransactionComplete();
        verify(authenticatedUserStore).updateOrInsertWhere(authenticatedUserModel);
        verify(userHandler).handle(eq(user));
    }

    private void verifySuccessOffline() {
        AuthenticatedUser authenticatedUserModel =
                AuthenticatedUser.builder()
                        .user(UID)
                        .hash(md5(USERNAME, PASSWORD))
                        .build();
        verifyTransactionComplete();
        verify(authenticatedUserStore).updateOrInsertWhere(authenticatedUserModel);
    }
}
