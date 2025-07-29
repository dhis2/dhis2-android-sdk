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
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class IsUserLoggedInCallableShould {
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()
    private val credentials: Credentials = mock()

    private lateinit var isUserLoggedInSingle: Single<Boolean>

    @Before
    fun setUp() {
        whenever(credentials.username).doReturn("user")
        whenever(credentials.password).doReturn("password")
        isUserLoggedInSingle = IsUserLoggedInCallableFactory(credentialsSecureStore, authenticatedUserStore).isLogged
    }

    @Test
    fun return_false_if_credentials_not_stored() {
        assertThat(isUserLoggedInSingle.blockingGet()).isFalse()
    }

    @Test
    fun return_false_if_database_is_not_ready() {
        whenever(credentialsSecureStore.get()).doReturn(credentials)
        assertThat(isUserLoggedInSingle.blockingGet()).isFalse()
    }

    @Test
    fun return_true_if_credentials_stored() {
        whenever(credentialsSecureStore.get()).doReturn(credentials)
        assertThat(isUserLoggedInSingle.blockingGet()).isTrue()
    }
}
