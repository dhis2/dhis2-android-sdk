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

import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.stubbing.Answer

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserCallShould : BaseCallShould() {
    private val userNetworkHandler: UserNetworkHandler = mock()
    private val userHandler: UserHandler = mock()
    private val userCall: User = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val user: User = mock()

    private lateinit var userSyncCall: suspend () -> User

    @Before
    override fun setUp() {
        super.setUp()
        whenAPICall { userCall }

        userSyncCall = {
            UserCall(
                genericCallData,
                coroutineAPICallExecutor,
                userNetworkHandler,
                userHandler,
            ).call()
        }

        whenever(dhisVersionManager.getVersion()).thenReturn(DHISVersion.V2_39)
    }

    private fun whenAPICall(answer: Answer<User>) {
        userNetworkHandler.stub {
            onBlocking { getUser() }.doAnswer(answer)
        }
    }

    @Test
    fun not_invoke_stores_on_call_io_exception() = runTest {
        whenAPICall { throw d2Error }

        try {
            userSyncCall.invoke()
            Assert.fail("Exception was not thrown")
        } catch (ex: Exception) {
            // verify that handlers was not touched
            verify(databaseAdapter, never()).beginNewTransaction()
            verify(transaction, never()).setSuccessful()
            verify(transaction, never()).end()
            verify(userHandler, never()).handle(user)
        }
    }

    @Test
    fun not_invoke_handler_after_call_failure() = runTest {
        whenAPICall { throw d2Error }
        try {
            userSyncCall.invoke()
            Assert.fail("Call should't succeed")
        } catch (d2Exception: D2Error) {
            // verify that database was not touched
            verify(databaseAdapter, never()).beginNewTransaction()
            verify(transaction, never()).setSuccessful()
            verify(transaction, never()).end()
            verify(userHandler, never()).handle(user)
        }
    }

    @Test
    fun invoke_handlers_on_success() = runTest {
        whenAPICall { user }
        userSyncCall.invoke()
        verify(userHandler).handle(eq(user))
    }
}
