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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// ToDo: implement integration tests for user authentication task
// ToDo: more tests to verify correct store behaviour
// ToDo:    - what will happen if the same user will be inserted twice?
@RunWith(D2JunitRunner::class)
class LogInCallMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {
    @Before
    fun setUp() {
        dhis2MockServer.enqueueLoginResponses()
    }

    @After
    fun tearDown() {
        d2.userModule().blockingLogOut()
    }

    @Test
    fun persist_user_in_data_base_when_call() {
        login()
        val user = d2.userModule().user().blockingGet()!!
        assertThat(user.uid()).isEqualTo("DXyJmlo9rge")
        assertThat(user.name()).isEqualTo("John Barnes")
        assertThat(user.username()).isEqualTo("android")

        val userCredentials = d2.userModule().userCredentials().blockingGet()!!
        assertThat(userCredentials.username()).isEqualTo("android")

        val authenticatedUser = d2.userModule().authenticatedUser().blockingGet()!!
        assertThat(authenticatedUser.user()).isEqualTo("DXyJmlo9rge")
    }

    @Test
    fun return_correct_user_when_call() {
        val user = login()

        // verify payload which has been returned from call
        assertThat(user.uid()).isEqualTo("DXyJmlo9rge")
        assertThat(user.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-03-31T13:31:09.324"))
        assertThat(user.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2016-04-06T00:05:57.495"))
        assertThat(user.name()).isEqualTo("John Barnes")
        assertThat(user.displayName()).isEqualTo("John Barnes")
        assertThat(user.firstName()).isEqualTo("John")
        assertThat(user.surname()).isEqualTo("Barnes")
        assertThat(user.email()).isEqualTo("john@hmail.com")
    }

    private fun login(): User {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        return d2.userModule().blockingLogIn("test_user", "test_password", dhis2MockServer.baseEndpoint, null)
    }
}
