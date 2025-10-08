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

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toKtxInstant
import java.util.Date

/**
 * Utility object for converting dates between client and server timezones.
 *
 * These conversions are applied only in the network layer (DTOs) to maintain
 * backward compatibility with domain and persistence layers.
 */
internal object DateTimezoneConverter {

    /**
     * Converts a date from client timezone to server timezone.
     *
     * @param date The date in client timezone
     * @param serverTimeZone The target server timezone
     * @return Date converted to server timezone context
     */
    fun convertClientToServer(date: Date, serverTimeZone: TimeZone): Date {
        val clientLocalDateTime = date.toKtxInstant()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val serverInstant = clientLocalDateTime.toInstant(serverTimeZone)

        return serverInstant.toJavaDate()
    }

    /**
     * Converts a date from server timezone to client timezone.
     *
     * @param date The date in server timezone
     * @param serverTimeZone The source server timezone
     * @return Date converted to client timezone context
     */
    fun convertServerToClient(date: Date, serverTimeZone: TimeZone): Date {
        val serverLocalDateTime = date.toKtxInstant()
            .toLocalDateTime(serverTimeZone)
        val clientInstant = serverLocalDateTime.toInstant(TimeZone.currentSystemDefault())

        return clientInstant.toJavaDate()
    }

    /**
     * Gets the server timezone from SystemInfo.
     * Falls back to UTC if not available.
     *
     * @param serverDate The server date from SystemInfo, or null
     * @return The server timezone, defaulting to UTC
     */
    fun getServerTimeZone(serverDate: Date?): TimeZone {
        // For now, we'll use UTC
        // In the future, this will be extracted from a dedicated timezone field in SystemInfo
        return TimeZone.UTC
    }
}
