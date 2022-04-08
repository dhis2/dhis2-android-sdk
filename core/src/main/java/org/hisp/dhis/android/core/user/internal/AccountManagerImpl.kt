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

import android.content.Context
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.AccountDeletionReason
import org.hisp.dhis.android.core.user.AccountManager

@Reusable
internal class AccountManagerImpl @Inject constructor(
    private val databasesConfigurationStore: ObjectKeyValueStore<DatabasesConfiguration>,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val databaseAdapterFactory: DatabaseAdapterFactory,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val logOutCall: LogOutCall,
    private val context: Context
) : AccountManager {
    private val accountDeletionSubject = PublishSubject.create<AccountDeletionReason>()

    override fun getAccounts(): List<DatabaseAccount> {
        return databasesConfigurationStore.get()?.accounts() ?: emptyList()
    }

    override fun setMaxAccounts(maxAccounts: Int) {
        multiUserDatabaseManager.setMaxAccounts(maxAccounts)
    }

    override fun getMaxAccounts(): Int {
        return databasesConfigurationStore.get()?.maxAccounts() ?: MultiUserDatabaseManager.DefaultMaxAccounts
    }

    @Throws(D2Error::class)
    override fun deleteCurrentAccount() {
        val credentials = credentialsSecureStore.get()

        if (credentials == null) {
            throwNotAnyAuthenticatedUser()
        } else {
            deleteAccount(credentials)
        }
    }

    @Throws(D2Error::class)
    internal fun deleteCurrentAccountAndEmit(deletionReason: AccountDeletionReason) {
        val credentials = credentialsSecureStore.get()

        if (credentials == null) {
            throwNotAnyAuthenticatedUser()
        } else {
            deleteAccountAndEmit(credentials, deletionReason)
        }
    }

    @Throws(D2Error::class)
    private fun throwNotAnyAuthenticatedUser() {
        throw D2Error.builder()
            .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER)
            .errorDescription("There is not any authenticated user")
            .errorComponent(D2ErrorComponent.SDK)
            .build()
    }

    @Throws(D2Error::class)
    fun deleteAccount(credentials: Credentials) {
        deleteAccountInternal(credentials, AccountDeletionReason.APPLICATION_REQUEST)
    }

    internal fun deleteAccountAndEmit(credentials: Credentials, deletionReason: AccountDeletionReason) {
        deleteAccountInternal(credentials, deletionReason)
    }

    @Throws(D2Error::class)
    private fun deleteAccountInternal(credentials: Credentials, deletionReason: AccountDeletionReason) {
        accountDeletionSubject.onNext(deletionReason)
        logOutCall.logOut().blockingAwait()
        val configuration = databasesConfigurationStore.get()
        val loggedAccount = DatabaseConfigurationHelper.getLoggedAccount(
            configuration,
            credentials.username,
            credentials.serverUrl
        )
        val updatedConfiguration = DatabaseConfigurationHelper.removeAccount(configuration, listOf(loggedAccount))
        databasesConfigurationStore.set(updatedConfiguration)

        FileResourceDirectoryHelper.deleteFileResourceDirectory(context, loggedAccount)
        databaseAdapterFactory.deleteDatabase(loggedAccount)
    }

    override fun accountDeletionObservable(): Observable<AccountDeletionReason> {
        return accountDeletionSubject
    }
}
