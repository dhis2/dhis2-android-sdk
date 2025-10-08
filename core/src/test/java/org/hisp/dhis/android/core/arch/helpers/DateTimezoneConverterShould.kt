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
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
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

@RunWith(JUnit4::class)
class DateTimezoneConverterShould {

    private val serverTimezoneManager: ServerTimezoneManager = mock()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single { serverTimezoneManager }
                },
            )
        }
    }

    @Test
    fun convert_client_to_server_with_same_timezone() {
        // Server uses client's system timezone (common scenario)
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.currentSystemDefault())

        val clientDate = dateFormat.parse("2024-01-15T10:30:00")!!
        val serverDate = DateTimezoneConverter.convertClientToServer(clientDate)

        // When server and client are in same timezone, date should not change
        assertThat(serverDate.time).isEqualTo(clientDate.time)
    }

    @Test
    fun convert_server_to_client_with_same_timezone() {
        // Server uses client's system timezone (common scenario)
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.currentSystemDefault())

        val serverDate = dateFormat.parse("2024-01-15T10:30:00")!!
        val clientDate = DateTimezoneConverter.convertServerToClient(serverDate)

        // When server and client are in same timezone, date should not change
        assertThat(clientDate.time).isEqualTo(serverDate.time)
    }

    @Test
    fun convert_client_to_server_with_different_timezone() {
        // Server timezone: Asia/Kolkata (UTC+5:30)
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("Asia/Kolkata"))

        val clientDate = dateFormat.parse("2024-01-15T10:30:00")!!
        val serverDate = DateTimezoneConverter.convertClientToServer(clientDate)

        // The conversion should preserve the local date-time but in server timezone context
        assertThat(serverDate).isNotNull()
        assertThat(serverDate).isNotEqualTo(clientDate)
    }

    @Test
    fun convert_server_to_client_with_different_timezone() {
        // Server timezone: Asia/Kolkata (UTC+5:30)
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("Asia/Kolkata"))

        val serverDate = dateFormat.parse("2024-01-15T10:30:00")!!
        val clientDate = DateTimezoneConverter.convertServerToClient(serverDate)

        // The conversion should preserve the local date-time but in client timezone context
        assertThat(clientDate).isNotNull()
        assertThat(clientDate).isNotEqualTo(serverDate)
    }

    @Test
    fun round_trip_conversion_preserves_original_date() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.of("America/New_York"))

        val originalDate = dateFormat.parse("2024-01-15T10:30:00")!!

        // Convert client -> server -> client
        val serverDate = DateTimezoneConverter.convertClientToServer(originalDate)
        val roundTripDate = DateTimezoneConverter.convertServerToClient(serverDate)

        // Round trip should preserve the original date
        assertThat(roundTripDate.time).isEqualTo(originalDate.time)
    }

    @Test
    fun handle_edge_case_with_epoch_date() {
        whenever(serverTimezoneManager.getServerTimeZone()).doReturn(TimeZone.UTC)

        val epochDate = Date(0)
        val serverDate = DateTimezoneConverter.convertClientToServer(epochDate)
        val clientDate = DateTimezoneConverter.convertServerToClient(serverDate)

        assertThat(serverDate).isNotNull()
        assertThat(clientDate.time).isEqualTo(epochDate.time)
    }
}
