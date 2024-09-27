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
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMethodScopedEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
@Ignore("Test start failing after change in ANDROSDK-1923")
class LogInOfflineCallMockIntegrationShould : BaseMockIntegrationTestMethodScopedEmptyEnqueable() {

    @Test
    fun login_offline_on_connection_error() {
        dhis2MockServer.enqueueLoginResponses()

        login()
        assertThat(getUser()).isNotNull()

        logout()
        assertThrowsException { getUser() }

        dhis2MockServer.shutdown()

        login()
        assertThat(d2.userModule().user().blockingGet()).isNotNull()
    }

    private fun login(): User {
        return d2.userModule().blockingLogIn("test_user", "test_password", dhis2MockServer.baseEndpoint)
    }

    private fun logout() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
    }

    private fun getUser(): User? {
        return d2.userModule().user().blockingGet()
    }

    private fun assertThrowsException(block: () -> Any?) {
        try {
            block()
            fail("Get user should fail after logout")
        } catch (_: RuntimeException) {
            //
        }
    }
}
