/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.core.systeminfo.internal

import com.google.common.truth.Truth.assertThat
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class PingImplShould {

    @Mock
    internal lateinit var service: PingService

    private lateinit var ping: PingImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        ping = PingImpl(service)
    }

    @Test
    fun get_should_return_success_when_ping_service_succeeds() {
        val expectedResponse = "pong"
        val responseBody = expectedResponse.toResponseBody()
        val response = Response.success(responseBody)
        runBlocking {
            `when`(service.getPing()).thenReturn(response)
        }

        assertThat(ping.blockingGet()).isEqualTo(expectedResponse)
    }

    @Test
    fun get_should_return_D2Error_when_ping_service_fails() {
        val errorCode = HttpURLConnection.HTTP_INTERNAL_ERROR
        val httpException = HttpException(Response.error<String>(errorCode, "Internal Server Error".toResponseBody()))

        runBlocking {
            `when`(service.getPing()).thenThrow(httpException)
        }

        try {
            ping.blockingGet()
            fail("D2Error was expected but not thrown.")
        } catch (e: RuntimeException) {
            val cause = e.cause
            assertTrue("Cause of RuntimeException should be D2Error", cause is D2Error)

            val d2Error = cause as D2Error
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            assertThat(d2Error.errorDescription()).isEqualTo("Unable to ping the server.")
            assertThat((d2Error.originalException() as HttpException).code()).isEqualTo(errorCode)
        }
    }
}
