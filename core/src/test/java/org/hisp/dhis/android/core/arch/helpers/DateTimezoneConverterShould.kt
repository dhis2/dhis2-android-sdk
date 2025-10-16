/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.arch.helpers

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone.getTimeZone

@RunWith(JUnit4::class)
class DateTimezoneConverterShould {

    private val serverTimezoneManager: ServerTimezoneManager = mock()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
        timeZone = getTimeZone(TimeZone.currentSystemDefault().toJavaZoneId())
    }

    @Before
    fun setUp() {
        try {
            stopKoin()
        } catch (_: Exception) {
            // Ignore if Koin is not started
        }
        val koinApp = startKoin {
            modules(
                module {
                    single { serverTimezoneManager }
                },
            )
        }
        DhisAndroidSdkKoinContext.koin = koinApp.koin

        // Force DateTimezoneConverter to use this test's mock
        DateTimezoneConverter.serverTimezoneManager = serverTimezoneManager
    }

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (_: Exception) {
            // Ignore if Koin is not started
        }

        // Reinitialize with neutral mock to avoid affecting other tests
        val neutralMock: ServerTimezoneManager = mock {
            on { getServerTimeZone() } doReturn TimeZone.currentSystemDefault()
        }
        val koinApp = startKoin {
            modules(
                module {
                    single { neutralMock }
                },
            )
        }
        DhisAndroidSdkKoinContext.koin = koinApp.koin

        // Force DateTimezoneConverter to use the neutral mock
        DateTimezoneConverter.serverTimezoneManager = neutralMock
    }

    @Test
    fun convert_client_to_server_with_same_timezone() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.currentSystemDefault())

        val clientDate = dateFormat.parse("2024-01-15T10:30:00.000")!!
        val serverDate = DateTimezoneConverter.convertClientToServer(clientDate)

        assertThat(serverDate.time).isEqualTo(clientDate.time)
    }

    @Test
    fun convert_server_to_client_with_same_timezone() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.currentSystemDefault())

        val serverDateString = "2024-01-15T10:30:00.000"
        val clientDate = DateTimezoneConverter.convertServerToClient(serverDateString)

        assertThat(clientDate).isNotNull()
        assertThat(clientDate).isEqualTo(dateFormat.parse(serverDateString))
    }

    @Test
    fun convert_client_to_server_with_different_timezone() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("Asia/Kolkata"))

        val clientDate = dateFormat.parse("2024-01-15T10:30:00.000")!!
        val serverDate = DateTimezoneConverter.convertClientToServer(clientDate)

        assertThat(serverDate).isNotNull()
        assertThat(serverDate).isNotEqualTo(clientDate)
    }

    @Test
    fun convert_server_to_client_with_different_timezone() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("Asia/Kolkata"))

        val serverDateString = "2024-01-15T10:30:00.000"
        val clientDate = DateTimezoneConverter.convertServerToClient(serverDateString)

        assertThat(clientDate).isNotNull()
        assertThat(clientDate).isNotEqualTo(dateFormat.parse(serverDateString))
    }

    @Test
    fun round_trip_conversion_preserves_original_date() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("America/New_York"))

        val originalDate = dateFormat.parse("2024-01-15T10:30:00.000")!!

        val serverDate = DateTimezoneConverter.convertClientToServer(originalDate)
        val serverDateString = dateFormat.format(serverDate)
        val roundTripDate = DateTimezoneConverter.convertServerToClient(serverDateString)

        assertThat(roundTripDate).isEqualTo(originalDate)
    }

    @Test
    fun handle_edge_case_with_epoch_date() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.UTC)

        val epochDate = Date(0)
        val serverDate = DateTimezoneConverter.convertClientToServer(epochDate)
        val clientDate = DateTimezoneConverter.convertServerToClient(dateFormat.format(serverDate))

        assertThat(serverDate).isNotNull()
        assertThat(clientDate).isEqualTo(epochDate)
    }
}
