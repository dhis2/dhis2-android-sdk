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
package org.hisp.dhis.android.core.trackedentity.internal

import kotlinx.datetime.*
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityAttributeReservedValueValidatorHelper {
    @Throws(IllegalStateException::class)
    fun getExpiryDateCode(pattern: String?): Instant {
        val matches = getCurrentDatePatternStrList(pattern)

        val flags = matches.flatMap { it.toCharArray().asIterable() }
            .fold(Triple(false, false, false)) { acc, ch ->
                when (ch) {
                    'Y' -> acc.copy(first = true)
                    'M' -> acc.copy(second = true)
                    'w' -> acc.copy(third = true)
                    else -> acc
                }
            }

        return nextExpiryDate(flags.first, flags.second, flags.third)
    }

    fun getCurrentDatePatternStrList(pattern: String?): List<String> {
        val regex = Regex("""CURRENT_DATE\((.*?)\)""")
        return regex.findAll(pattern ?: "")
            .flatMap { it.groupValues.drop(1) }
            .filter { it.isNotEmpty() }
            .toList()
    }

    @Throws(IllegalStateException::class)
    fun nextExpiryDate(yearly: Boolean, monthly: Boolean, weekly: Boolean): Instant {
        val now = ClockProviderFactory.clockProvider.clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val nextDate = when {
            weekly -> {
                val daysUntilNextWeek = DAYS_IN_A_WEEK - now.dayOfWeek.ordinal
                now.plus(daysUntilNextWeek, DateTimeUnit.DAY)
            }

            monthly -> {
                val nextMonth = now.plus(1, DateTimeUnit.MONTH)
                LocalDate(nextMonth.year, nextMonth.month, 1)
            }

            yearly -> {
                LocalDate(now.year + 1, 1, 1)
            }

            else -> throw IllegalStateException("No expiry date available for this pattern.")
        }
        return nextDate.atStartOfDayIn(TimeZone.currentSystemDefault())
    }

    companion object {
        const val DAYS_IN_A_WEEK = 7
    }
}
