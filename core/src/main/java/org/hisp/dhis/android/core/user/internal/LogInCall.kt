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
package org.hisp.dhis.android.core.user.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import net.openid.appauth.AuthState
import okhttp3.HttpUrl
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.wipe.internal.WipeModule

@Reusable
@Suppress("LongParameterList")
internal class LogInCall @Inject internal constructor(
    private val databaseAdapter: DatabaseAdapter,
    private val apiCallExecutor: APICallExecutor,
    private val userService: UserService,
    private val credentialsSecureStore: ObjectKeyValueStore<Credentials>,
    private val userIdStore: UserIdInMemoryStore,
    private val userHandler: Handler<User>,
    private val authenticatedUserStore: ObjectWithoutUidStore<AuthenticatedUser>,
    private val systemInfoRepository: ReadOnlyWithDownloadObjectRepository<SystemInfo>,
    private val userStore: IdentifiableObjectStore<User>,
    private val wipeModule: WipeModule,
    private val apiCallErrorCatcher: UserAuthenticateCallErrorCatcher,
    private val databaseManager: LogInDatabaseManager,
    private val exceptions: LogInExceptions
) {
    fun logIn(username: String?, password: String?, serverUrl: String?): Single<User> {
        return Single.fromCallable {
            blockingLogIn(username, password, serverUrl)
        }
    }

    @Throws(D2Error::class)
    private fun blockingLogIn(username: String?, password: String?, serverUrl: String?): User {
        exceptions.throwExceptionIfUsernameNull(username)
        exceptions.throwExceptionIfPasswordNull(password)
        exceptions.throwExceptionIfAlreadyAuthenticated()

        val parsedServerUrl = ServerUrlParser.parse(serverUrl)
        ServerURLWrapper.setServerUrl(parsedServerUrl.toString())

        val authenticateCall = userService.authenticate(
            okhttp3.Credentials.basic(username!!, password!!),
            UserFields.allFieldsWithoutOrgUnit
        )

        val credentials = Credentials(username, password, null)

        return try {
            val user = apiCallExecutor.executeObjectCallWithErrorCatcher(authenticateCall, apiCallErrorCatcher)
            loginOnline(parsedServerUrl, user, credentials)
        } catch (d2Error: D2Error) {
            if (d2Error.isOffline) {
                tryLoginOffline(parsedServerUrl, credentials, d2Error)
            } else {
                throw handleOnlineException(d2Error)
            }
        }
    }

    private fun handleOnlineException(d2Error: D2Error): D2Error {
        return if (d2Error.errorCode() == D2ErrorCode.USER_ACCOUNT_DISABLED) {
            wipeModule.wipeEverything()
            d2Error
        } else if (d2Error.errorCode() == D2ErrorCode.UNEXPECTED ||
            d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR
        ) {
            exceptions.noDHIS2Server()
        } else {
            d2Error
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loginOnline(serverUrl: HttpUrl, user: User, credentials: Credentials): User {
        credentialsSecureStore.set(credentials)
        userIdStore.set(user.uid())
        databaseManager.loadDatabaseOnline(serverUrl, credentials.username).blockingAwait()
        val transaction = databaseAdapter.beginNewTransaction()
        return try {
            val authenticatedUser = AuthenticatedUser.builder()
                .user(user.uid())
                .hash(credentials.getHash())
                .build()

            authenticatedUserStore.updateOrInsertWhere(authenticatedUser)
            systemInfoRepository.download().blockingAwait()
            userHandler.handle(user)
            transaction.setSuccessful()
            user
        } catch (e: Exception) {
            // Credentials are stored and then removed in case of error since they are required to download system info
            credentialsSecureStore.remove()
            userIdStore.remove()
            throw e
        } finally {
            transaction.end()
        }
    }

    @Throws(D2Error::class)
    @Suppress("ThrowsCount")
    private fun tryLoginOffline(serverUrl: HttpUrl, credentials: Credentials, originalError: D2Error): User {
        val existingDatabase = databaseManager.loadExistingKeepingEncryption(serverUrl, credentials.username)
        if (!existingDatabase) {
            throw originalError
        }
        val existingUser = authenticatedUserStore.selectFirst() ?: throw exceptions.noUserOfflineError()

        if (credentials.getHash() != existingUser.hash()) {
            throw exceptions.badCredentialsError()
        }
        credentialsSecureStore.set(credentials)
        userIdStore.set(existingUser.user()!!)
        return userStore.selectByUid(existingUser.user()!!)!!
    }

    @Throws(D2Error::class)
    fun blockingLogInOpenIDConnect(serverUrl: String, openIDConnectState: AuthState): User {
        val parsedServerUrl = ServerUrlParser.parse(serverUrl)
        ServerURLWrapper.setServerUrl(parsedServerUrl.toString())

        val authenticateCall = userService.authenticate(
            "Bearer ${openIDConnectState.idToken}",
            UserFields.allFieldsWithoutOrgUnit
        )

        return try {
            val user = apiCallExecutor.executeObjectCallWithErrorCatcher(authenticateCall, apiCallErrorCatcher)
            val credentials = getOpenIdConnectCredentials(user, openIDConnectState)
            loginOnline(parsedServerUrl, user, credentials)
        } catch (d2Error: D2Error) {
            throw handleOnlineException(d2Error)
        }
    }

    private fun getOpenIdConnectCredentials(user: User, openIDConnectState: AuthState): Credentials {
        val username = UserInternalAccessor.accessUserCredentials(user).username()!!
        return Credentials(username, null, openIDConnectState)
    }
}
