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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager

class EventTimezoneConversionRealIntegrationShould : BaseRealIntegrationTest() {

    // @Test
    fun download_events_and_convert_timezones_correctly() {
        d2.userModule().blockingLogIn(username, "Android123!", "https://android.im.dhis2.org/dev")
        d2.metadataModule().blockingDownload()
        d2.eventModule().eventDownloader().limit(3).blockingDownload()

        // Verify server timezone was correctly retrieved from SystemInfo
        val serverTimezoneManager = koin.get<ServerTimezoneManager>()
        val serverTimeZone = serverTimezoneManager.getServerTimeZone()

        println("=== Timezone Information ===")
        println("Server timezone: $serverTimeZone")
        println("Client timezone: ${TimeZone.currentSystemDefault()}")

        // Get downloaded events
        val events = d2.eventModule().events().blockingGet()

        println("\n=== Event Download Results ===")
        println("Total events downloaded: ${events.size}")

        if (events.isEmpty()) {
            println("WARNING: No events were downloaded from server")
            return
        }

        println("\n=== Event Date Conversions ===")

        // Test the round-trip conversion: server string -> client Date -> verify LocalDateTime preservation
        val testCases = listOf(
            "2025-09-23T09:51:46.118",
            "2025-10-02T06:20:54.560",
        )

        testCases.forEach { serverDateString ->
            val clientDate = DateTimezoneConverter.convertServerToClient(serverDateString)
            val serverDate = DateTimezoneConverter.convertClientToServer(clientDate!!)
            val loopedServerDateString = DateUtils.DATE_FORMAT.format(serverDate)

            println("\n=== Test Case ===")
            println("\nServer date: $serverDateString")
            println("  Client date: $clientDate")
            println("  Server date after round trip: $serverDate")

            // Verify that LocalDateTime is preserved
            assertThat(serverDateString).isEqualTo(loopedServerDateString)
        }

        // Also verify real events from the server
        events.take(3).forEach { event ->
            event.created()?.let { created ->
                val clientLocalDateTime = created.toKtxInstant().toLocalDateTime(TimeZone.currentSystemDefault())

                println("\nEvent ${event.uid()}:")
                println("  Created timestamp: ${created.time}")
                println("  Client local time (${TimeZone.currentSystemDefault()}): $clientLocalDateTime")

                assertThat(created).isNotNull()
                assertThat(created.time).isGreaterThan(0)
            }
        }

        println("\n✓ Timezone conversions verified successfully")
    }
}
