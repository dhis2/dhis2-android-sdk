/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.user

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.helpers.internal.runBlockingOnIO
import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount
import org.hisp.dhis.android.core.maintenance.D2Error

@Suppress("TooManyFunctions")
interface AccountManager {
    @Deprecated(message = "Use blockingGetAccounts instead", ReplaceWith("blockingGetAccounts()"))
    fun getAccounts(): List<DatabaseAccount> = blockingGetAccounts()

    fun blockingGetAccounts(): List<DatabaseAccount> = runBlockingOnIO { suspendGetAccounts() }

    suspend fun suspendGetAccounts(): List<DatabaseAccount>

    @Deprecated(message = "Use blockingGetCurrentAccount instead", ReplaceWith("blockingGetCurrentAccount()"))
    fun getCurrentAccount(): DatabaseAccount? = blockingGetCurrentAccount()

    fun blockingGetCurrentAccount(): DatabaseAccount? = runBlockingOnIO { suspendGetCurrentAccount() }

    suspend fun suspendGetCurrentAccount(): DatabaseAccount?

    fun setMaxAccounts(maxAccounts: Int?)

    fun getMaxAccounts(): Int?

    @Deprecated(message = "Use blockingDeleteCurrentAccount instead", ReplaceWith("blockingDeleteCurrentAccount()"))
    @Throws(D2Error::class)
    fun deleteCurrentAccount() = blockingDeleteCurrentAccount()

    @Throws(D2Error::class)
    fun blockingDeleteCurrentAccount() = runBlockingOnIO { suspendDeleteCurrentAccount() }

    @Throws(D2Error::class)
    suspend fun suspendDeleteCurrentAccount()

    @Deprecated(message = "Use rxAccountDeletionObservable instead", ReplaceWith("rxAccountDeletionObservable()"))
    fun accountDeletionObservable(): Observable<AccountDeletionReason> = rxAccountDeletionObservable()

    fun rxAccountDeletionObservable(): Observable<AccountDeletionReason> = flowAccountDeletion().asObservable()

    fun flowAccountDeletion(): Flow<AccountDeletionReason>
}
