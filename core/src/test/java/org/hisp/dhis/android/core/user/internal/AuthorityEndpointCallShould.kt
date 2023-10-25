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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorImpl
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CoroutineCallFetcher
import org.hisp.dhis.android.core.arch.call.internal.EndpointCall
import org.hisp.dhis.android.core.arch.call.internal.EndpointCoroutineCall
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.user.Authority
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Callable

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AuthorityEndpointCallShould : BaseCallShould() {
    private val authorityService: AuthorityService = mock()
    private val handler: AuthorityHandler = mock()
    private val payload: List<String> = mock()
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor = CoroutineAPICallExecutorMock()

    private lateinit var endpointCall: suspend () -> List<Authority>

    @Before
    override fun setUp() {
        super.setUp()

        endpointCall = { AuthorityEndpointCallFactory(
            genericCallData,
            coroutineAPICallExecutor,
            handler,
            retrofit.create(AuthorityService::class.java),
        ).create() }
        authorityService.stub {
            onBlocking { authorities() }.thenReturn(payload)
        }
    }

    private fun castedEndpointCall(): EndpointCall<Authority> {
        return endpointCall as EndpointCall<Authority>
    }

/*    @Test
    fun have_payload_no_resource_fetcher() = runTest {
        assertThat(1 + 1 == 2).isTrue()
    }*/


 /*   @Test
    fun extend_endpoint_call() = runTest {
        assertThat(endpointCall is EndpointCall<*>).isTrue()
    }

    @Test
    fun have_payload_no_resource_fetcher() = runTest {
        assertThat(castedEndpointCall() is AuthorityCallFetcher).isTrue()
    }

    @Test
    fun have_transactional_no_resource_call_processor() {
        val castedEndpointCall = endpointCall as EndpointCall<Authority>
        assertThat(castedEndpointCall.processor is AuthorityCallProcessor).isTrue()
    }*/
}
