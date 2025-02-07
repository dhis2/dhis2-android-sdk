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
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.hisp.dhis.android.network.ping.PingNetworkHandlerImpl
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PingImplShould {
    private val dhisVersionManager: DHISVersionManager = mock()

    @Before
    fun setUp() {
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_36)).doReturn(true)
    }

    @Test
    fun get_should_return_success_when_ping_service_succeeds2() = runBlocking {
        val expectedResponse = ""
        val mockEngine = MockEngine { respond(content = "") }
        val client = HttpClient(mockEngine)
        val httpServiceClient = HttpServiceClientKotlinx(mock(), mock(), client)
        val pingNetworkHandler = PingNetworkHandlerImpl(httpServiceClient, dhisVersionManager)

        val response = pingNetworkHandler.getPing()
        val result = response.bodyAsText()
        assertThat(expectedResponse).isEqualTo(result)
    }

    @Test
    fun get_should_return_D2Error_when_ping_service_fails() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError,
            )
        }
        val client = HttpClient(mockEngine)
        val httpServiceClient = HttpServiceClientKotlinx(mock(), mock(), client)
        val pingNetworkHandler = PingNetworkHandlerImpl(httpServiceClient, dhisVersionManager)
        val pingImpl = PingImpl(pingNetworkHandler)

        try {
            pingImpl.blockingGet()
            fail("D2Error was expected but not thrown.")
        } catch (e: RuntimeException) {
            val cause = e.cause
            assertTrue("Cause of RuntimeException should be D2Error", cause is D2Error)

            val d2Error = cause as D2Error
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            assertThat(d2Error.errorDescription()).isEqualTo("Unable to ping the server.")
            assertThat(
                d2Error.originalException()?.message,
            ).isEqualTo("Ping to the server failed with status code: 500")
        }
    }
}
