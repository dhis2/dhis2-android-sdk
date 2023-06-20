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

package org.hisp.dhis.android.core.arch.api.executors.internal

import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleaner
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CoroutineAPICallExecutorShould {

    private val errorMapper: APIErrorMapper = mock()
    private val userAccountDisabledErrorCatcher: UserAccountDisabledErrorCatcher = mock()
    private val errorStore: D2ErrorStore = mock()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val foreignKeyCleaner: ForeignKeyCleaner = mock()

    private lateinit var coroutineAPICallExecutor: CoroutineAPICallExecutor

    @Before
    fun setUp() {
        coroutineAPICallExecutor = CoroutineAPICallExecutorImpl(
            errorMapper = errorMapper,
            userAccountDisabledErrorCatcher = userAccountDisabledErrorCatcher,
            errorStore = errorStore,
            databaseAdapter = databaseAdapter,
            foreignKeyCleaner = foreignKeyCleaner
        )
    }

    @Test
    fun `should return object when object api call succeed`() = runBlocking {
        // Given suspend function returns an Int
        val expectedResult = Result.Success<Int, D2Error>(13)
        val block: suspend () -> Int = { 13 }

        // When api call is executed
        val result = coroutineAPICallExecutor.wrap {
            block.invoke()
        }

        // Then the executor returns the expected result
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should return D2Error when object api call fails`() = runBlocking {
        // Given suspend function returns an error
        val d2Error = D2Error.builder()
            .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            .errorDescription("API call failed")
            .build()
        val expectedResult = Result.Failure<Int, D2Error>(d2Error)

        // When api call is executed
        val result = coroutineAPICallExecutor.wrap {
            throw d2Error
        }

        // Then the executor returns the D2Error
        assertEquals(expectedResult, result)
    }
}
