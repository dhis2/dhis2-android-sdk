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
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class LogOutCallShould {
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val userIdStore: UserIdInMemoryStore = mock()
    private val credentials: Credentials = mock()
    private val databaseManager: DatabaseManager = mock()
    private val serverTimezoneManager: ServerTimezoneManager = mock()
    private val databaseConfigurationHelper: DatabaseConfigurationHelper = mock()
    private val databaseConfigurationStore: DatabaseConfigurationInsecureStore = mock()

    private lateinit var logOutCall: LogOutCall

    @Before
    fun setUp() {
        whenever(credentials.username).thenReturn("user")
        whenever(credentials.password).thenReturn("password")
        logOutCall = LogOutCall(
            databaseManager,
            credentialsSecureStore,
            userIdStore,
            serverTimezoneManager,
            databaseConfigurationHelper,
            databaseConfigurationStore,
        )
    }

    @Test
    fun clear_user_credentials() {
        whenever(credentialsSecureStore.get()).thenReturn(credentials)
        whenever(userIdStore.get()).thenReturn("user-id")
        logOutCall.logOut().blockingAwait()
        verify(credentialsSecureStore, times(1)).remove()
        verify(serverTimezoneManager, times(1)).clearCache()
    }

    @Test
    fun clear_server_timezone_cache() {
        whenever(credentialsSecureStore.get()).thenReturn(credentials)
        logOutCall.logOut().blockingAwait()
        verify(serverTimezoneManager, times(1)).clearCache()
    }

    @Test
    fun throw_d2_exception_if_no_authenticated_user() {
        val testObserver = logOutCall.logOut().test()
        testObserver.awaitTerminalEvent()
        val d2Error = testObserver.errors()[0] as D2Error
        assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER)
        testObserver.dispose()
    }
}
