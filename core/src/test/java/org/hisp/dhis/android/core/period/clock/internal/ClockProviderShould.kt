/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.period.clock.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.hisp.dhis.android.core.period.PeriodDIModule
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Instant

@RunWith(JUnit4::class)
class ClockProviderShould {

    @After
    fun tearDown() {
        ClockProviderFactory.reset()
    }

    @Test
    fun truncate_instants_to_milliseconds() {
        val instant = Instant.fromEpochSeconds(1_575_973_800, 123_456_789)

        assertThat(instant.truncatedToMillis()).isEqualTo(Instant.fromEpochSeconds(1_575_973_800, 123_000_000))
    }

    @Test
    fun return_instants_that_survive_a_millisecond_round_trip_from_the_regular_clock() {
        val clock = RegularClockProvider().clock

        repeat(SAMPLES) {
            val now = clock.now()
            assertThat(now.nanosecondsOfSecond % NANOS_PER_MILLI).isEqualTo(0)
            assertThat(Instant.fromEpochMilliseconds(now.toEpochMilliseconds())).isEqualTo(now)
        }
    }

    @Test
    fun return_the_same_truncated_instant_from_the_fixed_clock() {
        val dateTime = LocalDateTime(2019, 12, 10, 10, 30, 0, 123_456_789)
        val clock = FixedClockProvider(dateTime).clock

        val expected = LocalDateTime(2019, 12, 10, 10, 30, 0, 123_000_000).toInstant(TimeZone.currentSystemDefault())
        assertThat(clock.now()).isEqualTo(expected)
        assertThat(clock.now()).isEqualTo(clock.now())
    }

    @Test
    fun return_today_in_the_device_time_zone() {
        val lastMillisOfDay = LocalDateTime(2019, 12, 10, 23, 59, 59, 999_000_000)

        assertThat(FixedClockProvider(lastMillisOfDay).today()).isEqualTo(LocalDate(2019, 12, 10))
        assertThat(FixedClockProvider(LocalDateTime(2019, 12, 11, 0, 0)).today()).isEqualTo(LocalDate(2019, 12, 11))
    }

    @Test
    fun provide_the_factory_clock_through_koin_module() {
        ClockProviderFactory.setFixed()
        val fixed = ClockProviderFactory.clockProvider

        assertThat(PeriodDIModule().clockProvider()).isSameInstanceAs(fixed)
    }

    private companion object {
        const val SAMPLES = 100
        const val NANOS_PER_MILLI = 1_000_000
    }
}
