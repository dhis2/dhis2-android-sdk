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
@file:OptIn(ExperimentalCoroutinesApi::class)

package org.hisp.dhis.android.core.user.internal

import com.nhaarman.mockitokotlin2.*
import java.util.concurrent.Callable
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
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
import retrofit2.Call

@RunWith(JUnit4::class)
class UserCallShould : BaseCallShould() {
    private val userService: UserService = mock()
    private val userHandler: UserHandler = mock()
    private val userCall: User = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = mock()

    private val user: User = mock()

    private lateinit var userSyncCall: Callable<User>

    @Before
    override fun setUp() {
        super.setUp()
        userSyncCall = UserCall(genericCallData, coroutineAPICallExecutor, userService, userHandler, dhisVersionManager)
        userService.stub {
            onBlocking { getUser(any()) }.doReturn(userCall)
        }

        whenever(dhisVersionManager.getVersion()).thenReturn(DHISVersion.V2_39)
    }

    @Test
    fun not_invoke_stores_on_call_io_exception() = runTest {
        whenever(coroutineAPICallExecutor.wrap { userCall }.getOrThrow()).thenThrow(d2Error)

        try {
            userSyncCall.call()
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
        whenever(coroutineAPICallExecutor.wrap { userCall }.getOrThrow()).thenThrow(d2Error)
        try {
            userSyncCall.call()
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
        whenever(coroutineAPICallExecutor.wrap { userCall }.getOrThrow()).thenReturn(user)
        userSyncCall.call()
        verify(userHandler).handle(eq(user))
    }
}
