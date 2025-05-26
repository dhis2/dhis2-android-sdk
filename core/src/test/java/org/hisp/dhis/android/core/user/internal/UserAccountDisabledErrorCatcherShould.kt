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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.api.internal.D2HttpResponse
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.AccountDeletionReason
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class UserAccountDisabledErrorCatcherShould {
    private lateinit var catcher: UserAccountDisabledErrorCatcher
    private lateinit var response: D2HttpResponse
    private var accountManager: AccountManagerImpl = mock()

    @Before
    fun setUp() {
        catcher = UserAccountDisabledErrorCatcher(accountManager)

        val responseError = "{\"httpStatus\": \"Unauthorized\",\"httpStatusCode\": 401,\"status\": \"ERROR\"," +
            "\"message\": \"Account disabled\"}"
        response = D2HttpResponse(
            statusCode = 401,
            message = "",
            errorBody = responseError,
            requestUrl = null,
        )
    }

    @Test
    fun return_account_disabled() {
        Truth.assertThat(catcher.catchError(response))
            .isEqualTo(D2ErrorCode.USER_ACCOUNT_DISABLED)
    }

    @Test
    fun delete_database() {
        catcher.catchError(response)
        verify(accountManager, times(1)).deleteCurrentAccountAndEmit(AccountDeletionReason.ACCOUNT_DISABLED)
    }
}
