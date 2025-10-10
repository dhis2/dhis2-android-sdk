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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import org.hisp.dhis.android.core.systeminfo.internal.ServerTimezoneManager
import org.junit.Test

/**
 * Integration test to verify that timezone conversions work correctly when downloading events.
 *
 * IMPORTANT: This test connects to a REAL server and is commented out by default.
 * To run it:
 * 1. Uncomment the @Test annotations
 * 2. Configure your server credentials in BaseRealIntegrationTest
 * 3. Ensure your server has events with dates
 *
 * Expected behavior:
 * - Server timezone (e.g., Etc/UTC) is retrieved from SystemInfo
 * - Client timezone (e.g., Europe/Madrid) is the device timezone
 * - DateTimezoneConverter converts dates preserving LocalDateTime (wall clock time)
 */
class EventTimezoneConversionRealIntegrationShould : BaseRealIntegrationTest() {

    //@Test
    fun download_events_and_convert_timezones_correctly() {
        d2.userModule().blockingLogIn(username,"Android123!", "https://android.im.dhis2.org/dev")
        d2.metadataModule().blockingDownload()
        d2.eventModule().eventDownloader().limit(10).blockingDownload()

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

        // Verify timezone conversion for downloaded events
        println("\n=== Event Date Conversions ===")

        // Test the round-trip conversion: server string -> client Date -> verify LocalDateTime preservation
        val testCases = listOf(
            "2025-09-23T07:51:46.118",  // Example timestamp from server
            "2025-10-02T06:37:08.735"   // Another example
        )

        testCases.forEach { serverDateString ->
            // Simulate what happens when server sends a date string
            val convertedDate = DateTimezoneConverter.convertServerStringToClient(serverDateString)!!

            // Parse the original server date string to LocalDateTime
            val originalLocalDateTime = LocalDateTime.parse(serverDateString)

            // Read the converted date in client timezone
            val clientLocalDateTime = convertedDate.toKtxInstant().toLocalDateTime(TimeZone.currentSystemDefault())

            println("\nTest case: $serverDateString")
            println("  Original LocalDateTime: $originalLocalDateTime")
            println("  After conversion, client sees: $clientLocalDateTime")
            println("  LocalDateTime preserved: ${originalLocalDateTime == clientLocalDateTime}")

            // Verify that LocalDateTime is preserved
            assertThat(clientLocalDateTime.toString()).isEqualTo(originalLocalDateTime.toString())
        }

        // Also verify real events from the server
        events.take(5).forEach { event ->
            event.created()?.let { created ->
                val clientLocalDateTime = created.toKtxInstant().toLocalDateTime(TimeZone.currentSystemDefault())

                println("\nEvent ${event.uid()}:")
                println("  Created timestamp: ${created.time}")
                println("  Client local time (${TimeZone.currentSystemDefault()}): $clientLocalDateTime")

                // Verify that the timestamp is not null (basic sanity check)
                assertThat(created).isNotNull()
                assertThat(created.time).isGreaterThan(0)
            }
        }

        println("\n✓ Timezone conversions verified successfully")
    }

}
