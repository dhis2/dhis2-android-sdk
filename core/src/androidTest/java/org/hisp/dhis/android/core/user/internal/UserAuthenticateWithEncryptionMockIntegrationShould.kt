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
package org.hisp.dhis.android.core.user.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Factory.clear
import org.hisp.dhis.android.core.D2Factory.forNewDatabase
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class UserAuthenticateWithEncryptionMockIntegrationShould {
    @Test
    fun return_false_for_blocking_is_logged_when_not_logged() {
        assertThat(d2.userModule().blockingIsLogged()).isFalse()
    }

    @Test
    fun return_false_for_is_logged_when_not_logged() {
        assertThat(d2.userModule().isLogged().blockingGet()).isFalse()
    }

    @Test
    fun return_true_for_blocking_is_logged_when_logged() {
        logIn()
        assertThat(d2.userModule().blockingIsLogged()).isTrue()
        logOut()
    }

    @Test
    fun unencrypted_login_logout_once_succeeds() {
        logIn()
        logOut()
    }

    @Test
    fun unencrypted_login_logout_twice_succeeds() {
        logIn()
        logOut()
        logIn()
        logOut()
    }

    /* TODO this has to be configured in json
    @Test
    public void encrypted_login_logout_login_succeeds() {
        DatabaseAdapterFactory.setExperimentalEncryption(true);
        logIn();
        logOut();
        logIn();
        logOut();
        DatabaseAdapterFactory.setExperimentalEncryption(false);
    }

    @Test
    public void unencrypted_and_then_encypted_login_logout_login_succeeds() {
        logIn();
        logOut();
        DatabaseAdapterFactory.setExperimentalEncryption(true);
        logIn();
        logOut();
        DatabaseAdapterFactory.setExperimentalEncryption(false);
    }*/
    private fun logIn() {
        d2.userModule().blockingLogIn("test_user", "test_password", dhis2MockServer.baseEndpoint)
    }

    private fun logOut() {
        d2.userModule().blockingLogOut()
    }

    companion object {
        private lateinit var d2: D2
        private lateinit var dhis2MockServer: Dhis2MockServer

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            d2 = forNewDatabase()
            dhis2MockServer = Dhis2MockServer(0)
            dhis2MockServer.setRequestDispatcher()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            clear()
        }
    }
}
