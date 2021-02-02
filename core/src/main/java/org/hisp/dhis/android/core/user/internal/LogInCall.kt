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
package org.hisp.dhis.android.core.user.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.HttpUrl
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.resource.internal.ResourceHandler
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.wipe.internal.WipeModule
import javax.inject.Inject

@Suppress("LongParameterList", "TooManyFunctions")
@Reusable
internal class LogInCall @Inject internal constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val apiCallExecutor: APICallExecutor,
    private val userService: UserService,
    private val credentialsSecureStore: ObjectKeyValueStore<Credentials>,
    private val userHandler: Handler<User>,
    private val resourceHandler: ResourceHandler,
    private val authenticatedUserStore: ObjectWithoutUidStore<AuthenticatedUser>,
    private val systemInfoRepository: ReadOnlyWithDownloadObjectRepository<SystemInfo>,
    private val userStore: IdentifiableObjectStore<User>,
    private val wipeModule: WipeModule,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val generalSettingCall: GeneralSettingCall,
    private val apiCallErrorCatcher: UserAuthenticateCallErrorCatcher
) {
    fun logIn(username: String?, password: String?, serverUrl: String?): Single<User> {
        return Single.fromCallable {
            blockingLogIn(username, password, serverUrl)
        }
    }

    @Throws(D2Error::class)
    @Suppress("ThrowsCount")
    private fun blockingLogIn(username: String?, password: String?, serverUrl: String?): User {
        throwExceptionIfUsernameNull(username)
        throwExceptionIfPasswordNull(password)
        throwExceptionIfAlreadyAuthenticated()
        val parsedServerUrl = ServerUrlParser.parse(serverUrl)
        ServerURLWrapper.setServerUrl(parsedServerUrl.toString())
        val authenticateCall = userService.authenticate(okhttp3.Credentials.basic(username!!, password!!),
            UserFields.allFieldsWithoutOrgUnit)
        return try {
            val authenticatedUser = apiCallExecutor.executeObjectCallWithErrorCatcher(
                authenticateCall,
                apiCallErrorCatcher
            )
            loginOnline(parsedServerUrl, authenticatedUser, username, password)
        } catch (d2Error: D2Error) {
            if (d2Error.isOffline) {
                loginOffline(parsedServerUrl, username, password)
            } else if (d2Error.errorCode() == D2ErrorCode.USER_ACCOUNT_DISABLED) {
                wipeModule.wipeEverything()
                throw d2Error
            } else if (d2Error.errorCode() == D2ErrorCode.UNEXPECTED ||
                d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR
            ) {
                throw noDHIS2Server()
            } else {
                throw d2Error
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loginOnline(serverUrl: HttpUrl, authenticatedUser: User, username: String, password: String): User {
        credentialsSecureStore.set(Credentials.create(username, password))
        loadDatabaseOnline(serverUrl, username).blockingAwait()
        val transaction = databaseAdapter.beginNewTransaction()
        return try {
            val authenticatedUserToStore = buildAuthenticatedUser(
                authenticatedUser.uid(),
                username, password
            )
            authenticatedUserStore.updateOrInsertWhere(authenticatedUserToStore)
            systemInfoRepository.download().blockingAwait()
            handleUser(authenticatedUser)
            transaction.setSuccessful()
            authenticatedUser
        } catch (e: Exception) {
            // Credentials are stored and then removed in case of error since they are required to download system info
            credentialsSecureStore.remove()
            throw e
        } finally {
            transaction.end()
        }
    }

    private fun loadDatabaseOnline(serverUrl: HttpUrl, username: String): Completable {
        return generalSettingCall.isDatabaseEncrypted()
            .doOnSuccess { encrypt: Boolean ->
                multiUserDatabaseManager.loadExistingChangingEncryptionIfRequiredOtherwiseCreateNew(
                    serverUrl.toString(), username, encrypt
                )
            }
            .doOnError {
                multiUserDatabaseManager.loadExistingKeepingEncryptionOtherwiseCreateNew(
                    serverUrl.toString(), username, false
                )
            }
            .ignoreElement()
            .onErrorComplete()
    }

    private fun noUserOfflineError(): D2Error {
        return D2Error.builder()
            .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE)
            .errorDescription("The user hasn't been previously authenticated. Cannot login offline.")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    @Throws(D2Error::class)
    @Suppress("ThrowsCount")
    private fun loginOffline(serverUrl: HttpUrl, username: String, password: String): User {
        val existingDatabase = multiUserDatabaseManager.loadExistingKeepingEncryption(
            serverUrl.toString(),
            username
        )
        if (!existingDatabase) {
            throw noUserOfflineError()
        }
        val existingUser = authenticatedUserStore.selectFirst() ?: throw noUserOfflineError()
        if (UserHelper.md5(username, password) != existingUser.hash()) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.BAD_CREDENTIALS)
                .errorDescription("Credentials do not match authenticated user. Cannot login offline.")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
        val transaction = databaseAdapter.beginNewTransaction()
        try {
            val authenticatedUser = buildAuthenticatedUser(
                existingUser.user()!!,
                username, password
            )
            authenticatedUserStore.updateOrInsertWhere(authenticatedUser)
            credentialsSecureStore.set(Credentials.create(username, password))
            transaction.setSuccessful()
        } finally {
            transaction.end()
        }
        return userStore.selectByUid(existingUser.user()!!)!!
    }

    @Throws(D2Error::class)
    private fun throwExceptionIfUsernameNull(username: String?) {
        if (username == null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.LOGIN_USERNAME_NULL)
                .errorDescription("Username is null")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    @Throws(D2Error::class)
    private fun throwExceptionIfPasswordNull(password: String?) {
        if (password == null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.LOGIN_PASSWORD_NULL)
                .errorDescription("Password is null")
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    @Throws(D2Error::class)
    private fun throwExceptionIfAlreadyAuthenticated() {
        val credentials = credentialsSecureStore.get()
        if (credentials != null) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                .errorDescription("A user is already authenticated: " + credentials.username())
                .errorComponent(D2ErrorComponent.SDK)
                .build()
        }
    }

    private fun noDHIS2Server(): D2Error {
        return D2Error.builder()
            .errorCode(D2ErrorCode.NO_DHIS2_SERVER)
            .errorDescription("The URL is no DHIS2 server")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    private fun handleUser(user: User) {
        userHandler.handle(user)
        resourceHandler.handleResource(Resource.Type.USER)
        resourceHandler.handleResource(Resource.Type.USER_CREDENTIALS)
        resourceHandler.handleResource(Resource.Type.AUTHENTICATED_USER)
    }

    private fun buildAuthenticatedUser(uid: String, username: String, password: String): AuthenticatedUser {
        return AuthenticatedUser.builder()
            .user(uid)
            .hash(UserHelper.md5(username, password))
            .build()
    }
}