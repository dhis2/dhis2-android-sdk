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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.period.clock.internal.setFixed
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackedEntityAttributeReservedValueValidatorHelperShould {
    private lateinit var helper: TrackedEntityAttributeReservedValueValidatorHelper

    @Before
    fun setUp() {
        ClockProviderFactory.setFixed()
        helper = TrackedEntityAttributeReservedValueValidatorHelper()
    }

    @Test
    fun test_get_expiry_date_code_with_yearly_pattern() {
        val pattern = "CURRENT_DATE(Y)"
        val expiryInstant = helper.getExpiryDateCode(pattern)
        val expectedInstant = LocalDate(2020, 1, 1)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(expiryInstant).isEqualTo(expectedInstant)
    }

    @Test
    fun test_get_expiry_date_code_with_monthly_pattern() {
        val pattern = "CURRENT_DATE(M)"
        val expiryInstant = helper.getExpiryDateCode(pattern)
        val expectedInstant = LocalDate(2020, 1, 1)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(expiryInstant).isEqualTo(expectedInstant)
    }

    @Test
    fun test_get_expiry_date_code_with_weekly_pattern() {
        val pattern = "CURRENT_DATE(w)"
        val expiryInstant = helper.getExpiryDateCode(pattern)
        val expectedInstant = LocalDate(2019, 12, 16)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(expiryInstant).isEqualTo(expectedInstant)
    }

    @Test
    fun test_get_expiry_date_code_with_combo_pattern() {
        val pattern = "CURRENT_DATE(YYYYMM) + RANDOM(###) + CURRENT_DATE(MMww)"
        val expiryInstant = helper.getExpiryDateCode(pattern)
        val expectedInstant = LocalDate(2019, 12, 16)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(expiryInstant).isEqualTo(expectedInstant)
    }

    @Test
    fun get_next_expiry_date() {
        val instant = helper.nextExpiryDate(yearly = true, monthly = false, weekly = false)
        val expectedInstant = LocalDate(2020, 1, 1)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(instant).isEqualTo(expectedInstant)
    }

    @Test
    fun get_expiry_date_from_pattern() {
        val expiryInstant = helper.getExpiryDateCode("CURRENT_DATE(YYYYMM) + RANDOM(###) + CURRENT_DATE(MMww)")
        val expectedInstant = LocalDate(2019, 12, 16)
            .atStartOfDayIn(TimeZone.currentSystemDefault())

        assertThat(expiryInstant).isEqualTo(expectedInstant)
    }

    @Test
    fun test_get_current_date_pattern_str_list_with_valid_pattern() {
        val pattern = "CURRENT_DATE(Y) CURRENT_DATE(M) CURRENT_DATE(w)"
        val result = helper.getCurrentDatePatternStrList(pattern)

        assertThat(result).containsExactly("Y", "M", "w")
    }

    @Test
    fun get_current_date_list_from_pattern() {
        val pattern = "CURRENT_DATE(YYYYMM) + RANDOM(###) + CURRENT_DATE(MMww)"
        val result = helper.getCurrentDatePatternStrList(pattern)

        assertThat(listOf("YYYYMM", "MMww")).isEqualTo(result)
    }

    @Test
    fun test_get_current_date_pattern_str_list_with_empty_pattern() {
        val pattern = ""
        val result = helper.getCurrentDatePatternStrList(pattern)
        assertThat(result).isEmpty()
    }

    @Test
    fun test_get_current_date_pattern_str_list_with_null_pattern() {
        val result = helper.getCurrentDatePatternStrList(null)
        assertThat(result).isEmpty()
    }

    @Test
    fun test_get_current_date_pattern_str_list_with_invalid_pattern() {
        val pattern = "SOME_TEXT CURRENT_DATE() MORE_TEXT"
        val result = helper.getCurrentDatePatternStrList(pattern)

        assertThat(result).isEmpty()
    }

    @Test
    fun test_get_expiry_date_code_with_no_valid_flags() {
        val pattern = "CURRENT_DATE()"
        try {
            helper.getExpiryDateCode(pattern)
            assertThat("Exception should be thrown").isEmpty()
        } catch (e: IllegalStateException) {
            assertThat(e).hasMessageThat().isEqualTo("No expiry date available for this pattern.")
        }
    }

    @Test
    fun test_next_expiry_date_with_no_flags_should_throw_exception() {
        try {
            helper.nextExpiryDate(yearly = false, monthly = false, weekly = false)
            assertThat("Exception should be thrown").isEmpty()
        } catch (e: IllegalStateException) {
            assertThat(e).hasMessageThat().isEqualTo("No expiry date available for this pattern.")
        }
    }
}
