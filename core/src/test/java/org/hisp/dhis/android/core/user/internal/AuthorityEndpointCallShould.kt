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
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl
import org.hisp.dhis.android.core.arch.call.internal.EndpointCall
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.user.Authority
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Callable

@RunWith(JUnit4::class)
class AuthorityEndpointCallShould : BaseCallShould() {
    private val authorityService: AuthorityService = mock()
    private val handler: AuthorityHandler = mock()
    private val retrofitCall: Call<List<String>> = mock()
    private val payload: List<String> = mock()
    private val userAccountDisabledErrorCatcher: UserAccountDisabledErrorCatcher = mock()

    private lateinit var endpointCall: Callable<List<Authority>>

    @Before
    override fun setUp() {
        super.setUp()
        val apiCallExecutor = APICallExecutorImpl.create(databaseAdapter, userAccountDisabledErrorCatcher)
        endpointCall = AuthorityEndpointCallFactory(
            genericCallData,
            apiCallExecutor,
            handler,
            retrofit.create(AuthorityService::class.java),
        ).create()
        whenever(retrofitCall.execute()).thenReturn(Response.success(payload))
        whenever(authorityService.authorities).thenReturn(retrofitCall)
    }

    private fun castedEndpointCall(): EndpointCall<Authority> {
        return endpointCall as EndpointCall<Authority>
    }

    @Test
    fun extend_endpoint_call() {
        assertThat(endpointCall is EndpointCall<*>).isTrue()
    }

    @Test
    fun have_payload_no_resource_fetcher() {
        assertThat(castedEndpointCall().fetcher is AuthorityCallFetcher).isTrue()
    }

    @Test
    fun have_transactional_no_resource_call_processor() {
        val castedEndpointCall = endpointCall as EndpointCall<Authority>
        assertThat(castedEndpointCall.processor is AuthorityCallProcessor).isTrue()
    }
}
